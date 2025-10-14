package com.example.employees.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Employee entity.
 *
 * Fields include managerId (nullable) to reference direct manager by UUID.
 * firstName and lastName are explicit to facilitate sorting by surname.
 */
public class Employee {
    private final UUID id;
    private final String firstName;
    private final String lastName;
    private final Role role;
    private final double salary;
    private final Department department;
    private final boolean isManager;
    private final UUID managerId;
    private final LocalDate hireDate;

    public Employee(UUID id, String firstName, String lastName, Role role, double salary,
                    Department department, boolean isManager, UUID managerId, LocalDate hireDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.salary = salary;
        this.department = department;
        this.isManager = isManager;
        this.managerId = managerId;
        this.hireDate = hireDate;
    }

    public UUID getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Role getRole() { return role; }
    public double getSalary() { return salary; }
    public Department getDepartment() { return department; }
    public boolean isManager() { return isManager; }
    public UUID getManagerId() { return managerId; }
    public LocalDate getHireDate() { return hireDate; }

    public String getFullName() { return firstName + " " + lastName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Double.compare(salary, employee.salary) == 0 &&
                isManager == employee.isManager &&
                Objects.equals(id, employee.id) &&
                Objects.equals(firstName, employee.firstName) &&
                Objects.equals(lastName, employee.lastName) &&
                role == employee.role &&
                Objects.equals(department, employee.department) &&
                Objects.equals(managerId, employee.managerId) &&
                Objects.equals(hireDate, employee.hireDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, role, salary, department, isManager, managerId, hireDate);
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s) - %s - $%.2f - Manager=%b - managerId=%s",
                firstName, lastName, role, department.getName(), salary, isManager,
                managerId == null ? "-" : managerId.toString());
    }
}