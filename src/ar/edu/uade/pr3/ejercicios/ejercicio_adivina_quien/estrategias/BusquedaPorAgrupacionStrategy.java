package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.estrategias;

import ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.modelos.Persona;

import java.util.ArrayList;
import java.util.List;

/**
 * Estrategia de resolución automática por Agrupación (Descarte de Grupos).
 * Agrupa las opciones en bloques de tamaño 3, identifica en qué grupo está el objetivo y descarta los demás grupos.
 */
public class BusquedaPorAgrupacionStrategy implements EstrategiaResolucion {

    private final int tamanoGrupo;

    public BusquedaPorAgrupacionStrategy() {
        this(3); // Por defecto agrupa de a 3 según el enunciado
    }

    public BusquedaPorAgrupacionStrategy(int tamanoGrupo) {
        this.tamanoGrupo = tamanoGrupo;
    }

    @Override
    public String getNombre() {
        return "Búsqueda por Agrupación y Descarte (Grupos de " + tamanoGrupo + ")";
    }

    @Override
    public Persona resolver(List<Persona> personajes) {
        System.out.println("\n--- [MAQUINA] Iniciando resolución por Agrupación y Descarte ---");
        List<Persona> candidatos = new ArrayList<>(personajes);
        int ronda = 1;

        while (!candidatos.isEmpty()) {
            System.out.printf("%nRonda %d: Evaluando %d candidatos restantes.%n", ronda++, candidatos.size());
            
            // Si solo queda 1 candidato, es la respuesta directamente
            if (candidatos.size() == 1) {
                Persona unico = candidatos.get(0);
                if (unico.esElegido()) {
                    System.out.printf("¡Solución aislada! El personaje elegido es ID %d: %s%n", unico.getId(), unico.getNombreCompleto());
                    return unico;
                }
            }

            // Dividir los candidatos en subgrupos de a 3
            List<List<Persona>> grupos = crearGrupos(candidatos, tamanoGrupo);
            System.out.printf("Se dividieron los candidatos en %d grupo(s):%n", grupos.size());

            List<Persona> grupoGanador = null;

            for (int i = 0; i < grupos.size(); i++) {
                List<Persona> grupo = grupos.get(i);
                int minId = grupo.get(0).getId();
                int maxId = grupo.get(grupo.size() - 1).getId();
                System.out.printf("  Grupo %d: Rango ID [%d - %d] (%d elementos)%n", (i + 1), minId, maxId, grupo.size());

                // Verificar si la respuesta está en este grupo
                boolean contieneElegido = false;
                for (Persona p : grupo) {
                    if (p.esElegido()) {
                        contieneElegido = true;
                        break;
                    }
                }

                if (contieneElegido) {
                    grupoGanador = grupo;
                    System.out.printf("  -> [RECUERDO MAQUINA]: El personaje está entre el ID %d y ID %d.%n", minId, maxId);
                } else {
                    System.out.printf("  -> Descartando Grupo %d (IDs %d a %d no contienen al elegido).%n", (i + 1), minId, maxId);
                }
            }

            if (grupoGanador != null) {
                // Conservar únicamente el grupo ganador
                candidatos = grupoGanador;

                // Si el grupo ganador ya tiene pocos elementos, evaluar minuciosamente
                if (candidatos.size() <= tamanoGrupo) {
                    System.out.println("Inspeccionando elementos del grupo seleccionado:");
                    for (Persona p : candidatos) {
                        if (p.esElegido()) {
                            System.out.printf("¡ENCONTRADO! ID %d: %s%n", p.getId(), p.getNombreCompleto());
                            return p;
                        } else {
                            System.out.printf("%s: no es el elegido.%n", p.getNombreCompleto());
                        }
                    }
                }
            } else {
                // Ningún grupo tenía al elegido
                break;
            }
        }

        return null;
    }

    private List<List<Persona>> crearGrupos(List<Persona> lista, int tamano) {
        List<List<Persona>> resultado = new ArrayList<>();
        for (int i = 0; i < lista.size(); i += tamano) {
            int fin = Math.min(i + tamano, lista.size());
            resultado.add(new ArrayList<>(lista.subList(i, fin)));
        }
        return resultado;
    }
}
