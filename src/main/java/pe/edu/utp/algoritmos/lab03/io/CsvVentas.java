package pe.edu.utp.algoritmos.lab03.io;

import pe.edu.utp.algoritmos.lab03.metrics.MetricasOrdenacionExterna;
import pe.edu.utp.algoritmos.lab03.model.VentaEcommerce;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Funciones auxiliares para leer, escribir y validar archivos CSV.
 */
public final class CsvVentas {

    public static final String CABECERA = "orden_id,fecha_compra,cliente_id,region,canal,producto,cantidad,total_soles,prioridad_entrega";

    private CsvVentas() {}

    /**
     * Lee las primeras n ventas para mostrar evidencia en consola o PPT.
     */
    public static List<VentaEcommerce> leerPrimeras(Path archivo, int n) throws IOException {
        List<VentaEcommerce> lista = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            br.readLine();
            String linea;
            while (lista.size() < n && (linea = br.readLine()) != null) {
                if (!linea.isBlank()) {
                    lista.add(VentaEcommerce.desdeCsv(linea));
                }
            }
        }
        return lista;
    }

    /**
     * Valida que el archivo esté ordenado por fecha_compra ASC.
     */
    public static boolean estaOrdenadoPorFecha(Path archivo) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            br.readLine();
            String linea;
            LocalDateTime anterior = null;
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) continue;
                LocalDateTime actual = VentaEcommerce.desdeCsv(linea).fechaCompra();
                if (anterior != null && actual.isBefore(anterior)) {
                    return false;
                }
                anterior = actual;
            }
            return true;
        }
    }

    public static String leerLinea(BufferedReader br, MetricasOrdenacionExterna m) throws IOException {
        String linea = br.readLine();
        if (linea != null) m.lectura();
        return linea;
    }

    public static void escribirLinea(BufferedWriter bw, String linea, MetricasOrdenacionExterna m) throws IOException {
        bw.write(linea);
        bw.newLine();
        m.escritura();
    }
}
