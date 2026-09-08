package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.test;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.algoritmos.FiltroParticion;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.algoritmos.HistorialPreguntas;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.algoritmos.SelectorPreguntaGreedy;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.estrategias.BusquedaLinealStrategy;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.estrategias.BusquedaPorAgrupacionStrategy;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.juego.CatalogoPersonajes;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.juego.JuegoAdivinaQuien;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.juego.PartidaAdivinaQuien;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.juego.RangoGrupo;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores.Jugador;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores.JugadorMaquina1;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores.JugadorMaquina2;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.ColorPelo;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.FiltroPregunta;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.Genero;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.Persona;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.persistencia.MarcadorRecord;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JuegoTest {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("          EJECUTANDO SUITE COMPLETA DE PRUEBAS (JUEGO TEST)");
        System.out.println("================================================================================");

        testCatalogo23Personajes();
        testDivideAndConquerParticion();
        testAlgoritmoVorazGreedy();
        testVentajaAsimilacionMaquina2();
        testComodinGruposRango();
        testBusquedaLinealEvolutiva();
        testBusquedaPorAgrupacionEvolutiva();
        testPersistenciaMarcadorMemoria();
        testSimulacionPartidasCompletas();

        System.out.println("\n================================================================================");
        System.out.println("       ¡TODAS LAS PRUEBAS (9/9) HAN FINALIZADO CON ÉXITO ROTUNDO!");
        System.out.println("================================================================================");
    }

    private static void testCatalogo23Personajes() {
        System.out.print("[TEST 1/9] Validando catálogo oficial de 23 personajes y atributos... ");
        List<Persona> catalogo = CatalogoPersonajes.crearCatalogoOficial();

        assertCondition(catalogo.size() == 23, "El catálogo debe contener exactamente 23 personajes.");

        Set<Integer> ids = new HashSet<>();
        boolean ordenGeneroPreservado = true;
        Genero generoAnterior = null;

        for (int i = 0; i < catalogo.size(); i++) {
            Persona p = catalogo.get(i);
            int esperadoId = i + 1;
            assertCondition(p.getId() == esperadoId, "El ID debe ser autoincremental de 1 a 23. Esperado: " + esperadoId + ", Real: " + p.getId());
            assertCondition(ids.add(p.getId()), "No deben existir IDs repetidos: " + p.getId());
            assertCondition(p.getNombre() != null && !p.getNombre().isEmpty(), "El nombre no debe ser nulo ni vacío.");
            assertCondition(p.getApellido() != null && !p.getApellido().isEmpty(), "El apellido no debe ser nulo ni vacío.");
            assertCondition(p.getGenero() != null, "El género no debe ser nulo.");
            assertCondition(p.getColorPelo() != null, "El color de pelo no debe ser nulo.");

            if (p.esCalvo()) {
                assertCondition(p.getColorPelo() == ColorPelo.NINGUNO, "Un personaje calvo debe tener color de pelo NINGUNO.");
            } else {
                assertCondition(p.getColorPelo() != ColorPelo.NINGUNO, "Un personaje con pelo debe tener uno de los colores válidos.");
            }

            if (generoAnterior != null && generoAnterior.compareTo(p.getGenero()) > 0) {
                ordenGeneroPreservado = false;
            }
            generoAnterior = p.getGenero();
        }

        assertCondition(ordenGeneroPreservado, "El catálogo inicial debe respetar el orden por género.");
        System.out.println("OK");
    }

    private static void testDivideAndConquerParticion() {
        System.out.print("[TEST 2/9] Validando algoritmo Divide & Conquer (FiltroParticion)... ");
        List<Persona> catalogo = CatalogoPersonajes.crearCatalogoOficial();

        // Probar división por género masculino
        FiltroPregunta fMasculino = FiltroPregunta.esMasculino();
        FiltroParticion.ResultadoParticion res = FiltroParticion.dividir(catalogo, fMasculino);

        assertCondition(res.getCumplen().size() + res.getNoCumplen().size() == 23,
                "La suma de los subconjuntos divididos debe ser igual al total original (23).");
        assertCondition(res.getCumplen().size() == 12, "Debe haber 12 masculinos.");
        assertCondition(res.getNoCumplen().size() == 11, "Debe haber 11 femeninos.");

        // Probar poda
        List<Persona> podadosSi = FiltroParticion.podarEspacio(catalogo, fMasculino, true);
        assertCondition(podadosSi.size() == 12, "Podar con respuesta SÍ debe dejar sólo a los 12 masculinos.");

        List<Persona> podadosNo = FiltroParticion.podarEspacio(catalogo, fMasculino, false);
        assertCondition(podadosNo.size() == 11, "Podar con respuesta NO debe dejar sólo a los 11 femeninos.");

        System.out.println("OK");
    }

    private static void testAlgoritmoVorazGreedy() {
        System.out.print("[TEST 3/9] Validando Algoritmo Voraz (SelectorPreguntaGreedy)... ");
        List<Persona> catalogo = CatalogoPersonajes.crearCatalogoOficial();

        // Con 23 personajes (12 masculinos, 11 femeninos), la mejor bisección debe tener diferencia de desbalance = 1 (12 vs 11)
        SelectorPreguntaGreedy.EvaluacionGreedy mejor = SelectorPreguntaGreedy.seleccionarMejorPregunta(catalogo, Set.of());
        assertCondition(mejor != null, "Debe encontrar una pregunta óptima.");
        assertCondition(mejor.getDiferenciaBalance() == 1, "El desbalance mínimo sobre 23 candidatos debe ser 1 (12 vs 11). Real: " + mejor.getDiferenciaBalance());
        assertCondition(mejor.getPregunta().getTipo() == FiltroPregunta.Tipo.GENERO_MASCULINO ||
                        mejor.getPregunta().getTipo() == FiltroPregunta.Tipo.GENERO_FEMENINO,
                "La pregunta voraz óptima inicial debe ser el género.");

        System.out.println("OK");
    }

    private static void testVentajaAsimilacionMaquina2() {
        System.out.print("[TEST 4/9] Validando ventaja informativa de Máquina 2 (Historial)... ");
        List<Persona> catalogo = CatalogoPersonajes.crearCatalogoOficial();
        Persona secreto = catalogo.get(0); // ID 1 (Ana Gomez, Femenino, Pelo Colorado, Usa Lentes)

        HistorialPreguntas historial = new HistorialPreguntas();
        JugadorMaquina2 m2 = new JugadorMaquina2("Máquina 2", catalogo.get(1), CatalogoPersonajes.clonarCatalogo(catalogo), historial);

        assertCondition(m2.getCantidadCandidatosRestantes() == 23, "Máquina 2 debe iniciar con 23 candidatos.");

        // Simulamos que Máquina 1 pregunta "¿Es masculino?" y el árbitro responde "NO"
        FiltroPregunta fMasc = FiltroPregunta.esMasculino();
        historial.registrar("Máquina 1", fMasc, false, 1);

        // Máquina 2 asimila las preguntas de Máquina 1
        int descartados = m2.asimilarPreguntasDeMaquina1();
        assertCondition(descartados == 12, "Máquina 2 debió descartar 12 personajes masculinos gracias a Máquina 1.");
        assertCondition(m2.getCantidadCandidatosRestantes() == 11, "A Máquina 2 le deben quedar 11 candidatos.");

        System.out.println("OK");
    }

    private static void testComodinGruposRango() {
        System.out.print("[TEST 5/9] Validando comodín de grupos de rango (Divide & Conquer)... ");
        JuegoAdivinaQuien juego = new JuegoAdivinaQuien();

        for (int id = 1; id <= 23; id++) {
            juego.setElegidoExplicitamente(id);
            RangoGrupo pista = juego.obtenerComodinGrupos(4);
            assertCondition(pista != null, "El comodín no debe ser nulo para ID " + id);
            assertCondition(id >= pista.getMinId() && id <= pista.getMaxId(),
                    "ID " + id + " debe estar contenido en el rango [" + pista.getMinId() + ".." + pista.getMaxId() + "]");
            assertCondition(pista.getPersonajes().size() <= 4, "El grupo no debe exceder 4 elementos.");
        }

        System.out.println("OK");
    }

    private static void testBusquedaLinealEvolutiva() {
        System.out.print("[TEST 6/9] Validando Búsqueda Lineal sobre 23 personajes... ");
        JuegoAdivinaQuien juego = new JuegoAdivinaQuien();
        BusquedaLinealStrategy estrategia = new BusquedaLinealStrategy();

        for (int id = 1; id <= 23; id++) {
            juego.setElegidoExplicitamente(id);
            Persona hallado = juego.resolverAutomaticamente(estrategia);
            assertCondition(hallado != null && hallado.getId() == id, "Búsqueda lineal falló al encontrar ID " + id);
        }

        System.out.println("OK");
    }

    private static void testBusquedaPorAgrupacionEvolutiva() {
        System.out.print("[TEST 7/9] Validando Búsqueda por Agrupación sobre 23 personajes... ");
        JuegoAdivinaQuien juego = new JuegoAdivinaQuien();
        BusquedaPorAgrupacionStrategy estrategia = new BusquedaPorAgrupacionStrategy(4);

        for (int id = 1; id <= 23; id++) {
            juego.setElegidoExplicitamente(id);
            Persona hallado = juego.resolverAutomaticamente(estrategia);
            assertCondition(hallado != null && hallado.getId() == id, "Búsqueda por agrupación falló al encontrar ID " + id);
        }

        System.out.println("OK");
    }

    private static void testPersistenciaMarcadorMemoria() {
        System.out.print("[TEST 8/9] Validando persistencia en memoria y ranking (MarcadorRecord)... ");
        MarcadorRecord marcador = new MarcadorRecord();
        marcador.limpiar();

        marcador.registrarVictoria("JugadorA");
        marcador.registrarVictoria("JugadorB");
        marcador.registrarVictoria("JugadorA");

        assertCondition(marcador.obtenerVictorias("JugadorA") == 2, "JugadorA debe tener 2 victorias.");
        assertCondition(marcador.obtenerVictorias("JugadorB") == 1, "JugadorB debe tener 1 victoria.");
        assertCondition(marcador.obtenerVictorias("Inexistente") == 0, "Jugador sin partidas debe tener 0 victorias.");

        List<MarcadorRecord.RegistroRanking> ranking = marcador.obtenerRanking();
        assertCondition(ranking.size() == 2, "El ranking debe tener 2 jugadores.");
        assertCondition(ranking.get(0).getJugador().equals("JugadorA"), "El puesto 1 debe ser JugadorA.");
        assertCondition(ranking.get(0).getVictorias() == 2, "JugadorA debe tener 2 victorias en ranking.");

        System.out.println("OK");
    }

    private static void testSimulacionPartidasCompletas() {
        System.out.print("[TEST 9/9] Simulando 10 partidas completas Máquina 1 vs Máquina 2... ");
        JuegoAdivinaQuien juego = new JuegoAdivinaQuien();

        for (int i = 0; i < 10; i++) {
            PartidaAdivinaQuien partida = juego.crearPartidaMaquinaVsMaquina();
            Jugador ganador = partida.ejecutarSimulacionMaquinaVsMaquina(false);

            assertCondition(ganador != null, "La partida " + (i + 1) + " debe tener un ganador.");
            assertCondition(partida.estaFinalizada(), "La partida " + (i + 1) + " debe marcarse como finalizada.");
            assertCondition(partida.getTurnoActual() <= 15, "La partida no debe exceder 15 turnos (convergencia algorítmica). Turnos: " + partida.getTurnoActual());
        }

        System.out.println("OK");
    }

    private static void assertCondition(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("FALLO EN PRUEBA: " + message);
        }
    }
}
