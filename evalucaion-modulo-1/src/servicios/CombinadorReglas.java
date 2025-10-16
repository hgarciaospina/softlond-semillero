/*
   7. Combinación de Reglas: Combina dos condiciones: una para empleados
      del área 'Marketing' y otra para empleados con un salario
      superior a 70000, para crear una única regla que identifique
      a los empleados que cumplen ambos criterios. Pruébala con 'Elena' y 'Beto'.
 */

package servicios;

/**
 * Permite combinar reglas de negocio usando composición funcional.
 */
public class CombinadorReglas {

    /**
     * Combina dos reglas con AND lógico.
     */
    public static ReglaNegocio y(ReglaNegocio r1, ReglaNegocio r2) {
        return empleado -> r1.aplicar(empleado) && r2.aplicar(empleado);
    }
}
