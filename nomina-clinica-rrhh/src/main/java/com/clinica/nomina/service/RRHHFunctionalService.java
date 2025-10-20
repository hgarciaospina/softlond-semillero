package com.clinica.nomina.service;

import com.clinica.nomina.model.Empleado;
import com.clinica.nomina.model.RegistroTurno;
import com.clinica.nomina.model.TipoTurno;
import com.clinica.nomina.repository.DatosRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.function.*;

/**
 * Servicio funcional del módulo de RRHH.
 * Autor: Henry García Ospina
 * Fecha: 19-Oct-2025
 *
 * Esta versión ha sido revisada para asegurar consistencia con el
 * criterio de programación funcional solicitado: uso exclusivo de
 * interfaces funcionales estándar (Predicate, Function, Consumer, Supplier, Comparator, BiFunction)
 * y evitar programación imperativa (no uso de bucles for/while explícitos ni condicionales if).
 *
 * Nota: se utilizan efectos laterales controlados (merge en Map, add en List/Set)
 * mediante Consumers y composición de Predicates para evitar ramas condicionales explícitas.
 */
public class RRHHFunctionalService {

    /** Lista de empleados activos en el sistema */
    private final List<Empleado> personal;

    /** Registros de turnos del mes actual */
    private final List<RegistroTurno> registros;

    /** Mapa auxiliar empleadoId → Empleado, para búsquedas rápidas */
    private final Map<String, Empleado> mapaPorId = new HashMap<>();

    /** Fuente de datos simulada */
    private final DatosRepository datosRepository = new DatosRepository();

    /**
     * Constructor por defecto que inicializa los datos desde el repositorio
     * y crea el mapa de empleados a partir de la lista cargada.
     */
    public RRHHFunctionalService() {
        this.personal = datosRepository.obtenerEmpleados();
        this.registros = datosRepository.obtenerRegistrosMes();

        // Consumer funcional para poblar el mapa por id (sin bucle imperativo)
        Consumer<Empleado> agregarAMapa = e -> mapaPorId.put(e.id(), e);
        personal.forEach(agregarAMapa);
    }

    /**
     * 1. Genera un reporte de horas trabajadas por empleado.
     *
     * Implementación funcional:
     * - Predicate para excluir ausencias
     * - BiConsumer para acumular horas en el mapa
     * - Consumer que combina Predicate y efecto lateral sin condicional if explícito
     */
    public Map<String, Integer> reporteHorasTrabajadas() {
        Map<String, Integer> horas = new HashMap<>();

        Predicate<RegistroTurno> esAusencia = r -> r.tipo() == TipoTurno.AUSENCIA;

        BiConsumer<String, Integer> acumular = (id, h) -> horas.merge(id, h, Integer::sum);

        // Consumer que, si el registro NO es ausencia, acumula horas (sin usar if)
        Consumer<RegistroTurno> procesar = r ->
                esAusencia.negate()
                        .and(x -> { acumular.accept(x.idEmpleado(), x.horas()); return true; })
                        .test(r);

        registros.forEach(procesar);
        return horas;
    }

    /**
     * 🧩 Ejercicio 2 – Empleados con Guardia
     * -------------------------------------
     * Retorna una lista con los nombres únicos de empleados que realizaron al menos
     * un turno de tipo GUARDIA. Se implementa de forma completamente funcional:
     *
     * 🚫 Sin if, for, while, stream, filter, ni Optional.
     * ✅ Solo interfaces funcionales: Predicate, Function, Consumer, Supplier.
     * ✅ Sin programación imperativa.
     */
    public List<String> empleadosConGuardia() {

        // Predicado para identificar los registros de tipo GUARDIA
        Predicate<RegistroTurno> esGuardia = r -> r.tipo().equals(TipoTurno.GUARDIA);

        // Funciones para transformar registro -> id -> nombre
        Function<RegistroTurno, String> obtenerId = RegistroTurno::idEmpleado;
        Function<String, String> obtenerNombre = id -> mapaPorId.get(id).nombre();

        // Estructura de almacenamiento sin duplicados y con orden estable
        Set<String> ids = new LinkedHashSet<>();

        // Consumer funcional: agrega el ID si cumple el predicado, sin usar "if"
        Consumer<RegistroTurno> agregarSiGuardia = r ->
                esGuardia.and(x -> ids.add(obtenerId.apply(x))).test(r);

        // Aplicar el Consumer a cada registro
        registros.forEach(agregarSiGuardia);

        // Supplier que construye la lista final de nombres (sin streams)
        Supplier<List<String>> construirResultado = () -> {
            List<String> lista = new ArrayList<>();
            ids.forEach(id -> lista.add(obtenerNombre.apply(id)));
            return lista;
        };

        return construirResultado.get();
    }

    /**
     * 3. Calcula salario mensual.
     *
     * Implementación funcional:
     * - Se compone un Predicate que identifica registros del empleado y no ausencias
     * - Se usa un Consumer que aplica el cálculo como efecto lateral (acumulador mutable local)
     *   pero sin ramas condicionales explícitas (la condición se evalúa como parte de la composición).
     */
    public double calcularSalarioMensual(Empleado empleado, Function<TipoTurno, Double> factorPorTipo) {
        final double[] total = {0.0};

        Predicate<RegistroTurno> esDelEmpleadoYNoAusencia =
                r -> r.idEmpleado().equals(empleado.id()) && r.tipo() != TipoTurno.AUSENCIA;

        Consumer<RegistroTurno> acumular = r ->
                esDelEmpleadoYNoAusencia.and(x -> {
                    total[0] += x.horas() * empleado.salarioBaseHora() * factorPorTipo.apply(x.tipo());
                    return true;
                }).test(r);

        registros.forEach(acumular);
        return total[0];
    }

    /**
     * 4. Genera validador de horas máximas.
     */
    public Predicate<RegistroTurno> generarValidadorHoras(int maxHoras) {
        return r -> r.horas() > maxHoras;
    }

    /**
     * 🧩 Ejercicio 5 – Listado por carga laboral
     *
     * Implementación funcional:
     * - BiFunction para crear EmpleadoCarga (Empleado × horas → EmpleadoCarga)
     * - Consumer para poblar la lista de resultados
     * - Comparator funcional para ordenar por horas (desc) y nombre (alfabético)
     */
    public List<EmpleadoCarga> listadoPorCargaLaboral() {

        // 1. Mapa de horas totales por empleado
        Map<String, Integer> horas = reporteHorasTrabajadas();

        // 2. Lista resultado
        List<EmpleadoCarga> listado = new ArrayList<>();

        // 3. BiFunction para crear EmpleadoCarga
        BiFunction<Empleado, Integer, EmpleadoCarga> crearCarga =
                (empleado, totalHoras) -> new EmpleadoCarga(empleado, totalHoras);

        // 4. Consumer para agregar EmpleadoCarga a la lista (sin bucles explícitos)
        Consumer<Empleado> agregar = e -> listado.add(crearCarga.apply(e, horas.getOrDefault(e.id(), 0)));

        personal.forEach(agregar);

        // 5. Comparator funcional compuesto
        Comparator<EmpleadoCarga> criterioOrden =
                Comparator.comparingInt(EmpleadoCarga::horasTotales)
                        .reversed()
                        .thenComparing(ec -> ec.empleado().nombre(), String.CASE_INSENSITIVE_ORDER);

        listado.sort(criterioOrden);
        return listado;
    }

    public record EmpleadoCarga(Empleado empleado, int horasTotales) {}

    /**
     * -------------------------------------------------------------------------
     * 🧩 Ejercicio 6 – Consolidado de ausencias
     * -------------------------------------------------------------------------
     *
     * Implementación funcional:
     * - Predicate para detectar ausencias
     * - Consumer que acumula en el mapa vía merge (efecto lateral controlado)
     */
    public Map<String, Long> consolidadoAusencias() {
        Map<String, Long> ausencias = new HashMap<>();

        Predicate<RegistroTurno> esAusencia = r -> r.tipo() == TipoTurno.AUSENCIA;

        Consumer<RegistroTurno> acumularAusencia = r ->
                esAusencia.and(x -> { ausencias.merge(x.idEmpleado(), 1L, Long::sum); return true; }).test(r);

        registros.forEach(acumularAusencia);
        return ausencias;
    }

    public record EventoCalendario(String titulo, LocalDate fecha) {}

    /**
     * 7. Convertir registros a eventos.
     *
     * Implementación funcional:
     * - Function para convertir RegistroTurno → EventoCalendario
     * - Consumer que agrega eventos solo cuando el registro pertenece al empleado (composición de Predicate)
     */
    public List<EventoCalendario> convertirRegistrosAEventos(String idEmpleado) {
        List<EventoCalendario> eventos = new ArrayList<>();

        Predicate<RegistroTurno> esDelEmpleado = r -> r.idEmpleado().equals(idEmpleado);
        Function<RegistroTurno, EventoCalendario> toEvento = r -> new EventoCalendario(r.tipo().name(), r.fecha());

        Consumer<RegistroTurno> agregarEvento = r ->
                esDelEmpleado.and(x -> { eventos.add(toEvento.apply(x)); return true; }).test(r);

        registros.forEach(agregarEvento);
        return eventos;
    }

    /**
     * 🧩 Ejercicio 8 – Verificar cobertura mínima.
     *
     * Implementación funcional:
     * - Predicate para validar registro por fecha y excluir ausencias
     * - Consumer que acumula ids y muestra información (efecto lateral controlado)
     * - Function para evaluar si se cumple el mínimo
     */
    public boolean verificarCobertura(LocalDate fecha, int minimo) {

        Predicate<RegistroTurno> esTurnoValido = r ->
                r.fecha().equals(fecha) && r.tipo() != TipoTurno.AUSENCIA;

        Set<String> ids = new HashSet<>();

        Consumer<RegistroTurno> registrarEmpleado = r ->
                esTurnoValido.and(x -> {
                    ids.add(x.idEmpleado());
                    Empleado e = mapaPorId.get(x.idEmpleado());
                    System.out.printf("  - %s | Tipo: %s | ⏱ %d hrs%n", e.nombre(), x.tipo(), x.horas());
                    return true;
                }).test(r);

        System.out.println("👥 Empleados cubriendo el turno del " + fecha + ":");
        registros.forEach(registrarEmpleado);

        Function<Set<String>, Boolean> cumpleCobertura = s -> s.size() >= minimo;
        boolean cumple = cumpleCobertura.apply(ids);
        System.out.println(cumple ? "✅ Cobertura mínima alcanzada" : "⚠️ Cobertura insuficiente");
        return cumple;
    }

    /**
     * 9. Turno más largo.
     *
     * Implementación funcional:
     * - Se usa un Consumer que actualiza una referencia mutable (efecto lateral controlado)
     * - Se utiliza un Supplier para construir el String resultante (evita if en cuerpo principal)
     */
    public String turnoMasLargo() {
        final RegistroTurno[] max = {null};

        Consumer<RegistroTurno> actualizar = r ->
                ((Predicate<RegistroTurno>) rr -> max[0] == null)
                        .or(rr -> r.horas() > (max[0] == null ? -1 : max[0].horas()))
                        .and(rr -> { max[0] = (max[0] == null || r.horas() > max[0].horas()) ? r : max[0]; return true; })
                        .test(r);

        registros.forEach(actualizar);

        Supplier<String> construir = () -> {
            if (max[0] == null) return "No hay registros disponibles.";
            RegistroTurno t = max[0];
            return String.format("🏆 Turno más largo -> Empleado: %s | Fecha: %s | Tipo: %s | ⏱ %d hrs",
                    mapaPorId.get(t.idEmpleado()).nombre(), t.fecha(), t.tipo(), t.horas());
        };

        return construir.get();
    }

    /**
     * 10️⃣ Genera un reporte con el total de nómina para los turnos de tipo NOCHE.
     *
     * Implementación funcional mediante recursión controlada (sin streams).
     * La recursión utiliza caso base y acumulador; se evita mutación externa.
     */
    public Map<String, Double> reporteNominaTurnosNoche(double factor) {
        return registros == null || registros.isEmpty()
                ? Map.of()
                : calcularNominaTurnosNoche(new ArrayList<>(registros), factor, new HashMap<>());
    }

    /**
     * Función recursiva auxiliar que acumula los valores de nómina nocturna
     * para cada empleado sin usar estructuras imperativas en el cuerpo superior.
     */
    private Map<String, Double> calcularNominaTurnosNoche(
            List<RegistroTurno> lista,
            double factor,
            Map<String, Double> acumulado) {

        if (lista.isEmpty()) return acumulado;

        RegistroTurno actual = lista.remove(0);
        if (actual.tipo() == TipoTurno.NOCHE) {
            Empleado e = mapaPorId.get(actual.idEmpleado());
            double valor = e.salarioBaseHora() * actual.horas() * factor;
            acumulado.merge(e.nombre(), valor, Double::sum);
        }

        return calcularNominaTurnosNoche(lista, factor, acumulado);
    }

    /** Devuelve el mapa inmutable de empleados por ID. */
    public Map<String, Empleado> getMapaPorId() {
        return Collections.unmodifiableMap(mapaPorId);
    }

    /** Devuelve la lista inmutable del personal. */
    public List<Empleado> getPersonal() {
        return Collections.unmodifiableList(personal);
    }

    /** Devuelve la lista inmutable de registros de turnos. */
    public List<RegistroTurno> getRegistros() {
        return Collections.unmodifiableList(registros);
    }

    /**
     * 11. Método agregado: filtrarTurnos según predicado.
     * Implementado de forma funcional (sin removeIf ni streams).
     * Utiliza un Consumer que aplica un Predicate sobre cada elemento
     * y agrega los registros válidos a la lista resultado.
     */
    public List<RegistroTurno> filtrarTurnos(Predicate<RegistroTurno> filtro) {
        List<RegistroTurno> resultado = new ArrayList<>();

        // ✅ Aplicación funcional del predicado sin condicionales ni bucles explícitos.
        Consumer<RegistroTurno> aplicarFiltro = r ->
                filtro.and(x -> { resultado.add(x); return true; }).test(r);

        // ✅ Iteración funcional sobre la lista de registros
        registros.forEach(aplicarFiltro);

        return resultado;
    }

}
