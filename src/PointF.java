import org.jetbrains.annotations.NotNull;

public class PointF {

    @NotNull
    public static PointF randomRelative() {
        return new PointF(U.RANDOM.nextFloat(), U.RANDOM.nextFloat());
    }

    public float x;
    public float y;

    public PointF(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public PointF() {
    }


    public boolean equals(@NotNull PointF p) {
        return x == p.x && y == p.y;
    }

    public boolean equals(Object o) {
        return this == o || (o instanceof PointF && equals((PointF) o));
    }

    @Override
    public int hashCode() {
        int r = 1;
        r = 31 * r + Float.hashCode(x);
        r = 31 * r + Float.hashCode(y);
        return r;
    }


    @Override
    public String toString() {
        return "PointF{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
