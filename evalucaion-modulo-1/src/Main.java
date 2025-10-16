import modelo.Empleado;
import repositorio.EmpleadoRepository;
import servicios.*;

import java.util.function.Function;

/**
 * Demostración de programación funcional e inmutabilidad aplicada al dominio Empleado.
 */
public class Main {
    public static void main(String[] args) {

        EmpleadoRepository repo = new EmpleadoRepository();
        var empleados = repo.getAllEmployees();

        // ===============================
        // 2️⃣ Regla de Negocio - "Senior"
        // ===============================
        ReglaNegocio esSenior = e -> e.aniosDeServicio() > 5;

        System.out.println("Ana es senior? " + esSenior.aplicar(empleados.get(0)));   // false
        System.out.println("Diana es senior? " + esSenior.aplicar(empleados.get(3))); // false

        // ===============================
        // 3️⃣ Transformación de Datos - nombre en mayúsculas
        // ===============================
        TransformacionEmpleado<String> nombreEnMayusculas = e -> e.nombre().toUpperCase();
        System.out.println("Nombre de Beto en mayúsculas: " + nombreEnMayusculas.aplicar(empleados.get(1)));

        // ===============================
        // 4️⃣ Acción sobre Datos - Envío de correo
        // ===============================
        AccionEmpleado enviarCorreoAniversario = e ->
                System.out.println("Feliz aniversario, " + e.nombre() + "!");
        enviarCorreoAniversario.ejecutar(empleados.get(2)); // Carlos

        // ===============================
        // 5️⃣ Generador de Empleado "Becario"
        // ===============================
        GeneradorEmpleado generadorBecario = () -> new Empleado("Becario", "N/A", 15000, 0);
        Empleado becario = generadorBecario.generar();
        System.out.println("Empleado generado: " + becario);

        // ===============================
        // 6️⃣ Contrato Funcional de Bono
        // ===============================
        ContratoBono bonoAnual = e -> e.salario() * 0.10 * e.aniosDeServicio();
        Empleado gaby = empleados.get(6);
        System.out.println("Bono de Gaby: $" + bonoAnual.calcular(gaby));

        // ===============================
        // 7️⃣ Combinación de Reglas
        // ===============================
        ReglaNegocio esMarketing = e -> e.area().equals("Marketing");
        ReglaNegocio salarioMayorA70000 = e -> e.salario() > 70000;
        ReglaNegocio marketingYSueldoAlto = CombinadorReglas.y(esMarketing, salarioMayorA70000);

        System.out.println("Elena cumple regla combinada? " + marketingYSueldoAlto.aplicar(empleados.get(4))); // true
        System.out.println("Beto cumple regla combinada? " + marketingYSueldoAlto.aplicar(empleados.get(1)));  // false

        // ===============================
        // 8️⃣ Composición de Transformaciones
        // ===============================
        Function<Empleado, Double> aumento10 = e -> e.salario() * 1.10;
        Function<Double, String> formatear = nuevoSalario -> "Nuevo Salario: $" + String.format("%.2f", nuevoSalario);
        Function<Empleado, String> aumentoYFormato = aumento10.andThen(formatear);

        System.out.println("Fernando → " + aumentoYFormato.apply(empleados.get(5)));

        // ===============================
        // 9️⃣ Demostración de Pureza
        // ===============================
        System.out.println("\nDemostración de Pureza:");
        double nuevoSalario = calcularProximoAumentoPuro(empleados.get(0), 0.15);
        System.out.println("Salario nuevo (simulado): $" + nuevoSalario);
        System.out.println("Empleado original: " + empleados.get(0)); // sin cambios

        // ===============================
        // 🔟 Operación Inmutable (withers)
        // ===============================
        System.out.println("\nDemostración de inmutabilidad:");
        Empleado ana = empleados.get(0);
        Empleado anaPromocionada = ana.withArea("Gerencia de TI").withSalario(95000);
        System.out.println("Original: " + ana);
        System.out.println("Promocionada: " + anaPromocionada);
    }

    /**
     * Método puro: no modifica el empleado original.
     * Retorna el salario con el aumento aplicado.
     */
    public static double calcularProximoAumentoPuro(Empleado e, double porcentaje) {
        return e.salario() * (1 + porcentaje);
    }
}
