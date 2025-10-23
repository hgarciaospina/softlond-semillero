package com.clinica.nomina.service;

import com.clinica.nomina.model.ConsolidadoNovedadesNomina;
import com.clinica.nomina.repository.DatosRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio que determina el empleado del mes por área,
 * con base en el total de horas trabajadas.
 */
public class EmpleadoDelMesService {

    private final LiquidacionService liquidacionService;

    public EmpleadoDelMesService(DatosRepository datosRepository) {
        this.liquidacionService = new LiquidacionService(datosRepository);
    }

    /**
     * Retorna el empleado del mes (más horas trabajadas) por cada área.
     * @return Mapa con nombre de área y empleado destacado.
     */
    public Map<String, ConsolidadoNovedadesNomina> obtenerEmpleadoDelMesPorArea() {
        Map<String, List<ConsolidadoNovedadesNomina>> porArea = liquidacionService.agruparPorArea();

        return porArea.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .max(Comparator.comparingDouble(ConsolidadoNovedadesNomina::horasTrabajadas))
                                .orElse(null),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    /**
     * Retorna los empleados agrupados por área con su información consolidada.
     */
    public Map<String, List<ConsolidadoNovedadesNomina>> obtenerConsolidadoPorArea() {
        return liquidacionService.agruparPorArea();
    }
}
