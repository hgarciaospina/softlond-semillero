package com.clinica.nomina.service;

import com.clinica.nomina.model.NovedadesNomina;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * Servicio encargado de calcular el total de horas trabajadas
 * por área y por tipo de turno.
 */
public class DesgloseHorasPorAreaYTurnoService {

    private final LiquidacionService liquidacionService;

    public DesgloseHorasPorAreaYTurnoService(LiquidacionService liquidacionService) {
        this.liquidacionService = liquidacionService;
    }

    /**
     * Genera una estructura de datos anidada con el total de horas trabajadas
     * agrupadas por área y tipo de turno.
     *
     * Estructura: Map<Area, Map<TipoTurno, Double>>
     */
    public Map<String, Map<String, Double>> calcularDesgloseHorasPorAreaYTipoTurno() {
        List<NovedadesNomina> novedades = liquidacionService.obtenerNovedadesNomina();

        return novedades.stream()
                .collect(groupingBy(
                        NovedadesNomina::area,
                        TreeMap::new,
                        groupingBy(
                                NovedadesNomina::tipoTurno,
                                TreeMap::new,
                                Collectors.summingDouble(NovedadesNomina::horasTrabajadas)
                        )
                ));
    }
}
