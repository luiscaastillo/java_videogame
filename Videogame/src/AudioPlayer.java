import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {
    private Clip clip;

    // Plays background music from the specified file path.
    public void playBackgroundMusic(String filePath) {
        try {
            // Load the audio file and start looping playback.
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filePath));
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Loop forever
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Stops the currently playing audio clip.
    public void stop() {
        if (clip != null && clip.isRunning()) clip.stop();
    }

}
