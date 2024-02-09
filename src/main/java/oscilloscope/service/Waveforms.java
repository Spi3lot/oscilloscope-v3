package oscilloscope.service;

import lombok.RequiredArgsConstructor;
import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

import static processing.core.PApplet.abs;
import static processing.core.PApplet.min;

/**
 * @author Emilio Zottel (5AHIF)
 * @since 09.02.2024, Fr.
 */
@RequiredArgsConstructor
public class Waveforms {

    private final PApplet pApplet;

    public void draw(PVector[] positions) {
        if (pApplet.width == pApplet.height) {
            return;
        }

        float stripCenter = abs(pApplet.width - pApplet.height) * 0.25f;
        var offsets = new PVector(stripCenter, stripCenter + (pApplet.width + pApplet.height) * 0.5f);
        var leftWaveform = initWaveformShape(pApplet.createShape());
        var rightWaveform = initWaveformShape(pApplet.createShape());

        if (pApplet.width > pApplet.height) {
            int max = min(pApplet.height, positions.length);

            for (int y = 0; y < max; y++) {
                var waveformX = PVector.mult(positions[y], stripCenter).add(offsets);
                leftWaveform.vertex(waveformX.x, y);
                rightWaveform.vertex(waveformX.y, y);
            }
        } else {
            int max = min(pApplet.width, positions.length);

            for (int x = 0; x < max; x++) {
                var waveformY = PVector.mult(positions[x], stripCenter).add(offsets);
                leftWaveform.vertex(x, waveformY.x);
                rightWaveform.vertex(x, waveformY.y);
            }
        }

        leftWaveform.endShape();
        rightWaveform.endShape();
        pApplet.shape(leftWaveform);
        pApplet.shape(rightWaveform);
    }

    private static PShape initWaveformShape(PShape shape) {
        shape.beginShape();
        shape.stroke(255, 0, 0);
        shape.noFill();
        return shape;
    }

}
