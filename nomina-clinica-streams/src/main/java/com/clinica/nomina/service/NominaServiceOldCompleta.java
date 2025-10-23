package com.clinica.nomina.service;

import com.clinica.nomina.model.ConsolidadoNovedadesNomina;
import com.clinica.nomina.model.Empleado;
import com.clinica.nomina.model.NovedadesNomina;
import com.clinica.nomina.model.RegistroTurno;
import com.clinica.nomina.model.TipoTurno;
import com.clinica.nomina.repository.DatosRepository;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * Servicio principal que calcula la nómina y genera los datos para los reportes.
 */
public class NominaServiceOldCompleta {

    private final List<Empleado> empleados;
    private final List<RegistroTurno> registrosMes;
    private final NumberFormat formatoMoneda;

    public NominaServiceOldCompleta() {
        DatosRepository repo = new DatosRepository();
        this.empleados = Optional.ofNullable(repo.obtenerEmpleados()).orElse(Collections.emptyList());
        this.registrosMes = Optional.ofNullable(repo.obtenerRegistrosMes()).orElse(Collections.emptyList());
        this.formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
    }

    // ============================================================
    // ================== LÓGICA DE CÁLCULO =======================
    // ============================================================

    public List<ConsolidadoNovedadesNomina> calcularLiquidacionPorEmpleado() {
        Map<String, Double> horasPorEmpleado = registrosMes.stream()
                .filter(r -> r.idEmpleado() != null && r.horas() > 0)
                .collect(groupingBy(
                        RegistroTurno::idEmpleado,
                        Collectors.summingDouble(r -> r.horas() * getMultiplicador(r.tipo()))
                ));

        return empleados.stream()
                .filter(e -> e.id() != null && e.salarioBaseHora() > 0)
                .filter(e -> horasPorEmpleado.containsKey(e.id()))
                .map(e -> {
                    double horas = horasPorEmpleado.getOrDefault(e.id(), 0.0);
                    double salarioHora = e.salarioBaseHora();
                    double totalPagar = horas * salarioHora;
                    String area = e.area() != null ? e.area().name() : "NO ASIGNADA";

                    return new ConsolidadoNovedadesNomina(
                            e.id(),
                            e.nombre() != null ? e.nombre() : "SIN NOMBRE",
                            area,
                            horas,
                            salarioHora,
                            totalPagar
                    );
                })
                .sorted(Comparator.comparingDouble(ConsolidadoNovedadesNomina::totalPagar).reversed())
                .collect(Collectors.toList());
    }

    public Map<String, List<ConsolidadoNovedadesNomina>> agruparEmpleadosPorArea() {
        List<ConsolidadoNovedadesNomina> liquidaciones = calcularLiquidacionPorEmpleado();
        return liquidaciones.stream().collect(groupingBy(ConsolidadoNovedadesNomina::area));
    }

    public List<NovedadesNomina> obtenerNovedadesNomina() {
        return registrosMes.stream()
                .filter(r -> r.idEmpleado() != null)
                .map(r -> {
                    Empleado e = empleados.stream()
                            .filter(emp -> emp.id().equals(r.idEmpleado()))
                            .findFirst()
                            .orElse(null);

                    double salarioHora = e != null ? e.salarioBaseHora() : 0;
                    double totalPagar = r.horas() * salarioHora * getMultiplicador(r.tipo());

                    return new NovedadesNomina(
                            r.idEmpleado(),
                            e != null ? e.nombre() : "DESCONOCIDO",
                            e != null && e.area() != null ? e.area().name() : "SIN ÁREA",
                            r.tipo() != null ? r.tipo().name() : "DESCONOCIDO",
                            r.fecha(),
                            r.horas(),
                            salarioHora,
                            totalPagar
                    );
                })
                .sorted(Comparator
                        .comparing(NovedadesNomina::fecha)
                        .thenComparing(NovedadesNomina::area)
                        .thenComparing(NovedadesNomina::nombreEmpleado)
                )
                .collect(Collectors.toList());
    }

    public Map<String, Map<String, Double>> calcularDesgloseHorasPorAreaYTipoTurno() {
        return obtenerNovedadesNomina().stream()
                .collect(groupingBy(
                        NovedadesNomina::area,
                        groupingBy(
                                NovedadesNomina::tipoTurno,
                                Collectors.summingDouble(NovedadesNomina::horasTrabajadas)
                        )
                ));
    }

    public Map<String, Double> calcularNominaMensualDetalladaConInconsistencias() {
        return registrosMes.stream()
                .map(r -> {
                    Empleado emp = empleados.stream()
                            .filter(e -> e.id().equals(r.idEmpleado()))
                            .findFirst()
                            .orElse(null);

                    String nombre = emp != null ? emp.nombre() : "ID " + r.idEmpleado() + " inexistente";
                    double total = r.horas() * (emp != null ? emp.salarioBaseHora() : 0) * getMultiplicador(r.tipo());
                    return Map.entry(nombre, total);
                })
                .filter(entry -> entry.getValue() > 0)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        Double::sum,
                        TreeMap::new
                ));
    }

    /**
     * Calcula un Map con totales por empleado, excluyendo los registros cuyo tipoTurno sea AUSENCIA.
     * Solo empleados válidos y totales > 0. Ordena por nombre.
     */
    public Map<String, Double> calcularNominaMensualMap() {
        return registrosMes.stream()
                .filter(r -> r.tipo() != TipoTurno.AUSENCIA) // <-- Excluye AUSENCIA
                .map(r -> {
                    Empleado emp = empleados.stream()
                            .filter(e -> e.id().equals(r.idEmpleado()))
                            .findFirst()
                            .orElse(null);

                    String nombre = (emp != null) ? emp.nombre() : "ID " + r.idEmpleado() + " inexistente";
                    double total = r.horas() * ((emp != null) ? emp.salarioBaseHora() : 0) * getMultiplicador(r.tipo());
                    return Map.entry(nombre, total);
                })
                .filter(entry -> entry.getValue() > 0)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        Double::sum,
                        TreeMap::new // ordena por nombre
                ));
    }


    private double getMultiplicador(TipoTurno tipo) {
        if (tipo == null) return 1.0;
        return switch (tipo) {
            case GUARDIA -> 2.0;
            case NOCHE -> 1.5;
            case AUSENCIA -> 0.0;
            default -> 1.0;
        };
    }

    public NumberFormat getFormatoMoneda() {
        return formatoMoneda;
    }

    // ============================================================
    // ================== REPORTES (SOLO IMPRESIÓN) =============
    // ============================================================

    public void imprimirReporteGeneral(List<ConsolidadoNovedadesNomina> liquidaciones) {
        System.out.println("\n🧾 === LIQUIDACIÓN DE NÓMINA ===\n");
        System.out.printf("%-10s %-20s %-15s %20s %20s %20s%n",
                "ID", "Empleado", "Área", "Horas Trabajadas", "Valor Hora", "Total a Pagar");
        System.out.println("--------------------------------------------------------------------------------------------------------------");

        liquidaciones.forEach(liq -> System.out.printf(
                "%-10s %-20s %-15s %20.2f %20s %20s%n",
                liq.idEmpleado(),
                liq.nombreEmpleado(),
                liq.area(),
                liq.horasTrabajadas(),
                formatoMoneda.format(liq.salarioBaseHora()),
                formatoMoneda.format(liq.totalPagar())
        ));
        System.out.println("--------------------------------------------------------------------------------------------------------------\n");
    }

    public void imprimirReportePorArea(Map<String, List<ConsolidadoNovedadesNomina>> empleadosPorArea) {
        System.out.println("============================================");
        System.out.println("🏥  REPORTE DE LIQUIDACIÓN - EMPLEADOS POR ÁREA");
        System.out.println("============================================\n");

        empleadosPorArea.forEach((area, empleados) -> {
            System.out.printf("🔹 Área: %s%n", area);
            System.out.println("────────────────────────────────────────────");

            empleados.forEach(e -> System.out.printf(
                    "%-10s %-25s %15.2f %20s %22s%n",
                    e.idEmpleado(),
                    e.nombreEmpleado(),
                    e.horasTrabajadas(),
                    formatoMoneda.format(e.salarioBaseHora()),
                    formatoMoneda.format(e.totalPagar())
            ));

            System.out.println();
        });

        System.out.println("============================================");
        System.out.println("✅  Fin del reporte");
        System.out.println("============================================\n");
    }

    public void imprimirReporteNovedadesNomina(List<NovedadesNomina> novedades) {
        System.out.println("\n====================================================");
        System.out.println("📋  REPORTE DETALLADO DE NOVEDADES DE NÓMINA");
        System.out.println("====================================================\n");

        System.out.printf("%-10s %-22s %-15s %-12s %-8s %12s %20s %20s%n",
                "ID", "Empleado", "Área", "Tipo Turno", "% Turno", "Horas", "Salario/Hora", "Total a Pagar");
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------");

        novedades.forEach(nov -> {
            double porcentaje = getMultiplicador(TipoTurno.valueOf(nov.tipoTurno())) * 100;
            System.out.printf(
                    "%-10s %-22s %-15s %-12s %-8.0f %12.2f %20s %20s%n",
                    nov.idEmpleado(),
                    nov.nombreEmpleado(),
                    nov.area(),
                    nov.tipoTurno(),
                    porcentaje,
                    nov.horasTrabajadas(),
                    formatoMoneda.format(nov.salarioBaseHora()),
                    formatoMoneda.format(nov.totalPagar())
            );
        });

        System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("✅  Fin del reporte de novedades de nómina\n");
    }

    public void imprimirDesgloseHoras(Map<String, Map<String, Double>> desglose) {
        System.out.println("\n====================================================");
        System.out.println("🕒  DESGLOSE DE HORAS POR ÁREA Y TIPO DE TURNO");
        System.out.println("====================================================\n");

        desglose.forEach((area, tipos) -> {
            System.out.printf("🏥 Área: %s%n", area);
            tipos.forEach((tipo, horas) -> System.out.printf("   - %s: %.2f horas%n", tipo, horas));
            System.out.println();
        });

        System.out.println("====================================================");
        System.out.println("✅  Fin del desglose de horas\n");
    }

    public void imprimirReporteNomina(Map<String, Double> totales) {
        System.out.println("\n====================================================");
        System.out.println("💰 Valor a Pagar Nómina (Map funcional)");
        System.out.println("----------------------------------------------------");
        System.out.printf("%-25s %20s%n", "Nombre Empleado", "Total Devengado");
        System.out.println("----------------------------------------------------");

        totales.forEach((nombre, total) ->
                System.out.printf("%-25s %20s%n", nombre, formatoMoneda.format(total))
        );

        System.out.println("----------------------------------------------------");
        System.out.println("✅  Fin del reporte de nómina mensual (Map funcional)\n");
    }

    // ============================================================
    // ================== EJECUTAR TODOS LOS REPORTES ============
    // ============================================================

    public void ejecutarReportes() {
        List<ConsolidadoNovedadesNomina> liquidaciones = calcularLiquidacionPorEmpleado();
        imprimirReporteGeneral(liquidaciones);

        Map<String, List<ConsolidadoNovedadesNomina>> porArea = agruparEmpleadosPorArea();
        imprimirReportePorArea(porArea);

        List<NovedadesNomina> novedades = obtenerNovedadesNomina();
        imprimirReporteNovedadesNomina(novedades);

        Map<String, Map<String, Double>> desglose = calcularDesgloseHorasPorAreaYTipoTurno();
        imprimirDesgloseHoras(desglose);

        Map<String, Double> totalesMap = calcularNominaMensualMap();
        imprimirReporteNomina(totalesMap);
    }
}
