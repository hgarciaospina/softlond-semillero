package com.clinica.nomina.service;

import com.clinica.nomina.model.*;
import com.clinica.nomina.repository.DatosRepository;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * Servicio encargado de calcular la liquidación de nómina individual por empleado,
 * así como generar las novedades detalladas de la nómina mensual.
 */
public class LiquidacionService {

    private final List<Empleado> empleados;
    private final List<RegistroTurno> registrosMes;
    private final NumberFormat formatoMoneda;

    public LiquidacionService(DatosRepository datosRepository) {
        this.empleados = datosRepository.obtenerEmpleados();
        this.registrosMes = datosRepository.obtenerRegistrosMes();
        this.formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
    }

    /**
     * Genera la lista de novedades de nómina para todos los empleados,
     * incluyendo información de área, tipo de turno, horas trabajadas y valor total.
     */
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

    /**
     * Calcula la liquidación de nómina agrupando los turnos por empleado
     * y multiplicando las horas totales ponderadas por el valor hora base.
     */
    public List<ConsolidadoNovedadesNomina> calcularLiquidacionPorEmpleado() {
        Map<String, Double> horasPorEmpleado = registrosMes.stream()
                .filter(r -> r.tipo() != TipoTurno.AUSENCIA)
                .collect(groupingBy(
                        RegistroTurno::idEmpleado,
                        Collectors.summingDouble(r -> r.horas() * getMultiplicador(r.tipo()))
                ));

        return empleados.stream()
                .filter(e -> horasPorEmpleado.containsKey(e.id()))
                .map(e -> {
                    double horas = horasPorEmpleado.getOrDefault(e.id(), 0.0);
                    double total = horas * e.salarioBaseHora();
                    return new ConsolidadoNovedadesNomina(
                            e.id(),
                            e.nombre(),
                            e.area().name(),
                            horas,
                            e.salarioBaseHora(),
                            total
                    );
                })
                .sorted(Comparator.comparingDouble(ConsolidadoNovedadesNomina::totalPagar).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Devuelve un agrupamiento por área, útil para reportes detallados.
     */
    public Map<String, List<ConsolidadoNovedadesNomina>> agruparPorArea() {
        return calcularLiquidacionPorEmpleado().stream()
                .collect(groupingBy(ConsolidadoNovedadesNomina::area, TreeMap::new, Collectors.toList()));
    }

    /**
     * Determina el factor multiplicador según el tipo de turno.
     */
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
}
