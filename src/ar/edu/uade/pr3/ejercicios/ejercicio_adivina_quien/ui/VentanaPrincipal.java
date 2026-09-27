package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.ui;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.juego.JuegoAdivinaQuien;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.juego.PartidaAdivinaQuien;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.persistencia.MarcadorRecord;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.util.List;

/**
 * Ventana única del juego gráfico. Un JFrame es la ventana del sistema operativo;
 * dentro contiene un JPanel por pantalla. CardLayout muestra una de esas pantallas
 * a la vez, sin abrir otras ventanas ni modificar las reglas del juego.
 * La fachada JuegoAdivinaQuien crea las partidas y MarcadorRecord aporta el ranking.
 */
public class VentanaPrincipal extends JFrame {
    // Cada clave identifica un panel añadido a contenido; CardLayout.show usa esa clave.
    private static final String MENU = "menu";
    private static final String SELECCION = "seleccion";
    private static final String PARTIDA = "partida";
    private static final String SIMULACION = "simulacion";
    private static final String RECORDS = "records";

    private final JuegoAdivinaQuien juego = new JuegoAdivinaQuien();
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenido = new JPanel(tarjetas);
    private final PanelSeleccionPersonaje seleccion;
    private final JTextArea textoRecords = new JTextArea();
    private PanelPartida panelPartida;
    private int tipoMaquina;

    /**
     * Prepara las pantallas reutilizables y establece este JFrame como su contenedor.
     * La pantalla de partida se crea aparte cada vez que empieza una partida nueva.
     */
    public VentanaPrincipal() {
        super("Adivina Quién");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // this::iniciarPartida y this::mostrarMenu entregan acciones al panel de selección:
        // el panel avisa a la ventana, pero no necesita conocer cómo se cambian las pantallas.
        seleccion = new PanelSeleccionPersonaje(juego.getPersonajes(), this::iniciarPartida, this::mostrarMenu);
        contenido.add(crearMenu(), MENU);
        contenido.add(seleccion, SELECCION);
        contenido.add(new PanelSimulacion(juego, this::mostrarMenu), SIMULACION);
        contenido.add(crearPanelRecords(), RECORDS);
        setContentPane(contenido);
        mostrarMenu();
    }

    /**
     * BorderLayout ubica título arriba y botones en el centro; GridLayout apila
     * los botones. addActionListener define qué ocurrirá cuando se haga clic.
     */
    private JPanel crearMenu() {
        JPanel menu = new JPanel(new BorderLayout(10, 10));
        menu.setBorder(new EmptyBorder(50, 100, 50, 100));
        menu.add(new JLabel("Adivina Quién — elija un modo", JLabel.CENTER), BorderLayout.NORTH);

        JPanel botones = new JPanel(new GridLayout(0, 1, 8, 8));
        JButton maquina1 = new JButton("Humano vs Máquina 1");
        maquina1.addActionListener(e -> mostrarSeleccion(1));
        JButton maquina2 = new JButton("Humano vs Máquina 2 (partida nueva)");
        maquina2.addActionListener(e -> mostrarSeleccion(2));
        JButton simulacion = new JButton("Máquina vs Máquina");
        simulacion.addActionListener(e -> tarjetas.show(contenido, SIMULACION));
        JButton records = new JButton("Ver récords");
        records.addActionListener(e -> mostrarRecords());
        JButton salir = new JButton("Salir");
        salir.addActionListener(e -> dispose());

        botones.add(maquina1);
        botones.add(maquina2);
        botones.add(simulacion);
        botones.add(records);
        botones.add(salir);
        menu.add(botones, BorderLayout.CENTER);
        return menu;
    }

    /**
     * Crea una vista de solo lectura. JScrollPane permite desplazarse si el
     * texto de JTextArea crece más que el espacio visible de la ventana.
     */
    private JPanel crearPanelRecords() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.add(new JLabel("Marcador récord de victorias"), BorderLayout.NORTH);
        textoRecords.setEditable(false);
        panel.add(new JScrollPane(textoRecords), BorderLayout.CENTER);
        JButton volver = new JButton("Volver al menú");
        volver.addActionListener(e -> mostrarMenu());
        panel.add(volver, BorderLayout.SOUTH);
        return panel;
    }

    /** Guarda el tipo de rival elegido y reinicia la pantalla de selección antes de mostrarla. */
    private void mostrarSeleccion(int tipoMaquina) {
        this.tipoMaquina = tipoMaquina;
        seleccion.preparar(tipoMaquina);
        tarjetas.show(contenido, SELECCION);
    }

    /**
     * Inicia una partida independiente con el nombre e ID elegidos. Solo al
     * vencer a Máquina 1 se ofrece continuar con el historial de esa partida.
     */
    private void iniciarPartida(String nombre, int idSecreto) {
        PartidaAdivinaQuien partida = juego.crearPartidaHumanoVsMaquina(nombre, idSecreto, tipoMaquina);
        Runnable continuar = tipoMaquina == 1 ? () -> continuarConMaquina2(partida) : null;
        mostrarPartida(partida, continuar);
    }

    /** Mantiene el secreto humano y entrega las preguntas de Máquina 1 a Máquina 2. */
    private void continuarConMaquina2(PartidaAdivinaQuien anterior) {
        mostrarPartida(ContinuacionMaquina2.crear(juego, anterior), null);
    }

    /** Sustituye el panel anterior para no reutilizar los candidatos de otra partida. */
    private void mostrarPartida(PartidaAdivinaQuien partida, Runnable continuar) {
        if (panelPartida != null) {
            contenido.remove(panelPartida);
        }
        panelPartida = new PanelPartida(partida, this::mostrarMenu, continuar);
        contenido.add(panelPartida, PARTIDA);
        tarjetas.show(contenido, PARTIDA);
    }

    /**
     * Consulta el ranking al abrir la pantalla para incluir las victorias más recientes.
     * Solo formatea los datos de MarcadorRecord para mostrarlos; no cambia su almacenamiento.
     */
    private void mostrarRecords() {
        List<MarcadorRecord.RegistroRanking> ranking = MarcadorRecord.getInstancia().obtenerRanking();
        StringBuilder texto = new StringBuilder();
        if (ranking.isEmpty()) {
            texto.append("Aún no hay partidas ganadas registradas.");
        } else {
            int puesto = 1;
            for (MarcadorRecord.RegistroRanking registro : ranking) {
                texto.append(puesto++).append(". ").append(registro).append('\n');
            }
        }
        textoRecords.setText(texto.toString());
        tarjetas.show(contenido, RECORDS);
    }

    /** Cambia la tarjeta visible al menú sin crear otro JFrame. */
    private void mostrarMenu() {
        tarjetas.show(contenido, MENU);
    }
}
