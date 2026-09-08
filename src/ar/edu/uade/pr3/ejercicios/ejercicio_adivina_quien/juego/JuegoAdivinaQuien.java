package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.juego;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.algoritmos.HistorialPreguntas;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.estrategias.EstrategiaResolucion;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores.Jugador;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores.JugadorHumano;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores.JugadorMaquina1;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores.JugadorMaquina2;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.Persona;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.persistencia.MarcadorRecord;

import java.util.*;

/**
 * Controlador principal y fachada del juego "Adivina Quién".
 * Maneja el catálogo oficial de 23 personajes ordenados por género y autoincremento de ID,
 * la selección de personajes secretos, los comodines de rango (Divide & Conquer)
 * y la instanciación de partidas en sus distintos modos.
 */
public class JuegoAdivinaQuien {
    public static final int CANTIDAD_PERSONAJES = 23;
    public static final int TAMANO_GRUPO_COMODIN = 4;

    private final List<Persona> personajes;
    private final Map<Integer, Persona> mapaPersonajesById;
    private Persona elegido;

    public JuegoAdivinaQuien() {
        this.personajes = new ArrayList<>();
        this.mapaPersonajesById = new HashMap<>();
        inicializarPersonajes();
    }

    /**
     * Inicializa los 23 personajes oficiales garantizando el orden inicial por género
     * y la disposición en una lista ordenada de forma autoincremental de IDs.
     */
    public void inicializarPersonajes() {
        personajes.clear();
        mapaPersonajesById.clear();

        List<Persona> catalogo = CatalogoPersonajes.crearCatalogoOficial();
        personajes.addAll(catalogo);

        for (Persona p : personajes) {
            mapaPersonajesById.put(p.getId(), p);
        }

        // Seleccionar aleatoriamente el personaje elegido por defecto
        Random random = new Random();
        int indiceElegido = random.nextInt(personajes.size());
        this.elegido = personajes.get(indiceElegido);
        this.elegido.setEsElegido(true);
    }

    /**
     * Permite fijar un personaje elegido específico por ID (útil para pruebas unitarias).
     */
    public void setElegidoExplicitamente(int id) {
        if (!mapaPersonajesById.containsKey(id)) {
            throw new IllegalArgumentException("ID de personaje no válido: " + id);
        }
        for (Persona p : personajes) {
            p.setEsElegido(p.getId() == id);
        }
        this.elegido = mapaPersonajesById.get(id);
    }

    public List<Persona> getPersonajes() {
        return Collections.unmodifiableList(personajes);
    }

    public Persona getElegido() {
        return elegido;
    }

    public Persona buscarPorId(int id) {
        return mapaPersonajesById.get(id);
    }

    public Persona adivinar(int id) {
        return mapaPersonajesById.get(id);
    }

    /**
     * Divide las opciones en grupos (comodín) e informa en qué subrango se encuentra el elegido.
     */
    public RangoGrupo obtenerComodinGrupos(int tamanoGrupo) {
        if (tamanoGrupo <= 0) tamanoGrupo = TAMANO_GRUPO_COMODIN;

        for (int i = 0; i < personajes.size(); i += tamanoGrupo) {
            int fin = Math.min(i + tamanoGrupo, personajes.size());
            List<Persona> subgrupo = personajes.subList(i, fin);

            boolean estaEnGrupo = subgrupo.stream().anyMatch(Persona::esElegido);
            if (estaEnGrupo) {
                int minId = subgrupo.get(0).getId();
                int maxId = subgrupo.get(subgrupo.size() - 1).getId();
                return new RangoGrupo(minId, maxId, subgrupo);
            }
        }
        return null;
    }

    public RangoGrupo obtenerComodinGrupoTres() {
        return obtenerComodinGrupos(3);
    }

    /**
     * Resuelve automáticamente utilizando una estrategia dada (compatibilidad con versión anterior).
     */
    public Persona resolverAutomaticamente(EstrategiaResolucion estrategia) {
        if (estrategia == null) {
            throw new IllegalArgumentException("La estrategia de resolución no puede ser nula.");
        }
        return estrategia.resolver(personajes);
    }

    /**
     * Crea una partida Humano vs Máquina.
     */
    public PartidaAdivinaQuien crearPartidaHumanoVsMaquina(String nombreHumano, int idSecretoHumano, int tipoMaquina) {
        Persona secretoHumano = buscarPorId(idSecretoHumano);
        if (secretoHumano == null) {
            throw new IllegalArgumentException("ID de personaje no válido para el humano: " + idSecretoHumano);
        }

        // La máquina elige un personaje secreto DIFERENTE al del humano (requisito del enunciado)
        Random random = new Random();
        Persona secretoMaquina;
        do {
            int idAleatorio = 1 + random.nextInt(personajes.size());
            secretoMaquina = buscarPorId(idAleatorio);
        } while (secretoMaquina.getId() == secretoHumano.getId());

        Jugador jugadorHumano = new JugadorHumano(nombreHumano, secretoHumano, CatalogoPersonajes.clonarCatalogo(personajes));
        HistorialPreguntas historial = new HistorialPreguntas();

        Jugador jugadorMaquina;
        if (tipoMaquina == 2) {
            jugadorMaquina = new JugadorMaquina2(secretoMaquina, CatalogoPersonajes.clonarCatalogo(personajes), historial);
        } else {
            jugadorMaquina = new JugadorMaquina1(secretoMaquina, CatalogoPersonajes.clonarCatalogo(personajes));
        }

        return new PartidaAdivinaQuien(jugadorHumano, jugadorMaquina, historial, MarcadorRecord.getInstancia());
    }

    /**
     * Crea una partida Máquina 1 vs Máquina 2 con secretos distintos y trazabilidad compartida.
     */
    public PartidaAdivinaQuien crearPartidaMaquinaVsMaquina() {
        Random random = new Random();
        int id1 = 1 + random.nextInt(personajes.size());
        int id2;
        do {
            id2 = 1 + random.nextInt(personajes.size());
        } while (id1 == id2);

        Persona secretoM1 = buscarPorId(id1);
        Persona secretoM2 = buscarPorId(id2);

        HistorialPreguntas historial = new HistorialPreguntas();
        JugadorMaquina1 m1 = new JugadorMaquina1(secretoM1, CatalogoPersonajes.clonarCatalogo(personajes));
        JugadorMaquina2 m2 = new JugadorMaquina2(secretoM2, CatalogoPersonajes.clonarCatalogo(personajes), historial);

        return new PartidaAdivinaQuien(m1, m2, historial, MarcadorRecord.getInstancia());
    }
}
