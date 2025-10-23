package com.clinica.nomina.reportes;

import com.clinica.nomina.model.ConsolidadoNovedadesNomina;
import com.clinica.nomina.service.LiquidacionService;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

/**
 * Reporte visual de la liquidación general de nómina y por área.
 */
public class ReporteLiquidacion {

    private final LiquidacionService liquidacionService;

    public ReporteLiquidacion(LiquidacionService liquidacionService) {
        this.liquidacionService = liquidacionService;
    }

    /**
     * Imprime el reporte completo de liquidación general y por área.
     */
    public void imprimir() {
        System.out.println("======================================================");
        System.out.println("🧾  REPORTE GENERAL DE LIQUIDACIÓN DE NÓMINA");
        System.out.println("======================================================\n");

        List<ConsolidadoNovedadesNomina> lista = liquidacionService.calcularLiquidacionPorEmpleado();
        NumberFormat formato = liquidacionService.getFormatoMoneda();

        // --- Encabezado general ---
        System.out.printf("%-8s %-25s %-18s %20s %20s %20s%n",
                "ID", "Empleado", "Área", "Horas Trabajadas", "Valor Hora", "Total Devengado");
        System.out.println("--------------------------------------------------------------------------------------------------------------");

        lista.forEach(liq -> System.out.printf(
                "%-8s %-25s %-18s %20.2f %20s %20s%n",
                liq.idEmpleado(),
                liq.nombreEmpleado(),
                liq.area(),
                liq.horasTrabajadas(),
                formato.format(liq.salarioBaseHora()),
                formato.format(liq.totalPagar())
        ));

        System.out.println("--------------------------------------------------------------------------------------------------------------\n");

        // --- Agrupado por área ---
        Map<String, List<ConsolidadoNovedadesNomina>> porArea = liquidacionService.agruparPorArea();

        System.out.println("🏥  DETALLE POR ÁREA");
        System.out.println("======================================================");

        porArea.forEach((area, empleados) -> {
            System.out.printf("\n🔹 Área: %s%n", area);
            System.out.println("────────────────────────────────────────────────────────────────────────────");
            System.out.printf("%-8s %-25s %20s %20s %20s%n",
                    "ID", "Nombres", "Horas Trabajadas", "Valor Hora", "Total Devengado");
            System.out.println("--------------------------------------------------------------------------------------");

            empleados.forEach(e -> System.out.printf(
                    "%-8s %-25s %20.2f %20s %20s%n",
                    e.idEmpleado(),
                    e.nombreEmpleado(),
                    e.horasTrabajadas(),
                    formato.format(e.salarioBaseHora()),
                    formato.format(e.totalPagar())
            ));
        });

        System.out.println("\n✅  Fin del reporte de liquidación\n");
    }
}
