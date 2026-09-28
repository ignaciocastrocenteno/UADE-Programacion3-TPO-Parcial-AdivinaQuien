package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.test;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.algoritmos.HistorialPreguntas;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.juego.JuegoAdivinaQuien;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.juego.PartidaAdivinaQuien;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.FiltroPregunta;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.Persona;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.persistencia.MarcadorRecord;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.ui.ContinuacionMaquina2;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.ui.PanelPartida;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.ui.PanelSeleccionPersonaje;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.ui.PanelSimulacion;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/** Prueba de los botones Swing sin abrir una ventana (útil también en entornos sin pantalla). */
public class SwingUITest {
    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        SwingUtilities.invokeAndWait(SwingUITest::probarSeleccionYPartidas);
        probarSimulacion();
        System.out.println("Pruebas de la interfaz Swing: OK");
    }

    private static void probarSeleccionYPartidas() {
        JuegoAdivinaQuien juego = new JuegoAdivinaQuien();
        AtomicInteger idElegido = new AtomicInteger();
        AtomicBoolean volvio = new AtomicBoolean();
        PanelSeleccionPersonaje seleccion = new PanelSeleccionPersonaje(juego.getPersonajes(),
                (nombre, id) -> {
                    comprobar("Prueba".equals(nombre), "El nombre no llegó a la partida");
                    idElegido.set(id);
                }, () -> volvio.set(true));
        seleccion.preparar(1);
        JTable tabla = componentes(seleccion, JTable.class).get(0);
        comprobar(tabla.getRowCount() == 23, "La selección no muestra los 23 personajes");
        tabla.setRowSelectionInterval(0, 0);
        componentes(seleccion, JTextField.class).get(0).setText("Prueba");
        boton(seleccion, "Iniciar partida").doClick();
        comprobar(idElegido.get() == (Integer) tabla.getValueAt(0, 0), "El ID seleccionado no coincide");
        volvio.set(false);
        boton(seleccion, "Volver al menú").doClick();
        comprobar(volvio.get(), "No volvió al menú desde selección");

        Map<String, FiltroPregunta> filtros = new LinkedHashMap<>();
        filtros.put("Masculino", FiltroPregunta.esMasculino());
        filtros.put("Femenino", FiltroPregunta.esFemenino());
        filtros.put("Calvo", FiltroPregunta.esCalvo());
        filtros.put("Tiene pelo", FiltroPregunta.tienePelo());
        filtros.put("Usa lentes", FiltroPregunta.usaLentes());
        filtros.put("No usa lentes", FiltroPregunta.noUsaLentes());
        filtros.put("Pelo colorado", FiltroPregunta.peloColorado());
        filtros.put("Pelo negro", FiltroPregunta.peloNegro());
        filtros.put("Pelo amarillo", FiltroPregunta.peloAmarillo());

        for (int tipo = 1; tipo <= 2; tipo++) {
            for (Map.Entry<String, FiltroPregunta> entrada : filtros.entrySet()) {
                PartidaAdivinaQuien partida = juego.crearPartidaHumanoVsMaquina("Prueba", idElegido.get(), tipo);
                PanelPartida panel = new PanelPartida(partida, () -> volvio.set(true));
                boton(panel, entrada.getKey()).doClick();
                verificarTurno(partida, entrada.getValue(), juego.getPersonajes());
            }
            PartidaAdivinaQuien rango = juego.crearPartidaHumanoVsMaquina("Prueba", idElegido.get(), tipo);
            PanelPartida panelRango = new PanelPartida(rango, () -> volvio.set(true));
            List<JTextField> campos = componentes(panelRango, JTextField.class);
            campos.get(0).setText("4");
            campos.get(1).setText("11");
            boton(panelRango, "Preguntar rango").doClick();
            verificarTurno(rango, FiltroPregunta.rangoIds(4, 11), juego.getPersonajes());

            PartidaAdivinaQuien fallo = juego.crearPartidaHumanoVsMaquina("Prueba", idElegido.get(), tipo);
            PanelPartida panelFallo = new PanelPartida(fallo, () -> volvio.set(true));
            componentes(panelFallo, JTextField.class).get(2).setText(String.valueOf(idElegido.get()));
            boton(panelFallo, "Adivinar").doClick();
            comprobar(!fallo.getJugador1().getCandidatosRestantes().contains(juego.buscarPorId(idElegido.get())),
                    "Un ID incorrecto debe descartarse");
            comprobar(fallo.getTurnoActual() == 1, "Tras una adivinanza incorrecta debe jugar la máquina");
            volvio.set(false);
            boton(panelFallo, "Volver al menú").doClick();
            comprobar(volvio.get(), "No volvió al menú desde la partida");

            PartidaAdivinaQuien acierto = juego.crearPartidaHumanoVsMaquina("Prueba", idElegido.get(), tipo);
            PanelPartida panelAcierto = new PanelPartida(acierto, () -> {});
            int victoriasAntes = MarcadorRecord.getInstancia().obtenerVictorias("Prueba");
            componentes(panelAcierto, JTextField.class).get(2).setText(
                    String.valueOf(acierto.getJugador2().getPersonajeSecreto().getId()));
            boton(panelAcierto, "Adivinar").doClick();
            comprobar(acierto.estaFinalizada() && acierto.getGanador() == acierto.getJugador1(),
                    "Una adivinanza correcta debe terminar el juego a favor del humano");
            comprobar(!boton(panelAcierto, "Adivinar").isEnabled(), "No se deben permitir acciones al terminar");
            comprobar(MarcadorRecord.getInstancia().obtenerVictorias("Prueba") == victoriasAntes + 1,
                    "No se registró la victoria");

            PartidaAdivinaQuien derrota = juego.crearPartidaHumanoVsMaquina("Prueba", idElegido.get(), tipo);
            PanelPartida panelDerrota = new PanelPartida(derrota, () -> {},
                    tipo == 1 ? () -> {} : null);
            for (int turno = 0; turno < 30 && !derrota.estaFinalizada(); turno++) {
                boton(panelDerrota, "Masculino").doClick();
            }
            comprobar(derrota.estaFinalizada() && derrota.getGanador() == derrota.getJugador2(),
                    "La máquina debe poder ganar desde la interfaz");
            comprobar(!boton(panelDerrota, "Masculino").isEnabled(), "La partida terminada sigue aceptando preguntas");
            if (tipo == 1) {
                comprobar(!boton(panelDerrota, "Continuar contra Máquina 2").isVisible(),
                        "No se debe poder continuar tras perder contra Máquina 1");
            }
        }
        probarContinuacion(juego, idElegido.get());
    }

    private static void probarContinuacion(JuegoAdivinaQuien juego, int idSecreto) {
        PartidaAdivinaQuien primera = juego.crearPartidaHumanoVsMaquina("Prueba", idSecreto, 1);
        AtomicReference<PartidaAdivinaQuien> siguiente = new AtomicReference<>();
        PanelPartida panelPrimera = new PanelPartida(primera, () -> {},
                () -> siguiente.set(ContinuacionMaquina2.crear(juego, primera)));
        JButton continuar = boton(panelPrimera, "Continuar contra Máquina 2");
        comprobar(!continuar.isVisible(), "No debe ofrecerse la continuación antes de ganar");
        try {
            ContinuacionMaquina2.crear(juego, primera);
            throw new AssertionError("No se debe continuar antes de ganar a Máquina 1");
        } catch (IllegalArgumentException esperado) {
            // La continuación solo está permitida después de ganar.
        }

        // Una pregunta humana y luego una de Máquina 1: solo sirve la dirigida al humano.
        boton(panelPrimera, "Masculino").doClick();
        List<HistorialPreguntas.EntradaHistorial> preguntasMaquina1 = primera.getHistorialPreguntas()
                .getEntradasDe(primera.getJugador2().getNombre());
        comprobar(preguntasMaquina1.size() == 1 && preguntasMaquina1.get(0).getObjetivo().equals("Prueba"),
                "Máquina 1 no interrogó al personaje humano");
        componentes(panelPrimera, JTextField.class).get(2).setText(
                String.valueOf(primera.getJugador2().getPersonajeSecreto().getId()));
        boton(panelPrimera, "Adivinar").doClick();
        comprobar(primera.getGanador() == primera.getJugador1() && continuar.isVisible(),
                "La victoria sobre Máquina 1 debe permitir continuar");
        continuar.doClick();

        PartidaAdivinaQuien segunda = siguiente.get();
        comprobar(segunda != null && segunda.getJugador1().getPersonajeSecreto().getId() == idSecreto
                && segunda.getJugador1().getNombre().equals("Prueba"),
                "La segunda partida debe conservar el secreto y el nombre del humano");
        comprobar(segunda.getHistorialPreguntas().getEntradas().size() == preguntasMaquina1.size(),
                "Solo las preguntas de Máquina 1 deben llegar a la segunda partida");
        comprobar(segunda.getJugador2().getCantidadCandidatosRestantes() == 23,
                "Máquina 2 todavía no debe aplicar pistas antes de su primer turno");
        PanelPartida panelSegunda = new PanelPartida(segunda, () -> {});
        comprobar(componentes(panelSegunda, JTextArea.class).get(1).getText().contains("Máquina 2 conoce"),
                "La interfaz debe explicar qué información heredó Máquina 2");
        boton(panelSegunda, "Masculino").doClick();
        HistorialPreguntas.EntradaHistorial pista = preguntasMaquina1.get(0);
        comprobar(segunda.getJugador2().getPreguntasRealizadas().contains(pista.getPregunta()),
                "Máquina 2 no asimiló el filtro previo");
        comprobar(segunda.getJugador2().getCantidadCandidatosRestantes() < 23,
                "El filtro previo no redujo los candidatos de Máquina 2");
        for (Persona candidato : segunda.getJugador2().getCandidatosRestantes()) {
            comprobar(pista.getPregunta().cumple(candidato) == pista.getRespuesta(),
                    "Máquina 2 retuvo un candidato incompatible con la respuesta anterior");
        }
        comprobar(componentes(panelSegunda, JTextArea.class).get(1).getText().contains("Ventaja: Se descartaron"),
                "El relato de Máquina 2 no muestra la ventaja informativa");

        PartidaAdivinaQuien sinPreguntas = juego.crearPartidaHumanoVsMaquina("Prueba", idSecreto, 1);
        PanelPartida panelSinPreguntas = new PanelPartida(sinPreguntas, () -> {}, () -> {});
        componentes(panelSinPreguntas, JTextField.class).get(2).setText(
                String.valueOf(sinPreguntas.getJugador2().getPersonajeSecreto().getId()));
        boton(panelSinPreguntas, "Adivinar").doClick();
        comprobar(componentes(panelSinPreguntas, JTextArea.class).get(1).getText().contains("sin pistas previas"),
                "La interfaz debe aclarar cuando Máquina 1 no llegó a preguntar");
        comprobar(ContinuacionMaquina2.crear(juego, sinPreguntas).getHistorialPreguntas().getEntradas().isEmpty(),
                "No se deben inventar pistas si Máquina 1 no preguntó");

        PartidaAdivinaQuien independiente = juego.crearPartidaHumanoVsMaquina("Prueba", idSecreto, 2);
        comprobar(independiente.getHistorialPreguntas().getEntradas().isEmpty(),
                "El modo Máquina 2 independiente no debe heredar pistas");
    }

    private static void verificarTurno(PartidaAdivinaQuien partida, FiltroPregunta esperado, List<Persona> catalogo) {
        List<HistorialPreguntas.EntradaHistorial> preguntas = partida.getHistorialPreguntas().getEntradasDe("Prueba");
        comprobar(preguntas.size() == 1 && preguntas.get(0).getPregunta().equals(esperado),
                "El botón no usó el filtro correspondiente");
        boolean respuesta = esperado.cumple(partida.getJugador2().getPersonajeSecreto());
        comprobar(preguntas.get(0).getRespuesta() == respuesta, "La respuesta del árbitro no coincide");
        int restantes = 0;
        for (Persona persona : catalogo) {
            if (esperado.cumple(persona) == respuesta) restantes++;
        }
        comprobar(partida.getJugador1().getCantidadCandidatosRestantes() == restantes,
                "Los candidatos del humano no se actualizaron");
        comprobar(partida.getTurnoActual() == 1, "Debe avanzarse un solo turno por acción");
        comprobar(!partida.getHistorialPreguntas().getEntradasDe(partida.getJugador2().getNombre()).isEmpty(),
                "No se ejecutó el turno de la máquina");
    }

    private static void probarSimulacion() throws Exception {
        PanelSimulacion panel = new PanelSimulacion(new JuegoAdivinaQuien(), () -> {});
        JButton iniciar = boton(panel, "Iniciar simulación");
        SwingUtilities.invokeAndWait(iniciar::doClick);
        boolean[] finalizada = {false};
        for (int i = 0; i < 150 && !finalizada[0]; i++) {
            Thread.sleep(50);
            SwingUtilities.invokeAndWait(() -> finalizada[0] = iniciar.isEnabled());
        }
        comprobar(finalizada[0], "La simulación no terminó a tiempo");
        SwingUtilities.invokeAndWait(() -> {
            String pasos = componentes(panel, JTextArea.class).get(0).getText();
            comprobar(pasos.contains("RONDA") && pasos.contains("Pregunta:")
                    && pasos.contains("Árbitro responde") && pasos.contains("Candidatos restantes")
                    && pasos.contains("ADIVINAR DIRECTAMENTE") && pasos.contains("VICTORIA PARA"),
                    "El historial de simulación está incompleto: " + pasos);
            comprobar(boton(panel, "Volver al menú").isEnabled(), "No se puede volver después de simular");
        });
    }

    private static JButton boton(Container panel, String texto) {
        for (JButton boton : componentes(panel, JButton.class)) {
            if (texto.equals(boton.getText())) return boton;
        }
        throw new AssertionError("Falta el botón: " + texto);
    }

    private static <T extends Component> List<T> componentes(Container padre, Class<T> tipo) {
        List<T> hallados = new ArrayList<>();
        for (Component componente : padre.getComponents()) {
            if (tipo.isInstance(componente)) hallados.add(tipo.cast(componente));
            if (componente instanceof Container) hallados.addAll(componentes((Container) componente, tipo));
        }
        return hallados;
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }
}
