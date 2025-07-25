package com.agendadigital.dto;

public class TareaDTO {
    private String titulo;
    private String curso;
    private String descripcion;
    private String fechaFormateada;

    public TareaDTO(String titulo, String curso, String descripcion, String fechaFormateada) {
        this.titulo = titulo;
        this.curso = curso;
        this.descripcion = descripcion;
        this.fechaFormateada = fechaFormateada;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getCurso() {
        return curso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFechaFormateada() {
        return fechaFormateada;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setFechaFormateada(String fechaFormateada) {
        this.fechaFormateada = fechaFormateada;
    }
}
