package presenter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import modelo.Arista;
import modelo.Argentina;
import modelo.DiseneadorRegiones;
import modelo.Grafo;
import modelo.Pais;
import modelo.Provincia;
import modelo.Region;
import vista.IVentanaPrincipal;

public class Presenter {
    private List<Region> ultimasRegiones = new ArrayList<>();
    private final IVentanaPrincipal vista;
    private final Pais pais;
    private Grafo grafo;

    public Presenter(IVentanaPrincipal vista) {
        this.vista = vista;
        this.pais = Argentina.crearPais();
        this.grafo = new Grafo();
        for (Provincia provincia : pais.getProvincias()) {
            grafo.agregarProvincia(provincia);
        }
        // La vista ya debe haber creado los combos cuando se construye el Presenter.
        vista.mostrarProvincias(pais.getProvincias());
    }

    /** Se conserva por compatibilidad, pero ya no permite agregar provincias arbitrarias. */
    public void agregarProvincia(String nombreProvincia) {
        vista.mostrarError("Las provincias ya están precargadas. Selecciónelas desde los menús desplegables.");
    }

    public void agregarSimilaridad(Provincia p1, Provincia p2, double peso) {
        if (p1 == null || p2 == null) {
            vista.mostrarError("Debe seleccionar dos provincias.");
            return;
        }
        if (!pais.contieneProvincia(p1) || !pais.contieneProvincia(p2)) {
            vista.mostrarError("Solo puede seleccionar provincias argentinas precargadas.");
            return;
        }
        if (p1.equals(p2)) {
            vista.mostrarError("Debe seleccionar dos provincias diferentes.");
            return;
        }
        if (!pais.sonLimitrofes(p1, p2)) {
            vista.mostrarError("" + p1 + " y " + p2 + " no tienen una frontera válida.");
            return;
        }
        if (!Double.isFinite(peso) || peso <= 0) {
            vista.mostrarError("La similaridad debe ser un número finito positivo.");
            return;
        }
        if (grafo.existeArista(p1, p2)) {
            vista.mostrarError("La similaridad entre esas provincias ya fue cargada.");
            return;
        }

        try {
            pais.asignarSimilaridad(p1, p2, peso);
            grafo.agregarArista(p1, p2, peso);
        } catch (IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
            return;
        }
        vista.mostrarAristas(grafo.getAristas());
        vista.limpiarCampos();
    }

    public void calcularRegiones(int k) {
        int totalProvincias = grafo.getProvincias().size();
        if (totalProvincias == 0) {
            vista.mostrarError("No hay provincias cargadas.");
            return;
        }
        if (k <= 0 || k > totalProvincias) {
            vista.mostrarError("La cantidad de regiones (k) debe estar entre 1 y " + totalProvincias + ".");
            return;
        }
        if (!pais.estaCompleto()) {
            vista.mostrarError("Faltan asignar similaridades a "
                    + pais.fronterasSinSimilaridad().size()
                    + " fronteras. Debe completar las fronteras precargadas antes de diseñar regiones.");
            return;
        }

        try {
            List<Region> regiones = pais.generarRegiones(k);
            ultimasRegiones = new ArrayList<>(regiones);
            vista.mostrarRegiones(regiones);
        } catch (Exception e) {
            vista.mostrarError("No se pueden generar las regiones todavía. Verifique que las conexiones cargadas formen un grafo conexo. Detalle: " + e.getMessage());
        }
    }

    /**
     * Importa similaridades desde CSV. Formato esperado por fila:
     * Provincia1,Provincia2,Similaridad
     * También admite punto y coma como separador y una primera fila de encabezado.
     * Las provincias y las fronteras se validan contra el catálogo fijo de Argentina.
     * Se permiten archivos parciales para poder completar luego las fronteras faltantes desde la GUI.
     */
    public boolean cargarGrafoDesdeArchivo(File archivo) {
        if (archivo == null || !archivo.isFile()) {
            vista.mostrarError("Seleccione un archivo CSV válido.");
            return false;
        }

        Map<String, Provincia> provinciasPorNombre = new HashMap<>();
        for (Provincia provincia : pais.getProvincias()) {
            provinciasPorNombre.put(normalizar(provincia.getNombre()), provincia);
        }

        List<SimilaridadCSV> filas = new ArrayList<>();
        Set<modelo.Frontera> fronterasLeidas = new HashSet<>();
        List<String> errores = new ArrayList<>();
        int numeroLinea = 0;
        boolean primeraFilaUtil = true;

        try (BufferedReader lector = Files.newBufferedReader(archivo.toPath(), StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) continue;

                String[] columnas = separarCSV(linea);
                if (primeraFilaUtil) {
                    primeraFilaUtil = false;
                    if (columnas.length >= 3
                            && (normalizar(columnas[0]).contains("provincia")
                            || normalizar(columnas[2]).contains("similaridad")
                            || normalizar(columnas[2]).contains("peso"))) {
                        continue; // encabezado opcional
                    }
                }
                if (columnas.length != 3) {
                    errores.add("Línea " + numeroLinea + ": se esperaban 3 columnas (Provincia1, Provincia2, Similaridad).");
                    continue;
                }

                Provincia p1 = provinciasPorNombre.get(normalizar(columnas[0]));
                Provincia p2 = provinciasPorNombre.get(normalizar(columnas[1]));
                if (p1 == null || p2 == null) {
                    errores.add("Línea " + numeroLinea + ": provincia desconocida: "
                            + (p1 == null ? columnas[0].trim() : columnas[1].trim()) + ".");
                    continue;
                }
                if (p1.equals(p2)) {
                    errores.add("Línea " + numeroLinea + ": una provincia no puede conectarse consigo misma.");
                    continue;
                }
                if (!pais.sonLimitrofes(p1, p2)) {
                    errores.add("Línea " + numeroLinea + ": " + p1 + " y " + p2 + " no son una frontera válida del catálogo.");
                    continue;
                }

                double peso;
                try {
                    peso = Double.parseDouble(columnas[2].trim().replace(',', '.'));
                } catch (NumberFormatException ex) {
                    errores.add("Línea " + numeroLinea + ": similaridad no numérica: " + columnas[2].trim() + ".");
                    continue;
                }
                if (!Double.isFinite(peso) || peso <= 0) {
                    errores.add("Línea " + numeroLinea + ": la similaridad debe ser un número positivo y finito.");
                    continue;
                }

                modelo.Frontera frontera = new modelo.Frontera(p1, p2);
                if (!fronterasLeidas.add(frontera)) {
                    errores.add("Línea " + numeroLinea + ": la frontera " + p1 + " - " + p2 + " está repetida.");
                    continue;
                }
                filas.add(new SimilaridadCSV(p1, p2, peso));
            }
        } catch (IOException ex) {
            vista.mostrarError("No se pudo leer el archivo: " + ex.getMessage());
            return false;
        }

        if (!errores.isEmpty()) {
            StringBuilder detalle = new StringBuilder("No se importó el archivo porque se encontraron errores:\n");
            int limite = Math.min(errores.size(), 10);
            for (int i = 0; i < limite; i++) detalle.append("• ").append(errores.get(i)).append('\n');
            if (errores.size() > limite) detalle.append("• Y ").append(errores.size() - limite).append(" error(es) más.");
            vista.mostrarError(detalle.toString());
            return false;
        }
        if (filas.isEmpty()) {
            vista.mostrarError("El archivo no contiene filas de similaridades para importar.");
            return false;
        }

        // La importación reemplaza la carga anterior solo después de validar todo el archivo.
        pais.limpiarSimilaridades();
        grafo = new Grafo();
        for (Provincia provincia : pais.getProvincias()) grafo.agregarProvincia(provincia);
        for (SimilaridadCSV fila : filas) {
            pais.asignarSimilaridad(fila.p1, fila.p2, fila.peso);
            grafo.agregarArista(fila.p1, fila.p2, fila.peso);
        }
        vista.mostrarAristas(grafo.getAristas());

        int faltantes = pais.fronterasSinSimilaridad().size();
        String mensaje = "CSV importado correctamente: " + filas.size() + "/" + pais.getFronteras().size()
                + " fronteras cargadas.";
        if (faltantes > 0) {
            mensaje += "\nFaltan " + faltantes + " fronteras. Podés completar esas similaridades desde los menús de la interfaz.";
        } else {
            mensaje += "\nSe cargaron todas las fronteras del catálogo.";
        }
        vista.mostrarMensaje(mensaje);
        return true;
    }

    private static String[] separarCSV(String linea) {
        // Los nombres de provincias no contienen comas ni punto y coma; se admiten ambos separadores.
        String separador = linea.contains(";") ? ";" : ",";
        String[] partes = linea.split(java.util.regex.Pattern.quote(separador), -1);
        for (int i = 0; i < partes.length; i++) {
            partes[i] = partes[i].trim();
            if (partes[i].length() >= 2 && partes[i].startsWith("\"") && partes[i].endsWith("\"")) {
                partes[i] = partes[i].substring(1, partes[i].length() - 1).trim();
            }
        }
        return partes;
    }

    private static String normalizar(String texto) {
        String sinAcentos = Normalizer.normalize(texto == null ? "" : texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinAcentos.toLowerCase(Locale.ROOT);
    }

    private static final class SimilaridadCSV {
        private final Provincia p1;
        private final Provincia p2;
        private final double peso;
        private SimilaridadCSV(Provincia p1, Provincia p2, double peso) {
            this.p1 = p1;
            this.p2 = p2;
            this.peso = peso;
        }
    }

    public Grafo getGrafo() { return grafo; }

    public List<Region> getUltimasRegiones() { return new ArrayList<>(ultimasRegiones); }
    public Pais getPais() { return pais; }
}
