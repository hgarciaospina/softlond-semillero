/*
  3. Transformación de Datos: Define una lógica reutilizable que,
     para un empleado, devuelva su nombre en mayúsculas. Pruébala
     con el empleado 'Beto'.
* */

package servicios;

import modelo.Empleado;

/**
 * Contrato funcional para transformar un empleado en otro tipo de dato.
 */
@FunctionalInterface
public interface TransformacionEmpleado<T> {
    T aplicar(Empleado empleado);
}
