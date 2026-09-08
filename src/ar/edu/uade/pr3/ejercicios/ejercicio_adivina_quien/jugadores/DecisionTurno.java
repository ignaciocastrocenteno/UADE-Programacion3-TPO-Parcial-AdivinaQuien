package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.FiltroPregunta;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.Persona;

/**
 * Encapsula la acción elegida por un jugador (o la IA) durante su turno.
 */
public class DecisionTurno {
    public enum TipoAccion {
        PREGUNTAR_FILTRO,
        ADIVINAR_PERSONAJE
    }

    private final TipoAccion tipoAccion;
    private final FiltroPregunta pregunta;
    private final Persona personaAdivinada;
    private final String razonamiento;

    private DecisionTurno(TipoAccion tipoAccion, FiltroPregunta pregunta, Persona personaAdivinada, String razonamiento) {
        this.tipoAccion = tipoAccion;
        this.pregunta = pregunta;
        this.personaAdivinada = personaAdivinada;
        this.razonamiento = razonamiento;
    }

    public static DecisionTurno crearPregunta(FiltroPregunta pregunta, String razonamiento) {
        return new DecisionTurno(TipoAccion.PREGUNTAR_FILTRO, pregunta, null, razonamiento);
    }

    public static DecisionTurno crearAdivinanza(Persona persona, String razonamiento) {
        return new DecisionTurno(TipoAccion.ADIVINAR_PERSONAJE, null, persona, razonamiento);
    }

    public TipoAccion getTipoAccion() {
        return tipoAccion;
    }

    public FiltroPregunta getPregunta() {
        return pregunta;
    }

    public Persona getPersonaAdivinada() {
        return personaAdivinada;
    }

    public String getRazonamiento() {
        return razonamiento;
    }
}
