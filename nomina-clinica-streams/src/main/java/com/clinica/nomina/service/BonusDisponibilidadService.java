package com.clinica.nomina.service;

import com.clinica.nomina.model.ConsolidadoNovedadesNomina;
import com.clinica.nomina.model.EmpleadoConBonus;
import com.clinica.nomina.model.Area;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que calcula la lista de empleados que reciben bonus por disponibilidad
 * a partir del consolidado de nómina.
 *
 * Regla:
 * - bonusDisponibilidad = true en ConsolidadoNovedadesNomina
 */
public class BonusDisponibilidadService {

    private final List<ConsolidadoNovedadesNomina> consolidado;

    public BonusDisponibilidadService(List<ConsolidadoNovedadesNomina> consolidado) {
        this.consolidado = consolidado;
    }

    /**
     * Genera la lista de EmpleadoConBonus con monto calculado (5% del totalPagar)
     * solo para los empleados que tienen bonusDisponibilidad = true.
     */
    public List<EmpleadoConBonus> calcularBonus() {
        return consolidado.stream()
                // Solo empleados que cumplen la regla
                .filter(ConsolidadoNovedadesNomina::bonusDisponibilidad)
                .map(c -> {
                    double bonus = c.totalPagar() * 0.05;
                    double totalConBonus = c.totalPagar() + bonus;

                    // Conversión segura de String a Area enum
                    Area areaEnum = Arrays.stream(Area.values())
                            .filter(a -> a.name().equalsIgnoreCase(c.area()))
                            .findFirst()
                            .orElse(null); // null si no hay coincidencia

                    return new EmpleadoConBonus(
                            c.idEmpleado(),
                            c.nombreEmpleado(),
                            areaEnum,
                            c.horasTrabajadas(),
                            c.salarioBaseHora(),
                            c.totalPagar(),
                            bonus,
                            totalConBonus
                    );
                })
                .collect(Collectors.toList());
    }
}
