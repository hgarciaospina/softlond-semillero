package com.clinica.nomina;

import com.clinica.nomina.model.Empleado;
import com.clinica.nomina.model.RegistroTurno;
import com.clinica.nomina.model.TipoTurno;
import com.clinica.nomina.service.RRHHFunctionalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias ajustadas a los datos reales de DatosRepository
 * y la implementación actual de RRHHFunctionalService.
 */
class RRHHFunctionalServiceTest {

	private RRHHFunctionalService service;

	@BeforeEach
	void setUp() {
		service = new RRHHFunctionalService();
	}

	@Test
	void testReporteHorasTrabajadas() {
		Map<String, Integer> horas = service.reporteHorasTrabajadas();
		assertNotNull(horas);

		// Ana Gómez (E01): NOCHE(12) + NOCHE(12) + DIA(8) + NOCHE(12) + DIA(8) = 52h
		assertEquals(52, horas.get("E01"));

		// Luis Vera (E02): DIA(8) + GUARDIA(24) + DIA(8) + GUARDIA(24) + DIA(8) = 72h
		assertEquals(72, horas.get("E02"));
	}

	@Test
	void testEmpleadosConGuardia() {
		List<String> guardia = service.empleadosConGuardia();
		// Solo E02 tiene guardias
		assertTrue(guardia.contains("Luis Vera"));
		assertEquals(1, guardia.size());
	}

	@Test
	void testCalcularSalarioMensual() {
		Empleado luis = service.getMapaPorId().get("E02");
		double total = service.calcularSalarioMensual(luis, tipo -> {
			return switch (tipo) {
				case GUARDIA -> 1.2;
				case NOCHE -> 1.5;
				default -> 1.0;
			};
		});
		// Luis (E02): 72h totales (DIA + GUARDIA + GUARDIA + DIA + DIA)
		// 8h*45*1.0 + 24h*45*1.2 + 8h*45*1.0 + 24h*45*1.2 + 8h*45*1.0 = 3672.0
		assertEquals(3672.0, total, 0.01);
	}

	@Test
	void testGenerarValidadorHoras() {
		var pred = service.generarValidadorHoras(12);
		RegistroTurno r = new RegistroTurno("E02", LocalDate.of(2024, 10, 3), TipoTurno.GUARDIA, 24);
		assertTrue(pred.test(r));
	}

	@Test
	void testFiltrarTurnos() {
		var pred = service.generarValidadorHoras(10);
		List<RegistroTurno> filtrados = service.filtrarTurnos(pred);
		assertTrue(filtrados.stream().allMatch(r -> r.horas() > 10));
	}

	@Test
	void testListadoPorCargaLaboral() {
		List<Empleado> lista = service.listadoPorCargaLaboral();
		assertNotNull(lista);
		assertFalse(lista.isEmpty());
		// El primero debe ser el que más horas tiene (E02 con 72h)
		assertEquals("E02", lista.get(0).id());
	}

	@Test
	void testConsolidadoAusencias() {
		Map<String, Long> ausencias = service.consolidadoAusencias();
		// E01 (1) y E04 (1) tienen ausencias
		assertEquals(1L, ausencias.get("E01"));
		assertEquals(1L, ausencias.get("E04"));
	}

	@Test
	void testConvertirRegistrosAEventos() {
		List<RRHHFunctionalService.EventoCalendario> eventos = service.convertirRegistrosAEventos("E02");
		assertTrue(eventos.stream().allMatch(e -> e.titulo() != null && e.fecha() != null));
	}

	@Test
	void testVerificarCobertura() {
		boolean cobertura = service.verificarCobertura(LocalDate.of(2024, 10, 1), 2);
		assertTrue(cobertura);
	}

	@Test
	void testTurnoMasLargo() {
		RegistroTurno turno = service.turnoMasLargo();
		assertNotNull(turno);
		assertEquals(24, turno.horas());
		assertEquals("E02", turno.idEmpleado());
	}

	@Test
	void testReporteNominaTurnosNoche() {
		Map<String, Double> nomina = service.reporteNominaTurnosNoche(1.5);
		// Ana Gómez: NOCHE(12+12+12=36h) → 36 * 25 * 1.5 = 1350
		assertEquals(1350.0, nomina.get("Ana Gómez"), 0.01);
		// Juan Mora: NOCHE(12h) → 12 * 22 * 1.5 = 396
		assertEquals(396.0, nomina.get("Juan Mora"), 0.01);
	}
}
