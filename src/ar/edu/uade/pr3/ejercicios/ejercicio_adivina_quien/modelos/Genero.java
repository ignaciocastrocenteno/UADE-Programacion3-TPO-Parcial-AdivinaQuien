package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos;

/**
 * Representa el género de un personaje.
 */
public enum Genero {
    MASCULINO("Masculino"),
    FEMENINO("Femenino");

    private final String descripcion;

    Genero(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
