package com.example.employees.repository;

import com.example.employees.model.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Simulated repository with 20 employees. Departments and Employees have UUID identifiers.
 * Employee.managerId references the UUID of the manager employee when applicable.
 */
public class EmployeeRepository {

    public List<Employee> getAllEmployees() {
        Department dev = new Department(UUID.fromString("11111111-1111-1111-1111-111111111111"), "Development");
        Department hr = new Department(UUID.fromString("22222222-2222-2222-2222-222222222222"), "Human Resources");
        Department sales = new Department(UUID.fromString("33333333-3333-3333-3333-333333333333"), "Sales");
        Department qa = new Department(UUID.fromString("44444444-4444-4444-4444-444444444444"), "Quality Assurance");

        // Create manager UUIDs first for reference
        UUID johnId = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000001");
        UUID lauraId = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000002");
        UUID sofiaId = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000003");
        UUID luciaId = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000004");

        return Arrays.asList(
                new Employee(johnId, "John", "Doe", Role.MANAGER, 9000, dev, true, null, LocalDate.of(2014,1,10)),
                new Employee(UUID.fromString("bbbbbbbb-0000-0000-0000-000000000011"), "Jane", "Smith", Role.DEVELOPER, 6200, dev, false, johnId, LocalDate.of(2018,3,15)),
                new Employee(UUID.fromString("bbbbbbbb-0000-0000-0000-000000000012"), "Carlos", "Perez", Role.DEVELOPER, 5800, dev, false, johnId, LocalDate.of(2019,6,1)),
                new Employee(UUID.fromString("bbbbbbbb-0000-0000-0000-000000000013"), "Ana", "Gomez", Role.TESTER, 4500, qa, false, lauraId, LocalDate.of(2020,4,5)),
                new Employee(lauraId, "Laura", "Torres", Role.MANAGER, 7400, qa, true, null, LocalDate.of(2016,5,12)),
                new Employee(UUID.fromString("bbbbbbbb-0000-0000-0000-000000000014"), "Diana", "Martinez", Role.DESIGNER, 5000, dev, false, johnId, LocalDate.of(2021,8,9)),
                new Employee(UUID.fromString("bbbbbbbb-0000-0000-0000-000000000015"), "David", "Garcia", Role.DEVELOPER, 5900, dev, false, johnId, LocalDate.of(2019,7,20)),
                new Employee(UUID.fromString("bbbbbbbb-0000-0000-0000-000000000016"), "Andres", "Lopez", Role.ANALYST, 5500, sales, false, sofiaId, LocalDate.of(2017,11,14)),
                new Employee(sofiaId, "Sofia", "Castro", Role.MANAGER, 8000, sales, true, null, LocalDate.of(2014,9,2)),
                new Employee(UUID.fromString("bbbbbbbb-0000-0000-0000-000000000017"), "Maria", "Vargas", Role.TESTER, 4600, qa, false, lauraId, LocalDate.of(2021,10,3)),
                new Employee(UUID.fromString("bbbbbbbb-0000-0000-0000-000000000018"), "Ricardo", "Diaz", Role.DEVELOPER, 6100, dev, false, johnId, LocalDate.of(2020,1,19)),
                new Employee(UUID.fromString("bbbbbbbb-0000-0000-0000-000000000019"), "Camila", "Ruiz", Role.DESIGNER, 5200, dev, false, johnId, LocalDate.of(2022,2,1)),
                new Employee(UUID.fromString("bbbbbbbb-0000-0000-0000-000000000020"), "Esteban", "Morales", Role.ANALYST, 5300, hr, false, luciaId, LocalDate.of(2018,9,9)),
                new Employee(luciaId, "Lucia", "Rios", Role.MANAGER, 7600, hr, true, null, LocalDate.of(2013,7,22)),
                new Employee(UUID.fromString("bbbbbbbb-0000-0000-0000-000000000021"), "Julian", "Ortiz", Role.DEVELOPER, 6000, dev, false, johnId, LocalDate.of(2020,12,25)),
                new Employee(UUID.fromString("bbbbbbbb-0000-0000-0000-000000000022"), "Veronica", "Pena", Role.TESTER, 4700, qa, false, lauraId, LocalDate.of(2022,1,10)),
                new Employee(UUID.fromString("bbbbbbbb-0000-0000-0000-000000000023"), "Felipe", "Castillo", Role.ANALYST, 5100, hr, false, luciaId, LocalDate.of(2019,2,2)),
                new Employee(UUID.fromString("bbbbbbbb-0000-0000-0000-000000000024"), "Daniela", "Mendoza", Role.DESIGNER, 4800, sales, false, sofiaId, LocalDate.of(2023,1,5)),
                new Employee(UUID.fromString("bbbbbbbb-0000-0000-0000-000000000025"), "Miguel", "Santos", Role.TESTER, 4400, qa, false, lauraId, LocalDate.of(2020,11,11)),
                new Employee(UUID.fromString("bbbbbbbb-0000-0000-0000-000000000026"), "Natalia", "Rojas", Role.DEVELOPER, 5900, dev, false, johnId, LocalDate.of(2019,9,30))
        );
    }
}
