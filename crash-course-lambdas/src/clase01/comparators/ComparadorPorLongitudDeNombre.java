package clase01.comparators;

import clase01.Persona;
import java.util.Comparator;

public class ComparadorPorLongitudDeNombre implements Comparator<Persona> {
    @Override
    public int compare(Persona o1, Persona o2) {
        return Comparator.comparingInt((Persona p) -> p.getNombre().length())
                .compare(o1, o2);
    }
}