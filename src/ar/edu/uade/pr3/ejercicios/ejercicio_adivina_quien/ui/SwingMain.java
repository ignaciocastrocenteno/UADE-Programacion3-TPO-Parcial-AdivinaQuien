package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.ui;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada alternativo para jugar con la interfaz gráfica.
 * A diferencia del Main de consola, esta clase no lee teclado ni ejecuta turnos:
 * únicamente abre VentanaPrincipal, que es la ventana (JFrame) de Swing.
 */
public class SwingMain {
    public static void main(String[] args) {
        // Swing atiende clics y dibuja componentes en el Event Dispatch Thread (EDT).
        // invokeLater programa la creación de la ventana en ese hilo; setVisible la muestra.
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
