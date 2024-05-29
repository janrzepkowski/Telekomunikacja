package tips.zadanie4.tips_zadanie4.audio;

import tips.zadanie4.tips_zadanie4.exceptions.LineException;
import tips.zadanie4.tips_zadanie4.exceptions.LineWithGivenParametersNotSupported;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class RecordAudioManager {

    /*
        @ Class responsible for recording user input sound.
     */

    private final float sampleRate;
    private final int sameSizeInBits;
    private final int numberOfChannels;

    private AudioFormat audioFormat;
    private DataLine.Info dataLineInfo;
    private TargetDataLine targetDataLine;

    /*
        @ Method: RecordAudioManager

        @ Parameters:

        * sampleRate        -> amount of samples per 1 second.
        * sampleSizeInBits  -> amount of bits per sample.
        * numberOfChannels  -> number of channels in recording (1 for mono, 2 for stereo, and so on).

        @ Description:
     */

    public RecordAudioManager(float sampleRate, int sameSizeInBits, int numberOfChannels) throws LineException {
        this.sampleRate = sampleRate;
        this.sameSizeInBits = sameSizeInBits;
        this.numberOfChannels = numberOfChannels;
        try {
            audioFormat = new AudioFormat(sampleRate, sameSizeInBits, numberOfChannels, true, false);
            dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
        } catch (LineUnavailableException lineUnavailableException) {
            throw new LineException("Linia komunikacyjna jest zajęta.");
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new LineWithGivenParametersNotSupported("Linia komunikacyjna z takimi parametrami nie jest wspierana przez kartę dźwiękową.");
        }
    }

    /*
        @ Getters:
     */

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    public TargetDataLine getTargetDataLine() {
        return targetDataLine;
    }

    /*
        @ Method: recordSound()

        @ Parameters:

        * fileName -> name of the file, where data describing the audio
        should be written to.

        @ Description:  This method is used to record sound, and that is achieved by
        creating an audioRecorder thread and calling start() method of it.
     */

    public void recordSound(String fileName) {
        AudioRecorder audioRecorder = new AudioRecorder(fileName);
        audioRecorder.start();
    }

    /*
        @ Method: closeTargetDataLine()

        @ Parameters: None

        @ Description: This method is used for stopping and closing targetDataLine,
        effectively cutting recording.
     */

    public void closeTargetDataLine() {
        targetDataLine.stop();
        targetDataLine.close();
    }

    /*
        @ Thread used to record sound, since doing in it the main thread
        would block GUI, and recording would not be possible to be stopped.
     */

    class AudioRecorder extends Thread {

        /*
            @ fileName -> name of a file, where the recorded sound should be written to.
         */

        String fileName;

        /*
            @ Constructor for creating a thread object.
         */

        public AudioRecorder(String fileName) {
            this.fileName = fileName;
        }

        /*
            @ Method: start()

            @ Parameters: None

            @ Description: This method starts by default run method
            of this thread.
         */

        @Override
        public void start() {
            super.start();
        }

        /*
            @ Method: run()

            @ Parameters: None

            @ Description: This method is responsible for recording sound. In order
            to stop recording, external method from UserActionController is used.
         */

        @Override
        public void run() {
            AudioFileFormat.Type fileFormat = AudioFileFormat.Type.WAVE;
            File recordedFile = new File(fileName);

            try {
                targetDataLine.open(audioFormat);
                targetDataLine.start();
                AudioSystem.write(new AudioInputStream(targetDataLine), fileFormat, recordedFile);
            } catch (LineUnavailableException | IOException lineUnavailableException) {
                lineUnavailableException.printStackTrace();
            }

        }
    }
}
