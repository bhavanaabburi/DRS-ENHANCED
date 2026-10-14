package com.drs.drs.initial.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Shared repository for departments.
 *
 * Stores department information so that availability
 * changes remain available while navigating between
 * screens during the current application session.
 */
public class DepartmentRepository {

    private static final ObservableList<Department> departments =
            FXCollections.observableArrayList();

    static {

        departments.add(
                new Department(
                        "DEPT-001",
                        "Fire and Emergency",
                        "Fire and Emergency",
                        "Phone: 000, Email: fire@drs.gov",
                        "Available"
                )
        );

        departments.add(
                new Department(
                        "DEPT-002",
                        "City Hospital",
                        "Hospital",
                        "Phone: 1300-HOSPITAL, Email: hospital@drs.gov",
                        "Available"
                )
        );

        departments.add(
                new Department(
                        "DEPT-003",
                        "Police Department",
                        "Police",
                        "Phone: 131, Email: police@drs.gov",
                        "Busy"
                )
        );

        departments.add(
                new Department(
                        "DEPT-004",
                        "Transportation Authority",
                        "Transportation",
                        "Phone: 1800-TRANSPORT, Email: transport@drs.gov",
                        "Available"
                )
        );

        departments.add(
                new Department(
                        "DEPT-005",
                        "Water Supply Corporation",
                        "Water Supply",
                        "Phone: 1300-WATER, Email: water@drs.gov",
                        "Unavailable"
                )
        );

        departments.add(
                new Department(
                        "DEPT-006",
                        "Waste Management",
                        "Waste Management",
                        "Phone: 1800-WASTE, Email: waste@drs.gov",
                        "Available"
                )
        );

        departments.add(
                new Department(
                        "DEPT-007",
                        "School District",
                        "Schools",
                        "Phone: 1800-SCHOOL, Email: schools@drs.gov",
                        "Busy"
                )
        );

        departments.add(
                new Department(
                        "DEPT-008",
                        "Law Enforcement",
                        "Law Enforcement",
                        "Phone: 132, Email: law@drs.gov",
                        "Available"
                )
        );

        departments.add(
                new Department(
                        "DEPT-009",
                        "Utilities Department",
                        "Utilities",
                        "Phone: 1300-UTILITIES, Email: utilities@drs.gov",
                        "Available"
                )
        );

        departments.add(
                new Department(
                        "DEPT-010",
                        "Search and Rescue",
                        "Search and Rescue",
                        "Phone: 000-SAR, Email: sar@drs.gov",
                        "Busy"
                )
        );
    }


    /**
     * Prevent creation of repository objects.
     */
    private DepartmentRepository() {
    }


    /**
     * Returns all departments.
     *
     * @return shared observable department list
     */
    public static ObservableList<Department> getDepartments() {

        return departments;
    }


    /**
     * Finds a department by ID.
     *
     * @param departmentId department identifier
     * @return matching Department or null
     */
    public static Department findById(
            String departmentId) {

        if (departmentId == null) {
            return null;
        }


        for (Department department : departments) {

            if (department
                    .getDepartmentId()
                    .equalsIgnoreCase(
                            departmentId.trim())) {

                return department;
            }
        }


        return null;
    }


    /**
     * Finds the first department matching
     * the supplied department type.
     *
     * @param type department type
     * @return matching Department or null
     */
    public static Department findByType(
            String type) {

        if (type == null) {
            return null;
        }


        for (Department department : departments) {

            if (department
                    .getType()
                    .equalsIgnoreCase(
                            type.trim())) {

                return department;
            }
        }


        return null;
    }
}