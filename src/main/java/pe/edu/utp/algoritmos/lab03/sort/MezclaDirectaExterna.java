package pe.edu.utp.algoritmos.lab03.sort;

import pe.edu.utp.algoritmos.lab03.io.CsvVentas;
import pe.edu.utp.algoritmos.lab03.metrics.MetricasOrdenacionExterna;
import pe.edu.utp.algoritmos.lab03.model.VentaEcommerce;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Algoritmo de Mezcla Directa Externa.
 *
 * Flujo didáctico:
 * 1. Leer el archivo por bloques de tamaño fijo.
 * 2. Ordenar cada bloque en memoria.
 * 3. Guardar cada bloque como corrida inicial.
 * 4. Fusionar corridas de dos en dos hasta obtener una sola.
 */
public class MezclaDirectaExterna {

    public MetricasOrdenacionExterna ordenar(Path entrada, Path salida, int tamanoBloque) throws IOException {
        if (tamanoBloque <= 0) throw new IllegalArgumentException("El tamaño de bloque debe ser mayor a cero");

        MetricasOrdenacionExterna m = new MetricasOrdenacionExterna("Mezcla Directa");
        long inicio = System.nanoTime();
        Path tmp = Files.createTempDirectory("caso01-md-");

        try {
            List<Path> corridas = generarCorridasIniciales(entrada, tamanoBloque, tmp, m);
            List<Path> finales = fusionarHastaUnaCorrida(corridas, tmp, m);
            copiarSalidaFinal(finales.get(0), salida, m);
            m.setOrdenCorrecto(CsvVentas.estaOrdenadoPorFecha(salida));
        } finally {
            m.setTiempoNs(System.nanoTime() - inicio);
            limpiar(tmp);
        }
        return m;
    }

    private List<Path> generarCorridasIniciales(Path entrada, int tamanoBloque, Path tmp, MetricasOrdenacionExterna m) throws IOException {
        List<Path> corridas = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(entrada, StandardCharsets.UTF_8)) {
            br.readLine(); // cabecera
            int nroRun = 0;

            while (true) {
                List<VentaEcommerce> bloque = new ArrayList<>(tamanoBloque);
                while (bloque.size() < tamanoBloque) {
                    String linea = br.readLine();
                    if (linea == null) break;
                    if (linea.isBlank()) continue;
                    m.registroLeidoEntrada();
                    bloque.add(VentaEcommerce.desdeCsv(linea));
                }
                if (bloque.isEmpty()) break;

                // Ordena el bloque en memoria y cuenta comparaciones.
                // Aquí sí instrumentamos el comparador para que la métrica sea visible.
                bloque.sort((a, b) -> {
                    m.comparacion();
                    return a.fechaCompra().compareTo(b.fechaCompra());
                });

                Path run = tmp.resolve(String.format("run_md_%03d.tmp", ++nroRun));
                m.corridaInicial();
                m.temporalCreado();

                try (BufferedWriter bw = Files.newBufferedWriter(run, StandardCharsets.UTF_8)) {
                    for (VentaEcommerce v : bloque) {
                        CsvVentas.escribirLinea(bw, v.aCsv(), m);
                    }
                }
                corridas.add(run);
            }
        }
        return corridas;
    }

    private List<Path> fusionarHastaUnaCorrida(List<Path> corridas, Path tmp, MetricasOrdenacionExterna m) throws IOException {
        List<Path> actuales = new ArrayList<>(corridas);
        int nivel = 0;
        while (actuales.size() > 1) {
            m.pasadaFusion();
            nivel++;
            List<Path> nuevas = new ArrayList<>();
            for (int i = 0; i < actuales.size(); i += 2) {
                if (i + 1 >= actuales.size()) {
                    nuevas.add(actuales.get(i));
                } else {
                    Path fusion = tmp.resolve(String.format("md_n%d_%03d.tmp", nivel, i / 2));
                    m.temporalCreado();
                    FusionadorCsv.fusionar(actuales.get(i), actuales.get(i + 1), fusion, m);
                    nuevas.add(fusion);
                }
            }
            actuales = nuevas;
        }
        return actuales;
    }

    private void copiarSalidaFinal(Path corridaFinal, Path salida, MetricasOrdenacionExterna m) throws IOException {
        Files.createDirectories(salida.toAbsolutePath().getParent());
        try (BufferedReader br = Files.newBufferedReader(corridaFinal, StandardCharsets.UTF_8);
             BufferedWriter bw = Files.newBufferedWriter(salida, StandardCharsets.UTF_8)) {
            bw.write(CsvVentas.CABECERA);
            bw.newLine();
            String linea;
            while ((linea = CsvVentas.leerLinea(br, m)) != null) {
                CsvVentas.escribirLinea(bw, linea, m);
            }
        }
    }

    private void limpiar(Path dir) {
        try (var s = Files.walk(dir)) {
            s.sorted((a, b) -> b.compareTo(a)).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (IOException ignored) {}
            });
        } catch (IOException ignored) {}
    }
}
