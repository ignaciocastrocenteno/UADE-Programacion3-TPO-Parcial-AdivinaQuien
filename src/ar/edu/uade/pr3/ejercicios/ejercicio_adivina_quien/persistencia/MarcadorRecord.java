package ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestor de persistencia en memoria del marcador récord de victorias.
 * Almacena las partidas ganadas por cada jugador en memoria (HashMap / TreeMap),
 * con capacidad de respaldo opcional en un archivo JSON ubicado en la raíz del proyecto.
 */
public class MarcadorRecord {
    public static final String ARCHIVO_JSON_DEFAULT = "marcador_record.json";

    private static MarcadorRecord instancia;
    private final Map<String, Integer> victoriasPorUsuario;

    public MarcadorRecord() {
        this.victoriasPorUsuario = new HashMap<>();
        cargarDesdeJsonSiExiste(ARCHIVO_JSON_DEFAULT);
    }

    public static synchronized MarcadorRecord getInstancia() {
        if (instancia == null) {
            instancia = new MarcadorRecord();
        }
        return instancia;
    }

    /**
     * Registra e incrementa una victoria para el jugador en memoria y sincroniza al archivo JSON.
     */
    public synchronized void registrarVictoria(String nombreUsuario) {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            nombreUsuario = "Anónimo";
        }
        String clave = nombreUsuario.trim();
        int actual = victoriasPorUsuario.getOrDefault(clave, 0);
        victoriasPorUsuario.put(clave, actual + 1);
        guardarEnJson(ARCHIVO_JSON_DEFAULT);
    }

    /**
     * Obtiene la cantidad de victorias acumuladas de un usuario.
     */
    public synchronized int obtenerVictorias(String nombreUsuario) {
        if (nombreUsuario == null) return 0;
        return victoriasPorUsuario.getOrDefault(nombreUsuario.trim(), 0);
    }

    /**
     * Retorna una lista ordenada de jugadores según cantidad de victorias (de mayor a menor).
     */
    public synchronized List<RegistroRanking> obtenerRanking() {
        List<RegistroRanking> ranking = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : victoriasPorUsuario.entrySet()) {
            ranking.add(new RegistroRanking(entry.getKey(), entry.getValue()));
        }
        ranking.sort((a, b) -> Integer.compare(b.getVictorias(), a.getVictorias()));
        return Collections.unmodifiableList(ranking);
    }

    /**
     * Resetea el almacenamiento en memoria (útil para pruebas unitarias).
     */
    public synchronized void limpiar() {
        victoriasPorUsuario.clear();
    }

    /**
     * Persiste el estado actual de la memoria en formato JSON estándar en el archivo especificado.
     */
    public synchronized void guardarEnJson(String rutaArchivo) {
        File file = new File(rutaArchivo);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("{\n");
            writer.write("  \"marcador\": [\n");
            List<Map.Entry<String, Integer>> entries = new ArrayList<>(victoriasPorUsuario.entrySet());
            for (int i = 0; i < entries.size(); i++) {
                Map.Entry<String, Integer> e = entries.get(i);
                writer.write(String.format("    {\"jugador\": \"%s\", \"victorias\": %d}%s\n",
                        escapeJson(e.getKey()),
                        e.getValue(),
                        (i < entries.size() - 1 ? "," : "")));
            }
            writer.write("  ]\n");
            writer.write("}\n");
        } catch (IOException e) {
            System.err.println("[MarcadorRecord] Advertencia: No se pudo guardar en JSON: " + e.getMessage());
        }
    }

    /**
     * Carga el historial desde un archivo JSON si existe en la raíz.
     */
    public synchronized void cargarDesdeJsonSiExiste(String rutaArchivo) {
        File file = new File(rutaArchivo);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (linea.contains("\"jugador\"") && linea.contains("\"victorias\"")) {
                    String jugador = extraerCampo(linea, "jugador");
                    String victoriasStr = extraerCampo(linea, "victorias");
                    if (jugador != null && victoriasStr != null) {
                        try {
                            int vic = Integer.parseInt(victoriasStr.replaceAll("[^0-9]", ""));
                            victoriasPorUsuario.put(jugador, vic);
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[MarcadorRecord] Advertencia: No se pudo leer JSON: " + e.getMessage());
        }
    }

    private String escapeJson(String texto) {
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String extraerCampo(String linea, String campo) {
        int index = linea.indexOf("\"" + campo + "\"");
        if (index == -1) return null;
        int dosPuntos = linea.indexOf(":", index);
        if (dosPuntos == -1) return null;
        String resto = linea.substring(dosPuntos + 1).trim();
        if (resto.startsWith("\"")) {
            int cierre = resto.indexOf("\"", 1);
            if (cierre != -1) {
                return resto.substring(1, cierre);
            }
        } else {
            int coma = resto.indexOf(",");
            int llave = resto.indexOf("}");
            int fin = resto.length();
            if (coma != -1 && coma < fin) fin = coma;
            if (llave != -1 && llave < fin) fin = llave;
            return resto.substring(0, fin).trim();
        }
        return null;
    }

    public static class RegistroRanking {
        private final String jugador;
        private final int victorias;

        public RegistroRanking(String jugador, int victorias) {
            this.jugador = jugador;
            this.victorias = victorias;
        }

        public String getJugador() {
            return jugador;
        }

        public int getVictorias() {
            return victorias;
        }

        @Override
        public String toString() {
            return String.format("%-20s : %d victoria(s)", jugador, victorias);
        }
    }
}
