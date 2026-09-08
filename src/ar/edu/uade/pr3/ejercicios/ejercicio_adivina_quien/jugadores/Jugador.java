package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.algoritmos.FiltroParticion;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.FiltroPregunta;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.Persona;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Clase base abstracta para todos los tipos de jugadores (Humanos y Máquinas).
 * Mantiene encapsulado su personaje secreto y gestiona su tablero de deducción de candidatos.
 */
public abstract class Jugador {
    protected final String nombre;
    protected final Persona personajeSecreto;
    protected final List<Persona> candidatosRestantes;
    protected final Set<FiltroPregunta> preguntasRealizadas;

    public Jugador(String nombre, Persona personajeSecreto, List<Persona> catalogoInicial) {
        this.nombre = Objects.requireNonNull(nombre, "El nombre del jugador no puede ser nulo");
        this.personajeSecreto = Objects.requireNonNull(personajeSecreto, "El personaje secreto no puede ser nulo");
        this.candidatosRestantes = new ArrayList<>(Objects.requireNonNull(catalogoInicial, "El catálogo inicial no puede ser nulo"));
        this.preguntasRealizadas = new HashSet<>();
    }

    public String getNombre() {
        return nombre;
    }

    /**
     * Retorna el personaje secreto que este jugador eligió para que el oponente lo adivine.
     */
    public Persona getPersonajeSecreto() {
        return personajeSecreto;
    }

    public List<Persona> getCandidatosRestantes() {
        return Collections.unmodifiableList(candidatosRestantes);
    }

    public int getCantidadCandidatosRestantes() {
        return candidatosRestantes.size();
    }

    public Set<FiltroPregunta> getPreguntasRealizadas() {
        return Collections.unmodifiableSet(preguntasRealizadas);
    }

    /**
     * Aplica el principio de 'Divide & Conquer' para reducir el espacio de candidatos restantes
     * tras conocer la respuesta a una pregunta formulada.
     *
     * @param pregunta Pregunta evaluada.
     * @param respuesta Respuesta booleana (SÍ = true, NO = false).
     * @return Cantidad de candidatos descartados en este paso.
     */
    public int aplicarFiltro(FiltroPregunta pregunta, boolean respuesta) {
        preguntasRealizadas.add(pregunta);
        int cantidadPrevia = candidatosRestantes.size();
        List<Persona> podados = FiltroParticion.podarEspacio(candidatosRestantes, pregunta, respuesta);
        candidatosRestantes.clear();
        candidatosRestantes.addAll(podados);
        return cantidadPrevia - candidatosRestantes.size();
    }

    /**
     * Descarta directamente a una persona de los candidatos (por ejemplo, si se arriesgó un ID incorrecto).
     */
    public boolean descartarCandidatoPorId(int id) {
        return candidatosRestantes.removeIf(p -> p.getId() == id);
    }

    /**
     * Determina si el jugador tiene la certeza de 1 solo candidato restante.
     */
    public boolean tieneCertezaAbsoluta() {
        return candidatosRestantes.size() == 1;
    }

    public Persona getCandidatoUnico() {
        return tieneCertezaAbsoluta() ? candidatosRestantes.get(0) : null;
    }
}
