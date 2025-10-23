package com.clinica.nomina.model;

/**
 * Representa la liquidación de nómina individual por empleado.
 */
public record ConsolidadoNovedadesNomina(
        String idEmpleado,
        String nombreEmpleado,
        String area,
        double horasTrabajadas,
        double salarioBaseHora,
        double totalPagar
) {}
