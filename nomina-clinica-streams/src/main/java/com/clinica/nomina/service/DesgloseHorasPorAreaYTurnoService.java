package com.clinica.nomina.service;

import com.clinica.nomina.model.NovedadesNomina;
import com.clinica.nomina.model.TipoTurno;
import com.clinica.nomina.model.Area;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * Servicio que calcula el desglose total de horas trabajadas por Área y Tipo de Turno.
 *
 * ✅ Totalmente funcional:
 *    - Sin lógica imperativa
 *    - Sin try/catch
 *    - Filtra automáticamente los turnos con AUSENCIA o datos inválidos
 *
 * 🧩 Estructura devuelta:
 *    Map<Area, Map<TipoTurno, Integer>>
 */
public class DesgloseHorasPorAreaYTurnoService {

    private final LiquidacionService liquidacionService;

    public DesgloseHorasPorAreaYTurnoService(LiquidacionService liquidacionService) {
        this.liquidacionService = Objects.requireNonNull(liquidacionService);
    }

    /**
     * Calcula el desglose de horas trabajadas agrupado por Área y Tipo de Turno.
     *
     * 🔹 Reglas:
     *   - Excluye los turnos de tipo AUSENCIA.
     *   - Ignora registros con área o tipo de turno inválido.
     *   - Usa TreeMap para mantener el orden alfabético / ordinal.
     */
    public Map<Area, Map<TipoTurno, Integer>> calcularDesgloseHorasPorAreaYTipoTurno() {

        return liquidacionService.obtenerNovedadesNomina().stream()
                // Filtrar registros con datos válidos y convertirlos a enums de forma segura
                .flatMap(n -> convertirRegistroSeguro(n).stream())
                // Agrupar por área y tipo de turno (excluyendo AUSENCIA)
                .filter(entry -> entry.getKey().getValue() != TipoTurno.AUSENCIA)
                .collect(groupingBy(
                        e -> e.getKey().getKey(),  // Área
                        TreeMap::new,
                        groupingBy(
                                e -> e.getKey().getValue(),  // Tipo de turno
                                TreeMap::new,
                                Collectors.summingInt(e -> (int) Math.round(e.getValue()))
                        )
                ));
    }

    /**
     * Convierte un registro NovedadesNomina a un par ((Área, TipoTurno), horas) de forma funcional.
     * Si el área o tipo de turno no son válidos, retorna Stream.empty().
     */
    private Optional<Map.Entry<Map.Entry<Area, TipoTurno>, Double>> convertirRegistroSeguro(NovedadesNomina n) {
        return Optional.ofNullable(n)
                .flatMap(nov -> convertirArea(nov.area())
                        .flatMap(areaEnum -> convertirTipo(nov.tipoTurno())
                                .map(tipoEnum -> Map.entry(Map.entry(areaEnum, tipoEnum), nov.horasTrabajadas()))
                        )
                );
    }

    /**
     * Conversión segura de String a Enum Area usando Optional.
     */
    private Optional<Area> convertirArea(String areaStr) {
        return Optional.ofNullable(areaStr)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .flatMap(s -> Arrays.stream(Area.values())
                        .filter(a -> a.name().equalsIgnoreCase(s))
                        .findFirst());
    }

    /**
     * Conversión segura de String a Enum TipoTurno usando Optional.
     */
    private Optional<TipoTurno> convertirTipo(String tipoStr) {
        return Optional.ofNullable(tipoStr)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .flatMap(s -> Arrays.stream(TipoTurno.values())
                        .filter(t -> t.name().equalsIgnoreCase(s))
                        .findFirst());
    }
}