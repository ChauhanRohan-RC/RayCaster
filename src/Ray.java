import math.RMath;
import math.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class Ray {

    @NotNull
    private final Vector pos;
    @NotNull
    private final Vector dir;

    public Ray(@NotNull Vector pos, float angle) {
        this.pos = pos;
        this.dir = Vector.fromAngle(angle);
    }

    @NotNull
    public final Vector getPosition() {
        return pos;
    }

    @NotNull
    public final Vector getDirection() {
        return dir;
    }

    public final float getAngle() {
        return dir.heading2D();
    }

    public final void setAngle(float angle) {
        Vector.fromAngle(angle, dir);
    }

    public final void changeAngleBy(float angleDelta) {
        setAngle(getAngle() + angleDelta);
    }


    public final void lookAt(float x, float y) {
        dir.x = x - pos.x;
        dir.y = y - pos.y;
        dir.normalize();
    }

    @NotNull
    public final Vector lerp(float u) {
        return Vector.lerp(pos, Vector.add(pos, dir), u, null);
    }


    @Nullable
    public final U.TU cast(@NotNull PointF p1, @NotNull PointF p2, float maxDistance /* <= 0 for none */) {
        final U.TU tu = U.lineLineIntersection(p1.x, p1.y, p2.x, p2.y, pos.x, pos.y, pos.x + dir.x, pos.y + dir.y, true, false);
        if (tu == null)
            return null;

        if (tu.u < 0 || (maxDistance > 0 && tu.u > maxDistance))   // reverse direction || greater than maxDistance
            return null;

        return tu;
    }


    @Nullable
    public final List<CastInfo> cast(@NotNull WallI wall, float maxDistance /* <= 0 for none */, boolean sort) {
//        CastInfo info, closest = null;

        List<CastInfo> l = null;
        U.TU tu;

        final Iterator<LineF> itr = wall.linesIterator();
        LineF line;
        while (itr.hasNext()) {
            line = itr.next();
            tu = cast(line.p1, line.p2, maxDistance);
            if (tu != null) {
                if (l == null) {
                    l = sort? new ArrayList<>(wall.vertices()): new LinkedList<>();
                }

                l.add(new CastInfo(wall, line, tu));
            }

//            if (info != null && (closest == null || closest.tu.u > info.tu.u)) {
//                closest = info;
//                closest.closestLine = line;
//            }
        }

//        final int vertices = wall.vertices();
//
//        for (int i = 0; i < vertices - 1; i++) {
//            info = cast(wall.vertexAt(i), wall.vertexAt(i + 1), maxDistance);
//            if (info != null && (closest == null || closest.u > info.u)) {
//                closest = info;
//            }
//        }
//
//        if (wall.close() && vertices > 2) {
//            info = cast(wall.vertexAt(vertices - 1), wall.vertexAt(0), maxDistance);
//            if (info != null && (closest == null || closest.u > info.u)) {
//                closest = info;
//            }
//        }

        if (sort && l != null) {
            l.sort(CAST_INFO_COMPARATOR);
        }

        return l;
    }


    // TODO: return cast info's of all hitting walls, sorted by ascending u

    @Nullable
    public final List<CastInfo> cast(@NotNull Collection<WallI> walls, float maxDistance /* <= 0 for none */, boolean sort) {
//        CastInfo info;
//        CastInfo closest = null;

        List<CastInfo> r = null;
        List<CastInfo> infos;
        for (WallI wall: walls) {
            infos = cast(wall, maxDistance, false);
            if (infos != null) {
                if (r == null) {
                    r = new ArrayList<>();
                }

                for (CastInfo i: infos) {
                    r.add(i);
                }
            }

//            if (info != null && (closest == null || closest.tu.u > info.tu.u)) {
//                closest = info;
//                closest.closestWall = wall;
//            }
        }

        if (sort & r != null) {
            r.sort(CAST_INFO_COMPARATOR);
        }

        return r;
    }






    public static final Comparator<CastInfo> CAST_INFO_COMPARATOR = (o1, o2) -> Float.compare(o2.tu.u, o1.tu.u);            // descending (Far -> near)

    public class CastInfo {

        @NotNull
        public final U.TU tu;       // also the raw euclidean distance from pos to intersection point
        @NotNull
        public final Vector intersectionPoint;

        @NotNull
        public final WallI wall;
        @NotNull
        public final LineF line;

        public CastInfo(@NotNull WallI wall, @NotNull LineF line, @NotNull U.TU tu, @NotNull Vector intersectionPoint) {
            this.wall = wall;
            this.line = line;
            this.tu = tu;
            this.intersectionPoint = intersectionPoint;
        }

        public CastInfo(@NotNull WallI wall, @NotNull LineF line, @NotNull U.TU tu) {
            this(wall, line, tu, lerp(tu.u));
        }


        public float distanceProjection(float angle) {
            return tu.u * RMath.cos(angle);
        }

//        @Nullable
//        public final WallI getClosestWall() {
//            return closestWall;
//        }
//
//        public final float getWallHeightOrDefault(float defaultValue) {
//            return wall != null? wall.getHeight(): defaultValue;
//        }
//
//        public final float getWallHeightOrDefault() {
//            return getWallHeightOrDefault(WallI.DEFAULT_HEIGHT);
//        }

    }

}
