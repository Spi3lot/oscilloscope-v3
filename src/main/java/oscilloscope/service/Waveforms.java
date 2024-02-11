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

    private static final boolean PHASE_MATCH = false;

    private static PShape initWaveformShape(PShape shape) {
        shape.beginShape();
        shape.stroke(255, 0, 0);
        shape.noFill();
        return shape;
    }

    private static double findStrongestFrequency(float[] spectrum, int sampleRate) {
        return (double) sampleRate * findStrongestFrequencyIndex(spectrum) / spectrum.length;
    }

    private static int findStrongestFrequencyIndex(float[] spectrum) {
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

    public void draw(PVector[] positions, FFT fft, int sampleRate) {
        if (pApplet.width == pApplet.height) {
            return;
        }

        float halfStripThickness = abs(pApplet.width - pApplet.height) * 0.25f;
        var offsets = new PVector(halfStripThickness, halfStripThickness + (pApplet.width + pApplet.height) * 0.5f);
        var leftWaveform = initWaveformShape(pApplet.createShape());
        var rightWaveform = initWaveformShape(pApplet.createShape());
        int phaseOffsetFrames = 0;

        if (PHASE_MATCH) {
            var spectrum = fft.analyze();
            double phaseSeconds = 1.0 / findStrongestFrequency(spectrum, sampleRate);
            int phaseFrames = (int) (sampleRate * phaseSeconds);
            phaseOffsetFrames = positions.length % phaseFrames;
        }

        if (pApplet.width > pApplet.height) {
            for (int i = 0; i < positions.length - phaseOffsetFrames; i++) {
                var waveformX = PVector.mult(positions[i], halfStripThickness).add(offsets);
                float y = (float) pApplet.height * (i + phaseOffsetFrames) / positions.length;
                leftWaveform.vertex(waveformX.x, y);
                rightWaveform.vertex(waveformX.y, y);
            }
        } else {
            for (int i = 0; i < positions.length - phaseOffsetFrames; i++) {
                float x = (float) pApplet.width * (i + phaseOffsetFrames) / positions.length;
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

}
