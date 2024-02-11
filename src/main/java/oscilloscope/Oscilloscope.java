package oscilloscope;

import oscilloscope.service.FrameRetriever;
import oscilloscope.service.Shading;
import oscilloscope.service.Waveforms;
import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.PShader;
import processing.sound.AudioSample;
import processing.sound.FFT;
import processing.sound.SoundFile;

import java.io.File;

/**
 * @author Emilio Zottel (5AHIF)
 * @since 08.02.2024, Do.
 */
public class Oscilloscope extends PApplet {

    private static final int MAX_DRAWN_SAMPLES = 4096;
    private static final boolean DRAW_WAVEFORMS = true;
    private final Shading shading = new Shading(this);
    private final Waveforms waveforms = new Waveforms(this);
    private final PVector lastVertex = new PVector();
    private PShader oscilloscopeShader;
    private AudioSample audioSample;
    private FrameRetriever frameRetriever;
    private FFT fft;

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
        size(800, 600, P3D);
    }

    @Override
    public void setup() {
        frameRate(1000);
        fill(0, 200);
        selectSoundFile();
        fft = new FFT(this, MAX_DRAWN_SAMPLES);
        oscilloscopeShader = loadShader("src/main/resources/shaders/oscilloscope.frag", "src/main/resources/shaders/oscilloscope.vert");
    }

    @Override
    public void draw() {
        if (audioSample == null) {
            return;
        }

        background(0);
        shader(oscilloscopeShader);
        int currentFrame = audioSample.positionFrame();
        var positions = frameRetriever.retrieveRecent(currentFrame, MAX_DRAWN_SAMPLES);
        var dimensions = new PVector(width, height);
        int minDimension = min(width, height);

        for (var position : positions) {
            var vertex = position.copy();
            vertex.y = -vertex.y;
            vertex.mult(minDimension).add(dimensions).mult(0.5f);

            float luminance = shading.calcLuminance(vertex.dist(lastVertex));
            stroke(0, 255, 0, luminance * 255);
            line(lastVertex.x, lastVertex.y, vertex.x, vertex.y);
            lastVertex.set(vertex);
        }

        if (DRAW_WAVEFORMS) {
            waveforms.draw(positions, fft, audioSample.sampleRate());
        }
    }

    private void selectSoundFile() {
        selectInput("Select an audio file", "selectSoundFile");
    }

    public void selectSoundFile(File selection) {
        if (selection == null) {
            selectSoundFile();
        } else {
            audioSample = new SoundFile(this, selection.getAbsolutePath());
            frameRetriever = new FrameRetriever(audioSample);
            fft.input(audioSample);
            lastVertex.set(0, 0);
            audioSample.play();
        }
    }

}
