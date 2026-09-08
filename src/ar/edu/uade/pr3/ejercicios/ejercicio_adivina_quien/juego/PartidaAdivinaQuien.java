package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.juego;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.algoritmos.HistorialPreguntas;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores.DecisionTurno;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores.Jugador;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores.JugadorMaquina1;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores.JugadorMaquina2;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.FiltroPregunta;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.Persona;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.persistencia.MarcadorRecord;

/**
 * Controlador del flujo y ciclo de vida de una partida de "Adivina Quién".
 */
public class PartidaAdivinaQuien {

    private final Jugador jugador1;
    private final Jugador jugador2;
    private final ArbitroJuego arbitro;
    private final HistorialPreguntas historialPreguntas;
    private final MarcadorRecord marcadorRecord;
    
    private int turnoActual;
    private Jugador ganador;
    private boolean partidaFinalizada;

    public PartidaAdivinaQuien(Jugador jugador1, Jugador jugador2) {
        this(jugador1, jugador2, new HistorialPreguntas(), MarcadorRecord.getInstancia());
    }

    public PartidaAdivinaQuien(Jugador jugador1, Jugador jugador2, HistorialPreguntas historial, MarcadorRecord marcador) {
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.historialPreguntas = historial;
        this.marcadorRecord = marcador;
        this.arbitro = new ArbitroJuego(jugador1, jugador2, historialPreguntas);
        this.turnoActual = 0;
        this.ganador = null;
        this.partidaFinalizada = false;

        if (jugador1 instanceof JugadorMaquina2) {
            ((JugadorMaquina2) jugador1).setNombreOponente(jugador2.getNombre());
        }
        if (jugador2 instanceof JugadorMaquina2) {
            ((JugadorMaquina2) jugador2).setNombreOponente(jugador1.getNombre());
        }
    }

    public Jugador getJugador1() {
        return jugador1;
    }

    public Jugador getJugador2() {
        return jugador2;
    }

    public ArbitroJuego getArbitro() {
        return arbitro;
    }

    public HistorialPreguntas getHistorialPreguntas() {
        return historialPreguntas;
    }

    public int getTurnoActual() {
        return turnoActual;
    }

    public Jugador getGanador() {
        return ganador;
    }

    public boolean estaFinalizada() {
        return partidaFinalizada;
    }

    /**
     * Ejecuta una simulación completa paso a paso de Máquina vs Máquina con reporte por consola.
     */
    public Jugador ejecutarSimulacionMaquinaVsMaquina(boolean modoDetallado) {
        if (modoDetallado) {
            System.out.println("\n==========================================================================");
            System.out.println("     INICIANDO MODO SIMULACIÓN: MÁQUINA 1 vs MÁQUINA 2");
            System.out.println("==========================================================================");
            System.out.printf("Jugador 1: %s [Secreto elegido: ID %d - %s]%n",
                    jugador1.getNombre(), jugador1.getPersonajeSecreto().getId(), jugador1.getPersonajeSecreto().getNombreCompleto());
            System.out.printf("Jugador 2: %s [Secreto elegido: ID %d - %s]%n",
                    jugador2.getNombre(), jugador2.getPersonajeSecreto().getId(), jugador2.getPersonajeSecreto().getNombreCompleto());
            System.out.println("--------------------------------------------------------------------------\n");
        }

        while (!partidaFinalizada && turnoActual < 100) {
            turnoActual++;

            if (modoDetallado) {
                System.out.printf(">>> [RONDA %d] <<<%n", turnoActual);
            }

            // Turno Jugador 1
            procesarTurnoIA(jugador1, modoDetallado);
            if (partidaFinalizada) break;

            // Turno Jugador 2
            procesarTurnoIA(jugador2, modoDetallado);
            if (partidaFinalizada) break;

            if (modoDetallado) {
                System.out.printf("--- Fin de Ronda %d: Candidatos restantes -> %s: %d | %s: %d ---%n%n",
                        turnoActual,
                        jugador1.getNombre(), jugador1.getCantidadCandidatosRestantes(),
                        jugador2.getNombre(), jugador2.getCantidadCandidatosRestantes());
            }
        }

        if (ganador != null) {
            marcadorRecord.registrarVictoria(ganador.getNombre());
            if (modoDetallado) {
                System.out.println("\n==========================================================================");
                System.out.printf("   ¡VICTORIA PARA: %s en %d turnos!%n", ganador.getNombre(), turnoActual);
                System.out.printf("   Personaje adivinado correctamente: %s (ID %d)%n",
                        arbitro.getOponenteDe(ganador).getPersonajeSecreto().getNombreCompleto(),
                        arbitro.getOponenteDe(ganador).getPersonajeSecreto().getId());
                System.out.printf("   Total victorias acumuladas de %s: %d%n",
                        ganador.getNombre(), marcadorRecord.obtenerVictorias(ganador.getNombre()));
                System.out.println("==========================================================================\n");
            }
        }

        return ganador;
    }

    private void procesarTurnoIA(Jugador jugadorIA, boolean modoDetallado) {
        DecisionTurno decision;

        if (jugadorIA instanceof JugadorMaquina1) {
            decision = ((JugadorMaquina1) jugadorIA).decidirSiguientePaso();
        } else if (jugadorIA instanceof JugadorMaquina2) {
            decision = ((JugadorMaquina2) jugadorIA).decidirSiguientePaso();
        } else {
            return;
        }

        if (decision == null) return;

        if (decision.getTipoAccion() == DecisionTurno.TipoAccion.ADIVINAR_PERSONAJE) {
            Persona suposicion = decision.getPersonaAdivinada();
            if (modoDetallado) {
                System.out.printf("[%s] Decide ADIVINAR DIRECTAMENTE: ID %d (%s)%n",
                        jugadorIA.getNombre(), suposicion.getId(), suposicion.getNombreCompleto());
                System.out.printf("    Razonamiento: %s%n", decision.getRazonamiento());
            }

            boolean acierto = arbitro.validarSuposicionDirecta(jugadorIA, suposicion.getId());
            if (acierto) {
                ganador = jugadorIA;
                partidaFinalizada = true;
                if (modoDetallado) {
                    System.out.printf("    -> ¡ACIERTO ROTUNDO! %s descubrió el personaje secreto.%n", jugadorIA.getNombre());
                }
            } else {
                if (modoDetallado) {
                    System.out.printf("    -> FALLO. El personaje no era %s. Descartado del espacio de búsqueda.%n", suposicion.getNombreCompleto());
                }
            }
        } else {
            FiltroPregunta pregunta = decision.getPregunta();
            if (modoDetallado) {
                System.out.printf("[%s] Pregunta: \"%s\"%n", jugadorIA.getNombre(), pregunta.getEnunciado());
                System.out.printf("    Estrategia: %s%n", decision.getRazonamiento());
            }

            boolean respuesta = arbitro.responderPregunta(jugadorIA, pregunta, turnoActual);
            if (modoDetallado) {
                System.out.printf("    Árbitro responde: [%s]. Candidatos restantes de %s: %d%n",
                        (respuesta ? "SÍ" : "NO"), jugadorIA.getNombre(), jugadorIA.getCantidadCandidatosRestantes());
            }
        }
    }

    /**
     * Avanza el contador de turnos de la partida.
     */
    public void avanzarTurno() {
        this.turnoActual++;
    }

    /**
     * Ejecuta el turno de un jugador humano cuando formula una pregunta de filtro o rango.
     *
     * @param pregunta Pregunta/Filtro a consultar.
     * @return boolean con la respuesta del árbitro (true = SÍ, false = NO).
     */
    public boolean jugarTurnoHumanoPregunta(FiltroPregunta pregunta) {
        if (partidaFinalizada) {
            throw new IllegalStateException("La partida ya ha finalizado.");
        }
        return arbitro.responderPregunta(jugador1, pregunta, turnoActual);
    }

    /**
     * Ejecuta el turno de un jugador humano cuando arriesga una suposición directa de ID.
     *
     * @param idPersonaje ID del personaje arriesgado.
     * @return boolean true si acertó (gana el juego), false si falló.
     */
    public boolean jugarTurnoHumanoAdivinanza(int idPersonaje) {
        if (partidaFinalizada) {
            throw new IllegalStateException("La partida ya ha finalizado.");
        }
        boolean acierto = arbitro.validarSuposicionDirecta(jugador1, idPersonaje);
        if (acierto) {
            ganador = jugador1;
            partidaFinalizada = true;
            marcadorRecord.registrarVictoria(jugador1.getNombre());
        }
        return acierto;
    }

    /**
     * Ejecuta el turno del oponente (Máquina 1 o Máquina 2) en partidas interactivas.
     *
     * @param modoDetallado true para imprimir el razonamiento algorítmico y poda.
     * @return DecisionTurno tomada por la IA.
     */
    public DecisionTurno jugarTurnoMaquina(boolean modoDetallado) {
        if (partidaFinalizada) {
            throw new IllegalStateException("La partida ya ha finalizado.");
        }
        procesarTurnoIA(jugador2, modoDetallado);
        if (ganador != null && ganador.equals(jugador2)) {
            marcadorRecord.registrarVictoria(jugador2.getNombre());
        }
        return null;
    }
}
