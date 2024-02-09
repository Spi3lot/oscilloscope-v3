package oscilloscope.service;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

import static processing.core.PApplet.abs;
import static processing.core.PApplet.min;

/**
 * @author Emilio Zottel (5AHIF)
 * @since 09.02.2024, Fr.
 */
public record Waveforms(PApplet pApplet) {

    private static PShape initWaveformShape(PShape shape) {
        shape.beginShape();
        shape.stroke(255, 0, 0);
        shape.noFill();
        return shape;
    }

    public void draw(PVector[] positions, float[] spectrum, int sampleRate) {
        if (pApplet.width == pApplet.height) {
            return;
        }

        float stripCenter = abs(pApplet.width - pApplet.height) * 0.25f;
        var offsets = new PVector(stripCenter, stripCenter + (pApplet.width + pApplet.height) * 0.5f);
        var leftWaveform = initWaveformShape(pApplet.createShape());
        var rightWaveform = initWaveformShape(pApplet.createShape());
        int strongestFrequency = findStrongestFrequency(spectrum);
        int frameCount = sampleRate / strongestFrequency;
        int vertexCount = min(frameCount, positions.length);

        if (pApplet.width > pApplet.height) {
            for (int i = 0; i < vertexCount; i++) {
                var waveformX = PVector.mult(positions[i], stripCenter).add(offsets);
                float y = (float) (frameCount * i) / vertexCount;
                leftWaveform.vertex(waveformX.x, y);
                rightWaveform.vertex(waveformX.y, y);
            }
        } else {
            for (int i = 0; i < vertexCount; i++) {
                float x = (float) (frameCount * i) / vertexCount;
                var waveformY = PVector.mult(positions[i], stripCenter).add(offsets);
                leftWaveform.vertex(x, waveformY.x);
                rightWaveform.vertex(x, waveformY.y);
            }
        }

        leftWaveform.endShape();
        rightWaveform.endShape();
        pApplet.shape(leftWaveform);
        pApplet.shape(rightWaveform);
    }

    private int findStrongestFrequency(float[] spectrum) {
        int strongestFrequency = 0;
        float strongestAmplitude = 0;

        for (int i = 0; i < spectrum.length; i++) {
            if (spectrum[i] > strongestAmplitude) {
                strongestAmplitude = spectrum[i];
                strongestFrequency = i;
            }
        }

        return strongestFrequency;
    }

}
