package org.exercice.testats.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.exercice.testats.exception.FileStorageException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class PdfTextExtractorService {

    public String extractTextFromPdf(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        try (PDDocument document = Loader.loadPDF(path.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            throw new FileStorageException("Erreur lors de lextraction du text PDF : " +e.getMessage());
        }
    }
}
