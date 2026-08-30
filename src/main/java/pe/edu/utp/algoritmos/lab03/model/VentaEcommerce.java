package pe.edu.utp.algoritmos.lab03.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Modelo de una venta e-commerce.
 *
 * Cada objeto representa una fila del CSV:
 * orden_id, fecha_compra, cliente_id, region, canal, producto,
 * cantidad, total_soles, prioridad_entrega.
 *
 * La clave de ordenamiento del caso 01 es fechaCompra ASC.
 */
public record VentaEcommerce(
        String ordenId,
        LocalDateTime fechaCompra,
        String clienteId,
        String region,
        String canal,
        String producto,
        int cantidad,
        double totalSoles,
        String prioridadEntrega
) {

    public static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Convierte una línea CSV en un objeto VentaEcommerce.
     *
     * Nota didáctica:
     * En un CSV real podrían existir comas dentro de texto; para esta práctica
     * los campos son simples y se separan con split(",").
     */
    public static VentaEcommerce desdeCsv(String linea) {
        String[] c = linea.split(",", -1);
        if (c.length != 9) {
            throw new IllegalArgumentException("Fila inválida: " + linea);
        }
        return new VentaEcommerce(
                c[0].trim(),
                LocalDateTime.parse(c[1].trim(), FORMATO_FECHA),
                c[2].trim(),
                c[3].trim(),
                c[4].trim(),
                c[5].trim(),
                Integer.parseInt(c[6].trim()),
                Double.parseDouble(c[7].trim()),
                c[8].trim()
        );
    }

    /**
     * Convierte el objeto nuevamente a línea CSV.
     */
    public String aCsv() {
        return String.format(
                Locale.US,
                "%s,%s,%s,%s,%s,%s,%d,%.2f,%s",
                ordenId,
                fechaCompra.format(FORMATO_FECHA),
                clienteId,
                region,
                canal,
                producto,
                cantidad,
                totalSoles,
                prioridadEntrega
        );
    }
}
