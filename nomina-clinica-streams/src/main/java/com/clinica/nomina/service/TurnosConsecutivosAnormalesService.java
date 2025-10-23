package com.clinica.nomina.service;

import com.clinica.nomina.model.NovedadesNomina;
import com.clinica.nomina.model.TipoTurno;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio que detecta turnos consecutivos anormales:
 * empleados que trabajaron un turno DÍA inmediatamente
 * después de una GUARDIA de 24 horas.
 *
 * ✅ Solo considera empleados existentes en la lista de Empleado.
 */
public class TurnosConsecutivosAnormalesService {

    private final LiquidacionService liquidacionService;

    public TurnosConsecutivosAnormalesService(LiquidacionService liquidacionService) {
        this.liquidacionService = liquidacionService;
    }

    /**
     * Lista de nombres de empleados con turnos consecutivos anormales.
     */
    public List<String> detectarTurnosConsecutivosAnormales() {

        // IDs válidos de empleados
        Set<String> empleadosValidos = liquidacionService.obtenerNovedadesNomina().stream()
                .map(NovedadesNomina::idEmpleado)
                .collect(Collectors.toSet());

        return liquidacionService.obtenerNovedadesNomina().stream()
                // Filtrar solo empleados válidos
                .filter(n -> empleadosValidos.contains(n.idEmpleado()))
                // Filtrar solo DIA o GUARDIA
                .filter(n -> n.tipoTurno().equals(TipoTurno.DIA.name()) || n.tipoTurno().equals(TipoTurno.GUARDIA.name()))
                // Agrupar por idEmpleado
                .collect(Collectors.groupingBy(NovedadesNomina::idEmpleado))
                .entrySet().stream()
                // Filtrar solo empleados con turnos consecutivos anormales
                .filter(entry -> tieneTurnosAnormales(entry.getValue()))
                // Mapear al nombre del empleado
                .map(entry -> entry.getValue().stream()
                        .map(NovedadesNomina::nombreEmpleado)
                        .findFirst()
                        .orElse("Empleado desconocido"))
                .sorted()
                .toList();
    }

    /**
     * Verifica si la lista de NovedadesNomina de un empleado contiene
     * una GUARDIA seguida inmediatamente por un DIA.
     */
    private boolean tieneTurnosAnormales(List<NovedadesNomina> turnosEmpleado) {
        // Ordenar por fecha
        List<NovedadesNomina> ordenados = turnosEmpleado.stream()
                .sorted(Comparator.comparing(NovedadesNomina::fecha))
                .toList();

        // Detectar si hay GUARDIA seguida de DIA
        return ordenados.stream()
                .filter(n -> n.tipoTurno().equals(TipoTurno.GUARDIA.name()))
                .anyMatch(guardia -> ordenados.stream()
                        .anyMatch(dia ->
                                dia.tipoTurno().equals(TipoTurno.DIA.name()) &&
                                        dia.fecha().equals(guardia.fecha().plusDays(1)) &&
                                        dia.idEmpleado().equals(guardia.idEmpleado())
                        ));
    }
}
