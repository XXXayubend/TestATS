package org.exercice.testats.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exercice.testats.dto.ScoreResponseDto;
import org.exercice.testats.entity.Candidat;
import org.exercice.testats.entity.Document;
import org.exercice.testats.entity.Offre;
import org.exercice.testats.exception.ResourceNotException;
import org.exercice.testats.repository.CandidatRepository;
import org.exercice.testats.repository.DocumentRepository;
import org.exercice.testats.repository.OffreRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiScoringService {

    private final CandidatRepository candidatRepository;
    private final OffreRepository offreRepository;
    private final DocumentRepository documentRepository;
    private final PdfTextExtractorService pdfTextExtractorService;
    private final WebClient openRouterWebClient;

    @Value("${openrouter.model}")
    private String model;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Durée maximale d'attente pour l'appel API
    private static final Duration TIMEOUT = Duration.ofSeconds(60);

    public ScoreResponseDto scoreCandidatForOffre(Long candidatId, Long offreId) throws Exception {
        // 1. Récupérer les entités
        Candidat candidat = candidatRepository.findById(candidatId)
                .orElseThrow(() -> new ResourceNotException("Candidat non trouvé avec l'ID : " + candidatId));
        Offre offre = offreRepository.findById(offreId)
                .orElseThrow(() -> new ResourceNotException("Offre non trouvée avec l'ID : " + offreId));

        // 2. Récupérer le CV (Document avec fileType "CV")
        Document cvDoc = documentRepository.findByCandidatIdAndFileType(candidatId, "CV")
                .orElseThrow(() -> new ResourceNotException("Aucun CV trouvé pour le candidat ID : " + candidatId));

        // 3. Extraire le texte du PDF
        log.info("Extraction du texte du CV pour le candidat ID : {}", candidatId);
        String cvText = pdfTextExtractorService.extractTextFromPdf(cvDoc.getStoragePath());
        String truncatedCv = cvText.length() > 6000 ? cvText.substring(0, 6000) : cvText;
        log.debug("Texte du CV extrait ({} caractères)", truncatedCv.length());

        // 4. Construire le prompt
        String prompt = buildPrompt(truncatedCv, offre);
        log.debug("Prompt construit, longueur : {}", prompt.length());

        // 5. Appeler OpenRouter
        log.info("Appel de l'API OpenRouter avec le modèle : {}", model);
        String responseText = callOpenRouter(prompt);

        // 6. Parser la réponse pour extraire le score et la justification
        ScoreResponseDto scoreDto = parseAiResponse(responseText);
        scoreDto.setCandidatNom(candidat.getNom());
        scoreDto.setOffreTitre(offre.getTitre());

        log.info("Scoring terminé : score = {}, justification = {}", scoreDto.getScore(), scoreDto.getJustification());
        return scoreDto;
    }

    private String buildPrompt(String cvText, Offre offre) {
        return """
                Tu es un recruteur expert en évaluation de CV.
                Voici le CV d'un candidat :
                ---
                %s
                ---
                Voici la description de l'offre d'emploi :
                Titre : %s
                Compétences requises : %s
                ---
                Analyse la correspondance entre le CV et l'offre.
                Donne une note sur 100 (un entier) en tenant compte des compétences, de l'expérience et de l'adéquation globale.
                Réponds UNIQUEMENT au format JSON valide sans texte supplémentaire, comme ceci :
                {"score": 85, "justification": "Le candidat maîtrise Java et Spring..."}
                """.formatted(cvText, offre.getTitre(), offre.getCompetences());
    }

    private String callOpenRouter(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", "Tu es un assistant qui répond toujours en JSON valide."),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.2
        );

        String response = openRouterWebClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Erreur HTTP {} : {}", clientResponse.statusCode(), errorBody);
                                    return Mono.error(new RuntimeException(
                                            "Erreur API OpenRouter : " + clientResponse.statusCode() + " - " + errorBody));
                                }))
                .bodyToMono(String.class)
                .block(TIMEOUT);

        if (response == null) {
            throw new RuntimeException("Aucune réponse reçue de l'API OpenRouter (timeout)");
        }

        // Log de la réponse brute (en DEBUG pour éviter de polluer)
        log.debug("Réponse brute d'OpenRouter : {}", response);

        // Vérifier que la réponse commence bien par '{' (JSON)
        String trimmed = response.trim();
        if (!trimmed.startsWith("{")) {
            // Ce n'est pas du JSON, probablement une erreur HTML
            log.error("La réponse d'OpenRouter n'est pas du JSON : {}", response);
            throw new RuntimeException("La réponse d'OpenRouter n'est pas du JSON. Contenu : " + response);
        }

        try {
            JsonNode root = objectMapper.readTree(response);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            log.error("Erreur lors du parsing de la réponse JSON : {}", response, e);
            throw new RuntimeException("Impossible de parser la réponse d'OpenRouter", e);
        }
    }

    private ScoreResponseDto parseAiResponse(String aiJson) {
        try {
            // Supprimer les backticks éventuels
            String cleaned = aiJson.replace("```json", "").replace("```", "").trim();
            JsonNode node = objectMapper.readTree(cleaned);
            int score = node.path("score").asInt();
            String justification = node.path("justification").asText();
            return new ScoreResponseDto(score, justification, null, null);
        } catch (Exception e) {
            log.error("Erreur lors du parsing de la réponse IA : {}", aiJson, e);
            throw new RuntimeException("La réponse de l'IA n'est pas au format JSON attendu : " + aiJson, e);
        }
    }
}