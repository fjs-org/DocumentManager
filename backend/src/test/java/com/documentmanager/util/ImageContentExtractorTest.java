package com.documentmanager.util;

import com.documentmanager.exception.DocumentContentExtractionException;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ImageContentExtractorTest {

    private final Tesseract tesseract = mock(Tesseract.class);

    private final ImageContentExtractor extractor = new ImageContentExtractor(tesseract);

    @Test
    void extractsTextFromJpgImage() throws IOException, TesseractException {
        byte[] data = jpgBytes();
        when(tesseract.doOCR(any(BufferedImage.class))).thenReturn("Hello OCR");

        String content = extractor.extractText(data);

        assertEquals("Hello OCR", content);
        verify(tesseract).doOCR(any(BufferedImage.class));
    }

    @Test
    void throwsWhenImageDataIsInvalid() {
        assertThrows(DocumentContentExtractionException.class,
                () -> extractor.extractText("not an image".getBytes()));
    }

    private byte[] jpgBytes() throws IOException {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", output);
        return output.toByteArray();
    }
}
