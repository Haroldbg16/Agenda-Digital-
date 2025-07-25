package com.agendadigital.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.agendadigital.model.Tarea;
import com.agendadigital.service.TareaService;

@Controller
@RequestMapping("/tareas")
public class TareaController {

    private final TareaService tareaService;

    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    @GetMapping
    public String mostrarInicio(Model model) {
        model.addAttribute("tareasProximas", tareaService.obtenerTareasProximas());
        model.addAttribute("tareasVencidas", tareaService.obtenerTareasVencidas());
        return "index";
    }

    @GetMapping("/lista")
    public String mostrarListaTareas(Model model, @RequestParam(required = false) String busqueda) {
        if (busqueda != null && !busqueda.isEmpty()) {
            model.addAttribute("tareas", tareaService.buscarTareas(busqueda));
        } else {
            model.addAttribute("tareas", tareaService.obtenerTodasLasTareas());
        }
        return "lista-tareas";
    }

    @GetMapping("/nueva")
    public String mostrarFormularioNuevaTarea(Model model) {
        model.addAttribute("tarea", new Tarea());
        model.addAttribute("hoy", LocalDate.now());
        return "nueva-tarea";
    }

    @PostMapping("/guardar")
    public String guardarTarea(@ModelAttribute Tarea tarea) {
        tareaService.guardarTarea(tarea);
        return "redirect:/tareas/lista";
    }

    @GetMapping("/completar/{id}")
    public String marcarComoCompletada(@PathVariable Long id) {
        tareaService.marcarComoCompletada(id);
        return "redirect:/tareas/lista";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarTarea(@PathVariable Long id) {
        tareaService.eliminarTarea(id);
        return "redirect:/tareas/lista";
    }

    @GetMapping("/hoy")
    public String mostrarTareasDeHoy(Model model) {
        model.addAttribute("tareas", tareaService.obtenerTareasDeHoy());
        return "tareas-hoy";
    }

    @GetMapping("/calendario")
    public String mostrarCalendario(Model model) {
        model.addAttribute("tareas", tareaService.obtenerTodasLasTareas());
        return "calendario";
    }

    @GetMapping("/progreso")
    public String mostrarProgreso(Model model) {
        long completadas = tareaService.contarTareasCompletadas();
        long pendientes = tareaService.contarTareasPendientes();
        long total = completadas + pendientes;
        double porcentaje = (total > 0) ? (completadas * 100.0 / total) : 0;

        model.addAttribute("completadas", completadas);
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("total", total);
        model.addAttribute("porcentaje", Math.round(porcentaje));
        model.addAttribute("tareasRecientes", tareaService.obtenerUltimasTareasCompletadas());

        return "progreso";
    }
    
    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        model.addAttribute("completadas", tareaService.contarTareasCompletadas());
        model.addAttribute("pendientes", tareaService.contarTareasPendientes());

        long total = tareaService.contarTareasCompletadas() + tareaService.contarTareasPendientes();
        double porcentaje = (total > 0) ? (tareaService.contarTareasCompletadas() * 100.0 / total) : 0;
        model.addAttribute("porcentaje", Math.round(porcentaje));

        model.addAttribute("tareasProximas", tareaService.obtenerTareasProximas());
        model.addAttribute("tareasVencidas", tareaService.obtenerTareasVencidas());
        model.addAttribute("tareasRecientes", tareaService.obtenerUltimasTareasCompletadas());
        return "dashboard"; // archivo dashboard.html
    }

 }
