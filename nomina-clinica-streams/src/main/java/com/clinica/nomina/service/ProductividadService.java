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
 * - Mantiene horas reales, sin multiplicar por ningún factor
 */
public class ProductividadService {

    private final List<Empleado> empleados;
    private final List<RegistroTurno> registrosMes;

    public ProductividadService(DatosRepository datosRepository) {
        this.empleados = Optional.ofNullable(datosRepository.obtenerEmpleados())
                .orElse(Collections.emptyList());
        this.registrosMes = Optional.ofNullable(datosRepository.obtenerRegistrosMes())
                .orElse(Collections.emptyList());
    }

    /**
     * Calcula la productividad de los empleados
     * sumando horas reales y calculando salario total.
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
                // Agrupar por idEmpleado sumando horas reales
                .collect(Collectors.groupingBy(
                        RegistroTurno::idEmpleado,
                        Collectors.summingDouble(RegistroTurno::horas)
                ))
                .entrySet().stream()
                .map(entry -> {
                    Empleado e = mapEmpleados.get(entry.getKey());
                    double totalHoras = entry.getValue();
                    double salarioTotal = totalHoras * e.salarioBaseHora(); // solo multiplicar por salario base
                    return new ProductividadEmpleado(
                            e.nombre(),
                            e.area(),
                            (int) Math.round(totalHoras),
                            salarioTotal
                    );
                })
                .sorted(Comparator.comparingDouble(ProductividadEmpleado::salarioTotal).reversed())
                .toList();
    }
}
