package oscilloscope.service;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;
import processing.sound.FFT;

import static processing.core.PApplet.abs;

/**
 * @author Emilio Zottel (5AHIF)
 * @since 09.02.2024, Fr.
 */
public record Waveforms(PApplet pApplet) {

    private static final boolean PHASE_SHIFT = false;

    private static PShape initWaveformShape(PShape shape) {
        shape.beginShape();
        shape.stroke(255, 0, 0);
        shape.noFill();
        return shape;
    }

    public void draw(PVector[] positions, FFT fft, int sampleRate) {
        if (pApplet.width == pApplet.height) {
            return;
        }

        float halfStripThickness = abs(pApplet.width - pApplet.height) * 0.25f;
        var offsets = new PVector(halfStripThickness, halfStripThickness + (pApplet.width + pApplet.height) * 0.5f);
        var leftWaveform = initWaveformShape(pApplet.createShape());
        var rightWaveform = initWaveformShape(pApplet.createShape());
        int phaseMissingFrames = 0;

        if (PHASE_SHIFT) {
            var spectrum = fft.analyze();
            double phaseSeconds = 1.0 / findStrongestFrequency(spectrum);
            int phaseFrames = (int) (sampleRate * phaseSeconds);
            int phaseStartingFrames = positions.length % phaseFrames;
            phaseMissingFrames = phaseFrames - phaseStartingFrames;
        }

        if (pApplet.width > pApplet.height) {
            for (int i = 0; i < positions.length - phaseMissingFrames; i++) {
                var waveformX = PVector.mult(positions[i], halfStripThickness).add(offsets);
                float y = (float) pApplet.height * (i + phaseMissingFrames) / positions.length;
                leftWaveform.vertex(waveformX.x, y);
                rightWaveform.vertex(waveformX.y, y);
            }
        } else {
            for (int i = 0; i < positions.length - phaseMissingFrames; i++) {
                float x = (float) pApplet.width * (i + phaseMissingFrames) / positions.length;
                var waveformY = PVector.mult(positions[i], -halfStripThickness).add(offsets);
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
        float strongestAmplitude = 0;
        int strongestFrequency = 1;

        for (int i = 1; i < spectrum.length; i++) {
            if (spectrum[i] > strongestAmplitude) {
                strongestAmplitude = spectrum[i];
                strongestFrequency = i;
            }
        }

        return strongestFrequency;
    }

}
