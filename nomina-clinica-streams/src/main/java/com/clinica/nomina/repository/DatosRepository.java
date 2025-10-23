package com.clinica.nomina.repository;

import com.clinica.nomina.model.*;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public class DatosRepository {

    // --- Datos de Prueba ---
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
            new RegistroTurno("E01", java.time.LocalDate.of(2025, 10, 1), TipoTurno.NOCHE, 12),
            new RegistroTurno("E02", java.time.LocalDate.of(2025, 10, 1), TipoTurno.DIA, 10),
            new RegistroTurno("E01", java.time.LocalDate.of(2025, 10, 2), TipoTurno.NOCHE, 12),
            new RegistroTurno("E03", java.time.LocalDate.of(2025, 10, 2), TipoTurno.DIA, 8),
            new RegistroTurno("E01", java.time.LocalDate.of(2025, 10, 3), TipoTurno.NOCHE, 12), // 3 noches seguidas para E01
            new RegistroTurno("E04", java.time.LocalDate.of(2025, 10, 3), TipoTurno.DIA, 8),
            new RegistroTurno("E02", java.time.LocalDate.of(2025, 10, 4), TipoTurno.GUARDIA, 24),
            new RegistroTurno("E05", java.time.LocalDate.of(2025, 10, 4), TipoTurno.NOCHE, 12),
            new RegistroTurno("E06", java.time.LocalDate.of(2025, 10, 5), TipoTurno.GUARDIA, 24), // (**)
            new RegistroTurno("E11", java.time.LocalDate.of(2025, 10, 5), TipoTurno.GUARDIA, 12), // Guardia de 12h
            new RegistroTurno("E09", java.time.LocalDate.of(2025, 10, 6), TipoTurno.DIA, 8),
            new RegistroTurno("E10", java.time.LocalDate.of(2025, 10, 6), TipoTurno.DIA, 8),
            new RegistroTurno("E08", java.time.LocalDate.of(2025, 10, 6), TipoTurno.AUSENCIA, 0),
            new RegistroTurno("E02", java.time.LocalDate.of(2025, 10, 7), TipoTurno.DIA, 8), // Después de guardia
            new RegistroTurno("E12", java.time.LocalDate.of(2025, 10, 8), TipoTurno.NOCHE, 12),
            new RegistroTurno("E13", java.time.LocalDate.of(2025, 10, 8), TipoTurno.NOCHE, 12),
            new RegistroTurno("E07", java.time.LocalDate.of(2025, 10, 9), TipoTurno.DIA, 8),
            new RegistroTurno("E14", java.time.LocalDate.of(2025, 10, 10), TipoTurno.DIA, 8),
            new RegistroTurno("E15", java.time.LocalDate.of(2025, 10, 11), TipoTurno.GUARDIA, 24),
            new RegistroTurno("E01", java.time.LocalDate.of(2025, 10, 11), TipoTurno.AUSENCIA, 0),
            new RegistroTurno("E06", java.time.LocalDate.of(2025, 10, 6), TipoTurno.DIA, 8), // Día despues de guardia (**)
            new RegistroTurno("E09", java.time.LocalDate.of(2025, 10, 13), TipoTurno.NOCHE, 12),
            new RegistroTurno("E99", java.time.LocalDate.of(2025, 10, 13), TipoTurno.DIA, 8), // Empleado inexistente
            new RegistroTurno("E03", java.time.LocalDate.of(2025, 10, 14), TipoTurno.GUARDIA, 24),
            new RegistroTurno("E05", java.time.LocalDate.of(2025, 10, 15), TipoTurno.DIA, 8),
            new RegistroTurno("E05", java.time.LocalDate.of(2025, 10, 17), TipoTurno.GUARDIA, 24)

    );

    public List<Empleado> obtenerEmpleados() {
        return empleados;
    }

    public List<RegistroTurno> obtenerRegistrosMes() {
        return registrosMes;
    }
}
