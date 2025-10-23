package com.clinica.nomina.reportes;

import com.clinica.nomina.service.AuditoriaCoberturaService;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Reporte que imprime las fechas con cobertura mínima insuficiente
 * en turnos de GUARDIA.
 */
public class ReporteAuditoriaCobertura {

    private final AuditoriaCoberturaService service;

    public ReporteAuditoriaCobertura(AuditoriaCoberturaService service) {
        this.service = service;
    }

    public void imprimir() {
        List<java.time.LocalDate> fechas = service.fechasConCoberturaInsuficiente();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        System.out.println("\n====================================================");
        System.out.println("📋  AUDITORÍA DE COBERTURA MÍNIMA (GUARDIA)");
        System.out.println("====================================================\n");

        if (fechas.isEmpty()) {
            System.out.println("✅ Todas las fechas cumplen la cobertura mínima de 2 empleados en GUARDIA.");
        } else {
            System.out.println("⚠️  Fechas con menos de 2 empleados en GUARDIA:");
            fechas.forEach(f -> System.out.println("- " + f.format(formatter)));
        }

        System.out.println("====================================================\n");
    }
}
