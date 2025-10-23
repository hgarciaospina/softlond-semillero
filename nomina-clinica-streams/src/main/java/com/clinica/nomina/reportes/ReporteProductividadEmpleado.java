package com.clinica.nomina.reportes;

import com.clinica.nomina.model.ProductividadEmpleado;
import com.clinica.nomina.service.ProductividadService;

import java.util.List;

/**
 * Reporte que imprime la productividad de los empleados.
 */
public class ReporteProductividadEmpleado {

    private final ProductividadService service;

    public ReporteProductividadEmpleado(ProductividadService service) {
        this.service = service;
    }

    public void imprimir() {
        List<ProductividadEmpleado> lista = service.calcularProductividad();

        System.out.println("\n====================================================");
        System.out.println("📊  PRODUCTIVIDAD DE EMPLEADOS");
        System.out.println("====================================================\n");

        System.out.printf("%-20s %-15s %12s %15s%n", "Nombre", "Área", "Total Horas", "Salario Total");
        System.out.println("----------------------------------------------------");

        lista.forEach(p -> System.out.printf(
                "%-20s %-15s %12d %15.2f%n",
                p.nombre(),
                p.area(),
                p.totalHoras(),
                p.salarioTotal()
        ));

        System.out.println("====================================================");
        System.out.println("✅ Fin del reporte\n");
    }
}
