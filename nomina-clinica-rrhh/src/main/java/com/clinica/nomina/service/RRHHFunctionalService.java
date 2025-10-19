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
 * Este servicio gestiona operaciones sobre empleados y registros de turnos
 * aplicando un enfoque **funcional** mediante:
 * - Predicate
 * - Function
 * - Consumer
 * - BiConsumer
 *
 * No se utilizan estructuras imperativas ni bucles explícitos.
 *
 * Responsabilidades principales:
 * - Procesar horas trabajadas y ausencias.
 * - Generar reportes de nómina y carga laboral.
 * - Aplicar validaciones y conversiones funcionales.
 *
 * Todas las colecciones se obtienen desde DatosRepository
 * y se procesan en copias locales para evitar efectos colaterales.
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

        // Consumer es una interfaz funcional de orden superior que aplica una acción a cada elemento
        Consumer<Empleado> agregarAMapa = e -> mapaPorId.put(e.id(), e);
        personal.forEach(agregarAMapa);
    }

    /**
     * 1. Genera un reporte de horas trabajadas por empleado.
     */
    public Map<String, Integer> reporteHorasTrabajadas() {
        Map<String, Integer> horas = new HashMap<>();
        List<RegistroTurno> copia = new ArrayList<>(registros);

        Predicate<RegistroTurno> esAusencia = r -> r.tipo() == TipoTurno.AUSENCIA;
        copia.removeIf(esAusencia);

        BiConsumer<String, Integer> acumular = (id, h) -> horas.merge(id, h, Integer::sum);
        Consumer<RegistroTurno> procesar = r -> acumular.accept(r.idEmpleado(), r.horas());

        copia.forEach(procesar);
        return horas;
    }

    /**
     * 2. Empleados con guardia.
     */
    public List<String> empleadosConGuardia() {
        List<RegistroTurno> copia = new ArrayList<>(registros);
        copia.removeIf(r -> r.tipo() != TipoTurno.GUARDIA);

        Set<String> ids = new LinkedHashSet<>();
        copia.forEach(r -> ids.add(r.idEmpleado()));

        List<String> nombres = new ArrayList<>();
        ids.forEach(id -> nombres.add(mapaPorId.get(id).nombre()));
        return nombres;
    }

    /**
     * 3. Calcula salario mensual.
     */
    public double calcularSalarioMensual(Empleado empleado, Function<TipoTurno, Double> factorPorTipo) {
        List<RegistroTurno> copia = new ArrayList<>(registros);
        copia.removeIf(r -> !r.idEmpleado().equals(empleado.id()) || r.tipo() == TipoTurno.AUSENCIA);

        final double[] total = {0.0};
        copia.forEach(r -> total[0] += r.horas() * empleado.salarioBaseHora() * factorPorTipo.apply(r.tipo()));
        return total[0];
    }

    /**
     * 4. Genera validador de horas máximas.
     */
    public Predicate<RegistroTurno> generarValidadorHoras(int maxHoras) {
        return r -> r.horas() > maxHoras;
    }

    /**
     * 5. Listado por carga laboral.
     */
    public List<EmpleadoCarga> listadoPorCargaLaboral() {
        Map<String, Integer> horas = reporteHorasTrabajadas();
        List<EmpleadoCarga> listado = new ArrayList<>();
        personal.forEach(e -> listado.add(new EmpleadoCarga(e, horas.getOrDefault(e.id(), 0))));
        listado.sort((a, b) -> {
            int comp = Integer.compare(b.horasTotales(), a.horasTotales());
            return comp != 0 ? comp : a.empleado().nombre().compareToIgnoreCase(b.empleado().nombre());
        });
        return listado;
    }

    public record EmpleadoCarga(Empleado empleado, int horasTotales) {}

    /**
     * 6. Consolidado ausencias.
     */
    public Map<String, Long> consolidadoAusencias() {
        Map<String, Long> ausencias = new HashMap<>();
        List<RegistroTurno> copia = new ArrayList<>(registros);
        copia.removeIf(r -> r.tipo() != TipoTurno.AUSENCIA);
        copia.forEach(r -> ausencias.merge(r.idEmpleado(), 1L, Long::sum));
        return ausencias;
    }

    public record EventoCalendario(String titulo, LocalDate fecha) {}

    /**
     * 7. Convertir registros a eventos.
     */
    public List<EventoCalendario> convertirRegistrosAEventos(String idEmpleado) {
        List<RegistroTurno> copia = new ArrayList<>(registros);
        copia.removeIf(r -> !r.idEmpleado().equals(idEmpleado));

        List<EventoCalendario> eventos = new ArrayList<>();
        Function<RegistroTurno, EventoCalendario> toEvento = r -> new EventoCalendario(r.tipo().name(), r.fecha());
        copia.forEach(r -> eventos.add(toEvento.apply(r)));
        return eventos;
    }

    /**
     * 8. Verificar cobertura mínima.
     */
    public boolean verificarCobertura(LocalDate fecha, int minimo) {
        List<RegistroTurno> copia = new ArrayList<>(registros);
        copia.removeIf(r -> !r.fecha().equals(fecha) || r.tipo() == TipoTurno.AUSENCIA);

        Set<String> ids = new HashSet<>();
        System.out.println("👥 Empleados cubriendo el turno del " + fecha + ":");
        copia.forEach(r -> {
            ids.add(r.idEmpleado());
            Empleado e = mapaPorId.get(r.idEmpleado());
            System.out.printf("  - %s | Tipo: %s | ⏱ %d hrs%n", e.nombre(), r.tipo(), r.horas());
        });

        boolean cumple = ids.size() >= minimo;
        System.out.println(cumple ? "✅ Cobertura mínima alcanzada" : "⚠️ Cobertura insuficiente");
        return cumple;
    }

    /**
     * 9. Turno más largo.
     */
    public String turnoMasLargo() {
        final RegistroTurno[] max = {null};
        Consumer<RegistroTurno> actualizar = r -> max[0] = (max[0] == null || r.horas() > max[0].horas()) ? r : max[0];
        registros.forEach(actualizar);

        if (max[0] == null) return "No hay registros disponibles.";
        RegistroTurno t = max[0];
        return String.format("🏆 Turno más largo -> Empleado: %s | Fecha: %s | Tipo: %s | ⏱ %d hrs",
                mapaPorId.get(t.idEmpleado()).nombre(), t.fecha(), t.tipo(), t.horas());
    }

    /**
     * 10. Nómina turnos noche.
     */
    public Map<String, Double> reporteNominaTurnosNoche(double factor) {
        Map<String, Double> nomina = new HashMap<>();
        List<RegistroTurno> copia = new ArrayList<>(registros);
        copia.removeIf(r -> r.tipo() != TipoTurno.NOCHE);
        copia.forEach(r -> {
            Empleado e = mapaPorId.get(r.idEmpleado());
            nomina.merge(e.nombre(), e.salarioBaseHora() * r.horas() * factor, Double::sum);
        });
        return nomina;
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
     * 11. Método agregado: filtrarTurnos según predicado
     * Corregido para que main pueda llamar rrHHService.filtrarTurnos(...)
     */
    public List<RegistroTurno> filtrarTurnos(Predicate<RegistroTurno> filtro) {
        List<RegistroTurno> copia = new ArrayList<>(registros);
        copia.removeIf(filtro.negate()); // Mantener solo los que cumplen el filtro
        return copia;
    }
}
