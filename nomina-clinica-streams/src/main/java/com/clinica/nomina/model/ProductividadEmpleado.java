package com.clinica.nomina.model;

import com.clinica.nomina.model.Area;

/**
 * Representa la productividad de un empleado.
 *
 * - nombre: nombre completo del empleado
 * - area: área a la que pertenece
 * - totalHoras: suma de horas trabajadas (excluye ausencias)
 * - salarioTotal: total devengado (horas * salario hora)
 * - En el serviciose plica el factor multiplicador de turno
 */
public record ProductividadEmpleado(
        String nombre,
        Area area,
        int totalHoras,
        double salarioTotal
) {}
