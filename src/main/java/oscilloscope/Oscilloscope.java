package oscilloscope;

import oscilloscope.service.Waveforms;
import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.PShader;
import processing.sound.AudioSample;
import processing.sound.SoundFile;

import java.io.File;

/**
 * @author Emilio Zottel (5AHIF)
 * @since 08.02.2024, Do.
 */
public class Oscilloscope extends PApplet {

    public static final float SQRT_2 = sqrt(2);
    private static final int MAX_DRAWN_SAMPLES = 2048;
    private static final boolean DRAW_WAVEFORMS = true;
    private final Waveforms waveforms = new Waveforms(this);
    private final PVector dimensions = new PVector(800, 600);
    private int smallerDimension;

    private AudioSample audioSample;
    private PVector lastVertex = new PVector(0, 0);
    private PShader oscilloscopeShader;

    public static void main(String[] args) {
        PApplet.main(Oscilloscope.class);
    }

    /**
     * P3D is required for the vertices to be sent to the GPU,
     * or at least the program runs at approximately 3 FPS
     * on my machine when using P2D in fullscreen mode,
     * in contrast to ~400 FPS with P3D.
     */
    @Override
    public void settings() {
        size((int) dimensions.x, (int) dimensions.y, P2D);
    }

    @Override
    public void setup() {
        oscilloscopeShader = loadShader("src/main/resources/shaders/oscilloscope.frag", "src/main/resources/shaders/oscilloscope.vert");
        surface.setResizable(false);
        frameRate(1000);
        fill(0, 200);
        selectInput("Select an audio file", "selectSoundFile");
    }

    @Override
    public void draw() {
        if (audioSample == null) {
            return;
        }

        background(0);
        shader(oscilloscopeShader);
        int currentFrame = audioSample.positionFrame();
        var positions = retrievePreviousPositions(currentFrame);

        for (var position : positions) {
            var vertex = position.copy();
            vertex.y = -vertex.y;
            vertex.mult(smallerDimension).add(dimensions).mult(0.5f);

            float luminance = calcLuminance(vertex.dist(lastVertex));
            stroke(0, 255, 0, luminance * 255);
            line(vertex.x, vertex.y, lastVertex.x, lastVertex.y);
            lastVertex = vertex;
        }

        if (DRAW_WAVEFORMS) {
            waveforms.draw(positions);
        }
    }

    private PVector[] retrievePreviousPositions(int currentFrame) {
        int length = min(currentFrame, MAX_DRAWN_SAMPLES);
        int start = 1 + currentFrame - length;
        var positions = new PVector[length];

        for (int i = 0; i < length; i++) {
            int frame = start + i;

            positions[i] = new PVector(
                    audioSample.read(frame, 0),
                    audioSample.read(frame, audioSample.channels() - 1)
            );
        }

        return positions;
    }

    private float calcLuminance(float distance) {
        float t = distance / (smallerDimension * SQRT_2);
        return pow(1 - t, 10);
    }

    @Override
    public void windowResized() {
        dimensions.set(width, height);
        smallerDimension = (int) min(dimensions.x, dimensions.y);
    }

    public void selectSoundFile(File selection) {
        if (selection == null) {
            exit();
        } else {
            audioSample = new SoundFile(this, selection.getAbsolutePath());
            audioSample.play();
        }
    }

}
