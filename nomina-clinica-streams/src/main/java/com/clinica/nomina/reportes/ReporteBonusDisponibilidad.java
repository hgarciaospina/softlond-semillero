package com.clinica.nomina.reportes;

import com.clinica.nomina.model.EmpleadoConBonus;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Reporte de Bonus por Disponibilidad
 */
public class ReporteBonusDisponibilidad {

    private final List<EmpleadoConBonus> empleadosConBonus;
    private final NumberFormat formatoMoneda;

    public ReporteBonusDisponibilidad(List<EmpleadoConBonus> empleadosConBonus) {
        this.empleadosConBonus = empleadosConBonus;
        this.formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
    }

    public void imprimir() {
        System.out.println("\n======================================================");
        System.out.println("🎯  BONUS POR DISPONIBILIDAD");
        System.out.println("======================================================");
        System.out.printf("%-8s %-20s %-15s %-18s %-12s %-15s %-12s %-15s %-6s%n",
                "ID", "Empleado", "Área", "Horas Trabajadas", "Valor Hora",
                "Total Devengado", "Bonus", "Total con Bonus", "Aplica");
        System.out.println("--------------------------------------------------------------------------------------------------------------");

        empleadosConBonus.forEach(e -> {
            String aplicaBonus = e.totalConBonus() > e.totalDevengado() ? "✅" : "❌";
            System.out.printf(
                    "%-8s %-20s %-15s %-18.2f %-12s %-15s %-12s %-15s %-6s%n",
                    e.idEmpleado(),
                    e.nombre(),
                    e.area() != null ? e.area().name() : "SIN_AREA",
                    e.horasTrabajadas(),
                    formatoMoneda.format(e.valorHora()),
                    formatoMoneda.format(e.totalDevengado()),
                    formatoMoneda.format(e.bonus()),
                    formatoMoneda.format(e.totalConBonus()),
                    aplicaBonus
            );
        });

        if (empleadosConBonus.isEmpty()) {
            System.out.println("No hay empleados con bonus aplicable.");
        }

        System.out.println("======================================================");
        System.out.println("✅ Fin del reporte de Bonus por Disponibilidad");
    }
}
