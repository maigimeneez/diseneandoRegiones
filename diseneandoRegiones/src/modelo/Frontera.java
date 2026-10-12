package modelo;

public class Frontera {
    private final Provincia provincia1;
    private final Provincia provincia2;

    public Frontera(Provincia provincia1, Provincia provincia2) {
        if (provincia1 == null || provincia2 == null) {
            throw new IllegalArgumentException("Las provincias no pueden ser null.");
        }
        if (provincia1.equals(provincia2)) {
            throw new IllegalArgumentException("Una provincia no limita consigo misma.");
        }
        this.provincia1 = provincia1;
        this.provincia2 = provincia2;
    }

    public Provincia getProvincia1() { return provincia1; }
    public Provincia getProvincia2() { return provincia2; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Frontera)) return false;
        Frontera otra = (Frontera) o;
        return (provincia1.equals(otra.provincia1) && provincia2.equals(otra.provincia2))
                || (provincia1.equals(otra.provincia2) && provincia2.equals(otra.provincia1));
    }

    @Override
    public int hashCode() {
        return provincia1.hashCode() + provincia2.hashCode();
    }

    @Override
    public String toString() { return provincia1 + " - " + provincia2; }
}
