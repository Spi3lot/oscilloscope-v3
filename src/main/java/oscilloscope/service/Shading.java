package oscilloscope.service;

import processing.core.PApplet;

import static processing.core.PApplet.*;

/**
 * @author Emilio Zottel (5AHIF)
 * @since 09.02.2024, Fr.
 */
public record Shading(PApplet pApplet) {

    public static final float SQRT_2 = sqrt(2);

    public float calcLuminance(float pixelDistance) {
        float t = normalizePixelDistance(pixelDistance);
        return pow(1 - t, 10);
    }

    public float normalizePixelDistance(float pixelDistance) {
        return pixelDistance / (SQRT_2 * min(pApplet.width, pApplet.height));
    }

}
