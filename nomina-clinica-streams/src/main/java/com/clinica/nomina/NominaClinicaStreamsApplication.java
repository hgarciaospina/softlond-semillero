package com.clinica.nomina;

import com.clinica.nomina.model.ConsolidadoNovedadesNomina;
import com.clinica.nomina.model.EmpleadoConBonus;
import com.clinica.nomina.reportes.*;
import com.clinica.nomina.repository.DatosRepository;
import com.clinica.nomina.service.*;

import java.util.List;

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
        ReporteDesgloseHorasPorAreaYTurno reporteDesglose = new ReporteDesgloseHorasPorAreaYTurno();
        reporteDesglose.imprimir(desgloseService.calcularDesgloseHorasPorAreaYTipoTurnoStringDouble());

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
        List<ConsolidadoNovedadesNomina> consolidado = liquidacionService.calcularLiquidacionPorEmpleado();

        // Imprime la lista principal de nómina
        ReporteBonusDisponibilidad reporteBonus = new ReporteBonusDisponibilidad(consolidado);
        System.out.println("\n📌 LISTA PRINCIPAL DE NÓMINA:");
        reporteBonus.imprimir();

        // Genera y muestra la lista de empleados con bono
        BonusDisponibilidadService bonusService = new BonusDisponibilidadService(consolidado);
        List<EmpleadoConBonus> empleadosConBonus = bonusService.calcularBonus();

        System.out.println("\n📌 EMPLEADOS CON BONO POR DISPONIBILIDAD:");
        if (empleadosConBonus.isEmpty()) {
            System.out.println("📌 No hay empleados que cumplan los criterios para recibir bono de disponibilidad.");
        } else {
            // Se imprime en tabla con todos los campos de EmpleadoConBonus
            ReporteEmpleadosConBono reporteConBono = new ReporteEmpleadosConBono(empleadosConBonus);
            reporteConBono.imprimir();
        }

        System.out.println("\n✅ Todos los reportes fueron generados exitosamente.");
    }
}
