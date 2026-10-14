package com.drs.drs.initial.features;

import com.drs.drs.initial.model.ResponseTask;
import com.drs.drs.initial.model.ResponseTaskRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * AutoTaskGenerator - Creative Feature #2
 *
 * Automatically generates appropriate response tasks
 * based on disaster type and priority.
 *
 * Generated tasks use the main ResponseTask model
 * and are stored in ResponseTaskRepository.
 *
 * @author DRS Development Team
 * @version 2.0
 */
public class AutoTaskGenerator {

    // ========== TASK TEMPLATE ==========

    /**
     * Internal template used to define recommended
     * response activities for each disaster type.
     */
    private static class TaskTemplate {

        private final String description;

        private final String assignedDepartment;

        private final int hoursToComplete;


        TaskTemplate(
                String description,
                String assignedDepartment,
                int hoursToComplete) {

            this.description =
                    description;

            this.assignedDepartment =
                    assignedDepartment;

            this.hoursToComplete =
                    hoursToComplete;
        }
    }


    // ========== GENERATE TASKS ==========

    /**
     * Generates response tasks for an incident.
     *
     * The generated tasks are real ResponseTask objects
     * and are added to ResponseTaskRepository.
     *
     * @param incidentId incident receiving the tasks
     * @param disasterType type of disaster
     * @param priority incident priority
     * @return generated response tasks
     */
    public static List<ResponseTask> generateTasks(
            String incidentId,
            String disasterType,
            String priority) {

        List<ResponseTask> generatedTasks =
                new ArrayList<>();


        List<TaskTemplate> templates =
                getTaskTemplates(
                        disasterType
                );


        for (TaskTemplate template
                : templates) {

            String taskId =
                    ResponseTaskRepository
                            .generateTaskId();


            int adjustedHours =
                    adjustTimeframeByPriority(
                            template.hoursToComplete,
                            priority
                    );


            LocalDateTime createdAt =
                    LocalDateTime.now();


            LocalDateTime targetTime =
                    createdAt.plusHours(
                            adjustedHours
                    );


            String notes =
                    generateNotes(
                            incidentId,
                            disasterType,
                            priority
                    );


            ResponseTask task =
                    new ResponseTask(
                            taskId,
                            incidentId,
                            template.description,
                            "Pending",
                            template.assignedDepartment,
                            createdAt,
                            targetTime,
                            notes
                    );


            ResponseTaskRepository
                    .addTask(task);


            generatedTasks.add(task);
        }


        return generatedTasks;
    }


    // ========== PREVIEW GENERATION ==========

    /**
     * Generates tasks without an incident association.
     *
     * This overload is retained temporarily for
     * compatibility and preview purposes.
     *
     * These tasks are NOT stored in the repository.
     */
    public static List<ResponseTask> generateTasks(
            String disasterType,
            String priority) {

        List<ResponseTask> generatedTasks =
                new ArrayList<>();


        List<TaskTemplate> templates =
                getTaskTemplates(
                        disasterType
                );


        int number = 1;


        for (TaskTemplate template
                : templates) {

            int adjustedHours =
                    adjustTimeframeByPriority(
                            template.hoursToComplete,
                            priority
                    );


            LocalDateTime createdAt =
                    LocalDateTime.now();


            ResponseTask task =
                    new ResponseTask(
                            "PREVIEW-" + number,
                            "",
                            template.description,
                            "Pending",
                            template.assignedDepartment,
                            createdAt,
                            createdAt.plusHours(
                                    adjustedHours
                            ),
                            generateNotes(
                                    "",
                                    disasterType,
                                    priority
                            )
                    );


            generatedTasks.add(task);

            number++;
        }


        return generatedTasks;
    }


    // ========== TASK TEMPLATES ==========

    private static List<TaskTemplate>
            getTaskTemplates(
                    String disasterType) {

        List<TaskTemplate> templates =
                new ArrayList<>();


        if (disasterType == null) {

            return templates;
        }


        switch (disasterType
                .trim()
                .toLowerCase()) {


            case "hurricane":

                templates.add(
                        new TaskTemplate(
                                "Evacuate residents from coastal areas",
                                "Fire and Emergency",
                                3
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Deploy emergency shelters",
                                "School District",
                                4
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Set up medical triage centers",
                                "City Hospital",
                                2
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Clear debris and restore roads",
                                "Transportation Authority",
                                6
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Distribute emergency supplies",
                                "Waste Management",
                                5
                        )
                );

                break;


            case "fire":

                templates.add(
                        new TaskTemplate(
                                "Contain fire perimeter",
                                "Fire and Emergency",
                                2
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Evacuate nearby buildings",
                                "Fire and Emergency",
                                1
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Set up medical aid stations",
                                "City Hospital",
                                1
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Establish traffic control",
                                "Police Department",
                                2
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Conduct search and rescue",
                                "Search and Rescue",
                                4
                        )
                );

                break;


            case "earthquake":

                templates.add(
                        new TaskTemplate(
                                "Search for trapped survivors",
                                "Search and Rescue",
                                4
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Provide emergency medical care",
                                "City Hospital",
                                3
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Check structural integrity of buildings",
                                "Law Enforcement",
                                3
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Restore water and utilities",
                                "Utilities Department",
                                5
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Set up emergency shelters",
                                "School District",
                                4
                        )
                );

                break;


            case "flood":

                templates.add(
                        new TaskTemplate(
                                "Evacuate flood-prone areas",
                                "Fire and Emergency",
                                2
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Deploy rescue boats and teams",
                                "Search and Rescue",
                                3
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Set up sanitation facilities",
                                "Waste Management",
                                4
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Provide medical assistance",
                                "City Hospital",
                                3
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Restore transportation routes",
                                "Transportation Authority",
                                6
                        )
                );

                break;


            case "tornado":

                templates.add(
                        new TaskTemplate(
                                "Issue warning sirens",
                                "Police Department",
                                1
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Conduct damage assessment",
                                "Law Enforcement",
                                2
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Rescue survivors from debris",
                                "Search and Rescue",
                                3
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Treat injured persons",
                                "City Hospital",
                                4
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Clear hazardous debris",
                                "Fire and Emergency",
                                5
                        )
                );

                break;


            case "tsunami":

                templates.add(
                        new TaskTemplate(
                                "Evacuate coastal zones immediately",
                                "Fire and Emergency",
                                1
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Set up emergency shelters inland",
                                "School District",
                                3
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Search for missing persons",
                                "Search and Rescue",
                                5
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Provide medical treatment",
                                "City Hospital",
                                4
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Restore infrastructure",
                                "Utilities Department",
                                6
                        )
                );

                break;


            case "landslide":

                templates.add(
                        new TaskTemplate(
                                "Evacuate at-risk areas",
                                "Fire and Emergency",
                                2
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Conduct geological assessment",
                                "Law Enforcement",
                                3
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Search for victims",
                                "Search and Rescue",
                                4
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Clear blocked roads",
                                "Transportation Authority",
                                5
                        )
                );

                break;


            case "wildfire":

                templates.add(
                        new TaskTemplate(
                                "Establish fire containment lines",
                                "Fire and Emergency",
                                3
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Evacuate surrounding areas",
                                "Police Department",
                                2
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Deploy air support if available",
                                "Fire and Emergency",
                                4
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Provide medical support",
                                "City Hospital",
                                3
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Monitor air quality",
                                "Utilities Department",
                                2
                        )
                );

                break;


            default:

                templates.add(
                        new TaskTemplate(
                                "Assess incident severity",
                                "Fire and Emergency",
                                1
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Coordinate response efforts",
                                "Police Department",
                                2
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Provide medical support",
                                "City Hospital",
                                2
                        )
                );

                templates.add(
                        new TaskTemplate(
                                "Set up emergency shelters",
                                "School District",
                                3
                        )
                );

                break;
        }


        return templates;
    }


    // ========== PRIORITY TIME ADJUSTMENT ==========

    /**
     * Higher-priority incidents receive shorter
     * response deadlines.
     */
    private static int adjustTimeframeByPriority(
            int baseHours,
            String priority) {

        if (priority == null) {

            return baseHours;
        }


        switch (priority
                .trim()
                .toUpperCase()) {


            case "CRITICAL":

                return Math.max(
                        1,
                        baseHours / 3
                );


            case "HIGH":

                return Math.max(
                        1,
                        baseHours / 2
                );


            case "MEDIUM":

                return baseHours;


            case "LOW":

                return baseHours * 2;


            default:

                return baseHours;
        }
    }


    // ========== NOTES ==========

    private static String generateNotes(
            String incidentId,
            String disasterType,
            String priority) {

        StringBuilder notes =
                new StringBuilder();


        notes.append(
                "Auto-generated for "
        );


        if (incidentId != null
                && !incidentId
                        .trim()
                        .isEmpty()) {

            notes.append(
                    "incident "
            );

            notes.append(
                    incidentId
            );

            notes.append(
                    " - "
            );
        }


        notes.append(
                disasterType
        );


        notes.append(
                " ("
        );


        notes.append(
                priority
        );


        notes.append(
                " priority). "
        );


        notes.append(
                "Coordinate with other departments as needed."
        );


        return notes.toString();
    }


    // ========== TASK COUNT ==========

    public static int getTaskCount(
            String disasterType) {

        return getTaskTemplates(
                disasterType
        ).size();
    }


    // ========== SUMMARY ==========

    public static String getSummary(
            String incidentId,
            String disasterType,
            String priority,
            List<ResponseTask> tasks) {

        StringBuilder summary =
                new StringBuilder();


        summary.append(
                "=== AUTO TASK GENERATOR SUMMARY ===\n\n"
        );


        if (incidentId != null
                && !incidentId
                        .isBlank()) {

            summary.append(
                    "Incident ID: "
            );

            summary.append(
                    incidentId
            );

            summary.append(
                    "\n"
            );
        }


        summary.append(
                "Disaster Type: "
        );

        summary.append(
                disasterType
        );

        summary.append(
                "\n"
        );


        summary.append(
                "Priority Level: "
        );

        summary.append(
                priority
        );

        summary.append(
                "\n"
        );


        summary.append(
                "Tasks Generated: "
        );

        summary.append(
                tasks.size()
        );

        summary.append(
                "\n\nGENERATED TASKS:\n"
        );


        for (int i = 0;
                i < tasks.size();
                i++) {

            ResponseTask task =
                    tasks.get(i);


            summary.append(
                    i + 1
            );

            summary.append(
                    ". "
            );

            summary.append(
                    task.getDescription()
            );

            summary.append(
                    "\n   Task ID: "
            );

            summary.append(
                    task.getTaskId()
            );

            summary.append(
                    "\n   Department: "
            );

            summary.append(
                    task.getAssignedDepartment()
            );

            summary.append(
                    "\n   Target: "
            );

            summary.append(
                    task.getTargetTime()
            );

            summary.append(
                    "\n\n"
            );
        }


        summary.append(
                "====================================="
        );


        return summary.toString();
    }


    /**
     * Compatibility summary for task previews.
     */
    public static String getSummary(
            String disasterType,
            String priority,
            List<ResponseTask> tasks) {

        return getSummary(
                "",
                disasterType,
                priority,
                tasks
        );
    }
}