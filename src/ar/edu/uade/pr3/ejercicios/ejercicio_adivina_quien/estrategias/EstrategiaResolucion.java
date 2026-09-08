package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.estrategias;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.Persona;
import java.util.List;

/**
 * Interfaz para definir la estrategia para resolver automáticamente el juego.
 * Terminé aplicando el patrón de diseño Strategy, pero podría no haberlo implementado adicionando algunos pasos
 */
public interface EstrategiaResolucion {
    /**
     * Retorna el nombre legible de la estrategia de resolución.
     * 
     * @return Nombre de la estrategia.
     */
    String getNombre();

    /**
     * Resuelve el juego buscando al personaje elegido entre la lista dada.
     * 
     * @param personajes Lista de personajes disponibles.
     * @return El personaje que es el elegido, o null si no se encuentra.
     */
    Persona resolver(List<Persona> personajes);
}
