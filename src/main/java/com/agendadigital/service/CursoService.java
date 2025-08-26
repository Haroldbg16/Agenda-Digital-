package com.agendadigital.service;

import com.agendadigital.model.Curso;
import com.agendadigital.repository.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CursoService {

    private final CursoRepository repo;

    public CursoService(CursoRepository repo) {
        this.repo = repo;
    }

    public List<Curso> listar() { return repo.findAll(); }

    public Curso guardar(Curso c) { return repo.save(c); }

    public Curso obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Curso no encontrado"));
    }

    public void eliminar(Long id) { repo.deleteById(id); }

    public List<Curso> buscar(String q) { return repo.findByNombreContainingIgnoreCase(q); }

    public boolean existePorNombre(String nombre){ return repo.existsByNombreIgnoreCase(nombre); }
}
