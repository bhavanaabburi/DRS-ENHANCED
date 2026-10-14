package com.drs.drs.initial.features;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for Creative Feature #1 - PriorityCalculator.
 */
public class PriorityCalculatorTest {

    /**
     * Tests the same type of High-priority calculation
     * demonstrated through the Priority Calculator GUI.
     */
    @Test
    public void testCalculatePriorityScoreHigh() {

        int score = PriorityCalculator.calculatePriorityScore(
                "Critical",
                600,
                "Not Accessible",
                7,
                500
        );

        assertEquals(76, score);
    }

    /**
     * A score of 80 or above should be Critical.
     */
    @Test
    public void testCriticalPriorityThreshold() {

        assertEquals(
                "CRITICAL",
                PriorityCalculator.getPriorityLevel(80)
        );
    }

    /**
     * A score from 60 should be High.
     */
    @Test
    public void testHighPriorityThreshold() {

        assertEquals(
                "HIGH",
                PriorityCalculator.getPriorityLevel(60)
        );
    }

    /**
     * A score from 40 should be Medium.
     */
    @Test
    public void testMediumPriorityThreshold() {

        assertEquals(
                "MEDIUM",
                PriorityCalculator.getPriorityLevel(40)
        );
    }

    /**
     * A score below 40 should be Low.
     */
    @Test
    public void testLowPriorityThreshold() {

        assertEquals(
                "LOW",
                PriorityCalculator.getPriorityLevel(39)
        );
    }

    /**
     * Critical incidents should receive a
     * recommended response time of 1 hour.
     */
    @Test
    public void testCriticalResponseTime() {

        assertEquals(
                1,
                PriorityCalculator.getRecommendedResponseTime(80)
        );
    }

    /**
     * High-priority incidents should receive
     * a recommended response time of 4 hours.
     */
    @Test
    public void testHighResponseTime() {

        assertEquals(
                4,
                PriorityCalculator.getRecommendedResponseTime(76)
        );
    }

    /**
     * High-priority incidents should receive
     * 75 percent resource allocation.
     */
    @Test
    public void testHighResourceAllocation() {

        assertEquals(
                75,
                PriorityCalculator.getRecommendedResourceAllocation(76)
        );
    }

    /**
     * Detailed recommendation should contain
     * the calculated priority information.
     */
    @Test
    public void testDetailedRecommendation() {

        String recommendation =
                PriorityCalculator.getDetailedRecommendation(
                        76,
                        "HIGH"
                );

        assertTrue(recommendation.contains("Priority Level: HIGH"));
        assertTrue(recommendation.contains("Priority Score: 76/100"));
        assertTrue(recommendation.contains("URGENT RESPONSE NEEDED"));
    }
}