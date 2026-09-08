package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.algoritmos.SelectorPreguntaGreedy;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.FiltroPregunta;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.Persona;

import java.util.List;

/**
 * Inteligencia Artificial de la Máquina 1.
 * Aplica una heurística Voraz (Greedy) para seleccionar el filtro que maximice
 * la bisección de candidatos (50/50) y cuando el espacio se reduce a 1 elemento,
 * lanza la suposición directa definitiva.
 */
public class JugadorMaquina1 extends Jugador {

    public JugadorMaquina1(String nombre, Persona personajeSecreto, List<Persona> catalogoInicial) {
        super(nombre, personajeSecreto, catalogoInicial);
    }

    public JugadorMaquina1(Persona personajeSecreto, List<Persona> catalogoInicial) {
        this("Máquina 1 (Greedy)", personajeSecreto, catalogoInicial);
    }

    /**
     * Calcula la mejor decisión del turno aplicando el Algoritmo Voraz sobre los candidatos restantes.
     */
    public DecisionTurno decidirSiguientePaso() {
        // Si sólo queda 1 candidato, certeza total: adivinar directamente
        if (candidatosRestantes.size() == 1) {
            Persona objetivo = candidatosRestantes.get(0);
            return DecisionTurno.crearAdivinanza(
                    objetivo,
                    String.format("[Deducción Certera] Espacio de búsqueda reducido a 1 único candidato: ID %d (%s).",
                            objetivo.getId(), objetivo.getNombreCompleto())
            );
        }

        // Si quedan 0 candidatos por algún desajuste externo, fallback
        if (candidatosRestantes.isEmpty()) {
            return null;
        }

        // Aplicar Algoritmo Voraz (Greedy) para seleccionar la pregunta con menor desbalance
        SelectorPreguntaGreedy.EvaluacionGreedy evaluacion =
                SelectorPreguntaGreedy.seleccionarMejorPregunta(candidatosRestantes, preguntasRealizadas);

        if (evaluacion != null && evaluacion.getPregunta() != null) {
            return DecisionTurno.crearPregunta(
                    evaluacion.getPregunta(),
                    evaluacion.getExplicacionAlgoritmica()
            );
        }

        // Si no quedan filtros discriminantes útiles pero hay más de 1 candidato,
        // aplicar Divide & Conquer por bisección de rango de IDs
        FiltroPregunta rango = SelectorPreguntaGreedy.generarPreguntaRangoDicotomico(candidatosRestantes);
        if (rango != null && !preguntasRealizadas.contains(rango)) {
            return DecisionTurno.crearPregunta(
                    rango,
                    String.format("Divide & Conquer: Partición dicotómica sobre IDs en rango [%d..%d]",
                            rango.getMinId(), rango.getMaxId())
            );
        }

        // Si ya no es posible filtrar más, arriesgar con el primer candidato
        Persona primerCandidato = candidatosRestantes.get(0);
        return DecisionTurno.crearAdivinanza(
                primerCandidato,
                "[Suposición Final] No hay más filtros discriminantes disponibles. Arriesgando al ID " + primerCandidato.getId()
        );
    }
}
