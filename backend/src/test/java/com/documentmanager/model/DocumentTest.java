package com.documentmanager.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DocumentTest {

    private static class TestDocument extends Document {
        void runOnCreate() {
            super.onCreate();
        }
    }

    @Test
    void prePersistAssignsIdLocationAndCreatedAt() {
        TestDocument document = new TestDocument();

        document.runOnCreate();

        assertNotNull(document.getId());
        assertEquals("documents/" + document.getId(), document.getLocation());
        assertNotNull(document.getCreatedAt());
    }

    @Test
    void prePersistKeepsExistingId() {
        TestDocument document = new TestDocument();
        UUID existingId = UUID.randomUUID();
        document.setId(existingId);

        document.runOnCreate();

        assertEquals(existingId, document.getId());
        assertEquals("documents/" + existingId, document.getLocation());
        assertNotNull(document.getCreatedAt());
    }
}