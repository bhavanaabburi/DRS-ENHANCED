package com.drs.drs.initial.features;
/** Suggests the response specialty using incident type and task description. */
public final class DepartmentMatcher {
    private DepartmentMatcher() {}
    public static String preferredType(String details) {
        String s = details == null ? "" : details.toLowerCase(java.util.Locale.ROOT);
        if (s.contains("fire") || s.contains("smoke") || s.contains("burn")) return "Fire";
        if (s.contains("injur") || s.contains("medical") || s.contains("hospital") || s.contains("ambulance")) return "Medical";
        if (s.contains("evacuat") || s.contains("transport") || s.contains("road")) return "Transport";
        if (s.contains("police") || s.contains("security") || s.contains("crowd")) return "Police";
        return "Emergency";
    }
}
