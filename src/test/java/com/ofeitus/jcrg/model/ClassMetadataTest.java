package com.ofeitus.jcrg.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClassMetadataTest {

    @Test
    void testEquality() {
        assertEquals(new ClassMetadata("Test", "com.test.Test"),
                new ClassMetadata("Test", "com.test.Test"));
    }

    @Test
    void testEqualityWithDifferentPackets() {
        assertNotEquals(new ClassMetadata("Test", "com.test.Test"),
                new ClassMetadata("Test", "com.demo.Test"));
    }
}