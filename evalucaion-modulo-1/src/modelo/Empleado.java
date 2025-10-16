package modelo;

/**
 * Representa un empleado inmutable.
 * Record = inmutabilidad automática (todos los campos son finales).
 */
public record Empleado(String nombre, String area, double salario, int aniosDeServicio) {

    /**
     * Método 'wither' que crea un nuevo empleado con un nuevo valor de área.
     */
    public Empleado withArea(String nuevaArea) {
        return new Empleado(this.nombre, nuevaArea, this.salario, this.aniosDeServicio);
    }

    /**
     * Método 'wither' que crea un nuevo empleado con un nuevo salario.
     */
    public Empleado withSalario(double nuevoSalario) {
        return new Empleado(this.nombre, this.area, nuevoSalario, this.aniosDeServicio);
    }
}
