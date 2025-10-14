package com.example.employees;

import com.example.employees.model.Employee;
import com.example.employees.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

import java.util.List;
import java.util.UUID;

/**
 * Demonstration main for functional vs imperative methods.
 * Functional outputs are shown in BLUE, imperative in GREEN.
 * The main title is GOLD (YELLOW).
 *
 * This version uses only methods that exist in EmployeeService:
 * - getTotalSalaryFunctional / getTotalSalaryImperative
 * - getNamesAboveAverageSalaryFunctional / getNamesAboveAverageSalaryImperative
 * - getManagersFunctional / getManagersImperative
 * - findDepartmentIdByName
 * - getEmployeesByDepartmentFunctional / getEmployeesByDepartmentImperative
 * - findManagerIdByName
 * - getEmployeesByManagerFunctional / getEmployeesByManagerImperative
 * - getOrderedListFunctional / getOrderedListImperative
 */
public class MainApp {

    // Separate loggers for visual distinction
    private static final Logger logFunctional = LoggerFactory.getLogger("functional");
    private static final Logger logImperative = LoggerFactory.getLogger("imperative");
    private static final Logger logMain = LoggerFactory.getLogger(MainApp.class);

    // ANSI Colors
    private static final String RESET = "\u001B[0m";
    private static final String GOLD = "\u001B[33m";         // Title
    private static final String BLUE = "\u001B[34m";         // Functional label
    private static final String BRIGHT_BLUE = "\u001B[94m";  // Functional values
    private static final String GREEN = "\u001B[32m";        // Imperative label
    private static final String BRIGHT_GREEN = "\u001B[92m"; // Imperative values
    private static final String MAGENTA = "\u001B[35m";      // Sections

    public static void main(String[] args) {
        // Set log level
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger("ROOT").setLevel(Level.INFO);

        EmployeeService service = new EmployeeService();

        // Header
        logMain.info(GOLD + "==================== EMPLOYEE FUNCTIONAL vs IMPERATIVE DEMO ====================" + RESET);

        // ---------------------- TOTAL SALARY ----------------------
        logFunctional.info(BLUE + "Total Salary (Functional): " + RESET + BRIGHT_BLUE + service.getTotalSalaryFunctional() + RESET);
        logImperative.info(GREEN + "Total Salary (Imperative): " + RESET + BRIGHT_GREEN + service.getTotalSalaryImperative() + RESET);

        // ---------------------- MANAGERS ----------------------
        logMain.info(MAGENTA + "\n-------------------- MANAGERS --------------------" + RESET);

        List<Employee> managersF = service.getManagersFunctional();
        logFunctional.info(BLUE + "Managers (Functional):" + RESET);
        if (managersF != null && !managersF.isEmpty()) {
            managersF.forEach(m -> logFunctional.info(BRIGHT_BLUE + "  → " + m.getFullName() + " - " + m.getRole() + RESET));
        } else {
            logFunctional.info(BRIGHT_BLUE + "  (none)" + RESET);
        }

        List<Employee> managersI = service.getManagersImperative();
        logImperative.info(GREEN + "Managers (Imperative):" + RESET);
        if (managersI != null && !managersI.isEmpty()) {
            managersI.forEach(m -> logImperative.info(BRIGHT_GREEN + "  → " + m.getFullName() + " - " + m.getRole() + RESET));
        } else {
            logImperative.info(BRIGHT_GREEN + "  (none)" + RESET);
        }

        // ---------------------- ABOVE AVERAGE SALARY ----------------------
        logMain.info(MAGENTA + "\n-------------------- ABOVE AVERAGE SALARY --------------------" + RESET);
        logFunctional.info(BLUE + "Names above avg (Functional): " + RESET + BRIGHT_BLUE + service.getNamesAboveAverageSalaryFunctional() + RESET);
        logImperative.info(GREEN + "Names above avg (Imperative): " + RESET + BRIGHT_GREEN + service.getNamesAboveAverageSalaryImperative() + RESET);

        // ---------------------- EMPLOYEES BY DEPARTMENT ----------------------
        logMain.info(MAGENTA + "\n-------------------- EMPLOYEES BY DEPARTMENT (Development) --------------------" + RESET);
        UUID devId = service.findDepartmentIdByName("Development");
        if (devId != null) {
            List<Employee> byDeptF = service.getEmployeesByDepartmentFunctional(devId);
            logFunctional.info(BLUE + "Employees in Development (Functional):" + RESET);
            if (byDeptF != null && !byDeptF.isEmpty()) {
                byDeptF.forEach(e -> logFunctional.info(BRIGHT_BLUE + "  → " + e.getFullName() + " (" + e.getRole() + ")" + RESET));
            } else {
                logFunctional.info(BRIGHT_BLUE + "  (none)" + RESET);
            }

            List<Employee> byDeptI = service.getEmployeesByDepartmentImperative(devId);
            logImperative.info(GREEN + "Employees in Development (Imperative):" + RESET);
            if (byDeptI != null && !byDeptI.isEmpty()) {
                byDeptI.forEach(e -> logImperative.info(BRIGHT_GREEN + "  → " + e.getFullName() + " (" + e.getRole() + ")" + RESET));
            } else {
                logImperative.info(BRIGHT_GREEN + "  (none)" + RESET);
            }
        } else {
            logMain.warn("Department 'Development' not found in repository.");
        }

        // ---------------------- REPORTS TO JOHN DOE ----------------------
        logMain.info(MAGENTA + "\n-------------------- REPORTS TO JOHN DOE --------------------" + RESET);
        UUID johnId = service.findManagerIdByName("John Doe");
        if (johnId != null) {
            List<Employee> reportsF = service.getEmployeesByManagerFunctional(johnId);
            logFunctional.info(BLUE + "Reports to John Doe (Functional):" + RESET);
            if (reportsF != null && !reportsF.isEmpty()) {
                reportsF.forEach(e -> logFunctional.info(BRIGHT_BLUE + "  → " + e.getFullName() + " - " + e.getRole() + RESET));
            } else {
                logFunctional.info(BRIGHT_BLUE + "  (none)" + RESET);
            }

            List<Employee> reportsI = service.getEmployeesByManagerImperative(johnId);
            logImperative.info(GREEN + "Reports to John Doe (Imperative):" + RESET);
            if (reportsI != null && !reportsI.isEmpty()) {
                reportsI.forEach(e -> logImperative.info(BRIGHT_GREEN + "  → " + e.getFullName() + " - " + e.getRole() + RESET));
            } else {
                logImperative.info(BRIGHT_GREEN + "  (none)" + RESET);
            }
        } else {
            logMain.warn("Manager 'John Doe' not found in repository.");
        }

        // ---------------------- ORDERED LIST ----------------------
        logMain.info(MAGENTA + "\n-------------------- ORDERED LIST --------------------" + RESET);
        logFunctional.info(BLUE + "Ordered List (Functional):" + RESET);
        List<String> orderedF = service.getOrderedListFunctional();
        if (orderedF != null && !orderedF.isEmpty()) {
            orderedF.forEach(s -> logFunctional.info(BRIGHT_BLUE + "  " + s + RESET));
        } else {
            logFunctional.info(BRIGHT_BLUE + "  (none)" + RESET);
        }

        logImperative.info(GREEN + "Ordered List (Imperative):" + RESET);
        List<String> orderedI = service.getOrderedListImperative();
        if (orderedI != null && !orderedI.isEmpty()) {
            orderedI.forEach(s -> logImperative.info(BRIGHT_GREEN + "  " + s + RESET));
        } else {
            logImperative.info(BRIGHT_GREEN + "  (none)" + RESET);
        }

        // Footer
        logMain.info(GOLD + "\n==================== END OF DEMO ====================" + RESET);
    }
}
