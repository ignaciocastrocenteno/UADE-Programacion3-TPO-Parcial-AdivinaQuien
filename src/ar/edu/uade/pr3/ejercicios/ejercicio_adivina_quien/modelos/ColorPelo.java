package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos;

/**
 * Representa los 3 colores de pelo requeridos por el enunciado más la opción NINGUNO (para calvos).
 */
public enum ColorPelo {
    COLORADO("Colorado"),
    NEGRO("Negro"),
    AMARILLO("Amarillo"),
    NINGUNO("Ninguno (Calvo)");

    private final String descripcion;

    ColorPelo(String descripcion) {
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
