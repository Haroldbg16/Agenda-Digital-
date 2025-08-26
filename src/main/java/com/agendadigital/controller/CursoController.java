package com.agendadigital.controller;

import com.agendadigital.model.Curso;
import com.agendadigital.service.CursoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @GetMapping("/lista")
    public String lista(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("cursos",
                (q != null && !q.isBlank()) ? cursoService.buscar(q) : cursoService.listar());
        model.addAttribute("q", q);
        return "cursos/lista-cursos";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("curso", new Curso());
        return "cursos/curso-form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Curso curso, RedirectAttributes ra) {
        if (curso.getId() == null) {
            // Creación
            if (cursoService.existePorNombre(curso.getNombre())) {
                ra.addFlashAttribute("warning", "Ya existe un curso con ese nombre.");
                return "redirect:/cursos/nuevo";
            }
            cursoService.guardar(curso);
            ra.addFlashAttribute("success", "✅ Curso guardado correctamente.");
        } else {
            // Actualización
            cursoService.guardar(curso);
            ra.addFlashAttribute("success", "✅ Curso actualizado correctamente.");
        }
        return "redirect:/cursos/lista";
    }


    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("curso", cursoService.obtener(id));
        return "cursos/curso-form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        cursoService.eliminar(id);
        ra.addFlashAttribute("warning", "🗑️ Curso eliminado correctamente.");
        return "redirect:/cursos/lista";
    }
}
