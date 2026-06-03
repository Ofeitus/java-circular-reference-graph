package com.ofeitus.jcrg.model;

public record ClassMetadata(String name, String fullName) implements Comparable<ClassMetadata> {

    @Override
    public int compareTo(ClassMetadata o) {
        return fullName.compareTo(o.fullName);
    }
}
