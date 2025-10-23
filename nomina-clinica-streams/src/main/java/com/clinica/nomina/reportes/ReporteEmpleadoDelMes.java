package com.clinica.nomina.reportes;

import com.clinica.nomina.model.ConsolidadoNovedadesNomina;
import com.clinica.nomina.service.EmpleadoDelMesService;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Reporte visual detallado del Empleado del Mes por cada área.
 */
public class ReporteEmpleadoDelMes {

    private final EmpleadoDelMesService empleadoDelMesService;

    public ReporteEmpleadoDelMes(EmpleadoDelMesService empleadoDelMesService) {
        this.empleadoDelMesService = empleadoDelMesService;
    }

    public void imprimir() {
        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        formato.setMinimumFractionDigits(2);

        Map<String, ConsolidadoNovedadesNomina> empleadosDelMes = empleadoDelMesService.obtenerEmpleadoDelMesPorArea();
        Map<String, List<ConsolidadoNovedadesNomina>> empleadosPorArea = empleadoDelMesService.obtenerConsolidadoPorArea();

        System.out.println("===========================================================");
        System.out.println("🏆  REPORTE DE EMPLEADOS DEL MES POR ÁREA");
        System.out.println("===========================================================\n");

        empleadosDelMes.forEach((area, destacado) -> {
            System.out.println("🏆 EMPLEADO DEL MES");
            System.out.printf("%s (Área: %s)%n", destacado.nombreEmpleado(), area);
            System.out.printf("Total de horas trabajadas: %.2f ⏱️%n%n", destacado.horasTrabajadas());

            System.out.println("--------------------------------------------");
            System.out.printf("Área: %s%n", area);
            System.out.println("--------------------------------------------");
            System.out.printf("%-8s %-22s %-8s %-13s %-16s%n",
                    "ID", "Nombre", "Horas", "Valor Hora", "Total Devengado");

            empleadosPorArea.get(area).forEach(emp -> {
                System.out.printf("%-8s %-22s %-8.2f %-13s %-16s%n",
                        emp.idEmpleado(),
                        emp.nombreEmpleado(),
                        emp.horasTrabajadas(),
                        formato.format(emp.salarioBaseHora()),
                        formato.format(emp.totalPagar()));
            });

            System.out.println();
        });

        System.out.println("✅ Fin del reporte de Empleados del Mes\n");
    }
}
