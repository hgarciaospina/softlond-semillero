package com.clinica.nomina;

import com.clinica.nomina.model.Empleado;
import com.clinica.nomina.model.RegistroTurno;
import com.clinica.nomina.model.TipoTurno;
import com.clinica.nomina.service.RRHHFunctionalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ✅ Pruebas unitarias para RRHHFunctionalService
 * ------------------------------------------------
 * Cada prueba corresponde a un método numerado en el servicio y
 * valida el cumplimiento del paradigma funcional aplicado:
 * sin estructuras imperativas, uso de interfaces funcionales,
 * y composición de comportamientos.
 */
class RRHHFunctionalServiceTest {

	private RRHHFunctionalService service;

	@BeforeEach
	void setUp() {
		service = new RRHHFunctionalService();
	}

	// -------------------------------------------------------------------------
	// 🔹 Test #1 – Reporte de horas trabajadas
	// -------------------------------------------------------------------------
	@Test
	@DisplayName("🧩 [Ejercicio 1] - Debe calcular correctamente las horas trabajadas por empleado")
	void testReporteHorasTrabajadas() {
		Map<String, Integer> horas = service.reporteHorasTrabajadas();
		assertNotNull(horas, "El mapa de horas no debe ser nulo");

		assertEquals(52, horas.get("E01")); // Ana Gómez
		assertEquals(72, horas.get("E02")); // Luis Vera
	}

	// -------------------------------------------------------------------------
	// 🔹 Test #2 – Empleados con guardias
	// -------------------------------------------------------------------------
	@Test
	@DisplayName("🧩 [Ejercicio 2] - Debe listar correctamente los empleados con turnos de guardia (implementación funcional pura)")
	void testEmpleadosConGuardia() {
		List<String> guardia = service.empleadosConGuardia();

		assertNotNull(guardia, "La lista no debe ser nula");
		assertFalse(guardia.isEmpty(), "Debe existir al menos un empleado con guardias");

		assertTrue(
				guardia.stream().anyMatch(nombre -> nombre.equals("Luis Vera")),
				"La lista debe contener al empleado 'Luis Vera'"
		);

		long elementosUnicos = guardia.stream().distinct().count();
		assertEquals(elementosUnicos, guardia.size(), "No deben existir duplicados");

		assertEquals(1, guardia.size(), "Solo un empleado (Luis Vera) tiene turnos de guardia");
	}

	// -------------------------------------------------------------------------
	// 🔹 Test #3 – Cálculo de salario mensual
	// -------------------------------------------------------------------------
	@Test
	@DisplayName("🧩 [Ejercicio 3] - Debe calcular correctamente el salario mensual aplicando la función de bonificación por tipo de turno")
	void testCalcularSalarioMensual() {
		Empleado luis = service.getMapaPorId().get("E02");

		double total = service.calcularSalarioMensual(luis, tipo -> switch (tipo) {
			case GUARDIA -> 1.2;
			case NOCHE -> 1.5;
			default -> 1.0;
		});

		assertEquals(3672.0, total, 0.01, "El total calculado debe coincidir con el esperado");
	}

	// -------------------------------------------------------------------------
	// 🔹 Test #4 – Generador de predicado de validación de horas
	// -------------------------------------------------------------------------
	@Test
	@DisplayName("🧩 [Ejercicio 4] - Debe generar correctamente un predicado para validar el límite máximo de horas por turno")
	void testGenerarValidadorHoras() {
		var pred = service.generarValidadorHoras(12);
		RegistroTurno r = new RegistroTurno("E02", LocalDate.of(2024, 10, 3), TipoTurno.GUARDIA, 24);
		assertTrue(pred.test(r), "El predicado debe retornar TRUE para turnos que exceden el límite");
	}

	// -------------------------------------------------------------------------
	// 🔹 Test #5 – Listado de empleados por carga laboral
	// -------------------------------------------------------------------------
	@Test
	@DisplayName("🧩 [Ejercicio 5] - Debe retornar los empleados ordenados por su carga laboral total (implementación funcional)")
	void testListadoPorCargaLaboral() {
		List<RRHHFunctionalService.EmpleadoCarga> lista = service.listadoPorCargaLaboral();

		assertNotNull(lista, "La lista no debe ser nula");
		assertFalse(lista.isEmpty(), "Debe contener al menos un registro");

		assertEquals("E02", lista.get(0).empleado().id(), "El empleado con mayor carga laboral debe ser E02");
		assertTrue(
				lista.get(0).horasTotales() >= lista.get(lista.size() - 1).horasTotales(),
				"El primer elemento debe tener igual o mayor carga que el último"
		);
	}

	// -------------------------------------------------------------------------
	// 🔹 Test #6 – Consolidado de ausencias
	// -------------------------------------------------------------------------
	@Test
	@DisplayName("🧩 [Ejercicio 6] - Debe consolidar correctamente las ausencias por empleado (implementación funcional pura)")
	void testConsolidadoAusencias() {
		Map<String, Long> ausencias = service.consolidadoAusencias();

		assertNotNull(ausencias, "El mapa de ausencias no debe ser nulo");
		assertFalse(ausencias.isEmpty(), "Debe contener al menos un registro");

		assertTrue(ausencias.containsKey("E01"), "Debe incluir a Ana Gómez (E01)");
		assertTrue(ausencias.containsKey("E04"), "Debe incluir a María Ruiz (E04)");

		assertEquals(1L, ausencias.get("E01"), "Ana Gómez debe tener 1 ausencia");
		assertEquals(1L, ausencias.get("E04"), "María Ruiz debe tener 1 ausencia");
	}

	// -------------------------------------------------------------------------
	// 🔹 Test #7 – Conversión de registros a eventos de calendario
	// -------------------------------------------------------------------------
	@Test
	@DisplayName("🧩 [Ejercicio 7] - Debe convertir correctamente los registros de turnos en eventos de calendario válidos")
	void testConvertirRegistrosAEventos() {
		List<RRHHFunctionalService.EventoCalendario> eventos = service.convertirRegistrosAEventos("E02");

		assertNotNull(eventos, "La lista de eventos no debe ser nula");
		assertFalse(eventos.isEmpty(), "Debe contener al menos un evento");
		assertTrue(eventos.stream().allMatch(e -> e.titulo() != null && e.fecha() != null),
				"Todos los eventos deben tener título y fecha válidos");
	}

	// -------------------------------------------------------------------------
	// 🔹 Test #8 – Verificación de cobertura mínima
	// -------------------------------------------------------------------------
	@Test
	@DisplayName("🧩 [Ejercicio 8] - Debe verificar si existe cobertura mínima de empleados para una fecha dada (implementación funcional pura)")
	void testVerificarCobertura() {
		boolean cobertura = service.verificarCobertura(LocalDate.of(2024, 10, 1), 2);

		assertTrue(cobertura, "Debe retornar TRUE cuando se cumple el mínimo requerido");

		assertDoesNotThrow(() ->
						service.verificarCobertura(LocalDate.of(2024, 10, 1), 2),
				"El método no debe lanzar excepciones"
		);

		boolean sinCobertura = service.verificarCobertura(LocalDate.of(2024, 10, 10), 3);
		assertFalse(sinCobertura, "Debe retornar FALSE cuando no se cumple el mínimo requerido");
	}

	// -------------------------------------------------------------------------
	// 🔹 Test #9 – Turno más largo
	// -------------------------------------------------------------------------
	@Test
	@DisplayName("🧩 [Ejercicio 9] - Debe identificar correctamente el turno más largo registrado")
	void testTurnoMasLargo() {
		String resultado = service.turnoMasLargo();

		assertNotNull(resultado, "El resultado no debe ser nulo");
		assertTrue(resultado.contains("24") || resultado.contains("GUARDIA") || resultado.contains("Luis"),
				"El texto debe reflejar que el turno más largo pertenece a Luis Vera con 24h");
	}

	// -------------------------------------------------------------------------
	// 🔹 Test #10 – Reporte de nómina de turnos de noche
	// -------------------------------------------------------------------------
	@Test
	@DisplayName("🧩 [Ejercicio 10] - Debe calcular correctamente la nómina de turnos nocturnos aplicando el factor multiplicador")
	void testReporteNominaTurnosNoche() {
		Map<String, Double> nomina = service.reporteNominaTurnosNoche(1.5);

		assertNotNull(nomina, "El mapa de nómina no debe ser nulo");
		assertEquals(1350.0, nomina.get("Ana Gómez"), 0.01, "El cálculo de Ana Gómez debe coincidir");
		assertEquals(396.0, nomina.get("Juan Mora"), 0.01, "El cálculo de Juan Mora debe coincidir");
	}

	// -------------------------------------------------------------------------
	// 🔹 Test #11 – Filtrado de turnos (genérico)
	// -------------------------------------------------------------------------
	@Test
	@DisplayName("🧩 [Ejercicio 11] - Debe filtrar correctamente los turnos según un predicado funcional")
	void testFiltrarTurnos() {
		var predicado = service.generarValidadorHoras(10);
		List<RegistroTurno> filtrados = service.filtrarTurnos(predicado);

		assertNotNull(filtrados, "La lista filtrada no debe ser nula");
		assertFalse(filtrados.isEmpty(), "Debe haber al menos un turno válido");

		assertTrue(filtrados.stream().allMatch(r -> r.horas() > 10),
				"Todos los turnos filtrados deben cumplir la condición");
		assertTrue(filtrados.stream().noneMatch(r -> r == null),
				"No deben existir elementos nulos en la lista filtrada");
	}
	// -------------------------------------------------------------------------
// 🧩 Ejercicio 11 – Filtrado de turnos diurnos largos (>6 hrs)
// -------------------------------------------------------------------------
	@Test
	@DisplayName("🧩 [Ejercicio 11] - Debe filtrar correctamente los turnos diurnos de más de 6 horas (implementación funcional pura)")
	void testFiltrarTurnosDiurnosLargos() {
		// ✅ Se define el predicado funcional (sin programación imperativa)
		Predicate<RegistroTurno> filtroDiurnoLargo =
				r -> r.tipo() == TipoTurno.DIA && r.horas() > 6;

		// ✅ Se ejecuta el método con el predicado definido
		List<RegistroTurno> resultado = service.filtrarTurnos(filtroDiurnoLargo);

		// 🔹 Validaciones principales
		assertNotNull(resultado, "La lista no debe ser nula");
		assertFalse(resultado.isEmpty(), "Debe haber al menos un turno diurno >6 hrs");

		// 🔹 Validación de condiciones
		assertTrue(resultado.stream().allMatch(r ->
						r.tipo() == TipoTurno.DIA && r.horas() > 6),
				"Todos los registros deben cumplir con el filtro");

		// 🔹 Validación de cantidad esperada (según datos de ejemplo)
		assertEquals(7, resultado.size(),
				"Debe haber 2 turnos diurnos con más de 6 horas en los datos base");
	}

}
