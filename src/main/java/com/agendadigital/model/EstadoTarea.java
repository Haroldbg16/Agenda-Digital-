package com.agendadigital.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum EstadoTarea {
    PENDIENTE("Pendiente", "badge bg-warning"),
    COMPLETADA("Completada", "badge bg-success");

    private final String descripcion;
    private final String claseCss;

    EstadoTarea(String descripcion, String claseCss) {
        this.descripcion = descripcion;
        this.claseCss = claseCss;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getClaseCss() {
        return claseCss;
    }

    @Converter(autoApply = true)
    public static class EstadoTareaConverter implements AttributeConverter<EstadoTarea, String> {
        @Override
        public String convertToDatabaseColumn(EstadoTarea estado) {
            return estado != null ? estado.name() : null;
        }

        @Override
        public EstadoTarea convertToEntityAttribute(String dbData) {
            if (dbData == null) return null;
            try {
                return EstadoTarea.valueOf(dbData);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}