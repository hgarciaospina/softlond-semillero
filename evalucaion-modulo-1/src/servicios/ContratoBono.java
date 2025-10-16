/*
6. Contrato Funcional a Medida: Define un 'contrato' personalizado
   para calcular bonos. Este contrato debe tener un único método abstracto
   llamado `calcular` que reciba un empleado y devuelva un `double`.
   Luego, implementa este contrato para calcular un bono del 10%
   del salario por cada año de servicio y pruébalo con 'Gaby'.
 */

package servicios;

import modelo.Empleado;

/**
 * Contrato funcional para calcular un bono a partir de un empleado.
 */
@FunctionalInterface
public interface ContratoBono {
    double calcular(Empleado empleado);
}
