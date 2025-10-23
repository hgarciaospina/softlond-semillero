package com.clinica.nomina.service;

import com.clinica.nomina.model.NovedadesNomina;
import com.clinica.nomina.model.TipoTurno;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
                // Filtrar solo DIA o GUARDIA de 24h
                .filter(n -> n.tipoTurno().equals(TipoTurno.DIA.name()) ||
                        (n.tipoTurno().equals(TipoTurno.GUARDIA.name()) && n.horasTrabajadas() == 24))
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
     * una GUARDIA de 24 horas seguida inmediatamente por un DIA.
     *
     * Optimizado: solo un recorrido de la lista.
     */
    private boolean tieneTurnosAnormales(List<NovedadesNomina> turnosEmpleado) {
        // Ordenar por fecha
        List<NovedadesNomina> ordenados = turnosEmpleado.stream()
                .sorted(Comparator.comparing(NovedadesNomina::fecha))
                .toList();

        // Usar IntStream para recorrer índices y comparar cada turno con el siguiente
        return IntStream.range(0, ordenados.size() - 1)
                .anyMatch(i -> {
                    NovedadesNomina actual = ordenados.get(i);
                    NovedadesNomina siguiente = ordenados.get(i + 1);
                    return actual.tipoTurno().equals(TipoTurno.GUARDIA.name()) &&
                            actual.horasTrabajadas() == 24 &&
                            siguiente.tipoTurno().equals(TipoTurno.DIA.name()) &&
                            siguiente.fecha().equals(actual.fecha().plusDays(1));
                });
    }
}
