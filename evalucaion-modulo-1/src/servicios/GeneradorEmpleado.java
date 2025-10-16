/*
*  5. Generador de Datos: Crea una pieza de código que pueda ser invocada
*      para generar un nuevo empleado "becario" con valores
*      por defecto (nombre "Becario", área "N/A", salario 15000,
*      0 años de servicio).
* */

package servicios;

import modelo.Empleado;

/**
 * Contrato funcional para generar empleados con valores por defecto.
 */
@FunctionalInterface
public interface GeneradorEmpleado {
    Empleado generar();
}
