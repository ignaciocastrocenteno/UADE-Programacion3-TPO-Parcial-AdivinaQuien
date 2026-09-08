package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.algoritmos;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.FiltroPregunta;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.Persona;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Aplica el principio de 'Greedy Algorithm' para seleccionar
 * en cada turno la mejor pregunta disponible que maximice la ganancia de información
 * (es decir, aquella que divida el espacio de candidatos lo más cercano posible al 50/50).
 */
public class SelectorPreguntaGreedy {

    /**
     * Retorna una lista con todas las preguntas atómicas por atributos físicos aplicables.
     */
    public static List<FiltroPregunta> generarBancoPreguntasAtributos() {
        List<FiltroPregunta> preguntas = new ArrayList<>();
        preguntas.add(FiltroPregunta.esMasculino());
        preguntas.add(FiltroPregunta.esFemenino());
        preguntas.add(FiltroPregunta.esCalvo());
        preguntas.add(FiltroPregunta.tienePelo());
        preguntas.add(FiltroPregunta.usaLentes());
        preguntas.add(FiltroPregunta.noUsaLentes());
        preguntas.add(FiltroPregunta.peloColorado());
        preguntas.add(FiltroPregunta.peloNegro());
        preguntas.add(FiltroPregunta.peloAmarillo());
        return preguntas;
    }

    /**
     * Genera una pregunta de rango biseccional óptima para el conjunto actual (Divide & Conquer binario).
     */
    public static FiltroPregunta generarPreguntaRangoDicotomico(List<Persona> candidatos) {
        if (candidatos == null || candidatos.size() < 2) {
            return null;
        }
        int mitad = candidatos.size() / 2;
        int minId = candidatos.get(0).getId();
        int maxId = candidatos.get(mitad - 1).getId();
        return FiltroPregunta.rangoIds(minId, maxId);
    }

    /**
     * Evalúa y selecciona vorazmente la mejor pregunta disponible.
     * Criterio Voraz: Maximizar la bisección perfecta (minimizar |cumplen - noCumplen|),
     * garantizando que ambos subconjuntos sean no vacíos para podar candidatos sí o sí.
     *
     * @param candidatos Lista de candidatos actualmente viables.
     * @param preguntasYaRealizadas Conjunto de preguntas ya ejecutadas previamente para evitar redundancia.
     * @return La FiltroPregunta óptima según el criterio Greedy, o null si ninguna discrimina.
     */
    public static EvaluacionGreedy seleccionarMejorPregunta(List<Persona> candidatos, Set<FiltroPregunta> preguntasYaRealizadas) {
        if (candidatos == null || candidatos.size() <= 1) {
            return null;
        }

        List<FiltroPregunta> universoPreguntas = generarBancoPreguntasAtributos();
        
        // Agregar también la opción de bisección dicotómica por ID como comodín
        FiltroPregunta preguntaRango = generarPreguntaRangoDicotomico(candidatos);
        if (preguntaRango != null) {
            universoPreguntas.add(preguntaRango);
        }

        FiltroPregunta mejorPregunta = null;
        int mejorDiferencia = Integer.MAX_VALUE;
        int mejorCumplen = 0;
        int mejorNoCumplen = 0;

        for (FiltroPregunta pregunta : universoPreguntas) {
            if (preguntasYaRealizadas != null && preguntasYaRealizadas.contains(pregunta)) {
                continue;
            }

            FiltroParticion.ResultadoParticion particion = FiltroParticion.dividir(candidatos, pregunta);
            int cantCumplen = particion.getCumplen().size();
            int cantNoCumplen = particion.getNoCumplen().size();

            // Si la pregunta no discrimina (0 cumplen o todos cumplen), no aporta ganancia de información
            if (cantCumplen == 0 || cantNoCumplen == 0) {
                continue;
            }

            int diferencia = Math.abs(cantCumplen - cantNoCumplen);

            // Criterio Voraz (Greedy): quedarse con la mínima discrepancia respecto a la bisección perfecta (50/50)
            if (diferencia < mejorDiferencia) {
                mejorDiferencia = diferencia;
                mejorPregunta = pregunta;
                mejorCumplen = cantCumplen;
                mejorNoCumplen = cantNoCumplen;

                // Si encontramos un corte exacto 50/50 (diferencia 0 o 1), es el óptimo absoluto
                if (diferencia <= 1) {
                    break;
                }
            }
        }

        if (mejorPregunta == null) {
            return null;
        }

        return new EvaluacionGreedy(mejorPregunta, mejorDiferencia, mejorCumplen, mejorNoCumplen, candidatos.size());
    }

    /**
     * Encapsula los detalles y métricas de la decisión voraz tomada.
     */
    public static class EvaluacionGreedy {
        private final FiltroPregunta pregunta;
        private final int diferenciaBalance;
        private final int cantidadCumplen;
        private final int cantidadNoCumplen;
        private final int totalCandidatos;

        public EvaluacionGreedy(FiltroPregunta pregunta, int diferenciaBalance, int cantidadCumplen, int cantidadNoCumplen, int totalCandidatos) {
            this.pregunta = pregunta;
            this.diferenciaBalance = diferenciaBalance;
            this.cantidadCumplen = cantidadCumplen;
            this.cantidadNoCumplen = cantidadNoCumplen;
            this.totalCandidatos = totalCandidatos;
        }

        public FiltroPregunta getPregunta() {
            return pregunta;
        }

        public int getDiferenciaBalance() {
            return diferenciaBalance;
        }

        public int getCantidadCumplen() {
            return cantidadCumplen;
        }

        public int getCantidadNoCumplen() {
            return cantidadNoCumplen;
        }

        public int getTotalCandidatos() {
            return totalCandidatos;
        }

        public String getExplicacionAlgoritmica() {
            return String.format(
                "Criterio Voraz: Pregunta '%s' divide %d candidatos en [Cumplen: %d | No Cumplen: %d] (Desbalance: %d)",
                pregunta.getEnunciado(), totalCandidatos, cantidadCumplen, cantidadNoCumplen, diferenciaBalance
            );
        }
    }
}
