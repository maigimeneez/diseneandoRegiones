package vista;

import java.util.List;
import modelo.Arista;
import modelo.Provincia;
import modelo.Region;

public interface IVentanaPrincipal {
    void mostrarProvincias(List<Provincia> provincias);
    void mostrarRegiones(List<Region> regiones);
    void mostrarError(String mensaje);
    void mostrarMensaje(String mensaje);
    void limpiarCampos();
    void mostrarAristas(List<Arista> aristas);
}
