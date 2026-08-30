# Semana 03 · Sesión 02 Laboratorio
## Caso 01 resuelto por el docente: Ventas e-commerce

### Problema
Una tienda e-commerce recibe un archivo CSV con 1000 ventas. El equipo de operaciones necesita reconstruir la línea de tiempo de compras, por eso se debe ordenar el archivo por `fecha_compra` en forma ascendente.

- Archivo de entrada: `caso_01_ventas_ecommerce_docente.csv`
- Registros: 1000 + cabecera
- Clave de ordenamiento: `fecha_compra ASC`
- Algoritmos implementados:
  1. Mezcla Directa Externa
  2. Fusión Natural Externa
- Tamaño de bloque por defecto para Mezcla Directa: 100 registros

### Objetivo didáctico
El estudiante observa que una ordenación externa no depende solo de comparar valores, sino también de leer y escribir archivos temporales llamados corridas o *runs*.

### Ejecutar con Maven

```bash
mvn clean package
mvn exec:java
```

### Ejecutar con tamaño de bloque personalizado

```bash
mvn exec:java -Dexec.args="50"
```

### Salidas generadas

```text
salida/
├── caso01_ordenado_mezcla_directa.csv
├── caso01_ordenado_fusion_natural.csv
└── metricas_caso01.csv
```

### Métricas registradas

| Métrica | Interpretación |
|---|---|
| registros | filas de ventas procesadas |
| corridasIniciales | archivos temporales ordenados inicialmente |
| pasadasFusion | rondas de fusión requeridas |
| comparaciones | comparaciones por fecha de compra |
| lecturas | lecturas de registros desde archivos |
| escrituras | escrituras de registros en archivos |
| temporales | archivos temporales usados |
| tiempoMs | tiempo aproximado de ejecución |
| ordenCorrecto | validación final por `fecha_compra ASC` |

### Preguntas para los estudiantes

1. ¿Por qué Mezcla Directa genera corridas de tamaño controlado?
2. ¿Por qué Fusión Natural depende del orden parcial del archivo original?
3. ¿Qué métrica representa mejor el costo de una ordenación externa?
4. ¿Qué pasaría si el archivo tuviera 10 millones de ventas?
