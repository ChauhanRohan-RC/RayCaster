import math.RMath;
import math.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * All angles are in radians (like {@link #h_head}, {@link #h_fov}, {@link #v_head}, {@link #v_fov})
 * */
public class Player {

    public static final int DEFAULT_RAYS_COUNT = 1920;

    public static final float DEFAULT_H_FOV = RMath.rad(90);
    public static final float DEFAULT_V_FOV = RMath.rad(100);
    public static final float DEFAULT_H_HEAD = 0;
    public static final float DEFAULT_V_HEAD = 0;

    public static final float V_HEAD_MAX = RMath.rad(60);
    public static final float V_HEAD_MIN = RMath.rad(-50);
    public static final float V_HEAD_RANGE = Math.abs(V_HEAD_MAX - V_HEAD_MIN);


    private static float normalizeHeading(float headingRad, boolean allowNegative) {
        return RMath.normalizeRad(headingRad, allowNegative);
    }

    private static float normalize_h_head(float h_head) {
        return normalizeHeading(h_head, false);
    }

    private static float normalize_v_head(float v_head) {
        return RMath.constraint(V_HEAD_MIN, V_HEAD_MAX, normalizeHeading(v_head, true));
    }

    private static float normalizeFov(float fovRad, float min, float max) {
        return RMath.constraint(min, max, Math.abs(fovRad));
    }

    private static float normalizeFov(float fovRad) {
        return normalizeFov(fovRad, 0, RMath.TWO_PI);
    }

    private static int normalizeRaysCount(int raysCount) {
        if (raysCount < 0) {
            throw new IllegalArgumentException("Rays count must be >= 0, given : " + raysCount);
        }

        return raysCount;
    }


    /**
     * Players's position
     * */
    @NotNull
    private final Vector pos;

    /**
     * Players's horizontal field of view (H_FOV) in radians
     * */
    private float h_fov;

    /**
     * Players's Vertical field of view (V_FOV) in radians
     * */
    private float v_fov;

    /**
     * Players's horizontal heading (direction centering {@link #h_fov}) in radians
     * */
    private float h_head;

    /**
     * Players's vertical heading (direction centering {@link #v_fov}) in radians
     * */
    private float v_head;

    /**
     * No of rays to cast
     * */
    private int raysCount;

    @NotNull
    private final List<Ray> rays;

    public Player(@NotNull Vector pos, float h_fov, float v_fov, int raysCount, float h_head, float v_head) {
        this.pos = pos;

        this.h_fov = normalizeFov(h_fov);
        this.v_fov = normalizeFov(v_fov);

        this.h_head = normalize_h_head(h_head);
        this.v_head = normalize_v_head(v_head);

        this.raysCount = normalizeRaysCount(raysCount);
        this.rays = new ArrayList<>(this.raysCount + 10);
        repopulateRays();
    }

    public Player(@NotNull Vector pos, float h_fov, float v_fov, int raysCount) {
        this(pos, h_fov, v_fov, raysCount, DEFAULT_H_HEAD, DEFAULT_V_HEAD);
    }

    public Player(@NotNull Vector pos, float h_fov, float v_fov) {
        this(pos, h_fov, v_fov, DEFAULT_RAYS_COUNT);
    }

    public Player(@NotNull Vector pos, int raysCount) {
        this(pos, DEFAULT_H_FOV, DEFAULT_V_FOV, raysCount);
    }

    public Player(@NotNull Vector pos) {
        this(pos, DEFAULT_RAYS_COUNT);
    }


    private void repopulateRays() {
        rays.clear();

        if (raysCount <= 0)
            return;

        if (raysCount == 1) {
            rays.add(new Ray(pos, h_head));            // only one ray
            return;
        }

        final float angleStep = h_fov / (raysCount - 1);
        final float halfFov = h_fov / 2;
        for (float a = halfFov; a >= -halfFov; a -= angleStep) {
            rays.add(new Ray(pos, a + h_head));
        }
    }

    /* Position */

    private static boolean isPosOutOfBounds(float x, float y) {
        return x < 0 || x > 1 || y < 0 || y > 1;
    }

    @NotNull
    public final Vector getPosition() {
        return pos;
    }

    protected void onPositionChanged(float prevX, float prevY, float x, float y) {

    }

    private void setPosInternal(float x, float y) {
        final float px = pos.x, py = pos.y;
        pos.x = x;
        pos.y = y;
        onPositionChanged(px, py, x, y);
    }

    public final boolean setPosition(float x, float y) {
        if (isPosOutOfBounds(x, y))
            return false;

        setPosInternal(x, y);
        return true;
    }

    public final boolean changePositionBy(float deltaX, float deltaY) {
        return setPosition(pos.x + deltaX, pos.y + deltaY);
    }

    public final boolean moveTowards(float angleRad, float amt, @Nullable Collection<WallI> walls) {
        final Vector dir = Vector.fromAngle(angleRad);
        dir.mult(amt);

        if (walls == null || walls.isEmpty()) {
            return changePositionBy(dir.x, dir.y);
        }

        final float x = pos.x + dir.x, y = pos.y + dir.y;
        if (isPosOutOfBounds(x, y))
            return false;

        for (WallI wall: walls) {
            if (!wall.isRigid())
                continue;

            final Iterator<LineF> itr = wall.linesIterator();
            LineF line;
            while (itr.hasNext()) {
                line = itr.next();
                if (U.intersects(line.p1.x, line.p1.y, line.p2.x, line.p2.y, pos.x, pos.y, x, y))
                    return false;
            }
        }

        setPosInternal(x, y);
        return true;
    }

    public final boolean moveTowardsHeading(float amt, @Nullable Collection<WallI> walls) {
        return moveTowards(h_head, amt, walls);
    }

    public final boolean movePerpendicularToHeading(float amt, @Nullable Collection<WallI> walls) {
        return moveTowards(h_head + RMath.HALF_PI, amt, walls);
    }



    /* Heading */

    /**
     * @return horizontal heading (in radians)
     *
     * @see #h_head
     * */
    public final float h_head() {
        return h_head;
    }

    protected void on_h_head_Changed(float prev_h_head, float new_h_head) {
        final float deltaRad = new_h_head - prev_h_head;
        for (Ray ray: rays) {
            ray.changeAngleBy(deltaRad);
        }
    }

    public final void set_h_head(float h_head_rad) {
        h_head_rad = normalize_h_head(h_head_rad);
        if (this.h_head == h_head_rad)
            return;

        final float ph = this.h_head;
        this.h_head = h_head_rad;
        on_h_head_Changed(ph, h_head_rad);
    }

    public final void rotate_h_head(float deltaRad) {
        set_h_head(h_head + deltaRad);
    }

    /**
     * @return vertical heading (in radians)
     *
     * @see #v_head
     * */
    public final float v_head() {
        return v_head;
    }

    protected void on_v_head_changed(float prev_v_head, float new_v_head) {
    }

    public final void set_v_head(float v_head_rad) {
        v_head_rad = normalize_v_head(v_head_rad);
        if (this.v_head == v_head_rad)
            return;

        final float ph = this.v_head;
        this.v_head = v_head_rad;
        on_v_head_changed(ph, v_head_rad);
    }

    public final void rotate_v_head(float deltaRad) {
        set_v_head(v_head + deltaRad);
    }


    /* FOV */

    /**
     * @return horizontal Field of View (fov) in radians
     *
     * @see #h_fov
     * */
    public final float h_fov() {
        return h_fov;
    }

    protected void on_h_fov_changed(float prev_h_fov, float new_h_fov) {
        repopulateRays();
    }

    public final void set_h_fov(float h_fov_rad) {
        h_fov_rad = normalizeFov(h_fov_rad);
        if (this.h_fov == h_fov_rad)
            return;

        final float prev = this.h_fov;
        this.h_fov = h_fov_rad;
        on_h_fov_changed(prev, h_fov_rad);
    }

    public final void change_h_fov(float deltaRad) {
        set_h_fov(h_fov + deltaRad);
    }


    /**
     * @return vertical Field of View (fov) in radians
     *
     * @see #v_fov
     * */
    public final float v_fov() {
        return v_fov;
    }

    public final float getProjectionDistance(float height) {
        return 0.5f * height / RMath.tan(v_fov);
    }

    protected void on_v_fov_changed(float prev_v_fov, float new_v_fov) {

    }

    public final void set_v_fov(float v_fov_rad) {
        v_fov_rad = normalizeFov(v_fov_rad);
        if (this.v_fov == v_fov_rad)
            return;

        final float prev = this.v_fov;
        this.v_fov = v_fov_rad;
        on_v_fov_changed(prev, v_fov_rad);
    }

    public final void change_v_fov(float deltaRad) {
        set_v_fov(v_fov + deltaRad);
    }





//    public final float getFovToRaysRatio() {
//        return fovToRaysRatio;
//    }

//    protected void onFovToRaysRatioChanged(float prevFovToRaysRatio, float newFovToRaysRatio) {
//        repopulateRays();
//    }
//
//    public final void setFovToRaysRatio(float fovToRaysRatio) {
//        if (this.fovToRaysRatio == fovToRaysRatio)
//            return;
//
//        final float prev = this.fovToRaysRatio;
//        this.fovToRaysRatio = fovToRaysRatio;
//        onFovToRaysRatioChanged(prev, fovToRaysRatio);
//    }



    /* Ray casting */

    public final int getRaysCountPreference() {
        return raysCount;
    }

    protected void onRaysCountChanged(int prevCount, int newCount) {
        repopulateRays();
    }

    public final void setRaysCountPreference(int raysCount) {
        raysCount = normalizeRaysCount(raysCount);
        if (this.raysCount == raysCount)
            return;

        final int prev = this.raysCount;
        this.raysCount = raysCount;
        onRaysCountChanged(prev, raysCount);
    }

    public final int rays() {
        return rays.size();
    }

    @NotNull
    public Ray rayAt(int index) {
        return rays.get(index);
    }

    public final float getRayProjectionPosition(float rayHeading) {
        return 0.5f * (1 - (RMath.tan(rayHeading - h_head) / RMath.tan(h_fov / 2)));
    }

    public final float getRayProjectionPosition(@NotNull Ray ray) {
        return getRayProjectionPosition(ray.getAngle());
    }

    public final float getRayProjectionPosition(int index) {
        return getRayProjectionPosition(rayAt(index));
    }



    public static final class RayCast {
        @NotNull
        public final Ray ray;
        @Nullable
        public final List<Ray.CastInfo> castInfos;

        public RayCast(@NotNull Ray ray, @Nullable List<Ray.CastInfo> castInfos) {
            this.ray = ray;
            this.castInfos = castInfos;
        }
    }

    @NotNull
    public RayCast[] cast(@NotNull Collection<WallI> walls, float maxDistance /* <= 0 for none */, boolean sort) {
        final int count = rays.size();
        final RayCast[] casts = new RayCast[count];

        for (int i=0; i < rays.size(); i++) {
            Ray ray = rays.get(i);
            casts[i] = new RayCast(ray, ray.cast(walls, maxDistance, sort));
        }

        return casts;
    }
}
