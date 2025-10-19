package com.clinica.nomina.repository;

import com.clinica.nomina.model.Area;
import com.clinica.nomina.model.Empleado;
import com.clinica.nomina.model.RegistroTurno;
import com.clinica.nomina.model.TipoTurno;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio simulado que contiene los datos de empleados y registros de turnos del mes.
 * Simula una fuente de datos persistente (por ejemplo, base de datos).
 */
public class DatosRepository {

    private final List<Empleado> empleados = List.of(
            new Empleado("E01", "Ana Gómez", Area.CARDIOLOGIA, 25.0),
            new Empleado("E02", "Luis Vera", Area.CIRUGIA, 45.0),
            new Empleado("E03", "Carlos Rivas", Area.PEDIATRIA, 28.0),
            new Empleado("E04", "Juan Mora", Area.CARDIOLOGIA, 22.0),
            new Empleado("E05", "Sofía Castro", Area.ONCOLOGIA, 35.0),
            new Empleado("E06", "Miguel Peña", Area.URGENCIAS, 38.0),
            new Empleado("E07", "Laura Díaz", Area.ADMINISTRACION, 18.0),
            new Empleado("E08", "Fernando Gil", Area.RADIOLOGIA, 33.0),
            new Empleado("E09", "Isabel Luna", Area.CIRUGIA, 48.0),
            new Empleado("E10", "Pedro Navas", Area.PEDIATRIA, 29.0),
            new Empleado("E11", "Valeria Sol", Area.URGENCIAS, 40.0),
            new Empleado("E12", "Ricardo León", Area.CARDIOLOGIA, 26.5),
            new Empleado("E13", "Mónica Marín", Area.ONCOLOGIA, 36.0),
            new Empleado("E14", "Andrés Salas", Area.ADMINISTRACION, 19.5),
            new Empleado("E15", "Gabriela Paz", Area.RADIOLOGIA, 34.5)
    );

    private final List<RegistroTurno> registrosMes = List.of(
            new RegistroTurno("E01", LocalDate.of(2024, 10, 1), TipoTurno.NOCHE, 12),
            new RegistroTurno("E02", LocalDate.of(2024, 10, 1), TipoTurno.DIA, 8),
            new RegistroTurno("E01", LocalDate.of(2024, 10, 2), TipoTurno.AUSENCIA, 0),
            new RegistroTurno("E04", LocalDate.of(2024, 10, 2), TipoTurno.DIA, 8),
            new RegistroTurno("E02", LocalDate.of(2024, 10, 3), TipoTurno.GUARDIA, 24),
            new RegistroTurno("E01", LocalDate.of(2024, 10, 3), TipoTurno.NOCHE, 12),
            new RegistroTurno("E04", LocalDate.of(2024, 10, 4), TipoTurno.NOCHE, 12),
            new RegistroTurno("E01", LocalDate.of(2024, 10, 5), TipoTurno.DIA, 8),
            new RegistroTurno("E02", LocalDate.of(2024, 10, 5), TipoTurno.DIA, 8),
            new RegistroTurno("E04", LocalDate.of(2024, 10, 6), TipoTurno.AUSENCIA, 0),
            new RegistroTurno("E01", LocalDate.of(2024, 10, 7), TipoTurno.NOCHE, 12),
            new RegistroTurno("E02", LocalDate.of(2024, 10, 8), TipoTurno.GUARDIA, 24),
            new RegistroTurno("E04", LocalDate.of(2024, 10, 8), TipoTurno.DIA, 8),
            new RegistroTurno("E01", LocalDate.of(2024, 10, 9), TipoTurno.DIA, 8),
            new RegistroTurno("E02", LocalDate.of(2024, 10, 10), TipoTurno.DIA, 8)
    );

    /**
     * Retorna todos los empleados.
     * @return Lista de empleados.
     */
    public List<Empleado> obtenerEmpleados() {
        return empleados;
    }

    /**
     * Retorna los registros de turnos del mes.
     * @return Lista de registros.
     */
    public List<RegistroTurno> obtenerRegistrosMes() {
        return registrosMes;
    }
}
