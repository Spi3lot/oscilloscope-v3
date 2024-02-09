package oscilloscope.service;

import processing.core.PVector;
import processing.sound.AudioSample;

import static processing.core.PApplet.min;

/**
 * @author Emilio Zottel (5AHIF)
 * @since 09.02.2024, Fr.
 */
public record FrameRetriever(AudioSample audioSample) {

    public PVector[] retrieveRecent(int currentFrame, int length) {
        int frameCount = min(currentFrame, length);
        int start = 1 + currentFrame - frameCount;
        var positions = new PVector[frameCount];

        for (int i = 0; i < frameCount; i++) {
            int frame = start + i;

            positions[i] = new PVector(
                    audioSample.read(frame, 0),
                    audioSample.read(frame, audioSample.channels() - 1)
            );
        }

        return positions;
    }

}
