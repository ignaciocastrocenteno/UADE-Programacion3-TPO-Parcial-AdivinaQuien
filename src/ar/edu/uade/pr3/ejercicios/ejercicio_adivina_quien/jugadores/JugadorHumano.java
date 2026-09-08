package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.jugadores;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.Persona;

import java.util.List;

/**
 * Representa al jugador humano, cuyas decisiones y consultas provienen de la interfaz de consola.
 */
public class JugadorHumano extends Jugador {

    public JugadorHumano(String nombre, Persona personajeSecreto, List<Persona> catalogoInicial) {
        super(nombre, personajeSecreto, catalogoInicial);
    }
}
