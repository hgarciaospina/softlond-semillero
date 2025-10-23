package com.clinica.nomina.reportes;

import com.clinica.nomina.model.TipoTurno;
import com.clinica.nomina.model.Area;
import com.clinica.nomina.service.DesgloseHorasPorAreaYTurnoService;

import java.util.Map;

/**
 * Reporte que imprime el Desglose de Horas por Área y Tipo de Turno.
 * Cambia el título y coloca "Total Horas Por Area" tal como solicitó.
 */
public class ReporteDesgloseHorasPorAreaYTurno {

    private final DesgloseHorasPorAreaYTurnoService service;

    public ReporteDesgloseHorasPorAreaYTurno(DesgloseHorasPorAreaYTurnoService service) {
        this.service = service;
    }

    public void imprimir() {
        System.out.println("\n====================================================");
        System.out.println("📊  DESGLOSE DE HORAS POR ÁREA Y TIPO DE TURNO");
        System.out.println("====================================================\n");

        Map<Area, Map<TipoTurno, Integer>> desglose = service.calcularDesgloseHorasPorAreaYTipoTurno();

        desglose.forEach((area, mapaTurnos) -> {
            System.out.println("--------------------------------------------");
            System.out.println("Área: " + area);
            System.out.println("--------------------------------------------");
            System.out.printf("%-12s %-20s %12s%n", "Tipo Turno", "Descripción", "Horas");
            System.out.println("--------------------------------------------");

            int totalArea = 0;
            for (Map.Entry<TipoTurno, Integer> t : mapaTurnos.entrySet()) {
                System.out.printf("%-12s %-20s %12d%n", t.getKey(), t.getKey().name(), t.getValue());
                totalArea += t.getValue();
            }

            System.out.println("--------------------------------------------");
            System.out.printf("Total Horas Por Area: %d%n", totalArea);
            System.out.println();
        });

        System.out.println("====================================================");
        System.out.println("✅  Fin del reporte\n");
    }
}
