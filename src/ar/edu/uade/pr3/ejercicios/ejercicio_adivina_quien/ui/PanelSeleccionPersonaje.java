package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.ui;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.Persona;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Pantalla de selección dentro de VentanaPrincipal. Un JPanel agrupa los
 * componentes de esta pantalla, pero no es una ventana independiente.
 * Muestra el catálogo en una JTable y entrega nombre e ID a la ventana;
 * la creación de la partida queda en la fachada del juego.
 */
public class PanelSeleccionPersonaje extends JPanel {
    private final JLabel titulo = new JLabel();
    private final JTextField nombre = new JTextField(20);
    private final JTable personajes;

    /**
     * @param catalogo personajes oficiales que se mostrarán como filas de la tabla
     * @param iniciar acción recibida de la ventana: se invoca con nombre e ID seleccionado
     * @param volver acción recibida de la ventana para regresar al menú
     */
    public PanelSeleccionPersonaje(List<Persona> catalogo, BiConsumer<String, Integer> iniciar, Runnable volver) {
        super(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        // BorderLayout separa cabecera (NORTH), tabla (CENTER) y controles (SOUTH).
        // GridLayout coloca el título y el campo de nombre en filas separadas.
        JPanel encabezado = new JPanel(new GridLayout(0, 1));
        encabezado.add(titulo);
        JPanel nombrePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nombrePanel.add(new JLabel("Tu nombre:"));
        nombrePanel.add(nombre);
        encabezado.add(nombrePanel);
        add(encabezado, BorderLayout.NORTH);

        // JTable pinta datos del DefaultTableModel; las filas son una vista de Persona,
        // no objetos Persona editables. La tabla solo sirve para elegir un secreto.
        DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Nombre", "Género", "Calvo", "Lentes", "Pelo"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        for (Persona persona : catalogo) {
            modelo.addRow(new Object[]{persona.getId(), persona.getNombreCompleto(), persona.getGenero(),
                    persona.esCalvo() ? "Sí" : "No", persona.usaLentes() ? "Sí" : "No", persona.getColorPelo()});
        }
        personajes = new JTable(modelo);
        personajes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // JScrollPane deja consultar los 23 personajes aunque no entren en pantalla.
        add(new JScrollPane(personajes), BorderLayout.CENTER);

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton empezar = new JButton("Iniciar partida");
        // El listener se ejecuta al pulsar el botón: primero valida la fila seleccionada.
        empezar.addActionListener(e -> {
            int fila = personajes.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccioná un personaje secreto.");
                return;
            }
            String nombreJugador = nombre.getText().trim();
            if (nombreJugador.isEmpty()) {
                nombreJugador = "Humano";
            }
            // La primera columna almacena el ID real; el índice de fila no es el ID.
            // accept llama a la acción de la ventana, que crea la partida y cambia de pantalla.
            iniciar.accept(nombreJugador, (Integer) personajes.getValueAt(fila, 0));
        });
        JButton atras = new JButton("Volver al menú");
        atras.addActionListener(e -> volver.run());
        controles.add(empezar);
        controles.add(atras);
        add(controles, BorderLayout.SOUTH);
    }

    /**
     * La ventana reutiliza este panel en ambos modos humanos. Actualiza el título
     * y limpia nombre y selección para no arrastrar el secreto de la partida anterior.
     */
    public void preparar(int tipoMaquina) {
        titulo.setText("Humano vs Máquina " + tipoMaquina + " — elegí tu personaje secreto (23 disponibles)");
        nombre.setText("");
        personajes.clearSelection();
    }
}
