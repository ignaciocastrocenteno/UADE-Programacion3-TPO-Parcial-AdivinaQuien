package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.juego;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.algoritmos.HistorialPreguntas;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores.Jugador;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.FiltroPregunta;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.Persona;

import java.util.Objects;

/**
 * Árbitro / Oráculo imparcial del juego.
 * Garantiza el principio de encapsulamiento: ningún jugador (humano o máquina)
 * puede acceder directamente a la variable del personaje secreto del oponente.
 */
public class ArbitroJuego {
    private final Jugador jugador1;
    private final Jugador jugador2;
    private final HistorialPreguntas historialPreguntas;

    public ArbitroJuego(Jugador jugador1, Jugador jugador2, HistorialPreguntas historialPreguntas) {
        this.jugador1 = Objects.requireNonNull(jugador1, "Jugador 1 no puede ser nulo");
        this.jugador2 = Objects.requireNonNull(jugador2, "Jugador 2 no puede ser nulo");
        this.historialPreguntas = historialPreguntas != null ? historialPreguntas : new HistorialPreguntas();
    }

    public Jugador getJugador1() {
        return jugador1;
    }

    public Jugador getJugador2() {
        return jugador2;
    }

    public HistorialPreguntas getHistorialPreguntas() {
        return historialPreguntas;
    }

    public Jugador getOponenteDe(Jugador jugador) {
        if (jugador.equals(jugador1)) return jugador2;
        if (jugador.equals(jugador2)) return jugador1;
        throw new IllegalArgumentException("El jugador no pertenece a esta partida.");
    }

    /**
     * Responde a una pregunta formulada por un jugador sobre el personaje secreto del oponente.
     * Registra la interacción en el historial compartido.
     *
     * @param consultor Jugador que formula la pregunta.
     * @param pregunta Filtro de atributo o rango consultado.
     * @param turnoActual Número de turno actual.
     * @return boolean: true si el personaje secreto del oponente cumple la condición; false en caso contrario.
     */
    public boolean responderPregunta(Jugador consultor, FiltroPregunta pregunta, int turnoActual) {
        Jugador oponente = getOponenteDe(consultor);
        Persona secretoOponente = oponente.getPersonajeSecreto();
        boolean respuesta = pregunta.cumple(secretoOponente);

        // Registrar en el historial para que la Máquina 2 (u observadores) puedan consumirlo con contexto de objetivo
        historialPreguntas.registrar(consultor.getNombre(), oponente.getNombre(), pregunta, respuesta, turnoActual);

        // El consultor actualiza su propio espacio de búsqueda mediante Divide & Conquer
        consultor.aplicarFiltro(pregunta, respuesta);

        return respuesta;
    }

    /**
     * Valida una suposición directa de personaje realizada por un jugador.
     *
     * @param consultor Jugador que arriesga la respuesta.
     * @param idPersonaje ID del personaje que cree que es el secreto del oponente.
     * @return true si acertó exactamente el personaje secreto del oponente; false en caso contrario.
     */
    public boolean validarSuposicionDirecta(Jugador consultor, int idPersonaje) {
        Jugador oponente = getOponenteDe(consultor);
        boolean acierto = oponente.getPersonajeSecreto().getId() == idPersonaje;
        if (!acierto) {
            consultor.descartarCandidatoPorId(idPersonaje);
        }
        return acierto;
    }
}
