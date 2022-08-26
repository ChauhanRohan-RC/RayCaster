import org.jetbrains.annotations.NotNull;

public class LineF {

    @NotNull
    public final PointF p1;
    @NotNull
    public final PointF p2;

    public LineF(@NotNull PointF p1, @NotNull PointF p2) {
        this.p1 = p1;
        this.p2 = p2;
    }
}
