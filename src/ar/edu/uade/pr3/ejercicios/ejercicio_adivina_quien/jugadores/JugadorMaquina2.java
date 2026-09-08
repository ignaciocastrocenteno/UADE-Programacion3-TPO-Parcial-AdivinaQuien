package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.algoritmos.HistorialPreguntas;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.algoritmos.SelectorPreguntaGreedy;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.FiltroPregunta;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.Persona;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Inteligencia Artificial de la Máquina 2.
 * Más asertiva y parte con la ventaja competitiva de conocer y explotar
 * las preguntas y respuestas formuladas previamente por la Máquina 1.
 */
public class JugadorMaquina2 extends Jugador {

    private final HistorialPreguntas historialCompartido;
    private final Set<HistorialPreguntas.EntradaHistorial> entradasProcesadas;
    private String nombreOponente;

    public JugadorMaquina2(String nombre, Persona personajeSecreto, List<Persona> catalogoInicial, HistorialPreguntas historialCompartido) {
        super(nombre, personajeSecreto, catalogoInicial);
        this.historialCompartido = historialCompartido;
        this.entradasProcesadas = new HashSet<>();
        this.nombreOponente = null;
    }

    public JugadorMaquina2(Persona personajeSecreto, List<Persona> catalogoInicial, HistorialPreguntas historialCompartido) {
        this("Máquina 2 (Asertiva)", personajeSecreto, catalogoInicial, historialCompartido);
    }

    public void setNombreOponente(String nombreOponente) {
        this.nombreOponente = nombreOponente;
    }

    public String getNombreOponente() {
        return nombreOponente;
    }

    /**
     * Sincroniza y aprovecha la ventaja de conocer las preguntas formuladas por la Máquina 1.
     * Poda su propio espacio de búsqueda en base a la información externa obtenida (Divide & Conquer acelerado).
     */
    public int asimilarPreguntasDeMaquina1() {
        if (historialCompartido == null) return 0;

        int descartadosTotales = 0;
        List<HistorialPreguntas.EntradaHistorial> entradas = historialCompartido.getEntradas();

        for (HistorialPreguntas.EntradaHistorial entrada : entradas) {
            // Solo procesamos preguntas no consumidas aún
            if (!entradasProcesadas.contains(entrada)) {
                entradasProcesadas.add(entrada);

                // Solo podemos aplicar el filtro si la pregunta fue formulada sobre nuestro objetivo actual
                // (por ejemplo, preguntas dirigidas al oponente que intentamos adivinar)
                boolean esSobreNuestroObjetivo = (nombreOponente == null 
                        || entrada.getObjetivo() == null 
                        || entrada.getObjetivo().equalsIgnoreCase(nombreOponente));

                if (esSobreNuestroObjetivo && !entrada.getEmisor().equalsIgnoreCase(this.nombre)) {
                    if (!preguntasRealizadas.contains(entrada.getPregunta())) {
                        int antes = candidatosRestantes.size();
                        aplicarFiltro(entrada.getPregunta(), entrada.getRespuesta());
                        int descartados = antes - candidatosRestantes.size();
                        descartadosTotales += descartados;
                    }
                }
            }
        }

        return descartadosTotales;
    }

    /**
     * Toma de decisiones más asertiva.
     * Si el espacio es pequeño (<= 2 candidatos) o no quedan particiones puras, arriesga adivinar con mayor prontitud.
     */
    public DecisionTurno decidirSiguientePaso() {
        // 1. Asimilar información del historial (ventaja de Máquina 2)
        int descartadosPorEspionaje = asimilarPreguntasDeMaquina1();
        String notaVentaja = descartadosPorEspionaje > 0
                ? String.format(" [Ventaja: Se descartaron %d candidatos usando preguntas de Máquina 1]", descartadosPorEspionaje)
                : "";

        if (candidatosRestantes.isEmpty()) {
            return null;
        }

        // 2. Si sólo queda 1 candidato, victoria segura
        if (candidatosRestantes.size() == 1) {
            Persona objetivo = candidatosRestantes.get(0);
            return DecisionTurno.crearAdivinanza(
                    objetivo,
                    String.format("[Asertividad Máxima] Certeza matemática: ID %d (%s).%s",
                            objetivo.getId(), objetivo.getNombreCompleto(), notaVentaja)
            );
        }

        // 3. Si quedan 2 candidatos, la Máquina 2 es más asertiva y opta por arriesgar directamente
        if (candidatosRestantes.size() == 2) {
            Persona candidatoElegido = candidatosRestantes.get(0);
            return DecisionTurno.crearAdivinanza(
                    candidatoElegido,
                    String.format("[Ataque Asertivo] Restan solo 2 candidatos. Arriesgando a ID %d (%s) para ganar en menos turnos.%s",
                            candidatoElegido.getId(), candidatoElegido.getNombreCompleto(), notaVentaja)
            );
        }

        // 4. Búsqueda voraz de la mejor partición (Greedy)
        SelectorPreguntaGreedy.EvaluacionGreedy evaluacion =
                SelectorPreguntaGreedy.seleccionarMejorPregunta(candidatosRestantes, preguntasRealizadas);

        if (evaluacion != null && evaluacion.getPregunta() != null) {
            return DecisionTurno.crearPregunta(
                    evaluacion.getPregunta(),
                    evaluacion.getExplicacionAlgoritmica() + notaVentaja
            );
        }

        // 5. Divide & Conquer por rango de IDs
        FiltroPregunta rango = SelectorPreguntaGreedy.generarPreguntaRangoDicotomico(candidatosRestantes);
        if (rango != null && !preguntasRealizadas.contains(rango)) {
            return DecisionTurno.crearPregunta(
                    rango,
                    String.format("Divide & Conquer Asertivo: Dicotomía en rango [%d..%d]%s",
                            rango.getMinId(), rango.getMaxId(), notaVentaja)
            );
        }

        // Fallback: arriesgar primer candidato disponible de forma segura
        if (!candidatosRestantes.isEmpty()) {
            Persona fallback = candidatosRestantes.get(0);
            return DecisionTurno.crearAdivinanza(
                    fallback,
                    "[Suposición Forzada] No restan filtros discriminantes. ID: " + fallback.getId()
            );
        }

        return null;
    }
}
