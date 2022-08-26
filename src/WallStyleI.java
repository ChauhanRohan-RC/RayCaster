import org.jetbrains.annotations.Nullable;

import java.awt.*;

public interface WallStyleI {

    boolean fill();

    void setFill(boolean fill);

    @Nullable
    Color fillColor();

    void setFillColor(@Nullable Color fillColor);


    boolean stroke();

    void setStroke(boolean stroke);

    float strokeWeight();

    void setStrokeWeight(float strokeWeight);

    @Nullable
    Color strokeColor();

    void setStrokeColor(@Nullable Color strokeColor);
}
