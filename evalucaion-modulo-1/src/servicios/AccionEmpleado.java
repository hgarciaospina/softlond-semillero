/*
   4. Acción sobre Datos: Implementa una acción que, al recibir un empleado, simule el
      envío de un correo de aniversario, imprimiendo "Feliz aniversario, [nombre]!".
      Aplícala al empleado 'Carlos'.
 */

package servicios;

import modelo.Empleado;

/**
 * Contrato funcional que ejecuta una acción sobre un empleado (efecto controlado).
 */
@FunctionalInterface
public interface AccionEmpleado {
    void ejecutar(Empleado empleado);
}
