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
import java.util.List;

/**
 * Algoritmo de Fusión Natural Externa.
 *
 * A diferencia de Mezcla Directa, no corta en bloques fijos.
 * Detecta secuencias que ya vienen ordenadas en el archivo original.
 */
public class FusionNaturalExterna {

    public MetricasOrdenacionExterna ordenar(Path entrada, Path salida) throws IOException {
        MetricasOrdenacionExterna m = new MetricasOrdenacionExterna("Fusión Natural");
        long inicio = System.nanoTime();
        Path tmp = Files.createTempDirectory("caso01-fn-");

        try {
            List<Path> corridas = detectarCorridasNaturales(entrada, tmp, m);
            List<Path> finales = fusionarHastaUnaCorrida(corridas, tmp, m);
            copiarSalidaFinal(finales.get(0), salida, m);
            m.setOrdenCorrecto(CsvVentas.estaOrdenadoPorFecha(salida));
        } finally {
            m.setTiempoNs(System.nanoTime() - inicio);
            limpiar(tmp);
        }
        return m;
    }

    private List<Path> detectarCorridasNaturales(Path entrada, Path tmp, MetricasOrdenacionExterna m) throws IOException {
        List<Path> corridas = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(entrada, StandardCharsets.UTF_8)) {
            br.readLine();
            String linea;
            VentaEcommerce anterior = null;
            BufferedWriter bw = null;
            int nroRun = 0;

            try {
                while ((linea = br.readLine()) != null) {
                    if (linea.isBlank()) continue;
                    m.registroLeidoEntrada();
                    VentaEcommerce actual = VentaEcommerce.desdeCsv(linea);

                    boolean nuevaCorrida = anterior == null;
                    if (anterior != null) {
                        m.comparacion();
                        if (actual.fechaCompra().isBefore(anterior.fechaCompra())) {
                            nuevaCorrida = true;
                        }
                    }

                    if (nuevaCorrida) {
                        if (bw != null) bw.close();
                        Path run = tmp.resolve(String.format("run_fn_%03d.tmp", ++nroRun));
                        bw = Files.newBufferedWriter(run, StandardCharsets.UTF_8);
                        corridas.add(run);
                        m.corridaInicial();
                        m.temporalCreado();
                    }

                    CsvVentas.escribirLinea(bw, actual.aCsv(), m);
                    anterior = actual;
                }
            } finally {
                if (bw != null) bw.close();
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
                    Path fusion = tmp.resolve(String.format("fn_n%d_%03d.tmp", nivel, i / 2));
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
