package com.documentmanager.repository;

import com.documentmanager.model.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DocumentPersistenceTest {

    @Autowired
    private DocumentRepository documentRepository;

    @Test
    void saveNewDocumentAssignsIdLocationAndCreatedAt() {
        Document document = new Document();
        document.setFileName("hello.txt");
        document.setData("file content".getBytes());

        Document saved = documentRepository.saveAndFlush(document);

        assertNotNull(saved.getId());
        assertEquals("documents/" + saved.getId(), saved.getLocation());
        assertNotNull(saved.getCreatedAt());
    }
}