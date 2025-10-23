package com.clinica.nomina.service;

import com.clinica.nomina.model.*;
import com.clinica.nomina.repository.DatosRepository;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * Servicio encargado de construir la lista de NovedadesNomina
 * (base para varios reportes) y adicionalmente producir
 * consolidado por empleado cuando se requiera.
 *
 * ✅ Totalmente funcional, sin programación imperativa ni try/catch
 * ✅ Lógica de bonusDisponibilidad: true solo si horasTrabajadas > 40 y sin AUSENCIA
 */
public class LiquidacionService {

    private final List<Empleado> empleados;
    private final List<RegistroTurno> registrosMes;
    private final NumberFormat formatoMoneda;

    public LiquidacionService(DatosRepository datosRepository) {
        this.empleados = Optional.ofNullable(datosRepository.obtenerEmpleados())
                .orElse(Collections.emptyList());
        this.registrosMes = Optional.ofNullable(datosRepository.obtenerRegistrosMes())
                .orElse(Collections.emptyList());
        this.formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
    }

    /**
     * Reconstruye la lista de NovedadesNomina a partir de los registros del mes
     * y la información de empleados.
     */
    public List<NovedadesNomina> obtenerNovedadesNomina() {
        return registrosMes.stream()
                .filter(r -> r != null && r.idEmpleado() != null)
                .map(r -> empleados.stream()
                        .filter(e -> e != null && e.id() != null && e.id().equals(r.idEmpleado()))
                        .findFirst()
                        .map(e -> {
                            double salarioHora = e.salarioBaseHora();
                            double multiplicador = getMultiplicador(r.tipo());
                            double totalPagar = r.horas() * salarioHora * multiplicador;

                            return new NovedadesNomina(
                                    r.idEmpleado(),
                                    Optional.ofNullable(e.nombre()).orElse("DESCONOCIDO"),
                                    Optional.ofNullable(e.area()).map(Area::name).orElse("SIN_AREA"),
                                    Optional.ofNullable(r.tipo()).map(TipoTurno::name).orElse("DESCONOCIDO"),
                                    r.fecha(),
                                    r.horas(), // horas reales
                                    salarioHora,
                                    totalPagar
                            );
                        })
                        .orElse(new NovedadesNomina(
                                r.idEmpleado(),
                                "DESCONOCIDO",
                                "SIN_AREA",
                                Optional.ofNullable(r.tipo()).map(TipoTurno::name).orElse("DESCONOCIDO"),
                                r.fecha(),
                                r.horas(),
                                0.0,
                                0.0
                        ))
                )
                .sorted(Comparator
                        .comparing(NovedadesNomina::fecha)
                        .thenComparing(NovedadesNomina::area)
                        .thenComparing(NovedadesNomina::nombreEmpleado)
                )
                .collect(Collectors.toList());
    }

    /**
     * Calcula el consolidado por empleado.
     * Establece bonusDisponibilidad según reglas:
     * true solo si horasTrabajadas > 40 y no tiene AUSENCIA.
     */
    public List<ConsolidadoNovedadesNomina> calcularLiquidacionPorEmpleado() {

        Map<String, List<NovedadesNomina>> novedadesPorEmpleado = obtenerNovedadesNomina().stream()
                .collect(groupingBy(NovedadesNomina::idEmpleado));

        return empleados.stream()
                .filter(Objects::nonNull)
                .map(e -> {
                    List<NovedadesNomina> novedades = novedadesPorEmpleado.getOrDefault(e.id(), Collections.emptyList());

                    double horas = novedades.stream()
                            .mapToDouble(NovedadesNomina::horasTrabajadas)
                            .sum();

                    double totalPagar = novedades.stream()
                            .mapToDouble(n -> n.horasTrabajadas() * n.salarioBaseHora() *
                                    Arrays.stream(TipoTurno.values())
                                            .filter(t -> t.name().equalsIgnoreCase(n.tipoTurno()))
                                            .findFirst()
                                            .map(this::getMultiplicador)
                                            .orElse(1.0)
                            )
                            .sum();

                    // Determinar bonusDisponibilidad según regla nueva
                    boolean tieneAusencia = novedades.stream()
                            .anyMatch(n -> TipoTurno.AUSENCIA.name().equalsIgnoreCase(n.tipoTurno()));
                    boolean bonusDisponibilidad = horas > 40 && !tieneAusencia;

                    NovedadesNomina primerN = novedades.stream().findFirst().orElse(null);
                    String nombre = primerN != null ? primerN.nombreEmpleado() : "DESCONOCIDO";
                    String area = primerN != null ? primerN.area() : "SIN_AREA";
                    double salarioBaseHora = primerN != null ? primerN.salarioBaseHora() : 0.0;

                    return new ConsolidadoNovedadesNomina(
                            e.id(),
                            nombre,
                            area,
                            horas,
                            salarioBaseHora,
                            totalPagar,
                            bonusDisponibilidad
                    );
                })
                .sorted(Comparator.comparingDouble(ConsolidadoNovedadesNomina::totalPagar).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Agrupa el consolidado por área.
     */
    public Map<String, List<ConsolidadoNovedadesNomina>> agruparPorArea() {
        return calcularLiquidacionPorEmpleado().stream()
                .collect(groupingBy(
                        ConsolidadoNovedadesNomina::area,
                        TreeMap::new,
                        Collectors.toList()
                ));
    }

    /* --- Helpers --- */

    private double getMultiplicador(TipoTurno tipo) {
        return Optional.ofNullable(tipo)
                .map(t -> switch (t) {
                    case GUARDIA -> 2.0;
                    case NOCHE -> 1.5;
                    case AUSENCIA -> 0.0;
                    default -> 1.0;
                })
                .orElse(1.0);
    }

    public NumberFormat getFormatoMoneda() {
        return formatoMoneda;
    }
}
