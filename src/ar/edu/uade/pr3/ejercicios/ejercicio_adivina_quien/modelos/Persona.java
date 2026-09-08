package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos;

import java.util.Objects;

/**
 * Entidad inmutable que modela a un Personaje en el juego Adivina Quién.
 * Contiene todas las características físicas requeridas por el enunciado evolutivo.
 */
public class Persona {
    private static int idCounter = 0;
    
    private final int id;
    private final String nombre;
    private final String apellido;
    private final Genero genero;
    private final boolean esCalvo;
    private final boolean usaLentes;
    private final ColorPelo colorPelo;
    private boolean esElegido;

    public Persona(int id, String nombre, String apellido, Genero genero, boolean esCalvo, boolean usaLentes, ColorPelo colorPelo, boolean esElegido) {
        this.id = id;
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.apellido = Objects.requireNonNull(apellido, "El apellido no puede ser nulo");
        this.genero = Objects.requireNonNull(genero, "El género no puede ser nulo");
        this.esCalvo = esCalvo;
        this.usaLentes = usaLentes;
        this.colorPelo = esCalvo ? ColorPelo.NINGUNO : Objects.requireNonNull(colorPelo, "El color de pelo no puede ser nulo");
        this.esElegido = esElegido;
    }

    public Persona(String nombre, String apellido, Genero genero, boolean esCalvo, boolean usaLentes, ColorPelo colorPelo) {
        this(++idCounter, nombre, apellido, genero, esCalvo, usaLentes, colorPelo, false);
    }

    public static void resetearContadorDeIDs() {
        idCounter = 0;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public Genero getGenero() {
        return genero;
    }

    public boolean esCalvo() {
        return esCalvo;
    }

    public boolean usaLentes() {
        return usaLentes;
    }

    public ColorPelo getColorPelo() {
        return colorPelo;
    }

    public boolean esElegido() {
        return esElegido;
    }

    public void setEsElegido(boolean esElegido) {
        this.esElegido = esElegido;
    }

    /**
     * Devuelve una descripción detallada en texto con los atributos del personaje.
     */
    public String getDescripcionAtributos() {
        return String.format(
            "ID: %02d | Nombre: %-18s | Género: %-9s | Calvo: %-3s | Lentes: %-3s | Pelo: %-8s",
            id,
            getNombreCompleto(),
            genero.getDescripcion(),
            (esCalvo ? "SÍ" : "NO"),
            (usaLentes ? "SÍ" : "NO"),
            colorPelo.getDescripcion()
        );
    }

    @Override
    public String toString() {
        return "Persona{" +
                "id=" + id +
                ", nombre='" + getNombreCompleto() + '\'' +
                ", genero=" + genero +
                ", esCalvo=" + esCalvo +
                ", usaLentes=" + usaLentes +
                ", colorPelo=" + colorPelo +
                ", esElegido=" + (esElegido ? "SI" : "NO") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Persona persona = (Persona) o;
        return id == persona.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
