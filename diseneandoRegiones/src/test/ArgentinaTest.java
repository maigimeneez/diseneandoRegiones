package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import modelo.Argentina;
import modelo.Frontera;
import modelo.Pais;
import modelo.Provincia;

public class ArgentinaTest {

    @Test
    public void testCantidadDeProvincias() {
        List<Provincia> provincias = Argentina.getProvincias();

        assertEquals(23, provincias.size());
    }


    @Test
    public void testProvinciasNoEsNula() {
        assertNotNull(Argentina.getProvincias());
    }

    @Test
    public void testFronterasNoEsNula() {
        assertNotNull(Argentina.getFronteras());
    }

    @Test
    public void testExistenProvinciasConocidas() {
        List<Provincia> provincias = Argentina.getProvincias();

        assertTrue(provincias.contains(new Provincia("Buenos Aires")));
        assertTrue(provincias.contains(new Provincia("Córdoba")));
        assertTrue(provincias.contains(new Provincia("Mendoza")));
        assertTrue(provincias.contains(new Provincia("Tucumán")));
        assertTrue(provincias.contains(new Provincia("Tierra del Fuego")));
    }

    @Test
    public void testNoHayProvinciasRepetidas() {
        List<Provincia> provincias = Argentina.getProvincias();

        assertEquals(provincias.size(),
                provincias.stream().distinct().count());
    }

    @Test
    public void testListasDeProvinciasNoModificables() {
        List<Provincia> provincias = Argentina.getProvincias();

        assertThrows(UnsupportedOperationException.class, () -> {
            provincias.add(new Provincia("Provincia inventada"));
        });
    }

    @Test
    public void testListasDeFronterasNoModificables() {
        List<Frontera> fronteras = Argentina.getFronteras();

        assertThrows(UnsupportedOperationException.class, () -> {
            fronteras.clear();
        });
    }

    @Test
    public void testCrearPaisNoEsNulo() {
        Pais pais = Argentina.crearPais();

        assertNotNull(pais);
    }

    @Test
    public void testCrearPaisDevuelveInstanciasDistintas() {
        Pais pais1 = Argentina.crearPais();
        Pais pais2 = Argentina.crearPais();

        assertNotSame(pais1, pais2);
    }

    @Test
    public void testConexionEspecialTierraDelFuego() {
        boolean existeConexion = Argentina.getFronteras().stream()
                .anyMatch(f ->
                        (f.getProvincia1().equals(new Provincia("Santa Cruz"))
                        && f.getProvincia2().equals(new Provincia("Tierra del Fuego")))
                        ||
                        (f.getProvincia2().equals(new Provincia("Santa Cruz"))
                        && f.getProvincia1().equals(new Provincia("Tierra del Fuego")))
                );

        assertTrue(existeConexion);
    }
}