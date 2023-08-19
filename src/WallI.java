
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;
import processing.core.PConstants;

import java.awt.*;
import java.util.Iterator;
import java.util.NoSuchElementException;


public interface WallI {

    @NotNull
    WallStyleI DEFAULT_STYLE_CLOSED = BaseWallStyle.fillOnly(null);
    @NotNull
    WallStyleI DEFAULT_STYLE_OPEN = BaseWallStyle.strokeOnly(3.6f, null);
    boolean DEFAULT_RIGID = true;

    float STANDARD_HEIGHT = 1;
    float DEFAULT_HEIGHT = STANDARD_HEIGHT;


    int vertices();

    @NotNull
    PointF vertexAt(int index);

    boolean close();

    boolean isRigid();

    WallI setRigid(boolean rigid);

    /**
     * height of this wall relative to vertical Field of View
     *
     * ex. 1 -> full  VERTICAL_FOV
     *     0.5 -> half  VERTICAL_FOV
     *
     * @return height of this wall relative to vertical Field of View
     * */
    float getHeight();

    @NotNull
    default Iterator<LineF> linesIterator() {
        return new LineIterator(this);
    }

    @NotNull
    WallStyleI style();


    boolean contains(@NotNull PointF p);


    default void draw(@NotNull PApplet p, @NotNull SizeF mapSize) {
        p.pushStyle();

        // fill
        final WallStyleI style = style();
        if (style.fill()) {
            Color c = style.fillColor();
            if (c == null) {
                c = Gl.MINIMAP_DEFAULT_WALL_FILL;
            }

            if (!isRigid()) {
                c = ColorU.withAlpha(c, Gl.MINIMAP_NON_RIGID_WALL_ALPHA);
            }

            p.fill(c.getRGB());
        } else {
            p.noFill();
        }

        // stroke
        if (style.stroke()) {
            p.strokeWeight(style.strokeWeight());
            Color c = style.strokeColor();
            if (c == null) {
                c = Gl.MINIMAP_DEFAULT_WALL_FILL;
            }

            if (!isRigid()) {
                c = ColorU.withAlpha(c, Gl.MINIMAP_NON_RIGID_WALL_ALPHA);
            }

            p.stroke(c.getRGB());
        } else {
            p.noStroke();
        }

        // Shape
        final int vxCount = vertices();
        if (vxCount == 1) {
            final PointF point = Gl.abs(vertexAt(0), mapSize);
            p.point(point.x, point.y);
        } else if (vxCount == 2) {
            final PointF p1 = Gl.abs(vertexAt(0), mapSize), p2 = Gl.abs(vertexAt(1), mapSize);
            p.line(p1.x, p1.y, p2.x, p2.y);
        } else {
            p.beginShape(PConstants.POLYGON);
            PointF vx;
            for (int i=0; i < vertices(); i++) {
                vx = Gl.abs(vertexAt(i), mapSize);
                p.vertex(vx.x, vx.y);
            }
            p.endShape(close()? PConstants.CLOSE: PConstants.OPEN);
        }

        p.popStyle();
    }



    class LineIterator implements Iterator<LineF> {

        @NotNull
        private final WallI wall;
        private final boolean close;
        private final int verticesCount, linesCount;
        private int i;

        public LineIterator(@NotNull WallI wall) {
            this.wall = wall;
            verticesCount = wall.vertices();
            close = wall.close() && verticesCount > 2;
            linesCount = close? verticesCount: verticesCount - 1;
        }

        @Override
        public boolean hasNext() {
            return i < linesCount;
        }

        @Override
        public LineF next() {
            final PointF start = wall.vertexAt(i);
            final int nextI = i + 1;
            final PointF end;
            if (nextI < verticesCount) {
                end = wall.vertexAt(nextI);
            } else if (nextI == verticesCount && close) {
                end = wall.vertexAt(0);
            } else {
                throw new NoSuchElementException("Should not reach here");
            }

            i++;
            return new LineF(start, end);
        }
    }


}
