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
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(classes.size()).append(") ");
        if (classes.size() == 1) {
            sb.append(classes.getFirst().getName());
        } else if (classes.size() == 2) {
            sb.append(classes.getFirst().getName()).append(" -> ").append(classes.get(1).getName());
        } else {
            sb.append(classes.getFirst().getName()).append(" -> ... -> ").append(classes.getLast().getName());
        }
        return sb.toString();
    }
}
