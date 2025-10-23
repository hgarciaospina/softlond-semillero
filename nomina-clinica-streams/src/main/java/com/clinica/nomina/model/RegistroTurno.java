package com.clinica.nomina.model;

import java.time.LocalDate;

public record RegistroTurno(String idEmpleado, LocalDate fecha, TipoTurno tipo, int horas) {}
