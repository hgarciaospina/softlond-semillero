package com.example.employees.service;

import com.example.employees.model.Employee;
import com.example.employees.model.Department;
import com.example.employees.repository.EmployeeRepository;

import java.util.*;


/**
 * Service implementation with corrected logic for the new data structure.
 * Handles both functional and imperative approaches with proper manager detection.
 */
public class EmployeeService {

    private final EmployeeRepository repository = new EmployeeRepository();

    /**
     * Finds employee by ID - used for manager resolution
     */
    private Optional<Employee> findById(UUID id) {
        return repository.getAllEmployees().stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
    }

    /**
     * Resolves manager name for display purposes
     */
    private String managerNameFor(Employee e) {
        if (e.getManagerId() == null) return "-";
        return findById(e.getManagerId())
                .map(Employee::getFullName)
                .orElse("Unknown Manager");
    }

    /**
     * Finds department ID by name for testing purposes
     */
    public UUID findDepartmentIdByName(String name) {
        return repository.getAllEmployees().stream()
                .map(Employee::getDepartment)
                .filter(Objects::nonNull)
                .filter(d -> d.getName().equalsIgnoreCase(name))
                .map(Department::getId)
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds manager ID by full name for testing purposes
     */
    public UUID findManagerIdByName(String fullName) {
        return repository.getAllEmployees().stream()
                .filter(e -> e.isManager() && e.getFullName().equalsIgnoreCase(fullName))
                .map(Employee::getId)
                .findFirst()
                .orElse(null);
    }

    // ========== BUSINESS OPERATIONS ==========

    /**
     * 1. TOTAL SALARY CALCULATION
     * Purpose: Calculate the organization's total payroll
     */
    public double getTotalSalaryFunctional() {
        return repository.getAllEmployees().stream()
                .mapToDouble(Employee::getSalary)
                .sum();
    }

    public double getTotalSalaryImperative() {
        double total = 0;
        for (Employee e : repository.getAllEmployees()) {
            total += e.getSalary();
        }
        return total;
    }

    /**
     * 2. EMPLOYEES BY DEPARTMENT
     * Purpose: Filter employees belonging to a specific department
     */
    public List<Employee> getEmployeesByDepartmentFunctional(UUID departmentId) {
        return repository.getAllEmployees().stream()
                .filter(e -> e.getDepartment() != null &&
                        e.getDepartment().getId().equals(departmentId))
                .toList();
    }

    public List<Employee> getEmployeesByDepartmentImperative(UUID departmentId) {
        List<Employee> result = new ArrayList<>();
        for (Employee e : repository.getAllEmployees()) {
            if (e.getDepartment() != null &&
                    e.getDepartment().getId().equals(departmentId)) {
                result.add(e);
            }
        }
        return result;
    }

    /**
     * 3. NAMES ABOVE AVERAGE SALARY
     * Purpose: Identify employees earning above organizational average
     */
    public List<String> getNamesAboveAverageSalaryFunctional() {
        List<Employee> employees = repository.getAllEmployees();
        if (employees.isEmpty()) return Collections.emptyList();

        double average = employees.stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);

        return employees.stream()
                .filter(e -> e.getSalary() > average)
                .map(Employee::getFullName)
                .sorted()
                .toList();
    }

    public List<String> getNamesAboveAverageSalaryImperative() {
        List<Employee> employees = repository.getAllEmployees();
        if (employees.isEmpty()) return Collections.emptyList();

        // Calculate average
        double total = 0;
        for (Employee e : employees) {
            total += e.getSalary();
        }
        double average = total / employees.size();

        // Collect names above average
        List<String> names = new ArrayList<>();
        for (Employee e : employees) {
            if (e.getSalary() > average) {
                names.add(e.getFullName());
            }
        }
        Collections.sort(names);
        return names;
    }

    /**
     * 4. MANAGERS LIST
     * Purpose: Get all employees with managerial responsibilities
     * CORRECTION: Now uses explicit isManager field instead of inference
     */
    public List<Employee> getManagersFunctional() {
        return repository.getAllEmployees().stream()
                .filter(Employee::isManager)  // Use explicit field
                .toList();
    }

    public List<Employee> getManagersImperative() {
        List<Employee> result = new ArrayList<>();
        for (Employee e : repository.getAllEmployees()) {
            if (e.isManager()) {  // Use explicit field
                result.add(e);
            }
        }
        return result;
    }

    /**
     * 5. EMPLOYEES BY MANAGER
     * Purpose: Get direct reports for a specific manager
     */
    public List<Employee> getEmployeesByManagerFunctional(UUID managerId) {
        if (managerId == null) return Collections.emptyList();

        return repository.getAllEmployees().stream()
                .filter(e -> e.getManagerId() != null &&
                        e.getManagerId().equals(managerId))
                .toList();
    }

    public List<Employee> getEmployeesByManagerImperative(UUID managerId) {
        List<Employee> result = new ArrayList<>();
        if (managerId == null) return result;

        for (Employee e : repository.getAllEmployees()) {
            if (e.getManagerId() != null &&
                    e.getManagerId().equals(managerId)) {
                result.add(e);
            }
        }
        return result;
    }

    /**
     * 6. ORDERED LIST WITH FORMATTING
     * Purpose: Generate formatted report with multi-criteria sorting
     */
    public List<String> getOrderedListFunctional() {
        Comparator<Employee> comparator = Comparator
                .comparing((Employee e) -> e.getDepartment().getName())
                .thenComparing(Employee::getLastName)
                .thenComparing(Employee::getFirstName);

        return repository.getAllEmployees().stream()
                .sorted(comparator)
                .map(e -> String.format("%s | %s, %s | %s | $%.2f | Manager=%b | ManagerName=%s",
                        e.getDepartment().getName(),
                        e.getLastName(),
                        e.getFirstName(),
                        e.getRole(),
                        e.getSalary(),
                        e.isManager(),
                        managerNameFor(e)))
                .toList();
    }

    public List<String> getOrderedListImperative() {
        List<Employee> employees = new ArrayList<>(repository.getAllEmployees());

        Comparator<Employee> comparator = Comparator
                .comparing((Employee e) -> e.getDepartment().getName())
                .thenComparing(Employee::getLastName)
                .thenComparing(Employee::getFirstName);

        employees.sort(comparator);

        List<String> result = new ArrayList<>();
        for (Employee e : employees) {
            result.add(String.format("%s | %s, %s | %s | $%.2f | Manager=%b | ManagerName=%s",
                    e.getDepartment().getName(),
                    e.getLastName(),
                    e.getFirstName(),
                    e.getRole(),
                    e.getSalary(),
                    e.isManager(),
                    managerNameFor(e)));
        }
        return result;
    }
}