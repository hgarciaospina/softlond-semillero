package com.clinica.nomina;

import com.clinica.nomina.reportes.*;
import com.clinica.nomina.repository.DatosRepository;
import com.clinica.nomina.service.*;

/**
 * Clase principal — Punto de entrada de la aplicación.
 *
 * Inicializa los servicios y ejecuta todos los reportes de nómina.
 */
public class NominaClinicaStreamsApplication {

    public static void main(String[] args) {

        // 🔹 Inicialización de datos base
        DatosRepository datosRepository = new DatosRepository();

        // =============================================================
        // 1️⃣ LIQUIDACIÓN GENERAL DE NÓMINA
        // =============================================================
        LiquidacionService liquidacionService = new LiquidacionService(datosRepository);
        ReporteLiquidacion reporteLiquidacion = new ReporteLiquidacion(liquidacionService);
        reporteLiquidacion.imprimir();

        // =============================================================
        // 2️⃣ EMPLEADO DEL MES POR ÁREA
        // =============================================================
        EmpleadoDelMesService empleadoDelMesService = new EmpleadoDelMesService(datosRepository);
        ReporteEmpleadoDelMes reporteEmpleadoDelMes = new ReporteEmpleadoDelMes(empleadoDelMesService);
        reporteEmpleadoDelMes.imprimir();

        // =============================================================
        // 3️⃣ DESGLOSE DE HORAS POR ÁREA Y TIPO DE TURNO
        // =============================================================
        DesgloseHorasPorAreaYTurnoService desgloseService = new DesgloseHorasPorAreaYTurnoService(liquidacionService);
        ReporteDesgloseHorasPorAreaYTurno reporteDesglose = new ReporteDesgloseHorasPorAreaYTurno(desgloseService);
        reporteDesglose.imprimir();

        // =============================================================
        // 4️⃣ DETECCIÓN DE TURNOS CONSECUTIVOS ANORMALES
        // =============================================================
        TurnosConsecutivosAnormalesService turnosAnormalesService = new TurnosConsecutivosAnormalesService(liquidacionService);
        ReporteTurnosConsecutivosAnormales reporteTurnosAnormales = new ReporteTurnosConsecutivosAnormales(turnosAnormalesService);
        reporteTurnosAnormales.imprimir();

        // =============================================================
        // 5️⃣ CHEQUEO DE INCONSISTENCIAS DE DATOS
        // =============================================================
        InconsistenciasDatosService inconsistenciasService =
                new InconsistenciasDatosService(datosRepository.obtenerEmpleados(), datosRepository.obtenerRegistrosMes());
        ReporteInconsistenciasDatos reporteInconsistencias = new ReporteInconsistenciasDatos(inconsistenciasService);
        reporteInconsistencias.imprimir();

        System.out.println("\n✅ Todos los reportes fueron generados exitosamente.");
    }
}
