package com.agendadigital.repository;

import com.agendadigital.model.EstadoTarea;
import com.agendadigital.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TareaRepository extends JpaRepository<Tarea, Long> {

    List<Tarea> findByOrderByFechaEntregaAsc();

    List<Tarea> findByFechaEntrega(LocalDate fecha);

    List<Tarea> findByFechaEntregaBetween(LocalDate inicio, LocalDate fin);

    List<Tarea> findByFechaEntregaAfter(LocalDate fecha);

    List<Tarea> findByEstado(EstadoTarea estado);

    long countByEstado(EstadoTarea estado);

    @Query("SELECT t FROM Tarea t WHERE t.estado = 'PENDIENTE' AND t.fechaEntrega < :hoy")
    List<Tarea> findTareasVencidas(@Param("hoy") LocalDate hoy);

    List<Tarea> findByCursoContainingIgnoreCase(String curso);

    @Query("SELECT t FROM Tarea t WHERE LOWER(t.titulo) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(t.curso) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Tarea> buscarPorTituloOCurso(@Param("query") String query);

    List<Tarea> findTop5ByEstadoOrderByFechaActualizacionDesc(EstadoTarea estado);
}