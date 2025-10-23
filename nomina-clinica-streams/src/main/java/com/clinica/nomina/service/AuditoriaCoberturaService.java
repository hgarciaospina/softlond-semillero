package com.clinica.nomina.service;

import com.clinica.nomina.model.NovedadesNomina;
import com.clinica.nomina.model.TipoTurno;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio encargado de detectar días con cobertura insuficiente
 * de empleados en turnos de GUARDIA.
 */
public class AuditoriaCoberturaService {

    private final List<NovedadesNomina> novedadesNomina;

    public AuditoriaCoberturaService(List<NovedadesNomina> novedadesNomina) {
        this.novedadesNomina = Optional.ofNullable(novedadesNomina).orElse(Collections.emptyList());
    }

    /**
     * Devuelve la lista de fechas en las que hubo menos de 2 empleados
     * en turno de GUARDIA.
     */
    public List<LocalDate> fechasConCoberturaInsuficiente() {
        return novedadesNomina.stream()
                // Filtrar solo turnos GUARDIA
                .filter(n -> TipoTurno.GUARDIA.name().equals(n.tipoTurno()))
                // Agrupar por fecha sumando empleados distintos (por idEmpleado)
                .collect(Collectors.groupingBy(
                        NovedadesNomina::fecha,
                        Collectors.mapping(NovedadesNomina::idEmpleado, Collectors.toSet())
                ))
                .entrySet().stream()
                // Filtrar fechas con menos de 2 empleados
                .filter(entry -> entry.getValue().size() < 2)
                .map(Map.Entry::getKey)
                .sorted()
                .toList();
    }
}
