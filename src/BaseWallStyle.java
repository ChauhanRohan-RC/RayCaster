import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class BaseWallStyle implements WallStyleI {

    @NotNull
    public static BaseWallStyle fillOnly(@Nullable Color fillC) {
        return new BaseWallStyle(true, fillC, false, 1, null);
    }

    @NotNull
    public static BaseWallStyle strokeOnly(float strokeWeight, @Nullable Color strokeC) {
        return new BaseWallStyle(false, null, true, strokeWeight, strokeC);
    }

    @NotNull
    public static BaseWallStyle fillAndStroke(@Nullable Color fillC, float strokeWeight, @Nullable Color strokeC) {
        return new BaseWallStyle(true, fillC, true, strokeWeight, strokeC);
    }



    private boolean fill;
    @Nullable
    private Color fillC;

    private boolean stroke;
    private float strokeWeight;
    @Nullable
    private Color strokeC;

    private BaseWallStyle(boolean fill, @Nullable Color fillC, boolean stroke, float strokeWeight, @Nullable Color strokeC) {
        this.fill = fill;
        this.fillC = fillC;

        this.stroke = stroke;
        this.strokeWeight = strokeWeight;
        this.strokeC = strokeC;
    }

    @Override
    public boolean fill() {
        return fill;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }

    @Override
    public @Nullable Color fillColor() {
        return fillC;
    }

    public void setFillColor(@Nullable Color fillC) {
        this.fillC = fillC;
    }

    @Override
    public boolean stroke() {
        return stroke;
    }

    public void setStroke(boolean stroke) {
        this.stroke = stroke;
    }

    @Override
    public float strokeWeight() {
        return strokeWeight;
    }

    public void setStrokeWeight(float strokeWeight) {
        this.strokeWeight = strokeWeight;
    }

    @Override
    public @Nullable Color strokeColor() {
        return strokeC;
    }

    public void setStrokeColor(@Nullable Color strokeC) {
        this.strokeC = strokeC;
    }
}
