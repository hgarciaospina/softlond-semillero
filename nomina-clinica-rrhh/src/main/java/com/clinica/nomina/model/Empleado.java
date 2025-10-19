package com.clinica.nomina.model;

/**
 * Representa un empleado con su nombre y área de trabajo.
 *
 * @param id Identificador único del empleado.
 * @param nombre Nombre completo del empleado.
 * @param area Área o departamento donde trabaja.
 * @param salarioBaseHora valor base a pagar por hora
 */
public record Empleado(String id, String nombre, Area area, double salarioBaseHora) {}
