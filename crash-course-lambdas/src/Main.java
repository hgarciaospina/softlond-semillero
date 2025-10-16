import clase01.FakerUtils;
import clase01.Persona;
import clase01.comparators.ComparadorPorLongitudDeNombre;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Persona> listaPersonas = generarPersonas(100);
        listaPersonas.add(new Persona("Lo", "Hidalgo"));
        System.out.println("Antes de ordenar la lista");
        System.out.println(listaPersonas);
        System.out.println("Después de ordenar la lista");

        listaPersonas.sort((o1, o2) -> Integer.compare(o1.getNombre().length(), o2.getNombre().length()));

        System.out.println(listaPersonas);
    }

    private static List<Persona> generarPersonas(int p) {
        List<Persona> personas = new ArrayList<>();

        for (int i = 0; i < p; i++) {
            String nombre = FakerUtils.generarNombre();
            String apellido = FakerUtils.generarApellido();
            personas.add(new Persona(nombre, apellido));
        }

        return personas;
    }
}