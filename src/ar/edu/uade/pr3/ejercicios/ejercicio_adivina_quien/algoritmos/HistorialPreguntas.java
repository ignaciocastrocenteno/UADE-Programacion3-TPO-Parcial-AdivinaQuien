package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.algoritmos;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.FiltroPregunta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Registro de preguntas realizadas en la partida.
 * Permite que la Máquina 2 consuma y aproveche las preguntas y respuestas
 * generadas por la Máquina 1 u otros participantes (ventaja informativa).
 */
public class HistorialPreguntas {

    public static class EntradaHistorial {
        private final String emisor;
        private final String objetivo;
        private final FiltroPregunta pregunta;
        private final boolean respuesta;
        private final int turno;

        public EntradaHistorial(String emisor, String objetivo, FiltroPregunta pregunta, boolean respuesta, int turno) {
            this.emisor = emisor;
            this.objetivo = objetivo;
            this.pregunta = pregunta;
            this.respuesta = respuesta;
            this.turno = turno;
        }

        public EntradaHistorial(String emisor, FiltroPregunta pregunta, boolean respuesta, int turno) {
            this(emisor, null, pregunta, respuesta, turno);
        }

        public String getEmisor() {
            return emisor;
        }

        public String getObjetivo() {
            return objetivo;
        }

        public FiltroPregunta getPregunta() {
            return pregunta;
        }

        public boolean getRespuesta() {
            return respuesta;
        }

        public int getTurno() {
            return turno;
        }

        @Override
        public String toString() {
            String destinoStr = objetivo != null ? " sobre " + objetivo : "";
            return String.format("[Turno %d] %s preguntó%s: '%s' -> Respuesta: %s",
                    turno, emisor, destinoStr, pregunta.getEnunciado(), (respuesta ? "SÍ" : "NO"));
        }
    }

    private final List<EntradaHistorial> entradas;

    public HistorialPreguntas() {
        this.entradas = new ArrayList<>();
    }

    public synchronized void registrar(String emisor, String objetivo, FiltroPregunta pregunta, boolean respuesta, int turno) {
        entradas.add(new EntradaHistorial(emisor, objetivo, pregunta, respuesta, turno));
    }

    public synchronized void registrar(String emisor, FiltroPregunta pregunta, boolean respuesta, int turno) {
        registrar(emisor, null, pregunta, respuesta, turno);
    }

    public synchronized List<EntradaHistorial> getEntradas() {
        return Collections.unmodifiableList(new ArrayList<>(entradas));
    }

    public synchronized List<EntradaHistorial> getEntradasDe(String emisor) {
        List<EntradaHistorial> filtradas = new ArrayList<>();
        for (EntradaHistorial e : entradas) {
            if (e.getEmisor().equalsIgnoreCase(emisor)) {
                filtradas.add(e);
            }
        }
        return filtradas;
    }

    public synchronized List<EntradaHistorial> getEntradasSobre(String objetivo) {
        List<EntradaHistorial> filtradas = new ArrayList<>();
        for (EntradaHistorial e : entradas) {
            if (e.getObjetivo() != null && e.getObjetivo().equalsIgnoreCase(objetivo)) {
                filtradas.add(e);
            }
        }
        return filtradas;
    }

    public synchronized void limpiar() {
        entradas.clear();
    }
}
