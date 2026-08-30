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

/**
 * Fusiona dos corridas ya ordenadas por fecha_compra ASC.
 */
final class FusionadorCsv {

    private FusionadorCsv() {}

    static void fusionar(Path runA, Path runB, Path salida, MetricasOrdenacionExterna m) throws IOException {
        try (BufferedReader a = Files.newBufferedReader(runA, StandardCharsets.UTF_8);
             BufferedReader b = Files.newBufferedReader(runB, StandardCharsets.UTF_8);
             BufferedWriter out = Files.newBufferedWriter(salida, StandardCharsets.UTF_8)) {

            String lineaA = CsvVentas.leerLinea(a, m);
            String lineaB = CsvVentas.leerLinea(b, m);

            while (lineaA != null && lineaB != null) {
                VentaEcommerce ventaA = VentaEcommerce.desdeCsv(lineaA);
                VentaEcommerce ventaB = VentaEcommerce.desdeCsv(lineaB);
                m.comparacion();

                if (!ventaA.fechaCompra().isAfter(ventaB.fechaCompra())) {
                    CsvVentas.escribirLinea(out, lineaA, m);
                    lineaA = CsvVentas.leerLinea(a, m);
                } else {
                    CsvVentas.escribirLinea(out, lineaB, m);
                    lineaB = CsvVentas.leerLinea(b, m);
                }
            }

            while (lineaA != null) {
                CsvVentas.escribirLinea(out, lineaA, m);
                lineaA = CsvVentas.leerLinea(a, m);
            }
            while (lineaB != null) {
                CsvVentas.escribirLinea(out, lineaB, m);
                lineaB = CsvVentas.leerLinea(b, m);
            }
        }
    }
}
