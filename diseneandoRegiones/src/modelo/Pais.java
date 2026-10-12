package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;

public class Pais {
    private final List<Provincia> provincias;
    private final List<Frontera> fronteras;
    private final Set<Frontera> conjuntoFronteras;
    private final Map<Frontera, Double> similaridades;

    public Pais(List<Provincia> provincias, List<Frontera> fronteras) {
        if (provincias == null || fronteras == null) {
            throw new IllegalArgumentException("Las provincias y las fronteras no pueden ser null.");
        }
        Set<Provincia> conocidas = new LinkedHashSet<>();
        for (Provincia p : provincias) {
            if (p == null) {
            	throw new IllegalArgumentException("Las provincias no pueden ser null.");
            }
            if (!conocidas.add(p)) {
            	throw new IllegalArgumentException("Provincia repetida: " + p);
            }
        }
        Set<Frontera> unicas = new LinkedHashSet<>();
        for (Frontera f : fronteras) {
            if (f == null) {
            	throw new IllegalArgumentException("Las fronteras no pueden ser null.");
            }
            if (!conocidas.contains(f.getProvincia1()) || !conocidas.contains(f.getProvincia2())) {
                throw new IllegalArgumentException("La frontera " + f + " usa una provincia que no pertenece al país.");
            }
            if (!unicas.add(f)) throw new IllegalArgumentException("Frontera repetida: " + f);
        }
        this.provincias = Collections.unmodifiableList(new ArrayList<>(conocidas));
        this.fronteras = Collections.unmodifiableList(new ArrayList<>(unicas));
        this.conjuntoFronteras = Collections.unmodifiableSet(new LinkedHashSet<>(unicas));
        this.similaridades = new HashMap<>();
    }

    public List<Provincia> getProvincias() { return provincias; }
    public List<Frontera> getFronteras() { return fronteras; }

    public boolean contieneProvincia(Provincia p) {
        return p != null && provincias.contains(p);
    }

    public boolean sonLimitrofes(Provincia p1, Provincia p2) {
        if (p1 == null || p2 == null || p1.equals(p2)) {
        	return false;
        }
        return conjuntoFronteras.contains(new Frontera(p1, p2));
    }

    public void asignarSimilaridad(Provincia p1, Provincia p2, double peso) {
        if (!Double.isFinite(peso) || peso <= 0) {
            throw new IllegalArgumentException("La similaridad debe ser un número finito positivo.");
        }
        similaridades.put(fronteraEntre(p1, p2), peso);
    }

    public OptionalDouble getSimilaridad(Provincia p1, Provincia p2) {
        Double peso = similaridades.get(fronteraEntre(p1, p2));
        return peso == null ? OptionalDouble.empty() : OptionalDouble.of(peso);
    }

    public List<Frontera> fronterasSinSimilaridad() {
        List<Frontera> faltantes = new ArrayList<>();
        for (Frontera f : fronteras) if (!similaridades.containsKey(f)) faltantes.add(f);
        return faltantes;
    }

    public boolean estaCompleto() { return similaridades.size() == fronteras.size(); }


    public void limpiarSimilaridades() {
        similaridades.clear();
    }

    public Grafo construirGrafo() {
        if (!estaCompleto()) {
            throw new IllegalStateException("Faltan asignar similaridades en " + fronterasSinSimilaridad().size() + " fronteras.");
        }
        Grafo grafo = new Grafo();
        for (Provincia p : provincias) {
        	grafo.agregarProvincia(p);
        }
        for (Frontera f : fronteras) {
        	grafo.agregarArista(f.getProvincia1(), f.getProvincia2(), similaridades.get(f));
        }
        return grafo;
    }

    public List<Region> generarRegiones(int k) {
        return DiseneadorRegiones.generarRegiones(construirGrafo(), k);
    }

    private Frontera fronteraEntre(Provincia p1, Provincia p2) {
        if (p1 == null || p2 == null) {
        	throw new IllegalArgumentException("Debe indicar dos provincias.");
        }
        if (p1.equals(p2)) {
        	throw new IllegalArgumentException("Una provincia no limita consigo misma.");
        }
        if (!contieneProvincia(p1) || !contieneProvincia(p2)) {
            throw new IllegalArgumentException("Solo se pueden usar las provincias precargadas de Argentina.");
        }
        Frontera frontera = new Frontera(p1, p2);
        if (!conjuntoFronteras.contains(frontera)) {
            throw new IllegalArgumentException(p1 + " y " + p2 + " no son limítrofes.");
        }
        return frontera;
    }
}
