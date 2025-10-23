package com.clinica.nomina.reportes;

import com.clinica.nomina.model.EmpleadoConBonus;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ReporteEmpleadosConBono {

    private final List<EmpleadoConBonus> empleados;
    private final NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

    // Códigos ANSI de colores
    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";

    public ReporteEmpleadosConBono(List<EmpleadoConBonus> empleados) {
        this.empleados = empleados;
    }

    public void imprimir() {
        // Cabecera con color
        System.out.printf(CYAN + "%-8s %-20s %-15s %-10s %-12s %-15s %-12s %-15s%n",
                "ID", "Empleado", "Área", "Horas", "Valor Hora", "Total Dev.", "Bono", "Total C/Bono" + RESET);
        System.out.println(CYAN + "--------------------------------------------------------------------------------------------------------" + RESET);

        for (EmpleadoConBonus e : empleados) {
            System.out.printf("%-8s %-20s %-15s %-10.2f %-12.2f %-15s %-12s %-15s%n",
                    e.idEmpleado(),
                    e.nombre(),
                    e.area(),
                    e.horasTrabajadas(),
                    e.valorHora(),
                    formatoMoneda.format(e.totalDevengado()),
                    formatoMoneda.format(e.bonus()),
                    formatoMoneda.format(e.totalConBonus())
            );
        }
    }
}
