package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.estrategias;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.Persona;
import java.util.List;

/**
 * Estrategia de resolución automática por Búsqueda Lineal.
 * Complejidad temporal que resuelve este algoritmo: BigO(n)
 */
public class BusquedaLinealStrategy implements EstrategiaResolucion {

    @Override
    public String getNombre() {
        return "Búsqueda Lineal (Secuencial)";
    }

    @Override
    public Persona resolver(List<Persona> personajes) {
        System.out.println("\n--- [MAQUINA] Iniciando resolución por Búsqueda Lineal ---");
        int paso = 1;
        for (Persona p : personajes) {
            System.out.printf("Paso %d: Evaluando ID %d - %s... ", paso++, p.getId(), p.getNombreCompleto());
            if (p.esElegido()) {
                System.out.println("¡ENCONTRADO!");
                return p;
            } else {
                System.out.println(p.getNombreCompleto() + ": no es el elegido.");
            }
        }
        return null;
    }
}
