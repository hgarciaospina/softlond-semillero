package com.clinica.nomina.service;

import com.clinica.nomina.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio que calcula el bonus por disponibilidad de los empleados.
 *
 * Regla:
 * - Empleados que no tuvieron ninguna AUSENCIA en el mes
 * - Y trabajaron más de 40 horas totales
 * - Bonus = 5% sobre el total devengado
 */
public class BonusDisponibilidadService {

    private final List<NovedadesNomina> novedades;
    private final List<RegistroTurno> registrosMes;
    private final List<Empleado> empleados;

    public BonusDisponibilidadService(List<NovedadesNomina> novedades,
                                      List<Empleado> empleados,
                                      List<RegistroTurno> registrosMes) {
        this.novedades = novedades != null ? novedades : Collections.emptyList();
        this.empleados = empleados != null ? empleados : Collections.emptyList();
        this.registrosMes = registrosMes != null ? registrosMes : Collections.emptyList();
    }

    /**
     * Calcula la lista de empleados con bonus aplicable.
     *
     * @return lista de EmpleadoConBonus ordenada por totalConBonus descendente
     */
    public List<EmpleadoConBonus> calcularBonus() {
        // 1️⃣ IDs de empleados con alguna AUSENCIA
        Set<String> empleadosConAusencia = registrosMes.stream()
                .filter(r -> r.tipo() == TipoTurno.AUSENCIA)
                .map(RegistroTurno::idEmpleado)
                .collect(Collectors.toSet());

        // 2️⃣ Filtrar novedades excluyendo empleados con AUSENCIA
        List<NovedadesNomina> novedadesFiltradas = novedades.stream()
                .filter(n -> !empleadosConAusencia.contains(n.idEmpleado()))
                .collect(Collectors.toList());

        // 3️⃣ Sumar horas y total devengado por empleado
        Map<String, Double> horasPorEmpleado = novedadesFiltradas.stream()
                .collect(Collectors.groupingBy(
                        NovedadesNomina::idEmpleado,
                        Collectors.summingDouble(NovedadesNomina::horasTrabajadas)
                ));

        Map<String, Double> totalDevengadoPorEmpleado = novedadesFiltradas.stream()
                .collect(Collectors.groupingBy(
                        NovedadesNomina::idEmpleado,
                        Collectors.summingDouble(NovedadesNomina::totalPagar)
                ));

        // 4️⃣ Construir objetos EmpleadoConBonus
        return empleados.stream()
                .filter(e -> horasPorEmpleado.getOrDefault(e.id(), 0.0) > 40) // Solo los que trabajaron >40h
                .map(e -> {
                    double horas = horasPorEmpleado.getOrDefault(e.id(), 0.0);
                    double totalDevengado = totalDevengadoPorEmpleado.getOrDefault(e.id(), 0.0);
                    double bonus = totalDevengado * 0.05; // 5% bonus
                    double totalConBonus = totalDevengado + bonus;

                    return new EmpleadoConBonus(
                            e.id(),
                            e.nombre(),
                            e.area(),
                            horas,
                            e.salarioBaseHora(),
                            totalDevengado,
                            bonus,
                            totalConBonus
                    );
                })
                .sorted((a, b) -> Double.compare(b.totalConBonus(), a.totalConBonus()))
                .toList();
    }
}
