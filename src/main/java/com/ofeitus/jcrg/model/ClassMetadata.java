package com.ofeitus.jcrg.model;

import lombok.Getter;

@Getter
public class ClassMetadata implements Comparable<ClassMetadata> {

    private final String name;
    private final String fullName;

    public ClassMetadata(String fullName) {
        String[] split = fullName.split("\\.");
        this.name = split[split.length - 1];
        this.fullName = fullName;
    }

    @Override
    public int compareTo(ClassMetadata o) {
        return fullName.compareTo(o.fullName);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ClassMetadata that = (ClassMetadata) o;
        return fullName.equals(that.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
