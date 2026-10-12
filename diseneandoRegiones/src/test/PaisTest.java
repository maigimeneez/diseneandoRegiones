package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import modelo.Frontera;
import modelo.Grafo;
import modelo.Pais;
import modelo.Provincia;

public class PaisTest {

    private Provincia buenosAires;
    private Provincia cordoba;
    private Provincia santaFe;
    private Pais pais;

    @BeforeEach
    public void setUp() {
        buenosAires = new Provincia("Buenos Aires");
        cordoba = new Provincia("Córdoba");
        santaFe = new Provincia("Santa Fe");

        List<Provincia> provincias =
                Arrays.asList(buenosAires, cordoba, santaFe);

        List<Frontera> fronteras = Arrays.asList(
                new Frontera(buenosAires, cordoba),
                new Frontera(buenosAires, santaFe)
        );

        pais = new Pais(provincias, fronteras);
    }

    // CONSTRUCTOR

    @Test
    public void testCrearPais() {
        assertNotNull(pais);
    }

    @Test
    public void testConstructorConProvinciasNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Pais(null, Collections.emptyList());
        });
    }

    @Test
    public void testConstructorConFronterasNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Pais(Collections.emptyList(), null);
        });
    }

    @Test
    public void testConstructorConProvinciaNull() {
        List<Provincia> provincias =
                Arrays.asList(buenosAires, null);

        assertThrows(IllegalArgumentException.class, () -> {
            new Pais(provincias, Collections.emptyList());
        });
    }

    @Test
    public void testConstructorConProvinciaRepetida() {
        List<Provincia> provincias =
                Arrays.asList(buenosAires, new Provincia("Buenos Aires"));

        assertThrows(IllegalArgumentException.class, () -> {
            new Pais(provincias, Collections.emptyList());
        });
    }

    @Test
    public void testConstructorConFronteraNull() {
        List<Frontera> fronteras =
                Collections.singletonList(null);

        assertThrows(IllegalArgumentException.class, () -> {
            new Pais(Arrays.asList(buenosAires, cordoba), fronteras);
        });
    }

    @Test
    public void testConstructorConProvinciaAjenaAlPais() {
        Provincia mendoza = new Provincia("Mendoza");

        List<Frontera> fronteras =
                Collections.singletonList(
                        new Frontera(buenosAires, mendoza));

        assertThrows(IllegalArgumentException.class, () -> {
            new Pais(Arrays.asList(buenosAires, cordoba), fronteras);
        });
    }

    @Test
    public void testConstructorConFronteraRepetida() {
        List<Frontera> fronteras = Arrays.asList(
                new Frontera(buenosAires, cordoba),
                new Frontera(cordoba, buenosAires)
        );

        assertThrows(IllegalArgumentException.class, () -> {
            new Pais(Arrays.asList(buenosAires, cordoba), fronteras);
        });
    }

    // GETTERS

    @Test
    public void testObtenerProvincias() {
        assertEquals(3, pais.getProvincias().size());
        assertTrue(pais.getProvincias().contains(buenosAires));
        assertTrue(pais.getProvincias().contains(cordoba));
        assertTrue(pais.getProvincias().contains(santaFe));
    }

    @Test
    public void testObtenerFronteras() {
        assertEquals(2, pais.getFronteras().size());
    }

    @Test
    public void testListaDeProvinciasNoModificable() {
        assertThrows(UnsupportedOperationException.class, () -> {
            pais.getProvincias().add(new Provincia("Mendoza"));
        });
    }

    @Test
    public void testListaDeFronterasNoModificable() {
        assertThrows(UnsupportedOperationException.class, () -> {
            pais.getFronteras().clear();
        });
    }

    // CONTIENE PROVINCIA

    @Test
    public void testContieneProvincia() {
        assertTrue(pais.contieneProvincia(buenosAires));
    }

    @Test
    public void testNoContieneProvinciaAjena() {
        assertFalse(pais.contieneProvincia(new Provincia("Mendoza")));
    }

    @Test
    public void testContieneProvinciaConNull() {
        assertFalse(pais.contieneProvincia(null));
    }

    // SON LIMITROFES

    @Test
    public void testSonLimitrofes() {
        assertTrue(pais.sonLimitrofes(buenosAires, cordoba));
    }

    @Test
    public void testSonLimitrofesConOrdenInvertido() {
        assertTrue(pais.sonLimitrofes(cordoba, buenosAires));
    }

    @Test
    public void testNoSonLimitrofes() {
        assertFalse(pais.sonLimitrofes(cordoba, santaFe));
    }

    @Test
    public void testProvinciaNoLimitaConsigoMisma() {
        assertFalse(pais.sonLimitrofes(buenosAires, buenosAires));
    }

    @Test
    public void testSonLimitrofesConProvinciaNull() {
        assertFalse(pais.sonLimitrofes(null, cordoba));
        assertFalse(pais.sonLimitrofes(buenosAires, null));
        assertFalse(pais.sonLimitrofes(null, null));
    }

    // SIMILARIDADES

    @Test
    public void testPaisComienzaSinSimilaridades() {
        assertTrue(pais.getSimilaridad(buenosAires, cordoba).isEmpty());
    }

    @Test
    public void testAsignarSimilaridad() {
        pais.asignarSimilaridad(buenosAires, cordoba, 0.8);

        OptionalDouble resultado =
                pais.getSimilaridad(buenosAires, cordoba);

        assertTrue(resultado.isPresent());
        assertEquals(0.8, resultado.getAsDouble(), 0.0001);
    }

    @Test
    public void testSimilaridadSeObtieneConOrdenInvertido() {
        pais.asignarSimilaridad(buenosAires, cordoba, 0.8);

        OptionalDouble resultado =
                pais.getSimilaridad(cordoba, buenosAires);

        assertTrue(resultado.isPresent());
        assertEquals(0.8, resultado.getAsDouble(), 0.0001);
    }

    @Test
    public void testSimilaridadDebeSerPositiva() {
        assertThrows(IllegalArgumentException.class, () -> {
            pais.asignarSimilaridad(buenosAires, cordoba, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            pais.asignarSimilaridad(buenosAires, cordoba, -0.5);
        });
    }

    @Test
    public void testSimilaridadNoPuedeSerNaN() {
        assertThrows(IllegalArgumentException.class, () -> {
            pais.asignarSimilaridad(
                    buenosAires, cordoba, Double.NaN);
        });
    }

    @Test
    public void testSimilaridadNoPuedeSerInfinita() {
        assertThrows(IllegalArgumentException.class, () -> {
            pais.asignarSimilaridad(
                    buenosAires, cordoba, Double.POSITIVE_INFINITY);
        });
    }

    @Test
    public void testSimilaridadConProvinciaNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            pais.asignarSimilaridad(null, cordoba, 0.5);
        });
    }

    @Test
    public void testSimilaridadConProvinciaAjena() {
        Provincia mendoza = new Provincia("Mendoza");

        assertThrows(IllegalArgumentException.class, () -> {
            pais.asignarSimilaridad(buenosAires, mendoza, 0.5);
        });
    }

    @Test
    public void testSimilaridadEntreProvinciasNoLimitrofes() {
        assertThrows(IllegalArgumentException.class, () -> {
            pais.asignarSimilaridad(cordoba, santaFe, 0.5);
        });
    }

    @Test
    public void testSimilaridadDeUnaProvinciaConsigoMisma() {
        assertThrows(IllegalArgumentException.class, () -> {
            pais.asignarSimilaridad(buenosAires, buenosAires, 0.5);
        });
    }

    @Test
    public void testAsignarSimilaridadReemplazaValorAnterior() {
        pais.asignarSimilaridad(buenosAires, cordoba, 0.4);
        pais.asignarSimilaridad(buenosAires, cordoba, 0.9);

        assertEquals(
                0.9,
                pais.getSimilaridad(buenosAires, cordoba).getAsDouble(),
                0.0001
        );
    }

    // FRONTERAS SIN SIMILARIDAD

    @Test
    public void testTodasLasFronterasComienzanSinSimilaridad() {
        assertEquals(2, pais.fronterasSinSimilaridad().size());
    }

    @Test
    public void testFronterasSinSimilaridadLuegoDeAsignarUna() {
        pais.asignarSimilaridad(buenosAires, cordoba, 0.7);

        List<Frontera> faltantes = pais.fronterasSinSimilaridad();

        assertEquals(1, faltantes.size());
        assertTrue(faltantes.contains(new Frontera(buenosAires, santaFe)));
    }

    @Test
    public void testEstaCompletoSinSimilaridades() {
        assertFalse(pais.estaCompleto());
    }

    @Test
    public void testEstaCompletoConTodasLasSimilaridades() {
        pais.asignarSimilaridad(buenosAires, cordoba, 0.7);
        pais.asignarSimilaridad(buenosAires, santaFe, 0.9);

        assertTrue(pais.estaCompleto());
        assertTrue(pais.fronterasSinSimilaridad().isEmpty());
    }

    @Test
    public void testLimpiarSimilaridades() {
        pais.asignarSimilaridad(buenosAires, cordoba, 0.7);
        pais.asignarSimilaridad(buenosAires, santaFe, 0.9);

        pais.limpiarSimilaridades();

        assertTrue(pais.getSimilaridad(buenosAires, cordoba).isEmpty());
        assertTrue(pais.getSimilaridad(buenosAires, santaFe).isEmpty());
        assertFalse(pais.estaCompleto());
        assertEquals(2, pais.fronterasSinSimilaridad().size());
    }

    // CONSTRUCCIÓN DEL GRAFO

    @Test
    public void testConstruirGrafoSinSimilaridadesLanzaExcepcion() {
        assertThrows(IllegalStateException.class, () -> {
            pais.construirGrafo();
        });
    }

    @Test
    public void testConstruirGrafoConSimilaridadesCompletas() {
        pais.asignarSimilaridad(buenosAires, cordoba, 0.7);
        pais.asignarSimilaridad(buenosAires, santaFe, 0.9);

        Grafo grafo = pais.construirGrafo();

        assertNotNull(grafo);
    }

    @Test
    public void testGenerarRegionesSinSimilaridadesLanzaExcepcion() {
        assertThrows(IllegalStateException.class, () -> {
            pais.generarRegiones(2);
        });
    }
}