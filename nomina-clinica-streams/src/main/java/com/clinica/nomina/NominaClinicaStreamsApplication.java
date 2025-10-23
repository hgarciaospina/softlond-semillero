package com.clinica.nomina;

import com.clinica.nomina.model.Area;
import com.clinica.nomina.model.EmpleadoConBonus;
import com.clinica.nomina.model.TipoTurno;
import com.clinica.nomina.reportes.*;
import com.clinica.nomina.repository.DatosRepository;
import com.clinica.nomina.service.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Clase principal — Punto de entrada de la aplicación.
 *
 * Inicializa los servicios y ejecuta todos los reportes.
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
        Map<Area, Map<TipoTurno, Integer>> mapaDesgloseOriginal = desgloseService.calcularDesgloseHorasPorAreaYTipoTurno();

        // Convertir a Map<String, Map<String, Double>> para el reporte
        Map<String, Map<String, Double>> mapaDesglose = mapaDesgloseOriginal.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().name(), // Area -> String
                        e -> e.getValue().entrySet().stream()
                                .collect(Collectors.toMap(
                                        t -> t.getKey().name(),        // TipoTurno -> String
                                        t -> t.getValue().doubleValue() // Integer -> Double
                                ))
                ));

        ReporteDesgloseHorasPorAreaYTurno reporteDesglose = new ReporteDesgloseHorasPorAreaYTurno();
        reporteDesglose.imprimir(mapaDesglose);

        // =============================================================
        // 6️⃣ PRODUCTIVIDAD DE EMPLEADOS
        // =============================================================
        ProductividadService productividadService = new ProductividadService(datosRepository);
        ReporteProductividadEmpleado reporteProductividad = new ReporteProductividadEmpleado(productividadService);
        reporteProductividad.imprimir();

        // =============================================================
        // 7️⃣ AUDITORÍA DE COBERTURA MÍNIMA (GUARDIA)
        // =============================================================
        AuditoriaCoberturaService auditoriaService = new AuditoriaCoberturaService(liquidacionService.obtenerNovedadesNomina());
        ReporteAuditoriaCobertura reporteAuditoria = new ReporteAuditoriaCobertura(auditoriaService);
        reporteAuditoria.imprimir();

        // =============================================================
        // 8️⃣ BONUS POR DISPONIBILIDAD
        // =============================================================
        BonusDisponibilidadService bonusService = new BonusDisponibilidadService(
                liquidacionService.obtenerNovedadesNomina(),
                datosRepository.obtenerEmpleados(),
                datosRepository.obtenerRegistrosMes()
        );
        List<EmpleadoConBonus> empleadosConBonus = bonusService.calcularBonus();
        ReporteBonusDisponibilidad reporteBonus = new ReporteBonusDisponibilidad(empleadosConBonus);
        reporteBonus.imprimir();

        System.out.println("\n✅ Todos los reportes fueron generados exitosamente.");
    }
}
