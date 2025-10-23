package com.clinica.nomina.model;

import java.time.LocalDate;

/**
 * Representa la liquidación de nómina individual por empleado.
 */
public record NovedadesNomina(
        String idEmpleado,
        String nombreEmpleado,
        String area,
        String tipoTurno,
        LocalDate fecha,
        double horasTrabajadas,
        double salarioBaseHora,
        double totalPagar
) {}
