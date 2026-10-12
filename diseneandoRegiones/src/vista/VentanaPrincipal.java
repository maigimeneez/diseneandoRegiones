package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import modelo.Arista;
import modelo.Provincia;
import modelo.Region;
import presenter.Presenter;

public class VentanaPrincipal extends JFrame implements IVentanaPrincipal {
    private static final long serialVersionUID = 1L;
    private Presenter presenter;
    private JComboBox<Provincia> comboProvincia1;
    private JComboBox<Provincia> comboProvincia2;
    private JTextField txtPeso;
    private JButton btnAgregarArista;
    private JButton btnCargarCSV;
    private JButton btnVerMapa;
    private JSpinner spinnerK;
    private JButton btnCalcularRegiones;
    private DefaultListModel<Provincia> listModelProvincias;
    private JList<Provincia> listProvincias;
    private DefaultTableModel modelAristas;
    private JTextArea txtResultadoRegiones;

    public VentanaPrincipal() {
        super("Diseñador de Regiones - Programación III");
        initGUI();
        presenter = new Presenter(this);
    }

    private void initGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setMinimumSize(new Dimension(980, 650));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));

        Color fondo = new Color(242, 246, 251);
        Color azulOscuro = new Color(24, 48, 82);
        Color azul = new Color(39, 105, 170);
        Color texto = new Color(38, 52, 69);
        getContentPane().setBackground(fondo);
        UIManager.put("Panel.background", fondo);
        UIManager.put("OptionPane.background", fondo);
        UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 13));
        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 12));
        UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 12));
        UIManager.put("ComboBox.font", new Font("SansSerif", Font.PLAIN, 12));
        UIManager.put("TextField.font", new Font("SansSerif", Font.PLAIN, 12));
        UIManager.put("TitledBorder.titleColor", azulOscuro);

        JPanel encabezado = new JPanel(new BorderLayout(12, 4));
        encabezado.setBackground(azulOscuro);
        encabezado.setBorder(new EmptyBorder(16, 22, 16, 22));
        JLabel titulo = new JLabel("Diseñador de Regiones");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 23));
        JLabel subtitulo = new JLabel("Programación III  ·  Cargá similaridades y generá regiones conexas");
        subtitulo.setForeground(new Color(218, 231, 246));
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JPanel textosEncabezado = new JPanel();
        textosEncabezado.setOpaque(false);
        textosEncabezado.setLayout(new BoxLayout(textosEncabezado, BoxLayout.Y_AXIS));
        textosEncabezado.add(titulo);
        textosEncabezado.add(Box.createVerticalStrut(3));
        textosEncabezado.add(subtitulo);
        encabezado.add(textosEncabezado, BorderLayout.CENTER);
        add(encabezado, BorderLayout.NORTH);

        JPanel contenido = new JPanel(new BorderLayout(12, 0));
        contenido.setBackground(fondo);
        contenido.setBorder(new EmptyBorder(14, 14, 14, 14));
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        panelIzquierdo.setPreferredSize(new Dimension(385, 0));
        panelIzquierdo.setBackground(fondo);
        panelIzquierdo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        panelIzquierdo.add(crearPanelProvincias());
        panelIzquierdo.add(Box.createVerticalStrut(10));
        panelIzquierdo.add(crearPanelAristas());
        panelIzquierdo.add(Box.createVerticalStrut(10));
        panelIzquierdo.add(crearPanelEjecucion());

        JPanel panelDerecho = new JPanel(new GridLayout(2, 1, 12, 12));
        panelDerecho.setBackground(fondo);
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        modelAristas = new DefaultTableModel(new Object[]{"Provincia 1", "Provincia 2", "Similaridad"}, 0);
        JTable tablaAristas = new JTable(modelAristas);
        tablaAristas.setRowHeight(25);
        tablaAristas.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tablaAristas.setForeground(texto);
        tablaAristas.setGridColor(new Color(226, 233, 241));
        tablaAristas.setSelectionBackground(new Color(211, 230, 249));
        tablaAristas.setSelectionForeground(azulOscuro);
        tablaAristas.setFillsViewportHeight(true);
        tablaAristas.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tablaAristas.getTableHeader().setBackground(new Color(226, 235, 246));
        tablaAristas.getTableHeader().setForeground(azulOscuro);
        tablaAristas.getTableHeader().setPreferredSize(new Dimension(0, 30));
        JScrollPane scrollTabla = new JScrollPane(tablaAristas);
        scrollTabla.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(214, 224, 235)), "Conexiones cargadas"));
        txtResultadoRegiones = new JTextArea();
        txtResultadoRegiones.setEditable(false);
        txtResultadoRegiones.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtResultadoRegiones.setForeground(texto);
        txtResultadoRegiones.setBackground(Color.WHITE);
        txtResultadoRegiones.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane scrollRegiones = new JScrollPane(txtResultadoRegiones);
        scrollRegiones.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(214, 224, 235)), "Regiones formadas"));
        panelDerecho.add(scrollTabla);
        panelDerecho.add(scrollRegiones);
        contenido.add(panelIzquierdo, BorderLayout.WEST);
        contenido.add(panelDerecho, BorderLayout.CENTER);
        add(contenido, BorderLayout.CENTER);
    }

    private JPanel crearPanelProvincias() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(214, 224, 235)), "1. Provincias precargadas (23)"),
                new EmptyBorder(6, 6, 6, 6)));
        panel.setBackground(Color.WHITE);
        listModelProvincias = new DefaultListModel<>();
        listProvincias = new JList<>(listModelProvincias);
        listProvincias.setFont(new Font("SansSerif", Font.PLAIN, 12));
        listProvincias.setSelectionBackground(new Color(211, 230, 249));
        listProvincias.setSelectionForeground(new Color(24, 48, 82));
        panel.add(new JScrollPane(listProvincias), BorderLayout.CENTER);
        JPanel pie = new JPanel(new BorderLayout(5, 5));
        JLabel ayuda = new JLabel("Las provincias no se pueden agregar manualmente.");
        btnCargarCSV = new JButton("Cargar similaridades desde CSV...");
        btnCargarCSV.setBackground(new Color(39, 105, 170));
        btnCargarCSV.setForeground(Color.WHITE);
        btnCargarCSV.setFocusPainted(false);
        btnCargarCSV.addActionListener(e -> seleccionarArchivoCSV());
        pie.add(ayuda, BorderLayout.NORTH);
        pie.add(btnCargarCSV, BorderLayout.SOUTH);
        panel.add(pie, BorderLayout.SOUTH);
        panel.setPreferredSize(new Dimension(360, 170));
        return panel;
    }

    private void seleccionarArchivoCSV() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Seleccionar archivo CSV de fronteras y similaridades");
        selector.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos CSV (*.csv)", "csv"));
        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = selector.getSelectedFile();
            boolean importado = presenter.cargarGrafoDesdeArchivo(archivo);
            if (importado) {
                if (presenter.getPais().estaCompleto() && presenter.getGrafo().esConexo()) {
                    presenter.calcularRegiones((Integer) spinnerK.getValue());
                } else {
                    txtResultadoRegiones.setText("Similaridades cargadas: "
                            + presenter.getGrafo().getAristas().size() + "/"
                            + presenter.getPais().getFronteras().size() + " fronteras.\n"
                            + "Faltan " + presenter.getPais().fronterasSinSimilaridad().size()
                            + " fronteras por cargar.");
                }
            }
        }
    }

    private JPanel crearPanelAristas() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(214, 224, 235)), "2. Cargar similaridad de una frontera"),
                new EmptyBorder(8, 8, 8, 8)));
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        comboProvincia1 = new JComboBox<>();
        comboProvincia2 = new JComboBox<>();
        txtPeso = new JTextField(8);
        btnAgregarArista = new JButton("Conectar provincias");
        btnAgregarArista.setBackground(new Color(39, 105, 170));
        btnAgregarArista.setForeground(Color.WHITE);
        btnAgregarArista.setFocusPainted(false);
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Provincia 1:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(comboProvincia1, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Provincia 2:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(comboProvincia2, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Similaridad:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(txtPeso, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; panel.add(btnAgregarArista, gbc);

        comboProvincia1.addActionListener(e -> actualizarProvinciaDisponible());
        comboProvincia2.addActionListener(e -> actualizarProvinciaDisponible());
        btnAgregarArista.addActionListener(e -> {
            try {
                Provincia p1 = (Provincia) comboProvincia1.getSelectedItem();
                Provincia p2 = (Provincia) comboProvincia2.getSelectedItem();
                double peso = Double.parseDouble(txtPeso.getText().trim().replace(',', '.'));
                int cantidadAntes = presenter.getGrafo().getAristas().size();
                presenter.agregarSimilaridad(p1, p2, peso);
                if (presenter.getGrafo().getAristas().size() > cantidadAntes) {
                    if (presenter.getPais().estaCompleto() && presenter.getGrafo().esConexo()) {
                        presenter.calcularRegiones((Integer) spinnerK.getValue());
                    } else {
                        txtResultadoRegiones.setText("Similaridades cargadas: "
                                + presenter.getGrafo().getAristas().size() + "/"
                                + presenter.getPais().getFronteras().size() + " fronteras.\n"
                                + "Complete todas las fronteras para diseñar las regiones.");
                    }
                }
            } catch (NumberFormatException ex) {
                mostrarError("Ingrese un número válido para la similaridad.");
            }
        });
        return panel;
    }

    private void actualizarProvinciaDisponible() {
        if (comboProvincia1 == null || comboProvincia2 == null) return;
        Provincia p1 = (Provincia) comboProvincia1.getSelectedItem();
        Provincia p2 = (Provincia) comboProvincia2.getSelectedItem();
        // Se evita que el segundo combo mantenga seleccionada la misma provincia.
        if (p1 != null && p1.equals(p2) && comboProvincia2.getItemCount() > 1) {
            int indice = comboProvincia2.getSelectedIndex();
            comboProvincia2.setSelectedIndex((indice + 1) % comboProvincia2.getItemCount());
        }
    }

    private JPanel crearPanelEjecucion() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(214, 224, 235)), "3. Diseñar regiones"),
                new EmptyBorder(8, 8, 8, 8)));
        panel.setBackground(Color.WHITE);
        spinnerK = new JSpinner(new SpinnerNumberModel(1, 1, 23, 1));
        btnCalcularRegiones = new JButton("Calcular regiones");
        btnVerMapa = new JButton("Ver mapa de conexiones");
        btnVerMapa.setBackground(new Color(39, 105, 170));
        btnVerMapa.setForeground(Color.WHITE);
        btnVerMapa.setFocusPainted(false);
        btnVerMapa.addActionListener(e -> new VentanaMapa(this, presenter.getGrafo(), presenter::getUltimasRegiones).setVisible(true));
        btnCalcularRegiones.setBackground(new Color(28, 135, 95));
        btnCalcularRegiones.setForeground(Color.WHITE);
        btnCalcularRegiones.setFocusPainted(false);
        panel.add(new JLabel("Cantidad de regiones (k):"));
        panel.add(spinnerK);
        panel.add(btnCalcularRegiones);
        panel.add(btnVerMapa);
        btnCalcularRegiones.addActionListener(e -> presenter.calcularRegiones((Integer) spinnerK.getValue()));
        return panel;
    }

    @Override
    public void mostrarAristas(List<Arista> aristas) {
        modelAristas.setRowCount(0);
        for (Arista a : aristas) {
            modelAristas.addRow(new Object[]{a.getProvincia1().getNombre(), a.getProvincia2().getNombre(), a.getPeso()});
        }
    }

    @Override
    public void mostrarProvincias(List<Provincia> provincias) {
        listModelProvincias.clear();
        comboProvincia1.removeAllItems();
        comboProvincia2.removeAllItems();
        for (Provincia p : provincias) {
            listModelProvincias.addElement(p);
            comboProvincia1.addItem(p);
            comboProvincia2.addItem(p);
        }
        if (comboProvincia2.getItemCount() > 1) comboProvincia2.setSelectedIndex(1);
    }

    @Override
    public void mostrarRegiones(List<Region> regiones) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== REGIONES GENERADAS (Total: ").append(regiones.size()).append(") ===\n\n");
        int nroRegion = 1;
        for (Region region : regiones) {
            sb.append("Región ").append(nroRegion++).append(":\n");
            for (Provincia p : region.getProvincias()) sb.append("   - ").append(p.getNombre()).append("\n");
            sb.append("\n");
        }
        txtResultadoRegiones.setText(sb.toString());
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Atención / Error", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Carga completada", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void limpiarCampos() { txtPeso.setText(""); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) { }
            new VentanaPrincipal().setVisible(true);
        });
    }
}
