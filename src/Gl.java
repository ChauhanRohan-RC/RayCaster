import math.RMath;
import math.Vector;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Gl {

    public static int withAlpha(int rgb, int alpha /* [0-255] */) {
        return ((alpha & 0xFF) << 24) | (rgb & 0xFFFFFF);
    }

    @NotNull
    public static Color withAlpha(@NotNull Color c, int alpha /* [0-255] */) {
        return new Color(withAlpha(c.getRGB(), alpha), true);
    }

    @NotNull
    public static PointF abs(@NotNull PointF pt, float width, float height) {
        return new PointF(pt.x * width, pt.y * height);
    }

    @NotNull
    public static PointF abs(@NotNull PointF pt, @NotNull SizeF size) {
        return abs(pt, size.width, size.height);
    }

    @NotNull
    public static Vector abs(@NotNull Vector pt, float width, float height) {
        return new Vector(pt.x * width, pt.y * height);
    }

    @NotNull
    public static Vector abs(@NotNull Vector pt, @NotNull SizeF size) {
        return abs(pt, size.width, size.height);
    }


    /* ..................................... Mini Map ...........................*/

    public static final boolean MINIMAP_DEFAULT_DRAW_WALLS = true;
    public static final boolean MINIMAP_DEFAULT_DRAW_RAYS = true;
    public static final boolean MINIMAP_DEFAULT_DRAW_NON_COLLIDING_RAYS = true;
    public static final boolean MINIMAP_DEFAULT_DRAW_INTERSECTION_POINTS = false;

    public static final float MINIMAP_PLAYER_DIAMETER_FRACTION = 0.012f;
    public static final float MINIMAP_RAY_STROKE_WEIGHT = 1f;
    public static final float MINIMAP_INTERSECTION_POINT_STROKE_WEIGHT = 3f;

    public static final int MINIMAP_NON_RIGID_WALL_ALPHA = 150;


    public static final Color MINIMAP_BG = Color.BLACK;
    public static final Color MINIMAP_FG_DARK = Color.WHITE;
    public static final Color MINIMAP_FG_MEDIUM = new Color(227, 227, 227);
    public static final Color MINIMAP_FG_LIGHT = new Color(200, 200, 200);

    public static final Color COLOR_SKY = new Color(73, 255, 255);
    public static final Color COLOR_GROUND = new Color(106, 255, 73);
    public static final Color DEFAULT_COLOR_WALLS = new Color(255, 73, 152);

    public static Color fogColor(int alpha /* [0, 255] */) {
        return withAlpha(COLOR_SKY, alpha);
    }

    public static Color fogColor(float fogLevel /* [0, 1] */) {
        return fogColor(Math.round(RMath.constraint(0, 1, fogLevel) * 255));
    }

    public static final Color MINIMAP_DEFAULT_WALL_FILL = MINIMAP_FG_DARK;
    public static final Color MINIMAP_DEFAULT_WALL_STROKE = MINIMAP_FG_DARK;

    public static final Color MINIMAP_DEFAULT_PLAYER_COLOR = MINIMAP_FG_DARK;
    public static final Color MINIMAP_DEFAULT_RAY_COLOR = withAlpha(MINIMAP_FG_LIGHT, 170);
    public static final Color MINIMAP_DEFAULT_INTERSECTION_POINT_COLOR = MINIMAP_FG_DARK;

}
