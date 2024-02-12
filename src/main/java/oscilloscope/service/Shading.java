package oscilloscope.service;

import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PApplet.*;

/**
 * @author Emilio Zottel (5AHIF)
 * @since 09.02.2024, Fr.
 */
public record Shading(PApplet pApplet) {

    public static final float SQRT_2 = sqrt(2);

    public void init() {
        pApplet.strokeCap(ROUND);
        pApplet.strokeJoin(ROUND);
    }

    public void drawLine(PVector start, PVector end, int sampleRate) {
        float luminance = calcLuminance(start.dist(end), sampleRate);
        float alpha = luminance * 255;
        pApplet.strokeWeight(3);
        pApplet.stroke(0, 255, 0, alpha);
        pApplet.line(start.x, start.y, end.x, end.y);
        pApplet.strokeWeight(1);
        pApplet.stroke(255, alpha);
        pApplet.line(start.x, start.y, end.x, end.y);
    }

    public float calcLuminance(float pixelDistance, int sampleRate) {
        float t = normalizePixelDistance(pixelDistance);
        return pow(1 - t, sampleRate / 2000.0f);
    }

    public float normalizePixelDistance(float pixelDistance) {
        return pixelDistance / (SQRT_2 * min(pApplet.width, pApplet.height));
    }

}
