package com.agendadigital.service;

import com.agendadigital.model.EstadoTarea;
import com.agendadigital.model.Tarea;
import com.agendadigital.repository.TareaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TareaService {

    private final TareaRepository tareaRepository;

    public TareaService(TareaRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    public List<Tarea> obtenerTodasLasTareas() {
        return tareaRepository.findByOrderByFechaEntregaAsc();
    }

    public List<Tarea> obtenerTareasDeHoy() {
        return tareaRepository.findByFechaEntrega(LocalDate.now());
    }

    public List<Tarea> obtenerTareasProximas() {
        LocalDate hoy = LocalDate.now();
        LocalDate enDosDias = hoy.plusDays(2);
        return tareaRepository.findByFechaEntregaBetween(hoy, enDosDias);
    }

    public List<Tarea> obtenerTareasVencidas() {
        return tareaRepository.findTareasVencidas(LocalDate.now());
    }

    public Tarea guardarTarea(Tarea tarea) {
        if (tarea.getEstado() == null) {
            tarea.setEstado(EstadoTarea.PENDIENTE);
        }
        return tareaRepository.save(tarea);
    }

    public Tarea obtenerTareaPorId(Long id) {
        return tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con ID: " + id));
    }

    public void eliminarTarea(Long id) {
        tareaRepository.deleteById(id);
    }

    public void marcarComoCompletada(Long id) {
        Tarea tarea = obtenerTareaPorId(id);
        tarea.setEstado(EstadoTarea.COMPLETADA);
        tarea.setFechaActualizacion(LocalDateTime.now());
        tareaRepository.save(tarea);
    }

    public String calcularProgreso() {
        long completadas = tareaRepository.countByEstado(EstadoTarea.COMPLETADA);
        long total = tareaRepository.count();

        if (total == 0) return "No hay tareas registradas";
        double porcentaje = (completadas * 100.0) / total;

        return String.format("%d tareas completadas de %d (%.0f%%)", completadas, total, porcentaje);
    }

    public List<Tarea> buscarTareas(String query) {
        return tareaRepository.buscarPorTituloOCurso(query);
    }

    // Para progreso.html
    public long contarTareasCompletadas() {
        return tareaRepository.countByEstado(EstadoTarea.COMPLETADA);
    }

    public long contarTareasPendientes() {
        return tareaRepository.countByEstado(EstadoTarea.PENDIENTE);
    }

    public List<Tarea> obtenerUltimasTareasCompletadas() {
        return tareaRepository.findTop5ByEstadoOrderByFechaActualizacionDesc(EstadoTarea.COMPLETADA);
    }

    // Métodos para el dashboard
    public List<Tarea> obtenerTareasCompletadas() {
        return tareaRepository.findByEstado(EstadoTarea.COMPLETADA);
    }

    public List<Tarea> obtenerTareasPendientes() {
        return tareaRepository.findByEstado(EstadoTarea.PENDIENTE);
    }

    public List<Tarea> obtenerTareasRecientes() {
        return tareaRepository.findTop5ByEstadoOrderByFechaActualizacionDesc(EstadoTarea.COMPLETADA);
    }
}
