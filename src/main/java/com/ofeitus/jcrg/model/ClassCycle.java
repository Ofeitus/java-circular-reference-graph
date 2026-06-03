package com.ofeitus.jcrg.model;

import java.util.List;

public record ClassCycle (List<ClassMetadata> classes) {

    public boolean contains(ClassMetadata classMetadata) {
        return classes.contains(classMetadata);
    }

    @Override
    public String toString() {
        if (classes.isEmpty()) {
            return "";
        } else if (classes.size() == 1) {
            return classes.getFirst().name();
        } else if (classes.size() == 2) {
            return classes.getFirst().name() + " -> " + classes.get(1).name();
        } else {
            return classes.getFirst().name() + " -> ... -> " + classes.getLast().name();
        }
    }
}
