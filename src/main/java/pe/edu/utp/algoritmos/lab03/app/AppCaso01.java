package pe.edu.utp.algoritmos.lab03.app;

import pe.edu.utp.algoritmos.lab03.io.CsvVentas;
import pe.edu.utp.algoritmos.lab03.metrics.MetricasOrdenacionExterna;
import pe.edu.utp.algoritmos.lab03.model.VentaEcommerce;
import pe.edu.utp.algoritmos.lab03.sort.FusionNaturalExterna;
import pe.edu.utp.algoritmos.lab03.sort.MezclaDirectaExterna;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Programa principal del Caso 01.
 *
 * El docente puede ejecutarlo en clase para demostrar:
 * - lectura de CSV,
 * - ordenación externa,
 * - comparación de Mezcla Directa vs Fusión Natural,
 * - evidencias antes/después,
 * - métricas por algoritmo.
 */
public class AppCaso01 {

    private static final int BLOQUE_DEFECTO = 100;
    private static final int MUESTRA = 10;

    public static void main(String[] args) throws Exception {
        int tamanoBloque = args.length > 0 ? Integer.parseInt(args[0]) : BLOQUE_DEFECTO;

        Path entrada = Paths.get("src/main/resources/datos/caso_01_ventas_ecommerce_docente.csv");
        Path salidaDir = Paths.get("salida");
        Files.createDirectories(salidaDir);

        Path salidaMezclaDirecta = salidaDir.resolve("caso01_ordenado_mezcla_directa.csv");
        Path salidaFusionNatural = salidaDir.resolve("caso01_ordenado_fusion_natural.csv");
        Path salidaMetricas = salidaDir.resolve("metricas_caso01.csv");

        System.out.println("============================================================");
        System.out.println(" SEMANA 03 · SESIÓN 02 LABORATORIO");
        System.out.println(" CASO 01: VENTAS E-COMMERCE");
        System.out.println("============================================================");
        System.out.println("Archivo : " + entrada.toAbsolutePath());
        System.out.println("Clave   : fecha_compra ASC");
        System.out.println("Bloque  : " + tamanoBloque + " registros");

        imprimirMuestra("ANTES DE ORDENAR", entrada);

        MetricasOrdenacionExterna md = new MezclaDirectaExterna()
                .ordenar(entrada, salidaMezclaDirecta, tamanoBloque);

        MetricasOrdenacionExterna fn = new FusionNaturalExterna()
                .ordenar(entrada, salidaFusionNatural);

        imprimirMuestra("DESPUÉS - MEZCLA DIRECTA", salidaMezclaDirecta);
        imprimirMuestra("DESPUÉS - FUSIÓN NATURAL", salidaFusionNatural);

        List<MetricasOrdenacionExterna> metricas = List.of(md, fn);
        imprimirMetricas(metricas);
        exportarMetricas(salidaMetricas, metricas);

        System.out.println("\nArchivos generados:");
        System.out.println("- " + salidaMezclaDirecta.toAbsolutePath());
        System.out.println("- " + salidaFusionNatural.toAbsolutePath());
        System.out.println("- " + salidaMetricas.toAbsolutePath());
    }

    private static void imprimirMuestra(String titulo, Path archivo) throws IOException {
        System.out.println("\n---- " + titulo + " ----");
        System.out.printf("%-10s %-20s %-12s %-13s %10s%n", "ORDEN", "FECHA", "REGIÓN", "PRODUCTO", "TOTAL");
        for (VentaEcommerce v : CsvVentas.leerPrimeras(archivo, MUESTRA)) {
            System.out.printf(Locale.US, "%-10s %-20s %-12s %-13s %10.2f%n",
                    v.ordenId(),
                    v.fechaCompra().format(VentaEcommerce.FORMATO_FECHA),
                    v.region(),
                    v.producto(),
                    v.totalSoles());
        }
    }

    private static void imprimirMetricas(List<MetricasOrdenacionExterna> metricas) {
        System.out.println("\n============================================================");
        System.out.println(" MÉTRICAS DE EJECUCIÓN");
        System.out.println("============================================================");
        System.out.printf("%-16s %8s %8s %8s %12s %10s %10s %9s %10s %8s%n",
                "Algoritmo", "Reg.", "Runs", "Pasadas", "Comparac.", "Lecturas", "Escrit.", "Tmp", "Tiempo", "OK");
        for (MetricasOrdenacionExterna m : metricas) {
            System.out.printf(Locale.US, "%-16s %8d %8d %8d %12d %10d %10d %9d %8.3fms %8s%n",
                    m.algoritmo(), m.registros(), m.corridasIniciales(), m.pasadasFusion(),
                    m.comparaciones(), m.lecturas(), m.escrituras(), m.archivosTemporales(),
                    m.tiempoMs(), m.ordenCorrecto() ? "SI" : "NO");
        }
    }

    private static void exportarMetricas(Path salida, List<MetricasOrdenacionExterna> metricas) throws IOException {
        List<String> lineas = new ArrayList<>();
        lineas.add(MetricasOrdenacionExterna.cabeceraCsv());
        for (MetricasOrdenacionExterna m : metricas) {
            lineas.add(m.aCsv());
        }
        Files.write(salida, lineas, StandardCharsets.UTF_8);
    }
}
