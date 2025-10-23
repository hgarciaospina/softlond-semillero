package com.clinica.nomina.reportes;

import com.clinica.nomina.service.InconsistenciasDatosService;

import java.util.List;

/**
 * Reporte que imprime los IDs de empleados con inconsistencias en los registros de nómina.
 */
public class ReporteInconsistenciasDatos {

    private final InconsistenciasDatosService service;

    public ReporteInconsistenciasDatos(InconsistenciasDatosService service) {
        this.service = service;
    }

    public void imprimir() {
        System.out.println("\n====================================================");
        System.out.println("⚠️  CHEQUEO DE INCONSISTENCIAS DE DATOS");
        System.out.println("====================================================\n");

        List<String> idsInconsistentes = service.detectarInconsistencias();

        if (idsInconsistentes.isEmpty()) {
            System.out.println("No se detectaron inconsistencias en los registros de empleados.");
        } else {
            System.out.println("IDs de empleados presentes en registrosMes pero no en personal:");
            idsInconsistentes.forEach(id -> System.out.println(" - " + id));
        }

        System.out.println("====================================================");
        System.out.println("✅ Fin del reporte\n");
    }
}
