package repositorio;

import modelo.Empleado;
import java.util.List;

/**
 * Repositorio simulado de empleados (datos en memoria, inmutables).
 */
public class EmpleadoRepository {

    /**
     * Retorna una lista fija de empleados simulando datos persistidos.
     */
    public List<Empleado> getAllEmployees() {
        return List.of(
                new Empleado("Ana", "TI", 75000, 5),
                new Empleado("Beto", "Marketing", 60000, 3),
                new Empleado("Carlos", "TI", 90000, 8),
                new Empleado("Diana", "Ventas", 55000, 2),
                new Empleado("Elena", "Marketing", 80000, 7),
                new Empleado("Fernando", "Ventas", 58000, 4),
                new Empleado("Gaby", "TI", 120000, 10)
        );
    }
}
