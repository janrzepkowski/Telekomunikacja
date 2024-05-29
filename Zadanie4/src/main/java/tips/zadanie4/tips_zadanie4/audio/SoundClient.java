package tips.zadanie4.tips_zadanie4.audio;

import tips.zadanie4.tips_zadanie4.exceptions.LineException;
import tips.zadanie4.tips_zadanie4.exceptions.NullArgException;
import tips.zadanie4.tips_zadanie4.exceptions.OutputStreamWriteException;
import tips.zadanie4.tips_zadanie4.exceptions.TargetDataLineException;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Target;

public class SoundClient {

    private Client clientThread;
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;

    /*
    @ Constructor: SoundClient()

        @ Parameters:

        * float sampleRate      -> the number of samples per second
        * int sampleSizeInBits  -> the number of bits in each sample
        * int channels          -> the number of supported channels (1 for mono, 2 for stereo and so on)

        @ Returns: Object instance of class SoundClient

        @ Description: This method is used for creating a SoundClient object, and as a result TargetDataLine
        that will be used later, in other methods, for sound transmission between two different hosts (actually this
        class will function as an entry point of sound from microphone, and other one: SoundServer will work as exit
        point for that sound).
     */

    public SoundClient(float sampleRate, int sampleSizeInBits, int channels) throws TargetDataLineException {
        try {
            audioFormat = new AudioFormat(sampleRate, sampleSizeInBits, channels, true, false);
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
        } catch(LineUnavailableException lineUnavailableException) {
            throw new TargetDataLineException("Podana linia komunikacyjna jest niedostępna z powodu ograniczeń zasobów; Powód:" + lineUnavailableException.getMessage());
        }
    }

    /*
        @ Getters: Used for getting audioFormat and targetDataLine in this class' scope.
     */

    private AudioFormat getAudioFormat() {
        return audioFormat;
    }

    private TargetDataLine getTargetDataLine() {
        return targetDataLine;
    }

    /*
        @ Setters: Used for setting audioFormat and targetDataLine outside this class' scope.
     */

    public void setAudioFormat(float sampleRage, int sampleSizeInBits, int channels) {
        this.audioFormat = new AudioFormat(sampleRage, sampleSizeInBits, channels, true, false);
    }

    public void setTargetDataLine() throws TargetDataLineException {
        try {
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
        } catch(LineUnavailableException lineUnavailableException) {
            throw new TargetDataLineException("Podana linia komunikacyjna jest niedostępna z powodu ograniczeń zasobów; Powód:" + lineUnavailableException.getMessage());
        }
    }

    /*
        @ Method: startSoundSending

        @ Parameters:

        * OutputStream outputStream -> this stream is used for writing data, which destination is other host.

        @ Returns: None

        @ Description: This method is responsible for sending data to other host, through stream, that is taken
        from socket.
     */

    public void startSoundSending(OutputStream outputStream) throws TargetDataLineException {
        try {
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            clientThread = new Client(outputStream);
            clientThread.start();
        } catch (LineUnavailableException lineUnavailableException) {
            throw new TargetDataLineException("Podana linia komunikacyjna jest niedostępna z powodu ograniczeń zasobów; Powód:" + lineUnavailableException.getMessage());
        }
    }

    /*
        @ Method: stopSoundSending

        @ Parameters: None

        @ Returns: None

        @ Description: This method is used when communication is about to end, in order to free all of used resources.
     */

    public void stopSoundSending() {
        try {
            clientThread.interrupt();
            targetDataLine.stop();
            targetDataLine.close();
        } catch (NullPointerException nullPointerException) {
            throw new NullArgException("Nie jest możliwe zakończenie połączenia, które się nie rozpoczęło.");
        }
    }

    /*
        @ Thread: Client

        @ Description: This thread will be used for continuous transmission of sound to other host.
     */

    private class Client extends Thread {

        /*
            @ helperFlag: boolean value used for stopping while loop in run method. As a result
            it finishes current thread execution.
         */

        private boolean helperFlag = true;

        /*
            @ outputStream: a stream used for sending audio data to the other side
            of communication, so that it can play it.
         */

        private OutputStream outputStream;

        /*
            @ Constructor: Client

            @ Parameters:

            * OutputStream outputStream -> output stream responsible for
            sending data to the other host.

            @ Returns: Client thread class instance.

            @ Description: This method is used for creating a Client thread class object, which
            will be responsible for getting all audio from microphone and passing it through socket
            to the other host.
         */

        public Client(OutputStream outputStream) {
            this.outputStream = outputStream;
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
            @ Method: run

            @ Parameters: None

            @ Returns: None

            @ Description: This method is executed by thread (so it is something like main for the entire program)
            and it main point is to read audio data from targetDataLine and put it into outputStream of a socket,
            in order to transmit it over network to other host.
         */

        @Override
        public void run() {
            byte[] dataToBeSent = new byte[1024];
            while (helperFlag) {
                try {
                    int count = targetDataLine.read(dataToBeSent, 0, dataToBeSent.length);
                    System.out.println("Sent data size: " + count);
                    outputStream.write(dataToBeSent);
                } catch (IOException ioException) {
                    System.out.println("Debug - Exception occured!");
                }
            }
        }

        /*
            @ Method: interrupt

            @ Parameters: None

            @ Returns: None

            @ Description: This method is used for terminating while loop in run method of this class.
         */

        @Override
        public void interrupt() {
            helperFlag = false;
        }
    }
}
