package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import modelo.Frontera;
import modelo.Provincia;

public class FronteraTest {

    @Test
    public void testCrearFrontera() {
        Provincia buenosAires = new Provincia("Buenos Aires");
        Provincia cordoba = new Provincia("Córdoba");

        Frontera frontera = new Frontera(buenosAires, cordoba);

        assertNotNull(frontera);
    }

    @Test
    public void testGetProvincia1() {
        Provincia buenosAires = new Provincia("Buenos Aires");
        Provincia cordoba = new Provincia("Córdoba");

        Frontera frontera = new Frontera(buenosAires, cordoba);

        assertEquals(buenosAires, frontera.getProvincia1());
    }

    @Test
    public void testGetProvincia2() {
        Provincia buenosAires = new Provincia("Buenos Aires");
        Provincia cordoba = new Provincia("Córdoba");

        Frontera frontera = new Frontera(buenosAires, cordoba);

        assertEquals(cordoba, frontera.getProvincia2());
    }

    @Test
    public void testConstructorConProvincia1Null() {
        Provincia cordoba = new Provincia("Córdoba");

        assertThrows(IllegalArgumentException.class, () -> {
            new Frontera(null, cordoba);
        });
    }

    @Test
    public void testConstructorConProvincia2Null() {
        Provincia buenosAires = new Provincia("Buenos Aires");

        assertThrows(IllegalArgumentException.class, () -> {
            new Frontera(buenosAires, null);
        });
    }

    @Test
    public void testConstructorConAmbasProvinciasNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Frontera(null, null);
        });
    }

    @Test
    public void testProvinciaNoPuedeLimitarConsigoMisma() {
        Provincia buenosAires = new Provincia("Buenos Aires");

        assertThrows(IllegalArgumentException.class, () -> {
            new Frontera(buenosAires, buenosAires);
        });
    }

    @Test
    public void testDosFronterasIguales() {
        Provincia buenosAires = new Provincia("Buenos Aires");
        Provincia cordoba = new Provincia("Córdoba");

        Frontera frontera1 = new Frontera(buenosAires, cordoba);
        Frontera frontera2 = new Frontera(buenosAires, cordoba);

        assertEquals(frontera1, frontera2);
    }

    @Test
    public void testFronterasIgualesConOrdenInvertido() {
        Provincia buenosAires = new Provincia("Buenos Aires");
        Provincia cordoba = new Provincia("Córdoba");

        Frontera frontera1 = new Frontera(buenosAires, cordoba);
        Frontera frontera2 = new Frontera(cordoba, buenosAires);

        assertEquals(frontera1, frontera2);
    }

    @Test
    public void testFronterasDistintas() {
        Provincia buenosAires = new Provincia("Buenos Aires");
        Provincia cordoba = new Provincia("Córdoba");
        Provincia mendoza = new Provincia("Mendoza");

        Frontera frontera1 = new Frontera(buenosAires, cordoba);
        Frontera frontera2 = new Frontera(buenosAires, mendoza);

        assertNotEquals(frontera1, frontera2);
    }

    @Test
    public void testHashCodeIgualParaFronterasIguales() {
        Provincia buenosAires = new Provincia("Buenos Aires");
        Provincia cordoba = new Provincia("Córdoba");

        Frontera frontera1 = new Frontera(buenosAires, cordoba);
        Frontera frontera2 = new Frontera(cordoba, buenosAires);

        assertEquals(frontera1.hashCode(), frontera2.hashCode());
    }

    @Test
    public void testEqualsConNull() {
        Provincia buenosAires = new Provincia("Buenos Aires");
        Provincia cordoba = new Provincia("Córdoba");

        Frontera frontera = new Frontera(buenosAires, cordoba);

        assertNotEquals(frontera, null);
    }

    @Test
    public void testEqualsConOtroTipoDeObjeto() {
        Provincia buenosAires = new Provincia("Buenos Aires");
        Provincia cordoba = new Provincia("Córdoba");

        Frontera frontera = new Frontera(buenosAires, cordoba);

        assertNotEquals(frontera, "Buenos Aires - Córdoba");
    }

    @Test
    public void testEqualsConMismoObjeto() {
        Provincia buenosAires = new Provincia("Buenos Aires");
        Provincia cordoba = new Provincia("Córdoba");

        Frontera frontera = new Frontera(buenosAires, cordoba);

        assertEquals(frontera, frontera);
    }

    @Test
    public void testToString() {
        Provincia buenosAires = new Provincia("Buenos Aires");
        Provincia cordoba = new Provincia("Córdoba");

        Frontera frontera = new Frontera(buenosAires, cordoba);

        assertEquals("Buenos Aires - Córdoba", frontera.toString());
    }
}