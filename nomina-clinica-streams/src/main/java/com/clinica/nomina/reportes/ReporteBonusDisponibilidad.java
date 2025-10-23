package com.clinica.nomina.reportes;

import com.clinica.nomina.model.ConsolidadoNovedadesNomina;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ReporteBonusDisponibilidad {

    private final List<ConsolidadoNovedadesNomina> consolidado;
    private final NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

    public ReporteBonusDisponibilidad(List<ConsolidadoNovedadesNomina> consolidado) {
        this.consolidado = consolidado;
    }

    public void imprimir() {
        System.out.println("\nID       Empleado                  Área                 Horas      Valor Hora      Total Dev.        Bono");
        System.out.println("---------------------------------------------------------------------------------------------------------------");

        for (ConsolidadoNovedadesNomina c : consolidado) {
            String checkBono = c.bonusDisponibilidad() ? "✅" : "❌";

            System.out.printf(
                    "%-8s %-25s %-20s %7.2f %12.2f %15s %10s%n",
                    c.idEmpleado(),
                    c.nombreEmpleado(),
                    c.area(),
                    c.horasTrabajadas(),
                    c.salarioBaseHora(),
                    formatoMoneda.format(c.totalPagar()),
                    checkBono
            );
        }
    }
}
