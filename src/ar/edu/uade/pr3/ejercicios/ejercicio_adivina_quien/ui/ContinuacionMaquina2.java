package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.ui;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.algoritmos.HistorialPreguntas;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.juego.JuegoAdivinaQuien;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.juego.PartidaAdivinaQuien;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores.Jugador;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores.JugadorHumano;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores.JugadorMaquina1;

/**
 * Conecta dos partidas desde la interfaz sin cambiar las reglas de ninguna.
 * Máquina 2 asimila las preguntas heredadas con su lógica habitual en su primer turno;
 * aquí solo se trasladan las respuestas de Máquina 1 sobre el mismo secreto humano.
 */
public final class ContinuacionMaquina2 {
    private ContinuacionMaquina2() {
    }

    public static PartidaAdivinaQuien crear(JuegoAdivinaQuien juego, PartidaAdivinaQuien anterior) {
        if (!anterior.estaFinalizada() || anterior.getGanador() != anterior.getJugador1()
                || !(anterior.getJugador1() instanceof JugadorHumano)
                || !(anterior.getJugador2() instanceof JugadorMaquina1)) {
            throw new IllegalArgumentException("Primero debe ganar el humano contra Máquina 1.");
        }

        Jugador humano = anterior.getJugador1();
        PartidaAdivinaQuien siguiente = juego.crearPartidaHumanoVsMaquina(
                humano.getNombre(), humano.getPersonajeSecreto().getId(), 2);

        // Las preguntas humanas eran sobre el secreto de Máquina 1 y no sirven
        // para deducir el secreto humano que Máquina 2 intentará descubrir.
        for (HistorialPreguntas.EntradaHistorial entrada : anterior.getHistorialPreguntas()
                .getEntradasDe(anterior.getJugador2().getNombre())) {
            if (humano.getNombre().equalsIgnoreCase(entrada.getObjetivo())) {
                siguiente.getHistorialPreguntas().registrar(entrada.getEmisor(), entrada.getObjetivo(),
                        entrada.getPregunta(), entrada.getRespuesta(), entrada.getTurno());
            }
        }
        return siguiente;
    }
}
