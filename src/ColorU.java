import org.jetbrains.annotations.NotNull;

import java.awt.*;

interface ColorU {

    static int alpha(int argb) {
        return (argb >>> 24) & 0xFF;
    }
    
    static int red(int argb) {
        return (argb >>> 16) & 0xFF;
    }

    static int green(int argb) {
        return (argb >>> 8) & 0xFF;
    }

    static int blue(int argb) {
        return (argb) & 0xFF;
    }
    
    /*@FloatRange(from = 0, to = 1)*/
    static float constraint0to1(float v) {
        return v <= 0.0f ? 0.0f : Math.min(v, 1.0f);
    }

    /*@@IntRange(from = 0, to = 255)*/
    static int constraint0to255(int v) {
        return v <= 0 ? 0 : Math.min(v, 255);
    }

    /**
     * Converts a rgb factor [0..1] to it's byte representation [0..255]
     *
     * @param factor alpha, red, green or blue factor ranging [0-1]
     * @return corresponding byte representation ranging [0-255]
     */
    /*@@IntRange(from = 0, to = 255)*/
    static int to255(/*@FloatRange(from = 0, to = 1)*/ float factor) {
        return constraint0to255((int) (factor * 255.0f + 0.5f));
    }

    /**
     * Return a color-int from alpha, red, green, blue float components
     * in the range \([0..255]\). If the components are out of range, the
     * returned color is undefined.
     *
     * @param alpha Alpha component \([0..255]\) of the color
     * @param red   Red component \([0..255]\) of the color
     * @param green Green component \([0..255]\) of the color
     * @param blue  Blue component \([0..255]\) of the color
     */
    /*@ColorInt*/
    static int argb(/*@@IntRange(from = 0, to = 255)*/ int alpha,
                    /*@@IntRange(from = 0, to = 255)*/ int red,
                    /*@@IntRange(from = 0, to = 255)*/ int green,
                    /*@@IntRange(from = 0, to = 255)*/ int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    /**
     * Return a color-int from alpha, red, green, blue float components
     * in the range \([0..1]\). If the components are out of range, the
     * returned color is undefined.
     *
     * @param alpha Alpha component \([0..1]\) of the color
     * @param red   Red component \([0..1]\) of the color
     * @param green Green component \([0..1]\) of the color
     * @param blue  Blue component \([0..1]\) of the color
     */
    /*@ColorInt*/
    static int argb(/*@FloatRange(from = 0, to = 1)*/ float alpha,
                    /*@FloatRange(from = 0, to = 1)*/ float red,
                    /*@FloatRange(from = 0, to = 1)*/ float green,
                    /*@FloatRange(from = 0, to = 1)*/ float blue) {
        return argb(to255(alpha), to255(red), to255(green), to255(blue));
    }

    /*@FloatRange(from = 0)*/
    static float contrastRatio(/*@FloatRange(from = 0, to = 1)*/ float fgRelLuminance,
                               /*@FloatRange(from = 0, to = 1)*/ float bgRelLuminance) {
        return (fgRelLuminance + 0.05f) / (bgRelLuminance + 0.05f);
    }

    /*@FloatRange(from = 0, to = 1)*/
    static float bgRelLuminance(/*@FloatRange(from = 0)*/ float contrastRatio,
                                /*@FloatRange(from = 0, to = 1)*/ float fgRelLuminance) {
        return ((fgRelLuminance + 0.05f) / contrastRatio) - 0.05f;
    }

    /*@FloatRange(from = 0, to = 1)*/
    static float fgRelLuminance(/*@FloatRange(from = 0)*/ float contrastRatio,
                                /*@FloatRange(from = 0, to = 1)*/ float bgRelLuminance) {
        return (contrastRatio * (bgRelLuminance + 0.05f)) - 0.05f;
    }

    /*@FloatRange(from = 0, to = 1)*/
    static float relLuminance(/*@FloatRange(from = 0, to = 1)*/ float red,
                              /*@FloatRange(from = 0, to = 1)*/ float green,
                              /*@FloatRange(from = 0, to = 1)*/ float blue) {
        return constraint0to1((float) ((0.2126 * red) + (0.7152 * green) + (0.0722 * blue)));
    }

    /*@FloatRange(from = 0, to = 1)*/
    static float relLuminance(/*@@IntRange(from = 0, to = 255)*/ int red,
                              /*@@IntRange(from = 0, to = 255)*/ int green,
                              /*@@IntRange(from = 0, to = 255)*/ int blue) {
        return relLuminance((float) red / 255, (float) green / 255, (float) blue / 255);
    }

    /*@FloatRange(from = 0, to = 1)*/
    static float relLuminance(/*@ColorInt*/ int rgb) {
        return relLuminance(red(rgb), green(rgb), blue(rgb));
    }

    /*@ColorInt*/
    static int withRGBfactor(/*@@IntRange(from = 0, to = 255)*/ int alpha,
                             /*@@IntRange(from = 0, to = 255)*/ int red,
                             /*@@IntRange(from = 0, to = 255)*/ int green,
                             /*@@IntRange(from = 0, to = 255)*/ int blue, /*@FloatRange(from = 0)*/ float rgbFactor) {

        red = constraint0to255((int) (red * rgbFactor));
        green = constraint0to255((int) (green * rgbFactor));
        blue = constraint0to255((int) (blue * rgbFactor));

        return argb(alpha, red, green, blue);
    }

    /*@ColorInt*/
    static int withRGBfactor(/*@ColorInt*/ int argb, /*@FloatRange(from = 0)*/ float rgbFactor) {
        return withRGBfactor(alpha(argb), red(argb), green(argb), blue(argb), rgbFactor);
    }

    /*@ColorInt*/
    static int withAlpha(/*@ColorInt*/ int argb, /*@@IntRange(from = 0, to = 255)*/ int alpha) {
        return (alpha << 24) | (argb & 0x00FFFFFF);
    }

    /*@ColorInt*/
    static int withAlphaF(/*@ColorInt*/ int argb, /*@FloatRange(from = 0, to = 1)*/ float alpha) {
        return withAlpha(argb, to255(alpha));
    }

    @NotNull
    static Color withAlpha(@NotNull Color c, int alpha /* [0-255] */) {
        return new Color(withAlpha(c.getRGB(), alpha), true);
    }


    @FunctionalInterface
    interface RelLuminanceTransform {

        /**
         * Return same input for no tranformation
         *
         * @param relLuminance input relative luminance
         * @return new relative luminance
         */
        float getRelLuminance(float relLuminance);

        @NotNull
        static RelLuminanceTransform exact(float relLuminance) {
            return in -> relLuminance;
        }

        @NotNull
        static RelLuminanceTransform delta(float relLuminanceDelta) {
            return in -> in + relLuminanceDelta;
        }

        @NotNull
        static RelLuminanceTransform max(float maxLuminanceDelta) {
            return in -> Math.min(in, maxLuminanceDelta);
        }

        @NotNull
        static RelLuminanceTransform min(float minLuminanceDelta) {
            return in -> Math.max(in, minLuminanceDelta);
        }
    }

    /*@ColorInt*/
    static int withLuminance(/*@ColorInt*/ int argb, @NotNull RelLuminanceTransform transform) {
        final int r = red(argb);
        final int g = green(argb);
        final int b = blue(argb);

        final float o = relLuminance(r, g, b);
        final float n = constraint0to1(transform.getRelLuminance(o));
        if (n == o)
            return argb;

        final float f = n / o;
        return withRGBfactor(alpha(argb), r, g, b, f);
    }

    /*@ColorInt*/
    static int withLuminance(/*@ColorInt*/ int argb, /*@FloatRange(from = 0, to = 1)*/ float relLuminance) {
        return withLuminance(argb, RelLuminanceTransform.exact(relLuminance));
    }

    /*@ColorInt*/
    static int withMaxLuminance(/*@ColorInt*/ int argb, float maxRelLuminance) {
        return withLuminance(argb, RelLuminanceTransform.max(maxRelLuminance));
    }

    /*@ColorInt*/
    static int withMinLuminance(/*@ColorInt*/ int argb, float minRelLuminance) {
        return withLuminance(argb, RelLuminanceTransform.min(minRelLuminance));
    }

    /*@ColorInt*/
    static int withLuminanceChange(/*@ColorInt*/ int argb, float delRelLuminance) {
        return withLuminance(argb, RelLuminanceTransform.delta(delRelLuminance));
    }
}
