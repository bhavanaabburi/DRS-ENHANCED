package com.drs.drs.initial.features;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class SeverityEscalationTest {
    @Test void lowToMedium() { assertEquals("Medium", SeverityEscalation.nextPriority("Low")); }
    @Test void mediumToHigh() { assertEquals("High", SeverityEscalation.nextPriority("Medium")); }
    @Test void highToCritical() { assertEquals("Critical", SeverityEscalation.nextPriority("High")); }
    @Test void criticalIsMaximum() { assertEquals("Critical", SeverityEscalation.nextPriority("Critical")); }
    @Test void invalidPriorityRejected() { assertThrows(IllegalArgumentException.class, () -> SeverityEscalation.nextPriority("Unknown")); }
}
