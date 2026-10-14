package com.drs.drs.initial.features;

/**
 * PriorityCalculator - Creative Feature #1
 * Intelligently calculates priority scores and recommendations
 * based on multiple disaster factors.
 * 
 * @author DRS Development Team
 * @version 1.0
 */
public class PriorityCalculator {

    // ========== Priority Scoring Constants ==========
    private static final int IMPACT_WEIGHT = 30;
    private static final int PEOPLE_WEIGHT = 25;
    private static final int ACCESSIBILITY_WEIGHT = 20;
    private static final int TIME_WEIGHT = 15;
    private static final int INJURY_WEIGHT = 10;

    // ========== Priority Score Ranges ==========
    private static final int CRITICAL_MIN = 80;
    private static final int HIGH_MIN = 60;
    private static final int MEDIUM_MIN = 40;
    private static final int LOW_MIN = 0;

    /**
     * Calculate priority score based on multiple factors
     * Score range: 0-100
     * 
     * @param impactLevel Impact level (Minor=10, Moderate=30, Severe=60, Critical=100)
     * @param peopleAffected Number of people affected
     * @param accessibility Accessibility status (Not Accessible=100, Partial=50, Accessible=10)
     * @param minutesSinceReport Minutes since incident reported
     * @param injuredCount Number of injured people
     * @return priority score (0-100)
     */
    public static int calculatePriorityScore(String impactLevel, int peopleAffected, 
                                        String accessibility, int minutesSinceReport, int injuredCount){    // Calculate impact score (0-30)
        int impactScore = getImpactScore(impactLevel) * IMPACT_WEIGHT / 100;
        
        // Calculate people score (0-25)
        int peopleScore = calculatePeopleScore(peopleAffected) * PEOPLE_WEIGHT / 100;
        
        // Calculate accessibility score (0-20)
        int accessibilityScore = getAccessibilityScore(accessibility) * ACCESSIBILITY_WEIGHT / 100;
        
        // Calculate time score (0-15) - older incidents get higher priority
        int timeScore = calculateTimeScore(minutesSinceReport) * TIME_WEIGHT / 100;
        
        // Calculate injury score (0-10)
        int injuryScore = calculateInjuryScore(injuredCount) * INJURY_WEIGHT / 100;
        
        // Total score
        int totalScore = impactScore + peopleScore + accessibilityScore + timeScore + injuryScore;
        
        return Math.min(100, Math.max(0, totalScore));
    }

    /**
     * Get impact level score (0-100)
     */
    private static int getImpactScore(String impactLevel) {
        switch (impactLevel) {
            case "Critical": return 100;
            case "Severe": return 75;
            case "Moderate": return 50;
            case "Minor": return 25;
            default: return 0;
        }
    }

    /**
     * Calculate people affected score (0-100)
     */
    private static int calculatePeopleScore(int peopleAffected) {
        if (peopleAffected >= 5000) return 100;
        if (peopleAffected >= 2000) return 80;
        if (peopleAffected >= 500) return 60;
        if (peopleAffected >= 100) return 40;
        if (peopleAffected > 0) return 20;
        return 0;
    }

    /**
     * Get accessibility score (0-100)
     */
    private static int getAccessibilityScore(String accessibility) {
        switch (accessibility) {
            case "Not Accessible": return 100;
            case "Partially Accessible": return 50;
            case "Accessible": return 10;
            default: return 0;
        }
    }

    /**
     * Calculate time elapsed score (0-100)
     * Older incidents get higher priority
     */
    private static int calculateTimeScore(long minutesSinceReport) {
        if (minutesSinceReport > 480) return 100;  // > 8 hours
        if (minutesSinceReport > 240) return 75;   // > 4 hours
        if (minutesSinceReport > 60) return 50;    // > 1 hour
        if (minutesSinceReport > 15) return 25;    // > 15 minutes
        return 10;
    }

    /**
     * Calculate injury count score (0-100)
     */
    private static int calculateInjuryScore(int injuredCount) {
        if (injuredCount >= 100) return 100;
        if (injuredCount >= 50) return 80;
        if (injuredCount >= 20) return 60;
        if (injuredCount >= 5) return 40;
        if (injuredCount > 0) return 20;
        return 0;
    }

    /**
     * Get priority level recommendation based on score
     * @param priorityScore the calculated priority score (0-100)
     * @return priority level string
     */
    public static String getPriorityLevel(int priorityScore) {
        if (priorityScore >= CRITICAL_MIN) return "CRITICAL";
        if (priorityScore >= HIGH_MIN) return "HIGH";
        if (priorityScore >= MEDIUM_MIN) return "MEDIUM";
        return "LOW";
    }

    /**
     * Get recommended response time in hours
     * @param priorityScore the calculated priority score
     * @return recommended response time in hours
     */
    public static int getRecommendedResponseTime(int priorityScore) {
        if (priorityScore >= CRITICAL_MIN) return 1;      // 1 hour
        if (priorityScore >= HIGH_MIN) return 4;          // 4 hours
        if (priorityScore >= MEDIUM_MIN) return 12;       // 12 hours
        return 24;                                        // 24 hours
    }

    /**
     * Get recommended resource allocation percentage
     * @param priorityScore the calculated priority score
     * @return percentage of resources to allocate (0-100)
     */
    public static int getRecommendedResourceAllocation(int priorityScore) {
        if (priorityScore >= CRITICAL_MIN) return 100;    // 100%
        if (priorityScore >= HIGH_MIN) return 75;         // 75%
        if (priorityScore >= MEDIUM_MIN) return 50;       // 50%
        return 25;                                        // 25%
    }

    /**
     * Get detailed recommendation text
     */
    public static String getDetailedRecommendation(int priorityScore, String priorityLevel) {
        StringBuilder recommendation = new StringBuilder();
        recommendation.append("Priority Level: ").append(priorityLevel).append("\n");
        recommendation.append("Priority Score: ").append(priorityScore).append("/100\n\n");
        recommendation.append("RECOMMENDATIONS:\n");
        
        if (priorityScore >= CRITICAL_MIN) {
            recommendation.append("- IMMEDIATE ACTION REQUIRED\n");
            recommendation.append("- Deploy all available resources\n");
            recommendation.append("- Notify all departments immediately\n");
            recommendation.append("- Establish incident command system\n");
            recommendation.append("- Target response time: 1 hour\n");
        } else if (priorityScore >= HIGH_MIN) {
            recommendation.append("- URGENT RESPONSE NEEDED\n");
            recommendation.append("- Deploy primary departments\n");
            recommendation.append("- Alert backup resources\n");
            recommendation.append("- Target response time: 4 hours\n");
        } else if (priorityScore >= MEDIUM_MIN) {
            recommendation.append("- STANDARD RESPONSE\n");
            recommendation.append("- Deploy assigned departments\n");
            recommendation.append("- Monitor situation closely\n");
            recommendation.append("- Target response time: 12 hours\n");
        } else {
            recommendation.append("- LOW PRIORITY\n");
            recommendation.append("- Routine handling\n");
            recommendation.append("- Schedule response appropriately\n");
            recommendation.append("- Target response time: 24 hours\n");
        }
        
        return recommendation.toString();
    }
}