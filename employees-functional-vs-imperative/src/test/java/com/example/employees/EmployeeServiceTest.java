package com.example.employees;

import com.example.employees.model.Employee;
import com.example.employees.repository.EmployeeRepository;
import com.example.employees.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite validated against the actual repository structure.
 * Uses UUID comparison for reliable employee matching.
 */
class EmployeeServiceTest {

    private EmployeeService service;
    private EmployeeRepository repo;
    private List<Employee> allEmployees;

    @BeforeEach
    void setUp() {
        service = new EmployeeService();
        repo = new EmployeeRepository();
        allEmployees = repo.getAllEmployees();
    }

    @Test
    void testTotalSalaryEquality() {
        double functional = service.getTotalSalaryFunctional();
        double imperative = service.getTotalSalaryImperative();

        assertEquals(functional, imperative, 0.001,
                "Total salary calculation should be identical between implementations");
    }

    @Test
    void testEmployeesByDepartmentEquality() {
        // Use Development department from actual data
        UUID devId = service.findDepartmentIdByName("Development");
        assertNotNull(devId, "Development department should exist");

        List<Employee> functional = service.getEmployeesByDepartmentFunctional(devId);
        List<Employee> imperative = service.getEmployeesByDepartmentImperative(devId);

        assertEquals(functional.size(), imperative.size(),
                "Both implementations should return same number of employees");

        // Compare using UUIDs for reliable equality
        List<UUID> functionalIds = functional.stream()
                .map(Employee::getId)
                .sorted()
                .collect(Collectors.toList());
        List<UUID> imperativeIds = imperative.stream()
                .map(Employee::getId)
                .sorted()
                .collect(Collectors.toList());

        assertEquals(functionalIds, imperativeIds,
                "Both implementations should return identical employees");
    }

    @Test
    void testNamesAboveAverageEquality() {
        List<String> functional = service.getNamesAboveAverageSalaryFunctional();
        List<String> imperative = service.getNamesAboveAverageSalaryImperative();

        assertIterableEquals(functional, imperative,
                "Names above average should be identical in content and order");
    }

    @Test
    void testManagersEquality() {
        List<Employee> functional = service.getManagersFunctional();
        List<Employee> imperative = service.getManagersImperative();

        assertEquals(functional.size(), imperative.size(),
                "Should identify same number of managers");

        // Verify all identified employees are actually managers
        for (Employee manager : functional) {
            assertTrue(manager.isManager(), "All returned employees should be managers");
        }
        for (Employee manager : imperative) {
            assertTrue(manager.isManager(), "All returned employees should be managers");
        }

        // Compare manager lists by UUID
        List<UUID> functionalIds = functional.stream()
                .map(Employee::getId)
                .sorted()
                .collect(Collectors.toList());
        List<UUID> imperativeIds = imperative.stream()
                .map(Employee::getId)
                .sorted()
                .collect(Collectors.toList());

        assertEquals(functionalIds, imperativeIds,
                "Should identify identical managers");
    }

    @Test
    void testEmployeesByManagerEquality() {
        // Use John Doe as manager (first employee in repository)
        UUID johnId = allEmployees.get(0).getId();
        assertTrue(allEmployees.get(0).isManager(), "John Doe should be a manager");

        List<Employee> functional = service.getEmployeesByManagerFunctional(johnId);
        List<Employee> imperative = service.getEmployeesByManagerImperative(johnId);

        assertEquals(functional.size(), imperative.size(),
                "Should find same number of direct reports");

        // All returned employees should report to the specified manager
        for (Employee emp : functional) {
            assertEquals(johnId, emp.getManagerId(),
                    "Employee should report to specified manager");
        }
        for (Employee emp : imperative) {
            assertEquals(johnId, emp.getManagerId(),
                    "Employee should report to specified manager");
        }

        // Compare by UUID
        List<UUID> functionalIds = functional.stream()
                .map(Employee::getId)
                .sorted()
                .collect(Collectors.toList());
        List<UUID> imperativeIds = imperative.stream()
                .map(Employee::getId)
                .sorted()
                .collect(Collectors.toList());

        assertEquals(functionalIds, imperativeIds,
                "Should return identical direct reports");
    }

    @Test
    void testOrderedListEquality() {
        List<String> functional = service.getOrderedListFunctional();
        List<String> imperative = service.getOrderedListImperative();

        assertEquals(functional.size(), imperative.size(),
                "Ordered lists should have same size");
        assertEquals(functional, imperative,
                "Ordered lists should be identical in content and order");
    }
}