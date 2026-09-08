package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.juego;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.Persona;
import java.util.List;

/**
 * Objeto de valor que representa un rango de comodín agrupado.
 */
public class RangoGrupo {
    private final int minId;
    private final int maxId;
    private final List<Persona> personajes;

    public RangoGrupo(int minId, int maxId, List<Persona> personajes) {
        this.minId = minId;
        this.maxId = maxId;
        this.personajes = personajes;
    }

    public int getMinId() {
        return minId;
    }

    public int getMaxId() {
        return maxId;
    }

    public List<Persona> getPersonajes() {
        return personajes;
    }

    @Override
    public String toString() {
        return String.format("El personaje está entre el int %d y int %d.", minId, maxId);
    }
}
