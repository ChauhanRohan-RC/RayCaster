import org.jetbrains.annotations.NotNull;

public class SizeF {

    public float width;
    public float height;

    public SizeF(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float minDimension() {
        return Math.min(width, height);
    }

    public float maxDimension() {
        return Math.max(width, height);
    }

    public boolean equals(@NotNull SizeF s) {
        return width == s.width && height == s.height;
    }

    public boolean equals(Object o) {
        return this == o || (o instanceof SizeF && equals((SizeF) o));
    }

    @Override
    public int hashCode() {
        int r = 1;
        r = 31 * r + Float.hashCode(width);
        r = 31 * r + Float.hashCode(height);
        return r;
    }

    @Override
    public String toString() {
        return "SizeF{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}
