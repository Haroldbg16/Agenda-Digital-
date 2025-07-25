package com.agendadigital.repository;

import com.agendadigital.model.EstadoTarea;
import com.agendadigital.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TareaRepository extends JpaRepository<Tarea, Long> {

    // Ordenar todas por fecha de entrega ascendente
    List<Tarea> findByOrderByFechaEntregaAsc();

    // Tareas del día
    List<Tarea> findByFechaEntrega(LocalDate fecha);

    // Tareas entre fechas
    List<Tarea> findByFechaEntregaBetween(LocalDate inicio, LocalDate fin);

    // Tareas por estado
    List<Tarea> findByEstado(EstadoTarea estado);

    // Contar por estado
    long countByEstado(EstadoTarea estado);

    // Tareas vencidas: fecha menor a hoy y no completadas
    @Query("SELECT t FROM Tarea t WHERE t.estado = 'PENDIENTE' AND t.fechaEntrega < :hoy")
    List<Tarea> findTareasVencidas(@Param("hoy") LocalDate hoy);

    // Buscar por curso
    List<Tarea> findByCursoContainingIgnoreCase(String curso);

    // Buscar por título o curso
    @Query("SELECT t FROM Tarea t WHERE LOWER(t.titulo) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(t.curso) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Tarea> buscarPorTituloOCurso(@Param("query") String query);

    // Últimas 5 tareas completadas
    List<Tarea> findTop5ByEstadoOrderByFechaActualizacionDesc(EstadoTarea estado);
}
