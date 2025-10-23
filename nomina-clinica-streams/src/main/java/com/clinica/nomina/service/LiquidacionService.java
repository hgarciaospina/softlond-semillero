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
 */
public class LiquidacionService {

    private final List<Empleado> empleados;
    private final List<RegistroTurno> registrosMes;
    private final NumberFormat formatoMoneda;

    public LiquidacionService(DatosRepository datosRepository) {
        this.empleados = Optional.ofNullable(datosRepository.obtenerEmpleados()).orElse(Collections.emptyList());
        this.registrosMes = Optional.ofNullable(datosRepository.obtenerRegistrosMes()).orElse(Collections.emptyList());
        this.formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
    }

    /**
     * Reconstruye la lista de NovedadesNomina a partir de los registros del mes
     * y la información de empleados.
     *
     * - Incluye AUSENCIA como tipo (pero puede ser filtrada por los servicios consumidores).
     * - El campo area y tipoTurno en NovedadesNomina se llenan con los nombres del enum (o "SIN_AREA"/"DESCONOCIDO").
     */
    public List<NovedadesNomina> obtenerNovedadesNomina() {
        return registrosMes.stream()
                .filter(Objects::nonNull)
                .filter(r -> r.idEmpleado() != null)
                .map(r -> {
                    Empleado e = empleados.stream()
                            .filter(emp -> emp != null && emp.id() != null && emp.id().equals(r.idEmpleado()))
                            .findFirst()
                            .orElse(null);

                    double salarioHora = e != null ? e.salarioBaseHora() : 0.0;
                    double multiplicador = getMultiplicador(r.tipo());
                    double totalPagar = r.horas() * salarioHora * multiplicador;

                    String areaStr = (e != null && e.area() != null) ? e.area().name() : "SIN_AREA";
                    String tipoStr = (r.tipo() != null) ? r.tipo().name() : "DESCONOCIDO";
                    String nombre = (e != null) ? e.nombre() : "DESCONOCIDO";

                    return new NovedadesNomina(
                            r.idEmpleado(),
                            nombre,
                            areaStr,
                            tipoStr,
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
     * Calcula el consolidado por empleado (lista de ConsolidadoNovedadesNomina)
     * a partir de las NovedadesNomina. Excluye registros de AUSENCIA.
     *
     * Este método mantiene compatibilidad con los reportes existentes.
     */
    public List<ConsolidadoNovedadesNomina> calcularLiquidacionPorEmpleado() {
        Map<String, Double> horasAjustadasPorEmpleado = obtenerNovedadesNomina().stream()
                .filter(n -> {
                    try {
                        TipoTurno t = TipoTurno.valueOf(n.tipoTurno());
                        return t != TipoTurno.AUSENCIA;
                    } catch (Exception ex) {
                        return true;
                    }
                })
                .collect(groupingBy(
                        NovedadesNomina::idEmpleado,
                        Collectors.summingDouble(n -> n.horasTrabajadas() * getMultiplicadorSafely(n.tipoTurno()))
                ));

        return empleados.stream()
                .filter(Objects::nonNull)
                .filter(e -> e.id() != null && horasAjustadasPorEmpleado.containsKey(e.id()))
                .map(e -> {
                    double horas = horasAjustadasPorEmpleado.getOrDefault(e.id(), 0.0);
                    double salarioHora = e.salarioBaseHora();
                    double totalPagar = horas * salarioHora;
                    String area = e.area() != null ? e.area().name() : "SIN_AREA";
                    String nombre = e.nombre() != null ? e.nombre() : "SIN_NOMBRE";

                    return new ConsolidadoNovedadesNomina(
                            e.id(),
                            nombre,
                            area,
                            horas,
                            salarioHora,
                            totalPagar
                    );
                })
                .sorted(Comparator.comparingDouble(ConsolidadoNovedadesNomina::totalPagar).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Agrupa el consolidado por área, útil para reportes por departamento.
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
        if (tipo == null) return 1.0;
        return switch (tipo) {
            case GUARDIA -> 2.0;
            case NOCHE -> 1.5;
            case AUSENCIA -> 0.0;
            default -> 1.0;
        };
    }

    private double getMultiplicadorSafely(String tipoStr) {
        if (tipoStr == null) return 1.0;
        try {
            return getMultiplicador(TipoTurno.valueOf(tipoStr));
        } catch (Exception ex) {
            return 1.0;
        }
    }

    public NumberFormat getFormatoMoneda() {
        return formatoMoneda;
    }
}
