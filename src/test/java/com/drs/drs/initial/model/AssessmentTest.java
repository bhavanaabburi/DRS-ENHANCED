package com.drs.drs.initial.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for the Assessment model.
 */
public class AssessmentTest {

    @Test
    public void testCalculateImpactLevelLow() {
        Assessment assessment = new Assessment();
        assessment.setPeopleAffected(100);

        assertEquals("Low", assessment.calculateImpactLevel());
    }

    @Test
    public void testCalculateImpactLevelMedium() {
        Assessment assessment = new Assessment();
        assessment.setPeopleAffected(500);

        assertEquals("Medium", assessment.calculateImpactLevel());
    }

    @Test
    public void testCalculateImpactLevelHigh() {
        Assessment assessment = new Assessment();
        assessment.setPeopleAffected(600);

        assertEquals("High", assessment.calculateImpactLevel());
    }

    @Test
    public void testCalculateImpactLevelCritical() {
        Assessment assessment = new Assessment();
        assessment.setPeopleAffected(2001);

        assertEquals("Critical", assessment.calculateImpactLevel());
    }

    @Test
    public void testPriorityEscalationWhenNotAccessible() {
        Assessment assessment = new Assessment();

        assessment.setImpactLevel("High");
        assessment.setAccessCondition("Not Accessible");

        assertEquals("Critical", assessment.calculatePriority());
    }

    @Test
    public void testCriticalPriorityRemainsCritical() {
        Assessment assessment = new Assessment();

        assessment.setImpactLevel("Critical");
        assessment.setAccessCondition("Not Accessible");

        assertEquals("Critical", assessment.calculatePriority());
    }

    @Test
    public void testEstimatedResponseTimeForCriticalNotAccessible() {
        Assessment assessment = new Assessment();

        assessment.setImpactLevel("Critical");
        assessment.setAccessCondition("Not Accessible");

        assertEquals(9, assessment.calculateEstimatedResponseTime());
    }
}