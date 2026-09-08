package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.juego;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.ColorPelo;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.Genero;
import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.Persona;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Fábrica y repositorio central del catálogo de 23 personajes.
 * Cumple con el requisito: "Los personajes empiezan ordenados únicamente según su género
 * y es la máquina quien debe disponerlos en una lista ordenada de forma autoincremental
 * según se agregan los personajes".
 */
public class CatalogoPersonajes {
    public static final int TOTAL_PERSONAJES = 23;

    /**
     * Retorna la lista oficial de los 23 personajes requeridos,
     * organizados con IDs autoincrementales del 1 al 23.
     */
    public static List<Persona> crearCatalogoOficial() {
        Persona.resetearContadorDeIDs();

        // 1. Definición inicial de los 23 personajes ordenados por género (requisito del enunciado)
        List<RawPersonaData> datosIniciales = new ArrayList<>();

        // --- FEMENINO (11 personajes) ---
        datosIniciales.add(new RawPersonaData("Ana", "Gomez", Genero.FEMENINO, false, true, ColorPelo.COLORADO));
        datosIniciales.add(new RawPersonaData("Beatriz", "Lopez", Genero.FEMENINO, false, false, ColorPelo.NEGRO));
        datosIniciales.add(new RawPersonaData("Clara", "Rodriguez", Genero.FEMENINO, false, true, ColorPelo.AMARILLO));
        datosIniciales.add(new RawPersonaData("Diana", "Fernandez", Genero.FEMENINO, false, false, ColorPelo.COLORADO));
        datosIniciales.add(new RawPersonaData("Elena", "Martinez", Genero.FEMENINO, false, true, ColorPelo.NEGRO));
        datosIniciales.add(new RawPersonaData("Florencia", "Alvarez", Genero.FEMENINO, false, false, ColorPelo.AMARILLO));
        datosIniciales.add(new RawPersonaData("Gabriela", "Perez", Genero.FEMENINO, true, true, ColorPelo.NINGUNO));
        datosIniciales.add(new RawPersonaData("Helena", "Sanchez", Genero.FEMENINO, false, true, ColorPelo.COLORADO));
        datosIniciales.add(new RawPersonaData("Ines", "Romero", Genero.FEMENINO, false, false, ColorPelo.NEGRO));
        datosIniciales.add(new RawPersonaData("Julia", "Torres", Genero.FEMENINO, false, true, ColorPelo.AMARILLO));
        datosIniciales.add(new RawPersonaData("Karina", "Diaz", Genero.FEMENINO, true, false, ColorPelo.NINGUNO));

        // --- MASCULINO (12 personajes) ---
        datosIniciales.add(new RawPersonaData("Lucas", "Gutierrez", Genero.MASCULINO, false, true, ColorPelo.COLORADO));
        datosIniciales.add(new RawPersonaData("Martin", "Gonzalez", Genero.MASCULINO, false, false, ColorPelo.NEGRO));
        datosIniciales.add(new RawPersonaData("Nicolas", "Vazquez", Genero.MASCULINO, false, true, ColorPelo.AMARILLO));
        datosIniciales.add(new RawPersonaData("Oscar", "Castro", Genero.MASCULINO, true, true, ColorPelo.NINGUNO));
        datosIniciales.add(new RawPersonaData("Pablo", "Morales", Genero.MASCULINO, true, false, ColorPelo.NINGUNO));
        datosIniciales.add(new RawPersonaData("Quique", "Herrera", Genero.MASCULINO, false, false, ColorPelo.COLORADO));
        datosIniciales.add(new RawPersonaData("Ramiro", "Medina", Genero.MASCULINO, false, true, ColorPelo.NEGRO));
        datosIniciales.add(new RawPersonaData("Santiago", "Rios", Genero.MASCULINO, false, false, ColorPelo.AMARILLO));
        datosIniciales.add(new RawPersonaData("Tomas", "Benitez", Genero.MASCULINO, true, true, ColorPelo.NINGUNO));
        datosIniciales.add(new RawPersonaData("Ulises", "Silva", Genero.MASCULINO, false, true, ColorPelo.COLORADO));
        datosIniciales.add(new RawPersonaData("Valentin", "Suarez", Genero.MASCULINO, false, false, ColorPelo.NEGRO));
        datosIniciales.add(new RawPersonaData("Walter", "Mendoza", Genero.MASCULINO, true, false, ColorPelo.NINGUNO));

        // Verificamos que el ordenamiento inicial por género esté garantizado
        datosIniciales.sort(Comparator.comparing(RawPersonaData::getGenero));

        // 2. La máquina dispone los personajes en una lista ordenada de forma autoincremental según se agregan
        List<Persona> catalogoOrdenado = new ArrayList<>(TOTAL_PERSONAJES);
        for (RawPersonaData data : datosIniciales) {
            catalogoOrdenado.add(new Persona(
                    data.nombre,
                    data.apellido,
                    data.genero,
                    data.esCalvo,
                    data.usaLentes,
                    data.colorPelo
            ));
        }

        return catalogoOrdenado;
    }

    /**
     * Genera una copia limpia e independiente del catálogo de personajes.
     */
    public static List<Persona> clonarCatalogo(List<Persona> original) {
        List<Persona> copia = new ArrayList<>(original.size());
        for (Persona p : original) {
            copia.add(new Persona(
                    p.getId(),
                    p.getNombre(),
                    p.getApellido(),
                    p.getGenero(),
                    p.esCalvo(),
                    p.usaLentes(),
                    p.getColorPelo(),
                    p.esElegido()
            ));
        }
        return copia;
    }

    private static class RawPersonaData {
        final String nombre;
        final String apellido;
        final Genero genero;
        final boolean esCalvo;
        final boolean usaLentes;
        final ColorPelo colorPelo;

        RawPersonaData(String nombre, String apellido, Genero genero, boolean esCalvo, boolean usaLentes, ColorPelo colorPelo) {
            this.nombre = nombre;
            this.apellido = apellido;
            this.genero = genero;
            this.esCalvo = esCalvo;
            this.usaLentes = usaLentes;
            this.colorPelo = colorPelo;
        }

        Genero getGenero() {
            return genero;
        }
    }
}
