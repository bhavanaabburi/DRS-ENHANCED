
package com.drs.drs.initial.database;

import com.drs.drs.initial.model.DepartmentRepository;
import com.drs.drs.initial.model.IncidentRepository;
import com.drs.drs.initial.model.ResponseTaskRepository;

public class DatabaseIntegrationTest {

    public static void main(String[] args) {

        DatabaseInitializer.initializeDatabase();

        IncidentRepository.loadIncidents();
        DepartmentRepository.loadDepartments();
        ResponseTaskRepository.loadTasks();

        int incidents = IncidentRepository.getIncidents().size();
        int departments = DepartmentRepository.getDepartments().size();
        int tasks = ResponseTaskRepository.getTasks().size();

        System.out.println("\n=== DRS DATABASE TEST ===");
        System.out.println("Incidents: " + incidents);
        System.out.println("Departments: " + departments);
        System.out.println("Response Tasks: " + tasks);

        if (incidents == 8 && departments == 10 && tasks == 8) {
            System.out.println("SUCCESS: All sample records loaded!");
        } else {
            System.out.println(
                "CHECK: Record counts differ from initial sample data."
            );
        }
    }
}
