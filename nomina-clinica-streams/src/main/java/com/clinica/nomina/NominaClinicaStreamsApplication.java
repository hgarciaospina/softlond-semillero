package com.clinica.nomina;

import com.clinica.nomina.model.*;
import com.clinica.nomina.reportes.*;
import com.clinica.nomina.repository.DatosRepository;
import com.clinica.nomina.service.*;

import java.util.List;
import java.util.Scanner;

/**
 * Clase principal — Punto de entrada de la aplicación.
 *
 * Ahora con menú interactivo para ejecutar cada reporte individualmente
 * y pausa después de cada opción.
 */
public class NominaClinicaStreamsApplication {

    public static void main(String[] args) {

        // 🔹 Inicialización de datos base
        DatosRepository datosRepository = new DatosRepository();
        LiquidacionService liquidacionService = new LiquidacionService(datosRepository);
        List<ConsolidadoNovedadesNomina> consolidado = liquidacionService.calcularLiquidacionPorEmpleado();
        List<Empleado> listaEmpleados = datosRepository.obtenerEmpleados();
        List<RegistroTurno> registrosMes = datosRepository.obtenerRegistrosMes();

        // 🔹 Inicialización de servicios y reportes
        EmpleadoDelMesService empleadoDelMesService = new EmpleadoDelMesService(datosRepository);
        DesgloseHorasPorAreaYTurnoService desgloseService = new DesgloseHorasPorAreaYTurnoService(liquidacionService);
        ProductividadService productividadService = new ProductividadService(datosRepository);
        AuditoriaCoberturaService auditoriaService = new AuditoriaCoberturaService(liquidacionService.obtenerNovedadesNomina());
        BonusDisponibilidadService bonusService = new BonusDisponibilidadService(consolidado);
        TurnosConsecutivosAnormalesService turnosService = new TurnosConsecutivosAnormalesService(liquidacionService);
        InconsistenciasDatosService inconsistenciasService = new InconsistenciasDatosService(listaEmpleados, registrosMes);

        ReporteLiquidacion reporteLiquidacion = new ReporteLiquidacion(liquidacionService);
        ReporteEmpleadoDelMes reporteEmpleadoDelMes = new ReporteEmpleadoDelMes(empleadoDelMesService);
        ReporteDesgloseHorasPorAreaYTurno reporteDesglose = new ReporteDesgloseHorasPorAreaYTurno();
        ReporteProductividadEmpleado reporteProductividad = new ReporteProductividadEmpleado(productividadService);
        ReporteAuditoriaCobertura reporteAuditoria = new ReporteAuditoriaCobertura(auditoriaService);
        ReporteBonusDisponibilidad reporteBonus = new ReporteBonusDisponibilidad(consolidado);
        ReporteEmpleadosConBono reporteConBono = new ReporteEmpleadosConBono(bonusService.calcularBonus());
        ReporteTurnosConsecutivosAnormales reporteTurnosAnormales = new ReporteTurnosConsecutivosAnormales(turnosService);
        ReporteInconsistenciasDatos reporteInconsistencias = new ReporteInconsistenciasDatos(inconsistenciasService);

        // 🔹 Menú interactivo
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n====================================================");
            System.out.println("📋 MENÚ DE REPORTES DE NÓMINA - CLÍNICA STREAMS");
            System.out.println("====================================================");
            System.out.println("1️⃣  Liquidación general de nómina");
            System.out.println("2️⃣  Empleado del mes por área");
            System.out.println("3️⃣  Desglose de horas por área y tipo de turno");
            System.out.println("4️⃣  Productividad de empleados");
            System.out.println("5️⃣  Auditoría de cobertura mínima (guardia)");
            System.out.println("6️⃣  Bonus por disponibilidad");
            System.out.println("7️⃣  Turnos consecutivos anormales");
            System.out.println("8️⃣  Chequeo de inconsistencias de datos");
            System.out.println("9️⃣  Costo operativo por área");
            System.out.println("0️⃣  Salir");
            System.out.print("Seleccione una opción: ");

            // Leer opción y limpiar buffer
            opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar el salto de línea pendiente

            switch (opcion) {
                case 1 -> {
                    reporteLiquidacion.imprimir();
                    esperarEnter(scanner);
                }
                case 2 -> {
                    reporteEmpleadoDelMes.imprimir();
                    esperarEnter(scanner);
                }
                case 3 -> {
                    reporteDesglose.imprimir(desgloseService.calcularDesgloseHorasPorAreaYTipoTurnoStringDouble());
                    esperarEnter(scanner);
                }
                case 4 -> {
                    reporteProductividad.imprimir();
                    esperarEnter(scanner);
                }
                case 5 -> {
                    reporteAuditoria.imprimir();
                    esperarEnter(scanner);
                }
                case 6 -> {
                    System.out.println("\n📌 LISTA PRINCIPAL DE NÓMINA:");
                    reporteBonus.imprimir();
                    System.out.println("\n📌 EMPLEADOS CON BONO POR DISPONIBILIDAD:");
                    if (bonusService.calcularBonus().isEmpty()) {
                        System.out.println("📌 No hay empleados que cumplan los criterios para recibir bono de disponibilidad.");
                    } else {
                        reporteConBono.imprimir();
                    }
                    esperarEnter(scanner);
                }
                case 7 -> {
                    System.out.println("\n📌 TURNOS CONSECUTIVOS ANORMALES:");
                    reporteTurnosAnormales.imprimir();
                    esperarEnter(scanner);
                }
                case 8 -> {
                    System.out.println("\n📌 CHEQUEO DE INCONSISTENCIAS DE DATOS:");
                    reporteInconsistencias.imprimir();
                    esperarEnter(scanner);
                }
                case 9 -> {
                    System.out.println("\n📌 COSTO OPERATIVO POR ÁREA:");
                    //reporteCosto.imprimir();
                    esperarEnter(scanner);
                }
                case 0 -> System.out.println("🔹 Saliendo del sistema. ¡Hasta luego!");
                default -> System.out.println("❌ Opción inválida, intente de nuevo.");
            }

        } while (opcion != 0);

        scanner.close();
    }

    private static void esperarEnter(Scanner scanner) {
        System.out.println("\nOprima <enter> para continuar...");
        scanner.nextLine();
    }
}
