package com.ofeitus.jcrg.model;

import java.util.List;

public record ClassCycle(List<ClassMetadata> classes) {

    public boolean containsVertex(ClassMetadata classMetadata) {
        return classes.contains(classMetadata);
    }

    public boolean containsEdge(ClassMetadata from, ClassMetadata to) {
        for (int i = 0; i < classes.size() - 1; i++) {
            if (classes.get(i).equals(from) && classes.get(i + 1).equals(to)) {
                return true;
            }
        }
        return classes.getLast().equals(from) && classes.getFirst().equals(to);
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
