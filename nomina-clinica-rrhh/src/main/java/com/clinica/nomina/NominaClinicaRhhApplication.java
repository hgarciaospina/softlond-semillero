package com.clinica.nomina;

import com.clinica.nomina.model.Empleado;
import com.clinica.nomina.model.RegistroTurno;
import com.clinica.nomina.model.TipoTurno;
import com.clinica.nomina.service.RRHHFunctionalService;
import com.clinica.nomina.service.RRHHFunctionalService.EmpleadoCarga;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 🏥 Aplicación principal - Nómina Clínica RRHH
 * ------------------------------------------------------------
 * Ejecución funcional que demuestra el uso de RRHHFunctionalService
 * sin estructuras imperativas (sin bucles ni condicionales explícitos).
 *
 * Cada bloque corresponde a un ejercicio funcional del servicio.
 * Se emplean funciones puras, lambdas y Consumers para mantener
 * la consistencia funcional en la impresión de resultados.
 */
@SpringBootApplication
public class NominaClinicaRhhApplication {

	public static void main(String[] args) {

		SpringApplication.run(NominaClinicaRhhApplication.class, args);

		RRHHFunctionalService rrHHService = new RRHHFunctionalService();

		// -------------------------------------------------------------------------
		// 🧩 Ejercicio 1 – Reporte de horas trabajadas
		// -------------------------------------------------------------------------
		System.out.println("📊 1) Horas trabajadas:");

		rrHHService.reporteHorasTrabajadas().forEach((id, horas) -> {
			String nombre = rrHHService.getMapaPorId().get(id).nombre();
			System.out.println("👤 " + nombre + " -> ⏱ " + horas + " hrs");
		});

		// -------------------------------------------------------------------------
		// 🧩 Ejercicio 2 – Empleados con Guardia
		// -------------------------------------------------------------------------
		System.out.println("\n🛡 2) Empleados con guardia:");

		Consumer<String> mostrarEmpleadoGuardia = nombre -> System.out.println("👮 " + nombre);
		rrHHService.empleadosConGuardia().forEach(mostrarEmpleadoGuardia);

		// -------------------------------------------------------------------------
		// 🧩 Ejercicio 3 – Cálculo de salario mensual
		// -------------------------------------------------------------------------
		System.out.println("\n💰 3) Salario mensual de Ana Gómez:");

		Empleado ana = rrHHService.getMapaPorId().get("E01");
		double salarioAna = rrHHService.calcularSalarioMensual(
				ana,
				tipo -> tipo == TipoTurno.NOCHE ? 1.5 : 1.0
		);
		System.out.println("👤 Ana Gómez -> 💵 $" + salarioAna);

		// -------------------------------------------------------------------------
		// 🧩 Ejercicio 4 – Filtrado de turnos largos (>10h)
		// -------------------------------------------------------------------------
		System.out.println("\n⏳ 4) Turnos largos (>10 hrs):");

		rrHHService.filtrarTurnos(rrHHService.generarValidadorHoras(10))
				.forEach(turno -> {
					String nombre = rrHHService.getMapaPorId().get(turno.idEmpleado()).nombre();
					System.out.println("👤 " + nombre + " | " + turno.tipo() + " | ⏱ " + turno.horas() + " hrs");
				});

		// -------------------------------------------------------------------------
		// 🧩 Ejercicio 5 – Listado de empleados por carga laboral
		// -------------------------------------------------------------------------
		System.out.println("\n📋 5) Listado por carga laboral:");

		rrHHService.listadoPorCargaLaboral()
				.forEach((EmpleadoCarga ec) ->
						System.out.println("👤 " + ec.empleado().nombre() + " -> ⏱ " + ec.horasTotales() + " hrs")
				);

		// -------------------------------------------------------------------------
		// 🧩 Ejercicio 6 – Consolidado de ausencias
		// -------------------------------------------------------------------------
		System.out.println("\n📅 6) Consolidado de ausencias:");

		rrHHService.consolidadoAusencias()
				.forEach((id, cantidad) -> {
					String nombre = rrHHService.getMapaPorId().get(id).nombre();
					System.out.println("👤 " + nombre + " -> ❌ " + cantidad + " ausencias");
				});

		// -------------------------------------------------------------------------
		// 🧩 Ejercicio 7 – Conversión de registros a eventos de calendario
		// -------------------------------------------------------------------------
		System.out.println("\n📆 7) Eventos del empleado E02:");

		rrHHService.convertirRegistrosAEventos("E02")
				.forEach(evento ->
						System.out.println("📌 " + evento.titulo() + " @ " + evento.fecha())
				);

		// -------------------------------------------------------------------------
		// 🧩 Ejercicio 8 – Verificación de cobertura mínima
		// -------------------------------------------------------------------------
		System.out.println("\n✅ 8) Cobertura para 1-Oct-2024 (mínimo 2 empleados):");

		boolean cobertura = rrHHService.verificarCobertura(LocalDate.of(2024, 10, 1), 2);
		System.out.println(cobertura
				? "✔ Cobertura suficiente"
				: "❌ Cobertura insuficiente"
		);

		// -------------------------------------------------------------------------
		// 🧩 Ejercicio 9 – Turno más largo
		// -------------------------------------------------------------------------
		System.out.println("\n🏆 9) Turno más largo registrado:");

		String turnoMax = rrHHService.turnoMasLargo();
		System.out.println(turnoMax);

		// -------------------------------------------------------------------------
		// 🧩 Ejercicio 10 – Nómina de turnos nocturnos
		// -------------------------------------------------------------------------
		System.out.println("\n🌙 10) Nómina turnos de noche (factor 1.5x):");

		rrHHService.reporteNominaTurnosNoche(1.5)
				.forEach((nombre, pago) ->
						System.out.println("👤 " + nombre + " -> 💵 $" + pago)
				);

		// -------------------------------------------------------------------------
		// 🧩 Ejercicio 11 – Filtrado de turnos diurnos largos (>6 hrs)
		// -------------------------------------------------------------------------
		System.out.println("\n🔎 11) Filtrado de turnos diurnos largos (>6 hrs):");

		// ✅ Se define el predicado funcional (sin programación imperativa)
		Predicate<RegistroTurno> filtroDiurnoLargo =
				r -> r.tipo() == TipoTurno.DIA && r.horas() > 6;

		// ✅ Se aplica el método filtrarTurnos con el predicado definido
		rrHHService.filtrarTurnos(filtroDiurnoLargo)
				.forEach(turno -> {
					Empleado emp = rrHHService.getMapaPorId().get(turno.idEmpleado());
					System.out.printf("👤 %s | Tipo: %s | ⏱ %d hrs | 📅 %s%n",
							emp.nombre(), turno.tipo(), turno.horas(), turno.fecha());
				});
	}
}
