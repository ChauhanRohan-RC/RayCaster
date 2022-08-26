package math;

public class RMath {

    public static final float PI = (float) Math.PI;
    public static final float HALF_PI = PI / 2;
    public static final float TWO_PI = PI * 2;

    static final float DEG_TO_RAD = PI / 180.0f;
    static final float RAD_TO_DEG = 180.0f / PI;

    public static float deg(float radians) {
        return radians * RAD_TO_DEG;
    }

    public static float rad(float degrees) {
        return degrees * DEG_TO_RAD;
    }

    public static float normalizeDeg(float deg, boolean allowNegative) {
        deg %= 360;
        return allowNegative || deg >= 0? deg: deg + 360;
    }

    public static float normalizeRad(float rad, boolean allowNegative) {
        rad %= TWO_PI;
        return allowNegative || rad >= 0? rad: rad + TWO_PI;
    }


    public static double constraint(double min, double max, double value) {
        return Math.max(min, Math.min(max, value));
    }

    public static float constraint(float min, float max, float value) {
        return Math.max(min, Math.min(max, value));
    }

    public static long constraint(long min, long max, long value) {
        return Math.max(min, Math.min(max, value));
    }

    public static int constraint(int min, int max, int value) {
        return Math.max(min, Math.min(max, value));
    }


    public static boolean isInt(float v) {
        return v == (int) v;
    }

    public static float cos(float theta) {
        return (float) Math.cos(theta);
    }

    public static float sin(float theta) {
        return (float) Math.sin(theta);
    }

    public static float tan(float theta) {
        return (float) Math.tan(theta);
    }

    public static float lerp(float start, float stop, float amt) {
        return start + (stop - start) * amt;
    }

    public static float norm(float value, float start, float stop) {
        return (value - start) / (stop - start);
    }

    public static float map(float value, float start1, float stop1, float start2, float stop2) {
        return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
    }

}
