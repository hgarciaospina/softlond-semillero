/*
 * 2. Regla de Negocio (Filtro): Define una regla reutilizable que determine si un empleado
 *    es "senior" (más de 5 años de servicio). Prueba esta regla con 'Ana' y 'Diana'.
*/

package servicios;

import modelo.Empleado;

/**
 * Contrato funcional para evaluar una condición de negocio sobre un empleado.
 */
@FunctionalInterface
public interface ReglaNegocio {
    boolean aplicar(Empleado empleado);
}
