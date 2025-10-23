package com.clinica.nomina.model;
public record EmpleadoConBonus(
        String idEmpleado,
        String nombre,
        Area area,
        double horasTrabajadas,
        double valorHora,
        double totalDevengado,
        double bonus,
        double totalConBonus
) { }
