package oscilloscope;

import processing.core.PApplet;
import processing.core.PShape;
import processing.opengl.PShader;
import processing.sound.AudioSample;
import processing.sound.SoundFile;

import java.io.File;

/**
 * @author Emilio Zottel (5AHIF)
 * @since 08.02.2024, Do.
 */
public class Oscilloscope extends PApplet {

    private static final int DRAWN_SAMPLE_COUNT = 2048;
    private PShader oscilloscopeShader;
    private AudioSample audioSample;

    public static void main(String[] args) {
        PApplet.main(Oscilloscope.class);
    }

    @Override
    public void settings() {
        size(640, 360, P2D);
    }

    @Override
    public void setup() {
        oscilloscopeShader = loadShader("src/main/resources/shaders/oscilloscope.frag", "src/main/resources/shaders/oscilloscope.vert");
        selectInput("Select an audio file", "selectSoundFile");
        surface.pauseThread();
    }

    @Override
    public void draw() {
        shader(oscilloscopeShader, LINES);

        var lineShape = createShape();
        lineShape.beginShape();
        lineShape.stroke(0, 255, 0);
        lineShape.noFill();
        int currentFrame = audioSample.positionFrame();

        for (int offset = 0; offset < DRAWN_SAMPLE_COUNT; offset++) {
            int frame = currentFrame + offset;
            float x = audioSample.read(frame, 0);
            float y = audioSample.read(frame, audioSample.channels() - 1);
            float halfSmallerDimension = min(width, height) / 2f;
            lineShape.vertex(width / 2f + x * halfSmallerDimension, height / 2f + y * halfSmallerDimension);
        }

        lineShape.endShape();
        shape(lineShape);
    }

    public void selectSoundFile(File selection) {
        if (selection == null) {
            exit();
        } else {
            audioSample = new SoundFile(this, selection.getAbsolutePath());
            audioSample.play();
            surface.resumeThread();
        }
    }

}
