package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Representa una pregunta / filtro aplicable sobre el conjunto de candidatos.
 * Permite dividir el espacio de búsqueda en subconjuntos disjuntos (Divide & Conquer).
 */
public class FiltroPregunta {

    public enum Tipo {
        GENERO_MASCULINO("¿El personaje es de género Masculino?"),
        GENERO_FEMENINO("¿El personaje es de género Femenino?"),
        ES_CALVO("¿El personaje es calvo?"),
        TIENE_PELO("¿El personaje tiene pelo?"),
        USA_LENTES("¿El personaje usa lentes?"),
        NO_USA_LENTES("¿El personaje NO usa lentes?"),
        PELO_COLORADO("¿El personaje tiene pelo colorado?"),
        PELO_NEGRO("¿El personaje tiene pelo negro?"),
        PELO_AMARILLO("¿El personaje tiene pelo amarillo?"),
        RANGO_ID("¿El ID del personaje se encuentra en el rango especificado?");

        private final String textoPorDefecto;

        Tipo(String textoPorDefecto) {
            this.textoPorDefecto = textoPorDefecto;
        }

        public String getTextoPorDefecto() {
            return textoPorDefecto;
        }
    }

    private final Tipo tipo;
    private final String enunciado;
    private final Predicate<Persona> predicado;
    private final int minId;
    private final int maxId;

    public FiltroPregunta(Tipo tipo, String enunciado, Predicate<Persona> predicado) {
        this(tipo, enunciado, predicado, -1, -1);
    }

    public FiltroPregunta(Tipo tipo, String enunciado, Predicate<Persona> predicado, int minId, int maxId) {
        this.tipo = Objects.requireNonNull(tipo);
        this.enunciado = Objects.requireNonNull(enunciado);
        this.predicado = Objects.requireNonNull(predicado);
        this.minId = minId;
        this.maxId = maxId;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public int getMinId() {
        return minId;
    }

    public int getMaxId() {
        return maxId;
    }

    /**
     * Evalúa si una persona cumple con la condición del filtro.
     */
    public boolean cumple(Persona persona) {
        if (persona == null) return false;
        return predicado.test(persona);
    }

    // --- Fábrica de filtros estándar requeridos por el enunciado ---

    public static FiltroPregunta esMasculino() {
        return new FiltroPregunta(Tipo.GENERO_MASCULINO, "¿Es de género Masculino?", p -> p.getGenero() == Genero.MASCULINO);
    }

    public static FiltroPregunta esFemenino() {
        return new FiltroPregunta(Tipo.GENERO_FEMENINO, "¿Es de género Femenino?", p -> p.getGenero() == Genero.FEMENINO);
    }

    public static FiltroPregunta esCalvo() {
        return new FiltroPregunta(Tipo.ES_CALVO, "¿Es calvo?", Persona::esCalvo);
    }

    public static FiltroPregunta tienePelo() {
        return new FiltroPregunta(Tipo.TIENE_PELO, "¿Tiene pelo?", p -> !p.esCalvo());
    }

    public static FiltroPregunta usaLentes() {
        return new FiltroPregunta(Tipo.USA_LENTES, "¿Usa lentes?", Persona::usaLentes);
    }

    public static FiltroPregunta noUsaLentes() {
        return new FiltroPregunta(Tipo.NO_USA_LENTES, "¿NO usa lentes?", p -> !p.usaLentes());
    }

    public static FiltroPregunta peloColorado() {
        return new FiltroPregunta(Tipo.PELO_COLORADO, "¿Tiene pelo colorado?", p -> p.getColorPelo() == ColorPelo.COLORADO);
    }

    public static FiltroPregunta peloNegro() {
        return new FiltroPregunta(Tipo.PELO_NEGRO, "¿Tiene pelo negro?", p -> p.getColorPelo() == ColorPelo.NEGRO);
    }

    public static FiltroPregunta peloAmarillo() {
        return new FiltroPregunta(Tipo.PELO_AMARILLO, "¿Tiene pelo amarillo?", p -> p.getColorPelo() == ColorPelo.AMARILLO);
    }

    public static FiltroPregunta rangoIds(int minId, int maxId) {
        return new FiltroPregunta(
                Tipo.RANGO_ID,
                String.format("¿El ID está en el rango [%d..%d]?", minId, maxId),
                p -> p.getId() >= minId && p.getId() <= maxId,
                minId,
                maxId
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FiltroPregunta that = (FiltroPregunta) o;
        return minId == that.minId && maxId == that.maxId && tipo == that.tipo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipo, minId, maxId);
    }

    @Override
    public String toString() {
        return enunciado;
    }
}
