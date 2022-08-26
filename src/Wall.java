import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Wall implements WallI {

    @NotNull
    private final PointF[] vertices;

    private final boolean close;
    private boolean rigid = DEFAULT_RIGID;
    private float height = DEFAULT_HEIGHT;
    @Nullable
    private WallStyleI style;

    public Wall(boolean close, @Nullable WallStyleI style, @NotNull PointF... vertices) {
        this.close = close;
        this.style = style;
        this.vertices = vertices;
    }

    public Wall(boolean close, @NotNull PointF... vertices) {
        this(close, null, vertices);
    }

    @Override
    public int vertices() {
        return vertices.length;
    }

    @Override
    @NotNull
    public PointF vertexAt(int index) {
        return vertices[index];
    }


    @Override
    public final boolean close() {
        return close;
    }

    @Override
    public boolean isRigid() {
        return rigid;
    }

    @Override
    @NotNull
    public Wall setRigid(boolean rigid) {
        this.rigid = rigid;
        return this;
    }

    /**
     * @see WallI#getHeight()
     * */
    @Override
    public float getHeight() {
        return height;
    }

    /**
     * @see WallI#getHeight()
     * */
    @NotNull
    public Wall setHeight(float height) {
        this.height = height;
        return this;
    }

    @Override
    public boolean contains(@NotNull PointF p) {
        return U.contains(p, close, vertices);
    }


    public void setStyle(@Nullable WallStyleI style) {
        this.style = style;
    }

    @Override
    public @NotNull WallStyleI style() {
        return style != null? style: close? DEFAULT_STYLE_CLOSED: DEFAULT_STYLE_OPEN;
    }
}
