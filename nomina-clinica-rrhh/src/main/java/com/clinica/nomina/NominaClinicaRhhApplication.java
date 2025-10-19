package com.clinica.nomina;

import com.clinica.nomina.model.Empleado;
import com.clinica.nomina.model.RegistroTurno;
import com.clinica.nomina.model.TipoTurno;
import com.clinica.nomina.service.RRHHFunctionalService;
import com.clinica.nomina.service.RRHHFunctionalService.EmpleadoCarga;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

@SpringBootApplication
public class NominaClinicaRhhApplication {

	public static void main(String[] args) {

		SpringApplication.run(NominaClinicaRhhApplication.class, args);

		RRHHFunctionalService rrHHService = new RRHHFunctionalService();

		System.out.println("📊 1) Horas trabajadas:");
		rrHHService.reporteHorasTrabajadas().forEach((id, h) -> {
			String nombre = rrHHService.getMapaPorId().get(id).nombre();
			System.out.println("👤 " + nombre + " -> ⏱ " + h + " hrs");
		});

		System.out.println("\n🛡 2) Empleados con guardia:");
		rrHHService.empleadosConGuardia().forEach(nombre -> System.out.println("👮 " + nombre));

		System.out.println("\n💰 3) Salario Ana:");
		Empleado ana = rrHHService.getMapaPorId().get("E01");
		double salarioAna = rrHHService.calcularSalarioMensual(ana, tipo -> tipo == TipoTurno.NOCHE ? 1.5 : 1.0);
		System.out.println("👤 Ana Gómez -> 💵 $" + salarioAna);

		System.out.println("\n⏳ 4) Turnos largos (>10 hrs):");
		rrHHService.filtrarTurnos(rrHHService.generarValidadorHoras(10))
				.forEach(r -> {
					String nombre = rrHHService.getMapaPorId().get(r.idEmpleado()).nombre();
					System.out.println("👤 " + nombre + " | " + r.tipo() + " | ⏱ " + r.horas() + " hrs");
				});

		System.out.println("\n📋 5) Listado por carga laboral:");
		rrHHService.listadoPorCargaLaboral().forEach((EmpleadoCarga ec) ->
				System.out.println("👤 " + ec.empleado().nombre() + " -> ⏱ " + ec.horasTotales() + " hrs"));

		System.out.println("\n📅 6) Consolidado ausencias:");
		rrHHService.consolidadoAusencias().forEach((id, c) -> {
			String nombre = rrHHService.getMapaPorId().get(id).nombre();
			System.out.println("👤 " + nombre + " -> ❌ " + c + " ausencias");
		});

		System.out.println("\n📆 7) Eventos E02:");
		rrHHService.convertirRegistrosAEventos("E02")
				.forEach(ev -> System.out.println("📌 " + ev.titulo() + " @ " + ev.fecha()));

		System.out.println("\n✅ 8) Cobertura 1-Oct-2024 >=2:");
		boolean cobertura = rrHHService.verificarCobertura(LocalDate.of(2024, 10, 1), 2);
		System.out.println(cobertura ? "✔ Cobertura suficiente" : "❌ Cobertura insuficiente");

		System.out.println("\n🏆 9) Turno más largo:");
		String maxTurno = rrHHService.turnoMasLargo();
		System.out.println(maxTurno);

		System.out.println("\n🌙 10) Nómina Noche (1.5x):");
		rrHHService.reporteNominaTurnosNoche(1.5)
				.forEach((name, pay) -> System.out.println("👤 " + name + " -> 💵 $" + pay));
	}
}
