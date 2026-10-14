package com.drs.drs.initial.model;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for ResponseTask lifecycle and incident association.
 */
public class ResponseTaskTest {

    private ResponseTask createPendingTask() {

        return new ResponseTask(
                "TASK-TEST-001",
                "INC-009",
                "Contain fire perimeter",
                "Pending",
                "Fire and Emergency",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                "JUnit test task"
        );
    }

    @Test
    public void testStartPendingTask() {

        ResponseTask task = createPendingTask();

        assertTrue(task.startTask());
        assertEquals("In Progress", task.getStatus());
    }

    @Test
    public void testHoldInProgressTask() {

        ResponseTask task = createPendingTask();

        task.startTask();

        assertTrue(task.holdTask());
        assertEquals("On Hold", task.getStatus());
    }

    @Test
    public void testResumeTaskOnHold() {

        ResponseTask task = createPendingTask();

        task.startTask();
        task.holdTask();

        assertTrue(task.resumeTask());
        assertEquals("In Progress", task.getStatus());
    }

    @Test
    public void testCompleteInProgressTask() {

        ResponseTask task = createPendingTask();

        task.startTask();

        assertTrue(task.completeTask());
        assertEquals("Completed", task.getStatus());
        assertNotNull(task.getCompletedAt());
    }

    @Test
    public void testCancelPendingTask() {

        ResponseTask task = createPendingTask();

        assertTrue(task.cancelTask());
        assertEquals("Cancelled", task.getStatus());
    }

    @Test
    public void testCannotCompletePendingTask() {

        ResponseTask task = createPendingTask();

        assertFalse(task.completeTask());
        assertEquals("Pending", task.getStatus());
    }

    @Test
    public void testBelongsToCorrectIncident() {

        ResponseTask task = createPendingTask();

        assertTrue(task.belongsToIncident("INC-009"));
        assertFalse(task.belongsToIncident("INC-008"));
    }

    @Test
    public void testPendingProgressStatus() {

        ResponseTask task = createPendingTask();

        assertEquals("Pending (0%)", task.getProgressStatus());
    }

    @Test
    public void testInProgressStatus() {

        ResponseTask task = createPendingTask();

        task.startTask();

        assertEquals("In Progress (50%)", task.getProgressStatus());
    }

    @Test
    public void testCompletedProgressStatus() {

        ResponseTask task = createPendingTask();

        task.startTask();
        task.completeTask();

        assertEquals("Completed (100%)", task.getProgressStatus());
    }

    @Test
    public void testFutureTaskIsNotOverdue() {

        ResponseTask task = createPendingTask();

        assertFalse(task.isOverdue());
    }
}