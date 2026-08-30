package pe.edu.utp.algoritmos.lab03.metrics;

import java.util.Locale;

/**
 * Métricas para explicar ordenación externa.
 *
 * En ordenación interna se suele hablar de swaps y comparaciones.
 * En ordenación externa también importan las lecturas y escrituras,
 * porque el archivo puede estar en disco y no caber en RAM.
 */
public class MetricasOrdenacionExterna {

    private final String algoritmo;
    private long registros;
    private long corridasIniciales;
    private long pasadasFusion;
    private long comparaciones;
    private long lecturas;
    private long escrituras;
    private long archivosTemporales;
    private double tiempoMs;
    private boolean ordenCorrecto;

    public MetricasOrdenacionExterna(String algoritmo) {
        this.algoritmo = algoritmo;
    }

    public String algoritmo() { return algoritmo; }
    public long registros() { return registros; }
    public long corridasIniciales() { return corridasIniciales; }
    public long pasadasFusion() { return pasadasFusion; }
    public long comparaciones() { return comparaciones; }
    public long lecturas() { return lecturas; }
    public long escrituras() { return escrituras; }
    public long archivosTemporales() { return archivosTemporales; }
    public double tiempoMs() { return tiempoMs; }
    public boolean ordenCorrecto() { return ordenCorrecto; }

    public void registroLeidoEntrada() { registros++; lecturas++; }
    public void lectura() { lecturas++; }
    public void escritura() { escrituras++; }
    public void comparacion() { comparaciones++; }
    public void corridaInicial() { corridasIniciales++; }
    public void pasadaFusion() { pasadasFusion++; }
    public void temporalCreado() { archivosTemporales++; }
    public void setTiempoNs(long ns) { this.tiempoMs = ns / 1_000_000.0; }
    public void setOrdenCorrecto(boolean ok) { this.ordenCorrecto = ok; }

    public static String cabeceraCsv() {
        return "algoritmo,registros,corridas_iniciales,pasadas_fusion,comparaciones,lecturas,escrituras,temporales,tiempo_ms,orden_correcto";
    }

    public String aCsv() {
        return String.format(Locale.US,
                "%s,%d,%d,%d,%d,%d,%d,%d,%.3f,%s",
                algoritmo, registros, corridasIniciales, pasadasFusion,
                comparaciones, lecturas, escrituras, archivosTemporales,
                tiempoMs, ordenCorrecto ? "SI" : "NO");
    }
}
