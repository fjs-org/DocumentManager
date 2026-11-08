package com.documentmanager.util;

import com.documentmanager.exception.DocumentContentExtractionException;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class ImageContentExtractor {
    private final Tesseract tesseract;

    @Autowired
    public ImageContentExtractor() {
        this.tesseract = new Tesseract();
        this.tesseract.setLanguage("eng");

        String tessDataPath = System.getenv().getOrDefault(
                "TESSDATA_PREFIX",
                "backend/src/main/resources/tessdata"
        );
        tesseract.setDatapath(tessDataPath);
    }

    ImageContentExtractor(Tesseract tesseract) {
        this.tesseract = tesseract;
    }

    public String extractText(byte[] data) {
        BufferedImage image;
        try {
            image = ImageIO.read(new ByteArrayInputStream(data));
        } catch (IOException ex) {
            throw new DocumentContentExtractionException("Failed to read image data", ex);
        }

        if (image == null) {
            throw new DocumentContentExtractionException("Unsupported image data");
        }

        try {
            return tesseract.doOCR(image);
        } catch (TesseractException | RuntimeException ex) {
            throw new DocumentContentExtractionException("Failed to extract content from image", ex);
        }
    }

}
