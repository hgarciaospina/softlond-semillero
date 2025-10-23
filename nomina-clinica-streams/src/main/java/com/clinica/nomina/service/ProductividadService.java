package com.clinica.nomina.service;

import com.clinica.nomina.model.*;
import com.clinica.nomina.repository.DatosRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio que calcula la productividad de los empleados
 * directamente desde los registros de turnos.
 *
 * - Filtra solo turnos que existan en la lista de empleados
 * - Excluye AUSENCIA
 * - Aplica multiplicador por tipo de turno
 */
public class ProductividadService {

    private final List<Empleado> empleados;
    private final List<RegistroTurno> registrosMes;

    public ProductividadService(DatosRepository datosRepository) {
        this.empleados = Optional.ofNullable(datosRepository.obtenerEmpleados()).orElse(Collections.emptyList());
        this.registrosMes = Optional.ofNullable(datosRepository.obtenerRegistrosMes()).orElse(Collections.emptyList());
    }

    /**
     * Calcula la productividad de los empleados
     * sumando horas ajustadas por multiplicador y calculando salario total.
     */
    public List<ProductividadEmpleado> calcularProductividad() {

        Map<String, Empleado> mapEmpleados = empleados.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Empleado::id, e -> e));

        return registrosMes.stream()
                // Filtrar empleados válidos
                .filter(r -> mapEmpleados.containsKey(r.idEmpleado()))
                // Filtrar turnos AUSENCIA
                .filter(r -> r.tipo() != TipoTurno.AUSENCIA)
                // Agrupar por idEmpleado sumando horas multiplicadas
                .collect(Collectors.groupingBy(
                        RegistroTurno::idEmpleado,
                        Collectors.summingDouble(r -> r.horas() * getMultiplicador(r.tipo()))
                ))
                .entrySet().stream()
                .map(entry -> {
                    Empleado e = mapEmpleados.get(entry.getKey());
                    double totalHorasMultiplicadas = entry.getValue();
                    double salarioTotal = totalHorasMultiplicadas * e.salarioBaseHora();
                    return new ProductividadEmpleado(
                            e.nombre(),
                            e.area(),
                            (int) Math.round(totalHorasMultiplicadas),
                            salarioTotal
                    );
                })
                .sorted(Comparator.comparingDouble(ProductividadEmpleado::salarioTotal).reversed())
                .toList();
    }

    /**
     * Multiplicador por tipo de turno
     */
    private double getMultiplicador(TipoTurno tipo) {
        if (tipo == null) return 1.0;
        return switch (tipo) {
            case GUARDIA -> 2.0;
            case NOCHE -> 1.5;
            case DIA -> 1.0;
            default -> 1.0;
        };
    }
}
