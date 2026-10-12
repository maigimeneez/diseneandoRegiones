package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Argentina {
    private static final List<Provincia> PROVINCIAS = new ArrayList<>();
    private static final List<Frontera> FRONTERAS = new ArrayList<>();

    private static final Provincia BUENOS_AIRES = provincia("Buenos Aires");
    private static final Provincia CATAMARCA = provincia("Catamarca");
    private static final Provincia CHACO = provincia("Chaco");
    private static final Provincia CHUBUT = provincia("Chubut");
    private static final Provincia CORDOBA = provincia("Córdoba");
    private static final Provincia CORRIENTES = provincia("Corrientes");
    private static final Provincia ENTRE_RIOS = provincia("Entre Ríos");
    private static final Provincia FORMOSA = provincia("Formosa");
    private static final Provincia JUJUY = provincia("Jujuy");
    private static final Provincia LA_PAMPA = provincia("La Pampa");
    private static final Provincia LA_RIOJA = provincia("La Rioja");
    private static final Provincia MENDOZA = provincia("Mendoza");
    private static final Provincia MISIONES = provincia("Misiones");
    private static final Provincia NEUQUEN = provincia("Neuquén");
    private static final Provincia RIO_NEGRO = provincia("Río Negro");
    private static final Provincia SALTA = provincia("Salta");
    private static final Provincia SAN_JUAN = provincia("San Juan");
    private static final Provincia SAN_LUIS = provincia("San Luis");
    private static final Provincia SANTA_CRUZ = provincia("Santa Cruz");
    private static final Provincia SANTA_FE = provincia("Santa Fe");
    private static final Provincia SANTIAGO_ESTERO = provincia("Santiago del Estero");
    private static final Provincia TIERRA_FUEGO = provincia("Tierra del Fuego");
    private static final Provincia TUCUMAN = provincia("Tucumán");

    static {
        frontera(BUENOS_AIRES, CORDOBA); frontera(BUENOS_AIRES, LA_PAMPA);
        frontera(BUENOS_AIRES, SANTA_FE); frontera(BUENOS_AIRES, ENTRE_RIOS);
        frontera(BUENOS_AIRES, RIO_NEGRO);
        
        frontera(CATAMARCA, SALTA); frontera(CATAMARCA, TUCUMAN);
        frontera(CATAMARCA, SANTIAGO_ESTERO); frontera(CATAMARCA, LA_RIOJA);
        frontera(CATAMARCA, CORDOBA);
    
        frontera(CHACO, FORMOSA); frontera(CHACO, SALTA);
        frontera(CHACO, SANTIAGO_ESTERO); frontera(CHACO, SANTA_FE);
        frontera(CHACO, CORRIENTES);
        
        frontera(CHUBUT, RIO_NEGRO); frontera(CHUBUT, SANTA_CRUZ);

        frontera(CORDOBA, SANTIAGO_ESTERO); frontera(CORDOBA, SANTA_FE);
        frontera(CORDOBA, LA_PAMPA); frontera(CORDOBA, SAN_LUIS);
        frontera(CORDOBA, LA_RIOJA);

        frontera(CORRIENTES, MISIONES); frontera(CORRIENTES, ENTRE_RIOS);
        frontera(CORRIENTES, SANTA_FE);

        frontera(ENTRE_RIOS, SANTA_FE);
  
        frontera(FORMOSA, SALTA); frontera(JUJUY, SALTA);

        frontera(LA_PAMPA, MENDOZA); frontera(LA_PAMPA, SAN_LUIS);
        frontera(LA_PAMPA, NEUQUEN); frontera(LA_PAMPA, RIO_NEGRO);

        frontera(LA_RIOJA, SAN_JUAN); frontera(LA_RIOJA, SAN_LUIS);

        frontera(MENDOZA, SAN_JUAN); frontera(MENDOZA, SAN_LUIS);
        frontera(MENDOZA, NEUQUEN);

        frontera(NEUQUEN, RIO_NEGRO);

        frontera(SALTA, SANTIAGO_ESTERO); frontera(SALTA, TUCUMAN);

        frontera(SAN_JUAN, SAN_LUIS);

        frontera(SANTA_FE, SANTIAGO_ESTERO);
        frontera(SANTIAGO_ESTERO, TUCUMAN);
      
        frontera(SANTA_CRUZ, TIERRA_FUEGO);
    }

    private Argentina() { }

    private static Provincia provincia(String nombre) {
        Provincia p = new Provincia(nombre);
        PROVINCIAS.add(p);
        return p;
    }

    private static void frontera(Provincia p1, Provincia p2) {
        FRONTERAS.add(new Frontera(p1, p2));
    }

    public static Pais crearPais() {
        return new Pais(PROVINCIAS, FRONTERAS);
    }

    public static List<Provincia> getProvincias() {
        return Collections.unmodifiableList(PROVINCIAS);
    }

    public static List<Frontera> getFronteras() {
        return Collections.unmodifiableList(FRONTERAS);
    }
}
