import com.jogamp.newt.opengl.GLWindow;
import math.RMath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import processing.awt.PSurfaceAWT;
import processing.core.PSurface;
import processing.opengl.PSurfaceJOGL;

import java.awt.*;


import java.util.Random;

public class U {

    @NotNull
    public static final Random RANDOM = new Random();

    private static boolean outside01(float t) {
        return t < 0 || t > 1;
    }

    private static boolean isIn01Inclusive(float t) {
        return !outside01(t);
    }

    /**
     * Interpolates a point within a line segment
     *
     * @param x1 line point1.x
     * @param y1 line point1.y
     * @param x2 line point2.x
     * @param y2 line point2.y
     *
     * @param x3 test point.x
     * @param y3 test point.y
     *
     * @return interpolation factor of point3 within line (p1 - p2), or {@code null} if the point3 does not lie on the line
     * */
    @Nullable
    public static Float lineInterpolationFactor(float x1, float y1, float x2, float y2, float x3, float y3) {
        if (x1 == x2)
            return x1 == x3? RMath.norm(y3, y1, y2): null;

        if (y1 == y2)
            return y1 == y3? RMath.norm(x3, x1, x2): null;

        return Math.abs((y2 - y1) / (x2 - x1)) == Math.abs((y3 - y1) / (x3 - x1))? RMath.norm(x3, x1, x2): null;
    }

    /**
     * @see #lineInterpolationFactor(float, float, float, float, float, float)
     * */
    @Nullable
    public static Float lineInterpolationFactor(@NotNull PointF p1, @NotNull PointF p2, @NotNull PointF p3) {
        return lineInterpolationFactor(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
    }

    /**
     * @see #lineInterpolationFactor(float, float, float, float, float, float)
     * */
    public static boolean isOnLine(float x1, float y1, float x2, float y2, float x3, float y3) {
        final Float t = lineInterpolationFactor(x1, y1, x2, y2, x3, y3);
        return t != null && isIn01Inclusive(t);
    }

    /**
     * @see #lineInterpolationFactor(float, float, float, float, float, float)
     * */
    public static boolean isOnLine(@NotNull PointF p1, @NotNull PointF p2, @NotNull PointF p3) {
        final Float t = lineInterpolationFactor(p1, p2, p3);
        return t != null && isIn01Inclusive(t);
    }




    public static class TU {

        public final float t;
        public final float u;

        public TU(float t, float u) {
            this.t = t;
            this.u = u;
        }
    }


    /**
     * Calculates the intersection factors for the intersection of 2 lines
     *
     * Line1 : p1 -> p2
     * Line2 : p3 -> p4
     *
     * If intersection factor for both lines are in range [0, 1], then the lines perfectly intersects,
     * otherwise they intersects after extrapolation
     *
     * @param x1 line1 start.x
     * @param y1 line1 start.y
     * @param x2 line1 end.x
     * @param y2 line1 end.y
     *
     * @param x3 line2 start.x
     * @param y3 line2 start.y
     * @param x4 line2 end.x
     * @param y4 line2 end.y
     *
     * @param boundLine1 whether line1 should not be extrapolated (t factor must be range [0, 1])
     * @param boundLine2 whether line2 should not be extrapolated (u factor must be range [0, 1])
     *
     * @return t: line1 (p1-p2) intersection factor
     *         u: line2 (p1-p2) intersection factor
     *
     *         null: if lines do not intersects
     * */
    @Nullable
    public static TU lineLineIntersection(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, boolean boundLine1, boolean boundLine2) {
        final float den = ((x1 - x2) * (y3 - y4)) - ((y1 - y2) * (x3 - x4));
        if (den == 0)
            return null;        // parallel

        final float t = (((x1 - x3) * (y3 - y4)) - ((y1 - y3) * (x3 - x4))) / den;
        if (boundLine1 && outside01(t))
            return null;        // intersection not within first line

        final float u = (((x1 - x3) * (y1 - y2)) - ((y1 - y3) * (x1 - x2))) / den;
        if (boundLine2 && outside01(u))
            return null;        // intersection not within second line

        return new TU(t, u);
    }

    /**
     * @see #lineLineIntersection(float, float, float, float, float, float, float, float, boolean, boolean)
     * */
    @Nullable
    public static TU lineLineIntersection(@NotNull PointF p1, @NotNull PointF p2, @NotNull PointF p3, @NotNull PointF p4, boolean boundLine1, boolean boundLine2) {
        return lineLineIntersection(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y, p4.x, p4.y, boundLine1, boundLine2);

//        final float den = ((p1.x - p2.x) * (p3.y - p4.y)) - ((p1.y - p2.y) * (p3.x - p4.x));
//        if (den == 0)
//            return null;        // parallel
//
//        final float t = (((p1.x - p3.x) * (p3.y - p4.y)) - ((p1.y - p3.y) * (p3.x - p4.x))) / den;
//        if (t < 0 || t > 1)
//            return null;        // intersection not within first (bounded) line
//
//        return (((p1.x - p3.x) * (p1.y - p2.y)) - ((p1.y - p3.y) * (p1.x - p2.x))) / den;
    }


    /**
     * @see #lineLineIntersection(float, float, float, float, float, float, float, float, boolean, boolean)
     * */
    public static boolean intersects(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        return lineLineIntersection(x1, y1, x2, y2, x3, y3, x4, y4, true, true) != null;
    }

    /**
     * @see #lineLineIntersection(float, float, float, float, float, float, float, float, boolean, boolean)
     * */
    public static boolean intersects(@NotNull PointF p1, @NotNull PointF p2, @NotNull PointF p3, @NotNull PointF p4) {
       return lineLineIntersection(p1, p2, p3, p4, true, true) != null;
    }




    /**
     * Checks if a given point is inside the area defined by the polygon
     *
     * @param p The point to check
     * @param closed true if the polygon is a closed shape, false if it is just lines
     * @param vertices vertices of the polygon
     *
     * @return true if the point is inside the polygon, false otherwise
     */
    public static boolean contains(@NotNull PointF p, boolean closed, @NotNull PointF... vertices) {
        if (vertices == null || vertices.length == 0)
            return false;

        if (vertices.length == 1)
            return p.equals(vertices[0]);

        if (vertices.length == 2) {
            return isOnLine(vertices[0], vertices[1], p);
        }

        if (closed) {
            boolean result = false;

            int i, j;
            for (i = 0, j = vertices.length - 1; i < vertices.length; j = i++) {
                if ((vertices[i].y > p.y) != (vertices[j].y > p.y) && (p.x < (vertices[j].x - vertices[i].x) * (p.y - vertices[i].y) / (vertices[j].y-vertices[i].y) + vertices[i].x)) {
                    result = !result;
                }
            }

            return result;
        } else {
            for (int i=0; i < vertices.length - 1; i++) {
                if (isOnLine(vertices[i], vertices[i + 1], p))
                    return true;
            }
        }

        return false;
    }




    @NotNull
    public static Point getLocationOnScreen(@NotNull PSurface surface) {
        final Point p = new Point();

        try {
            // JAVA2D
            if(surface instanceof PSurfaceAWT){
                Frame frame = ((PSurfaceAWT.SmoothCanvas) surface.getNative()).getFrame();
                if (frame.isShowing()) {
                    p.setLocation(frame.getLocationOnScreen());
                }
            }

            // P2D, P3D
            else if (surface instanceof PSurfaceJOGL) {
                GLWindow window = (GLWindow) surface.getNative();
                com.jogamp.nativewindow.util.Point point = window.getLocationOnScreen(new com.jogamp.nativewindow.util.Point());
                p.move(point.getX(), point.getY());
            }

            // FX2D
//            else if (surface instanceof processing.javafx.PSurfaceFX) {
//                javafx.scene.canvas.Canvas canvas = (javafx.scene.canvas.Canvas) ((processing.javafx.PSurfaceFX) surface).getNative();
//                javafx.geometry.Point2D point = canvas.localToScreen(0, 0);
//                p.move((float) point.getX(), (float) point.getY());
//            }
        } catch (Throwable ignored) {
        }

        return p;
    }

    public static void moveMouse(@NotNull Point p, @Nullable GraphicsDevice device) {
        try {
            final Robot r = device != null? new Robot(device): new Robot();
            r.mouseMove(p.x, p.y);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static void moveMouse(@NotNull Point p) {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice[] gs = ge.getScreenDevices();

        // Search the devices for the one that draws the specified point.
        for (GraphicsDevice device: gs) {
            GraphicsConfiguration[] configurations = device.getConfigurations();

            for (GraphicsConfiguration config: configurations) {
                Rectangle bounds = config.getBounds();

                if(bounds.contains(p)) {
                    Point l = bounds.getLocation();
                    Point s = new Point(p.x - l.x, p.y - l.y);      // Set point relative screen coordinates.

                    moveMouse(s, device);
                    return;
                }
            }
        }

        // Couldn't move to the point, it may be off screen.
    }



}
