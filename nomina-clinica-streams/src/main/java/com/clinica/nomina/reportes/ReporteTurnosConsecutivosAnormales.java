package com.clinica.nomina.reportes;

import com.clinica.nomina.service.TurnosConsecutivosAnormalesService;

import java.util.List;

/**
 * Reporte que imprime los empleados con turnos consecutivos anormales.
 */
public class ReporteTurnosConsecutivosAnormales {

    private final TurnosConsecutivosAnormalesService turnosService;

    public ReporteTurnosConsecutivosAnormales(TurnosConsecutivosAnormalesService turnosService) {
        this.turnosService = turnosService;
    }

    public void imprimir() {
        System.out.println("==============================================");
        System.out.println("🔎 REPORTE: Turnos Consecutivos Anormales");
        System.out.println("==============================================");

        List<String> empleados = turnosService.detectarTurnosConsecutivosAnormales();

        if (empleados.isEmpty()) {
            System.out.println("✅ No se encontraron turnos consecutivos anormales.");
        } else {
            empleados.forEach(nombre ->
                    System.out.println("⚠️  " + nombre + " trabajó un turno DÍA después de una GUARDIA."));
        }

        System.out.println("==============================================\n");
    }
}
