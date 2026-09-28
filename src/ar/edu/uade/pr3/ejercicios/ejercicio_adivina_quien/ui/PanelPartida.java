package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.ui;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.algoritmos.HistorialPreguntas;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.juego.JuegoAdivinaQuien;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.juego.PartidaAdivinaQuien;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores.Jugador;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores.JugadorMaquina2;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.FiltroPregunta;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.Persona;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla de Humano vs Máquina. Como JPanel, reúne etiquetas de estado, áreas
 * de texto y botones dentro de la ventana principal. Cada clic llama a métodos
 * de PartidaAdivinaQuien; la interfaz solo presenta sus resultados y candidatos,
 * sin implementar reglas ni estrategias de los jugadores.
 */
public class PanelPartida extends JPanel {
    private final PartidaAdivinaQuien partida;
    private final JLabel turno = new JLabel();
    private final JLabel estado = new JLabel();
    private final JLabel cantidades = new JLabel();
    private final JLabel resultado = new JLabel("Último resultado: aún no hay acciones.");
    private final JTextArea candidatos = new JTextArea();
    private final JTextArea acciones = new JTextArea();
    private final JTextField minimo = new JTextField(3);
    private final JTextField maximo = new JTextField(3);
    private final JTextField idAdivinanza = new JTextField(3);
    private final List<JButton> botonesAccion = new ArrayList<>();
    private final JButton continuar = new JButton("Continuar contra Máquina 2");
    private final boolean ofreceContinuacion;

    public PanelPartida(PartidaAdivinaQuien partida, Runnable mostrarMenu) {
        this(partida, mostrarMenu, null);
    }

    /**
     * @param partida partida ya creada por JuegoAdivinaQuien; conserva los dos jugadores
     * @param mostrarMenu acción provista por la ventana para regresar al menú
     * @param continuarConMaquina2 continuación opcional, visible solo si el humano vence a Máquina 1
     */
    public PanelPartida(PartidaAdivinaQuien partida, Runnable mostrarMenu, Runnable continuarConMaquina2) {
        super(new BorderLayout(8, 8));
        this.partida = partida;
        this.ofreceContinuacion = continuarConMaquina2 != null;
        setBorder(new EmptyBorder(12, 12, 12, 12));

        // BorderLayout reserva NORTH para el resumen, CENTER para las listas y SOUTH
        // para los controles. La cabecera usa una fila por cada JLabel informativa.
        JPanel cabecera = new JPanel(new GridLayout(0, 1));
        cabecera.add(new JLabel("Tu personaje secreto: ID " + partida.getJugador1().getPersonajeSecreto().getId()
                + " - " + partida.getJugador1().getPersonajeSecreto().getNombreCompleto()));
        cabecera.add(turno);
        cabecera.add(cantidades);
        cabecera.add(resultado);
        cabecera.add(estado);
        add(cabecera, BorderLayout.NORTH);

        // GridLayout deja ver candidatos e historial lado a lado. JTextArea muestra
        // texto de varias líneas; JScrollPane agrega desplazamiento cuando no cabe.
        JPanel tablero = new JPanel(new GridLayout(1, 2, 8, 8));
        candidatos.setEditable(false);
        candidatos.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane lista = new JScrollPane(candidatos);
        lista.setBorder(new TitledBorder("Tus candidatos posibles"));
        tablero.add(lista);
        acciones.setEditable(false);
        JScrollPane historial = new JScrollPane(acciones);
        historial.setBorder(new TitledBorder("Preguntas y resultados"));
        tablero.add(historial);
        add(tablero, BorderLayout.CENTER);

        // Al continuar, las preguntas heredadas aún no podaron candidatos: Máquina 2
        // las asimila con su propia lógica cuando llega su primer turno.
        List<HistorialPreguntas.EntradaHistorial> heredadas = partida.getHistorialPreguntas().getEntradas();
        if (partida.getJugador2() instanceof JugadorMaquina2 && !heredadas.isEmpty()) {
            acciones.append("Máquina 2 conoce " + heredadas.size() + " pregunta(s) y respuestas de Máquina 1:\n");
            for (HistorialPreguntas.EntradaHistorial entrada : heredadas) {
                acciones.append("  " + entrada + "\n");
            }
            acciones.append("Las aplicará en su primer turno.\n\n");
        }

        JPanel controles = new JPanel(new BorderLayout(8, 8));
        // Las fábricas de FiltroPregunta son las mismas que utiliza la consola.
        // GridLayout ordena los nueve filtros en tres columnas.
        JPanel filtros = new JPanel(new GridLayout(0, 3, 6, 6));
        agregarFiltro(filtros, "Masculino", FiltroPregunta.esMasculino());
        agregarFiltro(filtros, "Femenino", FiltroPregunta.esFemenino());
        agregarFiltro(filtros, "Calvo", FiltroPregunta.esCalvo());
        agregarFiltro(filtros, "Tiene pelo", FiltroPregunta.tienePelo());
        agregarFiltro(filtros, "Usa lentes", FiltroPregunta.usaLentes());
        agregarFiltro(filtros, "No usa lentes", FiltroPregunta.noUsaLentes());
        agregarFiltro(filtros, "Pelo colorado", FiltroPregunta.peloColorado());
        agregarFiltro(filtros, "Pelo negro", FiltroPregunta.peloNegro());
        agregarFiltro(filtros, "Pelo amarillo", FiltroPregunta.peloAmarillo());
        controles.add(filtros, BorderLayout.NORTH);

        JPanel otrasAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        otrasAcciones.add(new JLabel("Rango ID (1-23):"));
        otrasAcciones.add(minimo);
        otrasAcciones.add(new JLabel("a"));
        otrasAcciones.add(maximo);
        JButton rango = new JButton("Preguntar rango");
        rango.addActionListener(e -> preguntarRango());
        botonesAccion.add(rango);
        otrasAcciones.add(rango);
        otrasAcciones.add(new JLabel("Adivinar ID:"));
        otrasAcciones.add(idAdivinanza);
        JButton adivinar = new JButton("Adivinar");
        adivinar.addActionListener(e -> adivinar());
        botonesAccion.add(adivinar);
        otrasAcciones.add(adivinar);
        controles.add(otrasAcciones, BorderLayout.CENTER);

        JButton volver = new JButton("Volver al menú");
        volver.addActionListener(e -> mostrarMenu.run());
        JPanel navegacion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navegacion.add(volver);
        if (ofreceContinuacion) {
            continuar.setVisible(false);
            continuar.addActionListener(e -> continuarConMaquina2.run());
            navegacion.add(continuar);
        }
        controles.add(navegacion, BorderLayout.SOUTH);
        add(controles, BorderLayout.SOUTH);
        actualizarEstado();
    }

    /**
     * Asocia un filtro existente a un JButton: el listener ejecuta preguntar
     * al recibir un clic. Guarda el botón para deshabilitarlo al terminar el juego.
     */
    private void agregarFiltro(JPanel panel, String titulo, FiltroPregunta filtro) {
        JButton boton = new JButton(titulo);
        boton.addActionListener(e -> preguntar(filtro));
        botonesAccion.add(boton);
        panel.add(boton);
    }

    /**
     * Convierte los dos JTextField en IDs y valida el orden del rango antes de
     * formular la pregunta. Una entrada inválida no consume el turno del humano.
     */
    private void preguntarRango() {
        Integer min = leerId(minimo);
        if (min == null) return;
        Integer max = leerId(maximo);
        if (max == null) return;
        if (min > max) {
            JOptionPane.showMessageDialog(this, "El ID mínimo no puede ser mayor al máximo.");
            return;
        }
        preguntar(FiltroPregunta.rangoIds(min, max));
    }

    /**
     * Avanza el contador antes de consultar al árbitro mediante la partida.
     * El árbitro responde y actualiza los candidatos; el panel solo informa
     * el resultado y pasa el control a finalizarTurno para jugar con la IA.
     */
    private void preguntar(FiltroPregunta filtro) {
        if (partida.estaFinalizada()) return;
        partida.avanzarTurno();
        boolean respuesta = partida.jugarTurnoHumanoPregunta(filtro);
        String detalle = "Turno " + partida.getTurnoActual() + " - " + filtro.getEnunciado()
                + " Respuesta: " + (respuesta ? "SÍ" : "NO") + ".\n";
        acciones.append(detalle);
        resultado.setText("Último resultado: " + (respuesta ? "SÍ" : "NO") + " (ver turno de máquina a la derecha)");
        finalizarTurno();
    }

    /**
     * Envía el ID elegido a la partida. Si falla, la lógica existente descarta
     * ese candidato; si acierta, la partida termina sin turno de la máquina.
     */
    private void adivinar() {
        Integer id = leerId(idAdivinanza);
        if (id == null || partida.estaFinalizada()) return;
        partida.avanzarTurno();
        boolean acierto = partida.jugarTurnoHumanoAdivinanza(id);
        acciones.append("Turno " + partida.getTurnoActual() + " - Adivinanza ID " + id
                + ": " + (acierto ? "CORRECTO" : "INCORRECTO") + ".\n");
        resultado.setText("Último resultado: " + (acierto ? "Acierto" : "ID incorrecto, candidato descartado"));
        finalizarTurno();
    }

    /**
     * Convierte el texto introducido en un ID válido del catálogo. Devuelve null
     * y muestra un JOptionPane si la entrada no es válida, para no avanzar el turno.
     */
    private Integer leerId(JTextField campo) {
        try {
            int id = Integer.parseInt(campo.getText().trim());
            if (id >= 1 && id <= JuegoAdivinaQuien.CANTIDAD_PERSONAJES) {
                return id;
            }
        } catch (NumberFormatException ignored) {
            // El mensaje es el mismo para entradas no numéricas o fuera del catálogo.
        }
        JOptionPane.showMessageDialog(this, "Ingresá un ID entre 1 y " + JuegoAdivinaQuien.CANTIDAD_PERSONAJES + ".");
        return null;
    }

    /**
     * Mantiene el orden de la consola: la máquina juega después del humano,
     * salvo que la adivinanza humana ya haya terminado la partida. Si alguien gana,
     * deshabilita las acciones de juego y comunica el ganador mediante un diálogo modal.
     */
    private void finalizarTurno() {
        if (!partida.estaFinalizada()) {
            acciones.append("--- Turno de " + partida.getJugador2().getNombre() + " ---\n");
            // El callback acciones::append coloca cada mensaje de la IA en JTextArea;
            // la partida sigue decidiendo la jugada, sin redirigir el System.out global.
            partida.jugarTurnoMaquina(true, acciones::append);
            resultado.setText(partida.estaFinalizada() ? "Último resultado: la máquina acertó"
                    : "Último resultado: turno de " + partida.getJugador2().getNombre() + " (ver detalle)");
        }
        actualizarEstado();
        if (partida.estaFinalizada()) {
            Jugador ganador = partida.getGanador();
            String mensaje = "Ganador: " + ganador.getNombre() + " en el turno " + partida.getTurnoActual()
                    + ". Personaje adivinado: ID " + partida.getArbitro().getOponenteDe(ganador).getPersonajeSecreto().getId() + ".";
            estado.setText(mensaje);
            acciones.append(mensaje + "\n");
            if (ganador == partida.getJugador1() && ofreceContinuacion) {
                int preguntas = partida.getHistorialPreguntas().getEntradasDe(partida.getJugador2().getNombre()).size();
                acciones.append(preguntas == 0
                        ? "Máquina 1 no hizo preguntas: Máquina 2 empezará sin pistas previas.\n"
                        : "Máquina 2 podrá aprovechar las " + preguntas + " respuestas de Máquina 1.\n");
                continuar.setVisible(true);
            }
            for (JButton boton : botonesAccion) boton.setEnabled(false);
            minimo.setEnabled(false);
            maximo.setEnabled(false);
            idAdivinanza.setEnabled(false);
            // En pruebas sin pantalla se conserva el resultado en el panel sin abrir un diálogo.
            if (!GraphicsEnvironment.isHeadless()) {
                JOptionPane.showMessageDialog(this, mensaje);
            }
        }
        acciones.setCaretPosition(acciones.getDocument().getLength());
    }

    /**
     * Vuelve a leer turno y candidatos desde la partida después de cada jugada.
     * Muestra solo los candidatos del humano; el secreto rival permanece oculto
     * hasta que la partida termina. No mantiene un segundo tablero en la UI.
     */
    private void actualizarEstado() {
        Jugador humano = partida.getJugador1();
        Jugador maquina = partida.getJugador2();
        turno.setText(partida.estaFinalizada() ? "Turno final: " + partida.getTurnoActual()
                : "Turno " + (partida.getTurnoActual() + 1) + " — te toca jugar");
        cantidades.setText("Candidatos restantes — tuyos: " + humano.getCantidadCandidatosRestantes()
                + " | " + maquina.getNombre() + ": " + maquina.getCantidadCandidatosRestantes());
        if (!partida.estaFinalizada()) estado.setText("Partida en curso");
        StringBuilder lista = new StringBuilder();
        for (Persona persona : humano.getCandidatosRestantes()) {
            lista.append(persona.getDescripcionAtributos()).append('\n');
        }
        candidatos.setText(lista.toString());
        candidatos.setCaretPosition(0);
    }
}
