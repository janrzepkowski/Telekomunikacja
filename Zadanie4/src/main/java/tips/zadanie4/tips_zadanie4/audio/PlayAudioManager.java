package tips.zadanie4.tips_zadanie4.audio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class PlayAudioManager {

    /*
        @ Class responsible for playing recorded sound.
     */

    /*
        @ clipToPlay -> object holding a clip from the audioSystem, which later will
        be played.
     */

    private Clip clipToPlay;

    /*
        @ Variable used for holding audioPlayer thread object.
     */

    private AudioPlayer audioPlayer;

    /*
        @ Method: playRecordedSound()

        @ Parameters:

        * fileName -> name of a file, which data describing the audio should be read from.

        @ Description: This method is used to play recorded sound, and that is achieved by
        creating audioPlayer thread which works in the background.
     */

    public void playRecordedSound(String fileName) {
        audioPlayer = new AudioPlayer(fileName);
        audioPlayer.start();
    }

    /*
        @ Method:

        @ Parameters:

        @ Description: This method is used to cut played recording before the end of it, and that is achieved by
        call audioPlayer thread interrupt method.
     */

    public void closePlayedClip() {
        audioPlayer.interrupt();
    }

    /*
        @ Thread used to play sound - in order to make the app usable when
        sound is played and not block it.
     */

    class AudioPlayer extends Thread {

        /*
            @ fileName -> path to a file, which clip should be make of from.
         */

        private String fileName;

        /*
            @ recordedFile -> file object representing file with audio.
         */

        private File recordedFile;

        /*
            @ Constructor used for creating a thread object.
         */

        public AudioPlayer(String fileName) {
            this.fileName = fileName;
            recordedFile = new File(fileName);
        }

        /*
        @ Method: start()

        @ Parameters: None

        @ Description: When this method is run it calls run() method by default.
     */

        @Override
        public void start() {
            super.start();
        }

        /*
        @ Method: run()

        @ Parameters: None

        @ Description: This method is responsible for playing audio, and when
        playing is finished is closes all required resources.
     */

        @Override
        public void run() {
            try {
                clipToPlay = AudioSystem.getClip();
                clipToPlay.open(AudioSystem.getAudioInputStream(recordedFile));
                clipToPlay.start();
                while(clipToPlay.getFramePosition() <= clipToPlay.getFrameLength() && clipToPlay.isOpen()) {

                }
                clipToPlay.stop();
                clipToPlay.close();
            } catch (UnsupportedAudioFileException | LineUnavailableException |
                     IOException exc) {
                exc.printStackTrace();
            }
        }

        /*
        @ Method: interrupt()

        @ Parameters: None

        @ Description: This method is used to cut
        playing audio before the end of the recorded audio.
     */

        @Override
        public void interrupt() {
            clipToPlay.stop();
            clipToPlay.close();
        }
    }
}
