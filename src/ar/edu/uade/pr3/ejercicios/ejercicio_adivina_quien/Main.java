package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.algoritmos.HistorialPreguntas;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.juego.CatalogoPersonajes;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.juego.JuegoAdivinaQuien;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.juego.PartidaAdivinaQuien;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.juego.RangoGrupo;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores.Jugador;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.FiltroPregunta;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.Persona;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.persistencia.MarcadorRecord;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final JuegoAdivinaQuien juegoFachada = new JuegoAdivinaQuien();
    private static final MarcadorRecord marcador = MarcadorRecord.getInstancia();

    public static void main(String[] args) {
        mostrarBienvenida();

        boolean salir = false;
        while (!salir) {
            mostrarMenuPrincipal();
            int opcion = leerEntero("Seleccione una opción: ", 1, 6);

            switch (opcion) {
                case 1:
                    iniciarPartidaHumanoVsMaquina(1);
                    break;
                case 2:
                    iniciarPartidaHumanoVsMaquina(2);
                    break;
                case 3:
                    iniciarSimulacionMaquinaVsMaquina();
                    break;
                case 4:
                    demostrarComodinRango();
                    break;
                case 5:
                    mostrarMarcadorRecords();
                    break;
                case 6:
                    salir = true;
                    System.out.println("\n¡Gracias por jugar a 'Adivina Quién'! Hasta la próxima.");
                    break;
            }
        }
    }

    private static void mostrarBienvenida() {
        System.out.println("================================================================================");
        System.out.println("                ¡BIENVENIDO AL JUEGO 'ADIVINA QUIÉN' (EVOLUTIVO)!");
        System.out.println("         Desarrollado con algoritmos 'Divide & Conquer' y 'Greedy'");
        System.out.println("================================================================================");
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("\n--- MENÚ PRINCIPAL ---");
        System.out.println("1. Jugar Humano vs Máquina 1 (IA Greedy / Bisección Óptima)");
        System.out.println("2. Jugar Humano vs Máquina 2 (IA Asertiva con Ventaja Informativa)");
        System.out.println("3. Presenciar Batalla Máquina vs Máquina (Simulación Paso a Paso)");
        System.out.println("4. Demostración de Comodín de Rango (Divide & Conquer)");
        System.out.println("5. Ver Marcador Récord de Victorias");
        System.out.println("6. Salir");
    }

    private static void iniciarPartidaHumanoVsMaquina(int tipoMaquina) {
        String nombreTipo = tipoMaquina == 1 ? "Máquina 1 (Greedy)" : "Máquina 2 (Asertiva)";
        System.out.println("\n================================================================================");
        System.out.printf("           MODO: HUMANO vs %s%n", nombreTipo);
        System.out.println("================================================================================");

        System.out.print("Ingrese su nombre de jugador: ");
        String nombreHumano = scanner.nextLine().trim();
        if (nombreHumano.isEmpty()) {
            nombreHumano = "Humano";
        }

        mostrarCatalogoPersonajes(juegoFachada.getPersonajes());

        System.out.println("\nElija el ID del personaje secreto que el oponente deberá adivinar (1 a 23, o 0 para azar): ");
        int idSecreto = leerEntero("> ", 0, 23);
        if (idSecreto == 0) {
            idSecreto = new Random().nextInt(CatalogoPersonajes.TOTAL_PERSONAJES) + 1;
        }

        Persona personajeHumano = juegoFachada.buscarPorId(idSecreto);
        System.out.printf("Has elegido como tu personaje secreto a: [ID %d] %s (%s, Pelo: %s)%n",
                personajeHumano.getId(), personajeHumano.getNombreCompleto(),
                personajeHumano.getGenero(), personajeHumano.getColorPelo());

        PartidaAdivinaQuien partida = juegoFachada.crearPartidaHumanoVsMaquina(nombreHumano, idSecreto, tipoMaquina);
        Jugador jugadorHumano = partida.getJugador1();
        Jugador jugadorMaquina = partida.getJugador2();

        System.out.println("\nLa máquina ha elegido un personaje secreto diferente. ¡Comienza la partida!");

        while (!partida.estaFinalizada()) {
            partida.avanzarTurno();
            int turno = partida.getTurnoActual();
            System.out.println("\n--------------------------------------------------------------------------------");
            System.out.printf(">>> TURNO %d | Tu turno, %s (Candidatos restantes para ti: %d | Oponente: %d)%n",
                    turno, nombreHumano, jugadorHumano.getCantidadCandidatosRestantes(), jugadorMaquina.getCantidadCandidatosRestantes());
            System.out.println("--------------------------------------------------------------------------------");

            boolean accionTurnoCompletada = false;
            while (!accionTurnoCompletada && !partida.estaFinalizada()) {
                System.out.println("¿Qué deseas hacer en tu turno?");
                System.out.println("1. Preguntar por Género (Masculino / Femenino)");
                System.out.println("2. Preguntar por Calvicie (Tiene pelo / Es calvo)");
                System.out.println("3. Preguntar por Lentes (Usa lentes / NO usa lentes)");
                System.out.println("4. Preguntar por Color de Pelo (Colorado / Negro / Amarillo)");
                System.out.println("5. Usar Comodín de Rango de IDs (Divide & Conquer)");
                System.out.println("6. Adivinar Personaje directamente por ID");
                System.out.println("7. Ver la lista de mis candidatos posibles actuales");

                int eleccion = leerEntero("Opción: ", 1, 7);

                switch (eleccion) {
                    case 1: {
                        System.out.println("1. ¿Es Masculino?");
                        System.out.println("2. ¿Es Femenino?");
                        int sub = leerEntero("Elija: ", 1, 2);
                        FiltroPregunta f = sub == 1 ? FiltroPregunta.esMasculino() : FiltroPregunta.esFemenino();
                        ejecutarPreguntaHumano(partida, jugadorHumano, f);
                        accionTurnoCompletada = true;
                        break;
                    }
                    case 2: {
                        System.out.println("1. ¿Es Calvo?");
                        System.out.println("2. ¿Tiene Pelo?");
                        int sub = leerEntero("Elija: ", 1, 2);
                        FiltroPregunta f = sub == 1 ? FiltroPregunta.esCalvo() : FiltroPregunta.tienePelo();
                        ejecutarPreguntaHumano(partida, jugadorHumano, f);
                        accionTurnoCompletada = true;
                        break;
                    }
                    case 3: {
                        System.out.println("1. ¿Usa Lentes?");
                        System.out.println("2. ¿NO Usa Lentes?");
                        int sub = leerEntero("Elija: ", 1, 2);
                        FiltroPregunta f = sub == 1 ? FiltroPregunta.usaLentes() : FiltroPregunta.noUsaLentes();
                        ejecutarPreguntaHumano(partida, jugadorHumano, f);
                        accionTurnoCompletada = true;
                        break;
                    }
                    case 4: {
                        System.out.println("1. ¿Tiene Pelo Colorado?");
                        System.out.println("2. ¿Tiene Pelo Negro?");
                        System.out.println("3. ¿Tiene Pelo Amarillo?");
                        int sub = leerEntero("Elija: ", 1, 3);
                        FiltroPregunta f = sub == 1 ? FiltroPregunta.peloColorado() :
                                          (sub == 2 ? FiltroPregunta.peloNegro() : FiltroPregunta.peloAmarillo());
                        ejecutarPreguntaHumano(partida, jugadorHumano, f);
                        accionTurnoCompletada = true;
                        break;
                    }
                    case 5: {
                        System.out.println("Indique el rango de IDs para el comodín:");
                        int minId = leerEntero("ID mínimo (1-23): ", 1, 23);
                        int maxId = leerEntero("ID máximo (" + minId + "-23): ", minId, 23);
                        FiltroPregunta f = FiltroPregunta.rangoIds(minId, maxId);
                        ejecutarPreguntaHumano(partida, jugadorHumano, f);
                        accionTurnoCompletada = true;
                        break;
                    }
                    case 6: {
                        System.out.println("¡Hora de la verdad! Ingrese el ID del personaje secreto del oponente:");
                        int idAdivinado = leerEntero("ID del personaje (1-23): ", 1, 23);
                        boolean acerto = partida.jugarTurnoHumanoAdivinanza(idAdivinado);
                        if (acerto) {
                            System.out.println("\n********************************************************************************");
                            System.out.printf(" ¡¡¡FELICITACIONES %s, HAS GANADO LA PARTIDA EN EL TURNO %d!!!%n", nombreHumano.toUpperCase(), turno);
                            System.out.printf(" Adivinaste con éxito el personaje: [ID %d] %s%n",
                                    idAdivinado, jugadorMaquina.getPersonajeSecreto().getNombreCompleto());
                            System.out.printf(" Total victorias acumuladas de %s: %d%n",
                                    nombreHumano, marcador.obtenerVictorias(nombreHumano));
                            System.out.println("********************************************************************************\n");
                        } else {
                            System.out.println("\n[Árbitro] -> INCORRECTO. El ID " + idAdivinado + " NO es el personaje secreto.");
                            System.out.println("[Divide & Conquer] Personaje eliminado de tus candidatos restantes.");
                        }
                        accionTurnoCompletada = true;
                        break;
                    }
                    case 7: {
                        mostrarCatalogoPersonajes(jugadorHumano.getCandidatosRestantes());
                        break;
                    }
                }
            }

            // Si el humano ganó, no se juega el turno de la máquina
            if (partida.estaFinalizada()) {
                break;
            }

            // Turno de la máquina
            System.out.println("\n--- Turno de la Máquina ---");
            partida.jugarTurnoMaquina(true);

            if (partida.estaFinalizada()) {
                System.out.println("\n********************************************************************************");
                System.out.printf(" LA MÁQUINA HA DESCUBIERTO TU PERSONAJE SECRETO EN EL TURNO %d.%n", turno);
                System.out.printf(" Tu personaje era: [ID %d] %s%n",
                        personajeHumano.getId(), personajeHumano.getNombreCompleto());
                System.out.println(" ¡Mejor suerte para la próxima partida!");
                System.out.println("********************************************************************************\n");
                break;
            }
        }
    }

    private static void ejecutarPreguntaHumano(PartidaAdivinaQuien partida, Jugador humano, FiltroPregunta pregunta) {
        int candidatosAntes = humano.getCantidadCandidatosRestantes();
        boolean respuesta = partida.jugarTurnoHumanoPregunta(pregunta);
        int candidatosDespues = humano.getCantidadCandidatosRestantes();
        int descartados = candidatosAntes - candidatosDespues;

        System.out.println("\n[Árbitro responde]: " + (respuesta ? ">>> SÍ <<<" : ">>> NO <<<"));
        System.out.printf("[Divide & Conquer]: Descartaste %d candidatos. Te quedan %d opciones posibles.%n",
                descartados, candidatosDespues);
        if (humano.tieneCertezaAbsoluta()) {
            Persona unico = humano.getCandidatoUnico();
            System.out.printf("¡ATENCIÓN! Ya tienes certeza absoluta. El único candidato posible es: [ID %d] %s%n",
                    unico.getId(), unico.getNombreCompleto());
        }
    }

    private static void iniciarSimulacionMaquinaVsMaquina() {
        System.out.println("\nPreparando simulación con dos inteligencias artificiales...");
        PartidaAdivinaQuien simulacion = juegoFachada.crearPartidaMaquinaVsMaquina();
        simulacion.ejecutarSimulacionMaquinaVsMaquina(true);
    }

    private static void demostrarComodinRango() {
        System.out.println("\n================================================================================");
        System.out.println("             DEMOSTRACIÓN DE COMODÍN DE RANGO (DIVIDE & CONQUER)");
        System.out.println("================================================================================");
        System.out.println("El comodín agrupa los 23 personajes en bloques consecutivos (ej. de a 4 elementos)");
        System.out.println("e identifica el subrango exacto donde se encuentra el objetivo secreto.");

        JuegoAdivinaQuien juegoDemo = new JuegoAdivinaQuien();
        Persona elegido = juegoDemo.getElegido();
        System.out.printf("%n[Objetivo Oculto]: ID %d - %s%n", elegido.getId(), elegido.getNombreCompleto());

        RangoGrupo grupoGanador = juegoDemo.obtenerComodinGrupos(4);
        if (grupoGanador != null) {
            System.out.println("\n[Resultado del Comodín]:");
            System.out.println(" -> " + grupoGanador);
            System.out.println(" -> Candidatos contenidos en este subespacio:");
            for (Persona p : grupoGanador.getPersonajes()) {
                System.out.printf("    - ID %2d: %-20s (Género: %-9s, Pelo: %s)%n",
                        p.getId(), p.getNombreCompleto(), p.getGenero(), p.getColorPelo());
            }
            System.out.printf("%nSe redujo el espacio de búsqueda de 23 personajes a sólo %d candidatos (Reducción del %.1f%%).%n",
                    grupoGanador.getPersonajes().size(), (1.0 - (double) grupoGanador.getPersonajes().size() / 23.0) * 100.0);
        }
    }

    private static void mostrarMarcadorRecords() {
        System.out.println("\n================================================================================");
        System.out.println("                         MARCADOR RÉCORD DE VICTORIAS");
        System.out.println("================================================================================");
        List<MarcadorRecord.RegistroRanking> ranking = marcador.obtenerRanking();

        if (ranking.isEmpty()) {
            System.out.println("Aún no hay partidas ganadas registradas. ¡Sé el primero en jugar y ganar!");
        } else {
            System.out.printf("%-6s | %-30s | %s%n", "PUESTO", "JUGADOR", "VICTORIAS");
            System.out.println("---------------------------------------------------------------");
            int puesto = 1;
            for (MarcadorRecord.RegistroRanking reg : ranking) {
                System.out.printf("#%-5d | %-30s | %d%n", puesto++, reg.getJugador(), reg.getVictorias());
            }
        }
    }

    private static void mostrarCatalogoPersonajes(List<Persona> lista) {
        System.out.println("\n-------------------------------------------------------------------------------------------------");
        System.out.printf("%-4s | %-22s | %-10s | %-8s | %-8s | %-12s%n",
                "ID", "NOMBRE COMPLETO", "GÉNERO", "CALVO", "LENTES", "COLOR PELO");
        System.out.println("-------------------------------------------------------------------------------------------------");
        for (Persona p : lista) {
            System.out.printf("%-4d | %-22s | %-10s | %-8s | %-8s | %-12s%n",
                    p.getId(),
                    p.getNombreCompleto(),
                    p.getGenero(),
                    p.esCalvo() ? "SÍ" : "NO",
                    p.usaLentes() ? "SÍ" : "NO",
                    p.getColorPelo());
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.printf("Total listados: %d personajes.%n", lista.size());
    }

    private static int leerEntero(String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje);
            String input = scanner.nextLine().trim();
            try {
                int valor = Integer.parseInt(input);
                if (valor >= min && valor <= max) {
                    return valor;
                }
                System.out.printf("Por favor, ingrese un número entre %d y %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Debe ingresar un número entero.");
            }
        }
    }
}
