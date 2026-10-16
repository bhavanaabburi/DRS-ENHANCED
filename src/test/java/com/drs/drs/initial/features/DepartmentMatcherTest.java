package com.drs.drs.initial.features;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class DepartmentMatcherTest {
    @Test void fireMatching() { assertEquals("Fire", DepartmentMatcher.preferredType("Fire suppression")); }
    @Test void medicalMatching() { assertEquals("Medical", DepartmentMatcher.preferredType("injured people need medical help")); }
    @Test void transportMatching() { assertEquals("Transport", DepartmentMatcher.preferredType("Evacuate people")); }
    @Test void policeMatching() { assertEquals("Police", DepartmentMatcher.preferredType("Police security")); }
    @Test void fallback() { assertEquals("Emergency", DepartmentMatcher.preferredType(null)); }
}
