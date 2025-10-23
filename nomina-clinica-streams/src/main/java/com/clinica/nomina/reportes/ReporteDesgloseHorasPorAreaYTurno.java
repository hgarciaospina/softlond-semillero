package com.clinica.nomina.reportes;

import java.util.Locale;
import java.util.Map;

/**
 * Reporte que imprime el desglose de horas trabajadas
 * por área y tipo de turno.
 */
public class ReporteDesgloseHorasPorAreaYTurno {

    public void imprimir(Map<String, Map<String, Double>> desglose) {
        System.out.println("\n============================================");
        System.out.println("   📊 DESGLOSE DE HORAS POR ÁREA Y TIPO DE TURNO");
        System.out.println("============================================\n");

        desglose.forEach((area, turnos) -> {
            System.out.printf("Área: %s%n", area);
            System.out.println("--------------------------------------------");
            System.out.printf("%-20s %15s%n", "Tipo de Turno", "Total Horas Área");
            System.out.println("--------------------------------------------");

            turnos.forEach((tipo, horas) ->
                    System.out.printf(Locale.US, "%-20s %15.2f%n", tipo, horas)
            );

            double totalArea = turnos.values().stream().mapToDouble(Double::doubleValue).sum();
            System.out.println("--------------------------------------------");
            System.out.printf(Locale.US, "%-20s %15.2f%n", "TOTAL ÁREA", totalArea);
            System.out.println();
        });
    }
}
