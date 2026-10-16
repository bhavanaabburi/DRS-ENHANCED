package com.drs.drs.initial.features;
/** Moves an incident up one severity level; Critical is the maximum. */
public final class SeverityEscalation {
    private SeverityEscalation() {}
    public static String nextPriority(String current) {
        if (current == null) throw new IllegalArgumentException("Priority required");
        return switch (current) {
            case "Low" -> "Medium";
            case "Medium" -> "High";
            case "High" -> "Critical";
            case "Critical" -> "Critical";
            default -> throw new IllegalArgumentException("Unknown priority: " + current);
        };
    }
}
