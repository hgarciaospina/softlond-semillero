package com.clinica.nomina.service;

import com.clinica.nomina.model.Empleado;
import com.clinica.nomina.model.RegistroTurno;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio que detecta inconsistencias de datos:
 * IDs de empleados presentes en registrosMes que no existen en la lista de personal.
 */
public class InconsistenciasDatosService {

    private final List<Empleado> empleados;
    private final List<RegistroTurno> registrosMes;

    public InconsistenciasDatosService(List<Empleado> empleados, List<RegistroTurno> registrosMes) {
        this.empleados = Optional.ofNullable(empleados).orElse(Collections.emptyList());
        this.registrosMes = Optional.ofNullable(registrosMes).orElse(Collections.emptyList());
    }

    /**
     * Retorna la lista de IDs de empleados presentes en registrosMes
     * pero que no están en la lista de personal.
     */
    public List<String> detectarInconsistencias() {

        Set<String> idsEmpleadosValidos = empleados.stream()
                .map(Empleado::id)
                .collect(Collectors.toSet());

        return registrosMes.stream()
                .map(RegistroTurno::idEmpleado)
                .filter(Objects::nonNull)
                .filter(id -> !idsEmpleadosValidos.contains(id))
                .distinct()
                .sorted()
                .toList();
    }
}
