package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.algoritmos;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.FiltroPregunta;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.Persona;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aplica el algoritmo 'Divide & Conquer' para segmentar
 * el espacio de búsqueda de candidatos en subconjuntos disjuntos y descartar
 * la porción no compatible según la respuesta obtenida.
 */
public class FiltroParticion {

    /**
     * Resultado de la partición de un conjunto de candidatos.
     */
    public static class ResultadoParticion {
        private final List<Persona> cumplen;
        private final List<Persona> noCumplen;

        public ResultadoParticion(List<Persona> cumplen, List<Persona> noCumplen) {
            this.cumplen = Collections.unmodifiableList(cumplen);
            this.noCumplen = Collections.unmodifiableList(noCumplen);
        }

        public List<Persona> getCumplen() {
            return cumplen;
        }

        public List<Persona> getNoCumplen() {
            return noCumplen;
        }

        /**
         * Selecciona el subconjunto ganador según la respuesta del árbitro/oponente (SÍ / NO).
         */
        public List<Persona> seleccionarSubconjunto(boolean respuestaEsSi) {
            return respuestaEsSi ? cumplen : noCumplen;
        }

        public int getDiferenciaBalance() {
            return Math.abs(cumplen.size() - noCumplen.size());
        }
    }

    /**
     * Divide el conjunto de candidatos en dos subconjuntos (Divide).
     * Complejidad: O(N) donde N es la cantidad de candidatos actuales.
     */
    public static ResultadoParticion dividir(List<Persona> candidatos, FiltroPregunta pregunta) {
        List<Persona> cumplen = new ArrayList<>();
        List<Persona> noCumplen = new ArrayList<>();

        for (Persona persona : candidatos) {
            if (pregunta.cumple(persona)) {
                cumplen.add(persona);
            } else {
                noCumplen.add(persona);
            }
        }

        return new ResultadoParticion(cumplen, noCumplen);
    }

    /**
     * Conquista el subproblema reduciendo el espacio de búsqueda al subconjunto que coincide
     * con la respuesta confirmada (Conquer).
     */
    public static List<Persona> podarEspacio(List<Persona> candidatos, FiltroPregunta pregunta, boolean respuestaAfirmativa) {
        ResultadoParticion particion = dividir(candidatos, pregunta);
        return new ArrayList<>(particion.seleccionarSubconjunto(respuestaAfirmativa));
    }
}
