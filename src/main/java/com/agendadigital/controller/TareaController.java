package com.agendadigital.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.agendadigital.model.Tarea;
import com.agendadigital.service.TareaService;
import com.agendadigital.service.CursoService;

@Controller
@RequestMapping("/tareas")
public class TareaController {

    private final TareaService tareaService;
    private final CursoService cursoService;

    public TareaController(TareaService tareaService, CursoService cursoService) {
        this.tareaService = tareaService;
        this.cursoService = cursoService;
    }

    @GetMapping
    public String mostrarInicio(Model model) {
        model.addAttribute("tareasProximas", tareaService.obtenerTareasProximas());
        model.addAttribute("tareasVencidas", tareaService.obtenerTareasVencidas());

        // ✅ Agregar recordatorios: hoy hasta 7 días después
        LocalDate hoy = LocalDate.now();
        LocalDate en7dias = hoy.plusDays(7);
        var recordatorios = tareaService.obtenerTodasLasTareas().stream()
        	    .filter(t -> t.getEstado().name().equals("PENDIENTE")) // 👈 solo pendientes
        	    .filter(t -> !t.getFechaEntrega().isBefore(hoy) && !t.getFechaEntrega().isAfter(en7dias))
        	    .toList();

        model.addAttribute("tareasProximas", recordatorios); // sobrescribe tareasProximas con tareas desde hoy


        return "index";
    }


 // LISTA con filtro por curso (por nombre)
    @GetMapping("/lista")
    public String mostrarListaTareas(Model model,
                                     @RequestParam(required = false) String busqueda,
                                     @RequestParam(required = false, name = "curso") String cursoNombre) {

        if (cursoNombre != null && !cursoNombre.isBlank()) {
            model.addAttribute("tareas", tareaService.buscarTareasPorCurso(cursoNombre));
            model.addAttribute("cursoSeleccionado", cursoNombre);
        } else if (busqueda != null && !busqueda.isBlank()) {
            model.addAttribute("tareas", tareaService.buscarTareas(busqueda));
        } else {
            model.addAttribute("tareas", tareaService.obtenerTodasLasTareas());
        }
        model.addAttribute("cursos", cursoService.listar()); // para filtro
        return "lista-tareas";
    }

    @GetMapping("/nueva")
    public String mostrarFormularioNuevaTarea(Model model) {
        model.addAttribute("tarea", new Tarea());
        model.addAttribute("hoy", LocalDate.now());
        model.addAttribute("cursos", cursoService.listar()); // para el <select>
        return "nueva-tarea";
    }

    @PostMapping("/guardar")
    public String guardarTarea(@ModelAttribute Tarea tarea, RedirectAttributes ra) {
        tareaService.guardarTarea(tarea);
        ra.addFlashAttribute("success", "✅ Tarea registrada exitosamente.");
        ra.addFlashAttribute("toast", "Tarea \"" + tarea.getTitulo() + "\" creada.");
        return "redirect:/tareas/lista";
    }

    @GetMapping("/completar/{id}")
    public String marcarComoCompletada(@PathVariable Long id, RedirectAttributes ra) {
        tareaService.marcarComoCompletada(id);
        ra.addFlashAttribute("success", "✅ Tarea marcada como completada.");
        return "redirect:/tareas/lista";
    }
    
    @PostMapping("/{id}/completar-ajax")
    @ResponseBody
    public Map<String, Object> completarTareaAjax(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            tareaService.marcarComoCompletada(id);
            response.put("success", true);
            response.put("message", "✅ Tarea marcada como completada.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "❌ Error al completar la tarea.");
        }
        return response;
    }
    
    @PostMapping("/tareas/{id}/completar")
    @ResponseBody
    public ResponseEntity<String> completarTarea(@PathVariable Long id) {
        try {
            tareaService.marcarComoCompletada(id);  // lógica para actualizar en DB
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("error");
        }
    }



    @GetMapping("/eliminar/{id}")
    public String eliminarTarea(@PathVariable Long id, RedirectAttributes ra) {
        tareaService.eliminarTarea(id);
        ra.addFlashAttribute("warning", "🗑️ Tarea eliminada correctamente.");
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

        model.addAttribute("tareasHoy", tareaService.obtenerTareasDeHoy());
        model.addAttribute("tareasProximas", tareaService.obtenerTareasProximas());
        model.addAttribute("tareasFuturas", tareaService.obtenerTareasFuturas());
        model.addAttribute("tareasVencidas", tareaService.obtenerTareasVencidas());
        model.addAttribute("tareasRecientes", tareaService.obtenerUltimasTareasCompletadas());

        // 👉 Agregar recordatorios desde hoy hasta dentro de 7 días
        LocalDate hoy = LocalDate.now();
        LocalDate en7dias = hoy.plusDays(7);
        var recordatorios = tareaService.obtenerTodasLasTareas().stream()
                .filter(t -> !t.getFechaEntrega().isBefore(hoy) && !t.getFechaEntrega().isAfter(en7dias))
                .toList();
        model.addAttribute("recordatorios", recordatorios);

        return "dashboard";
    }

}