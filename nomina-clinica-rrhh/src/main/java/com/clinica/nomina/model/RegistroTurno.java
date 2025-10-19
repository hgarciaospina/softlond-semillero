package com.clinica.nomina.model;

import java.time.LocalDate;

/**
 * Representa un registro individual de turno trabajado por un empleado.
 *
 * @param idEmpleado Identificador único del empleado.
 * @param fecha Fecha del turno trabajado.
 * @param tipo Tipo de turno (DIA, NOCHE, GUARDIA, AUSENCIA).
 * @param horas Total de horas trabajadas en el turno.
 */
public record RegistroTurno(String idEmpleado, LocalDate fecha, TipoTurno tipo, int horas) {}
