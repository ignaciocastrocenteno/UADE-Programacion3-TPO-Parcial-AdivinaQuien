package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.ui;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.juego.JuegoAdivinaQuien;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.juego.PartidaAdivinaQuien;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores.Jugador;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Pantalla de observación Máquina vs Máquina. JTextArea muestra las trazas
 * generadas por PartidaAdivinaQuien; no reproduce las decisiones de las IA.
 * SwingWorker permite ejecutar la partida sin congelar la ventana mientras
 * llegan los mensajes para mostrar al usuario.
 */
public class PanelSimulacion extends JPanel {
    private final JuegoAdivinaQuien juego;
    private final JTextArea pasos = new JTextArea();
    private final JButton iniciar = new JButton("Iniciar simulación");
    private final JButton volver = new JButton("Volver al menú");

    /**
     * @param juego fachada utilizada para crear una partida nueva en cada simulación
     * @param mostrarMenu acción que la ventana ejecuta al pulsar Volver
     */
    public PanelSimulacion(JuegoAdivinaQuien juego, Runnable mostrarMenu) {
        super(new BorderLayout(8, 8));
        this.juego = juego;
        setBorder(new EmptyBorder(16, 16, 16, 16));
        add(new JLabel("Máquina 1 vs Máquina 2"), BorderLayout.NORTH);
        pasos.setEditable(false);
        pasos.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        // El área no es editable: funciona como un registro desplazable de la partida.
        add(new JScrollPane(pasos), BorderLayout.CENTER);

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT));
        iniciar.addActionListener(e -> iniciarSimulacion());
        volver.addActionListener(e -> mostrarMenu.run());
        controles.add(iniciar);
        controles.add(volver);
        add(controles, BorderLayout.SOUTH);
    }

    /**
     * Un clic dispara SwingWorker: doInBackground ejecuta la partida en otro hilo,
     * mientras process y done actualizan componentes en el hilo de eventos de Swing.
     * Así se mantiene la ventana operativa y nunca se escribe en JTextArea
     * directamente desde el hilo que simula el juego.
     */
    private void iniciarSimulacion() {
        pasos.setText("");
        // Impide iniciar otra simulación o salir de esta pantalla hasta que termine.
        iniciar.setEnabled(false);
        volver.setEnabled(false);
        new SwingWorker<Jugador, String>() {
            @Override
            protected Jugador doInBackground() {
                PartidaAdivinaQuien partida = juego.crearPartidaMaquinaVsMaquina();
                // this::publish es el callback de salida: la partida le entrega texto
                // en vez de escribirlo en System.out. SwingWorker lo pasa a process.
                return partida.ejecutarSimulacionMaquinaVsMaquina(true, this::publish);
            }

            /** Swing llama este método en el EDT, por eso aquí sí se modifica JTextArea. */
            @Override
            protected void process(List<String> mensajes) {
                for (String mensaje : mensajes) {
                    pasos.append(mensaje);
                }
                pasos.setCaretPosition(pasos.getDocument().getLength());
            }

            /** Al terminar, get obtiene el resultado o la excepción y habilita los botones. */
            @Override
            protected void done() {
                try {
                    if (get() == null) {
                        pasos.append("Simulación finalizada sin ganador.\n");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    pasos.append("Simulación interrumpida.\n");
                } catch (ExecutionException e) {
                    pasos.append("Error durante la simulación: " + e.getCause().getMessage() + "\n");
                } finally {
                    iniciar.setEnabled(true);
                    volver.setEnabled(true);
                }
            }
        }.execute();
    }
}
