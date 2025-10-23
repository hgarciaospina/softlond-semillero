package com.clinica.nomina;

import com.clinica.nomina.reportes.*;
import com.clinica.nomina.repository.DatosRepository;
import com.clinica.nomina.service.*;

/**
 * Clase principal — Punto de entrada de la aplicación.
 *
 * Su responsabilidad es mínima: inicializar los servicios y ejecutar los reportes.
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

        System.out.println("\n✅ Todos los reportes fueron generados exitosamente.");
    }
}
