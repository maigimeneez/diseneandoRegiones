package vista;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.function.Supplier;
import modelo.Region;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import modelo.Arista;
import modelo.Grafo;
import modelo.Provincia;

/**
 * Muestra un mapa base de Argentina y dibuja encima los vértices y aristas del grafo.
 * El mapa de fondo se carga desde recursos/mapa_argentina.png; funciona sin Internet.
 */
public class VentanaMapa extends JFrame {
    private static final long serialVersionUID = 1L;
    private final Grafo grafo;
    private final PanelMapa panelMapa;
    private final Supplier<List<Region>> proveedorRegiones;

    public VentanaMapa(JFrame padre, Grafo grafo, Supplier<List<Region>> proveedorRegiones) {
        super("Mapa de conexiones - Diseñando Regiones");
        this.grafo = grafo;
        this.proveedorRegiones = proveedorRegiones;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 800);
        setMinimumSize(new Dimension(850, 700));
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout(8, 8));

        JLabel titulo = new JLabel("Mapa de Argentina: provincias y similaridades", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(12, 10, 4, 10));
        add(titulo, BorderLayout.NORTH);

        panelMapa = new PanelMapa();
        add(panelMapa, BorderLayout.CENTER);

        JButton actualizar = new JButton("Actualizar mapa");
        actualizar.addActionListener(e -> { panelMapa.repaint(); panelMapa.revalidate(); });
        JLabel ayuda = new JLabel("Mapa provincial local. Puntos y líneas representan el grafo y sus similaridades.");
        ayuda.setFont(new Font("SansSerif", Font.PLAIN, 11));
        JPanel pie = new JPanel(new BorderLayout(8, 4));
        pie.setBorder(BorderFactory.createEmptyBorder(4, 10, 10, 10));
        pie.add(ayuda, BorderLayout.CENTER);
        pie.add(actualizar, BorderLayout.EAST);
        add(pie, BorderLayout.SOUTH);
    }

    private class PanelMapa extends JPanel {
        private static final long serialVersionUID = 1L;
        private final Map<String, double[]> posiciones = new HashMap<>();
        private BufferedImage mapaArgentina;

        PanelMapa() {
            setBackground(new Color(247, 250, 253));
            cargarMapaDeFondo();
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            // Coordenadas normalizadas: esquema aproximado de la distribución argentina.


            poner("Jujuy", 0.386, 0.114);
            poner("Salta", 0.476, 0.120);
            poner("Formosa", 0.614, 0.190);
            poner("Chaco", 0.564, 0.225);
            poner("Misiones", 0.766, 0.229);
            poner("Corrientes", 0.650, 0.270);
            poner("Santiago del Estero", 0.472, 0.246);
            poner("Tucumán", 0.412, 0.215);
            poner("Catamarca", 0.350, 0.223);
            poner("La Rioja", 0.350, 0.282);
            poner("Santa Fe", 0.549, 0.302);
            poner("Entre Ríos", 0.588, 0.363);
            poner("Córdoba", 0.455, 0.331);
            poner("San Juan", 0.303, 0.323);
            poner("San Luis", 0.384, 0.372);
            poner("Mendoza", 0.320, 0.419);
            poner("Buenos Aires", 0.552, 0.470);
            poner("La Pampa", 0.401, 0.489);
            poner("Neuquén", 0.288, 0.511);
            poner("Río Negro", 0.356, 0.566);
            poner("Chubut", 0.320, 0.657);
            poner("Santa Cruz", 0.305, 0.757);
            poner("Tierra del Fuego", 0.355, 0.905);
			

        }


			private void poner(String nombre, double x, double y) {
			    posiciones.put(nombre, new double[]{x, y});
			}


        private void cargarMapaDeFondo() {
            try {
                BufferedImage original = null;
                // Primero intenta cargar el recurso incluido en el proyecto compilado.
                try (InputStream recurso = VentanaMapa.class.getResourceAsStream("/recursos/mapa_argentina.png")) {
                    if (recurso != null) original = ImageIO.read(recurso);
                }
                // Alternativas para proyectos Java ejecutados desde la carpeta del proyecto.
                if (original == null) {
                    File archivo = new File("recursos/mapa_argentina.png");
                    if (!archivo.isFile()) archivo = new File("DisenandoRegiones_actualizado/recursos/mapa_argentina.png");
                    if (archivo.isFile()) original = ImageIO.read(archivo);
                }
                if (original == null) {
                    mapaArgentina = null;
                    return;
                }
                // Conserva los trazos oscuros del mapa y vuelve transparente el fondo blanco.
                BufferedImage transparente = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
                for (int y = 0; y < original.getHeight(); y++) {
                    for (int x = 0; x < original.getWidth(); x++) {
                        Color c = new Color(original.getRGB(x, y));
                        int max = Math.max(c.getRed(), Math.max(c.getGreen(), c.getBlue()));
                        int min = Math.min(c.getRed(), Math.min(c.getGreen(), c.getBlue()));
                        int luminancia = (c.getRed() * 299 + c.getGreen() * 587 + c.getBlue() * 114) / 1000;
                        if (luminancia < 155 || (max - min < 25 && luminancia < 190)) {
                            int alpha = luminancia < 90 ? 235 : 175;
                            transparente.setRGB(x, y, (alpha << 24) | (c.getRed() << 16) | (c.getGreen() << 8) | c.getBlue());
                        } else {
                            transparente.setRGB(x, y, 0x00000000);
                        }
                    }
                }
                mapaArgentina = transparente;
            } catch (Exception ex) {
                mapaArgentina = null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            // Mapa real como fondo: se escala conservando su proporción vertical.
            // Los puntos y las aristas siguen dibujándose dinámicamente encima.
            int mapaH = Math.max(1, h - 12);
            int mapaW = mapaArgentina == null ? (int)(mapaH * 0.518) : (int)(mapaH * ((double) mapaArgentina.getWidth() / mapaArgentina.getHeight()));
            int mapaX = (w - mapaW) / 2;
            int mapaY = 6;
            if (mapaArgentina != null) {
                g2.drawImage(mapaArgentina, mapaX, mapaY, mapaW, mapaH, this);
            } else {
                g2.setColor(new Color(180, 70, 70));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.drawString("No se pudo cargar recursos/mapa_argentina.png.", 12, 20);
            }

            // Aristas y sus pesos.
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            for (Arista a : grafo.getAristas()) {
                double[] p1 = posiciones.get(a.getProvincia1().getNombre());
                double[] p2 = posiciones.get(a.getProvincia2().getNombre());
                if (p1 == null || p2 == null) continue;
                int x1 = mapaX + (int)(p1[0] * mapaW), y1 = mapaY + (int)(p1[1] * mapaH);
                int x2 = mapaX + (int)(p2[0] * mapaW), y2 = mapaY + (int)(p2[1] * mapaH);
                g2.setColor(new Color(45, 112, 174, 190));
                g2.setStroke(new BasicStroke(1.8f));
                g2.draw(new Line2D.Double(x1, y1, x2, y2));
//                String peso = String.valueOf(a.getPeso());
//                int mx = (x1 + x2) / 2, my = (y1 + y2) / 2;
//                g2.setColor(Color.WHITE);
//                g2.fillRoundRect(mx - 13, my - 9, 28, 15, 6, 6);
//                g2.setColor(new Color(24, 65, 103));
//                g2.drawString(peso, mx - 10, my + 2);
            }

            // Si ya se calcularon regiones, cada provincia toma el color de su región.
            List<Region> regiones = proveedorRegiones.get();
            Map<String, Color> coloresPorProvincia = new HashMap<>();
            Color[] paleta = {new Color(244, 177, 131), new Color(157, 195, 230),
                new Color(169, 208, 142), new Color(255, 217, 102),
                new Color(197, 180, 224), new Color(128, 204, 198),
                new Color(242, 153, 173), new Color(190, 190, 190),
                new Color(190, 220, 150), new Color(250, 200, 130)};
            for (int i = 0; i < regiones.size(); i++) {
                Color color = paleta[i % paleta.length];
                for (Provincia provincia : regiones.get(i).getProvincias()) {
                    coloresPorProvincia.put(provincia.getNombre(), color);
                }
            }

            // Vértices de provincias. El color indica la región calculada, si existe.
            for (Map.Entry<String, double[]> entrada : posiciones.entrySet()) {
                String nombre = entrada.getKey();
                double[] p = entrada.getValue();
                int x = mapaX + (int)(p[0] * mapaW), y = mapaY + (int)(p[1] * mapaH);
                g2.setColor(coloresPorProvincia.getOrDefault(nombre, new Color(24, 48, 82)));
                g2.fillOval(x - 7, y - 7, 14, 14);
                g2.setColor(new Color(24, 48, 82));
                g2.drawOval(x - 7, y - 7, 14, 14);
//                g2.setColor(new Color(30, 43, 58));
//                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
//                int offsetX = x > w * .72 ? -g2.getFontMetrics().stringWidth(nombre) - 9 : 8;
//                int offsetY = (nombre.equals("Tierra del Fuego") || nombre.equals("Santa Cruz")) ? -8 : -7;
//                g2.drawString(nombre, x + offsetX, y + offsetY);
            }

            if (regiones.isEmpty()) {
                g2.setColor(new Color(65, 75, 85));
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2.drawString("Calculá las regiones para verlas diferenciadas por color", 16, h - 16);
            } else {
                g2.setColor(new Color(65, 75, 85));
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2.drawString("Regiones calculadas: " + regiones.size() + " (cada color representa una región)", 16, h - 16);
            }
            g2.dispose();
        }
    }
}
