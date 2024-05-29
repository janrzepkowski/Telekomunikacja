package tips.zadanie4.tips_zadanie4.audio;

import tips.zadanie4.tips_zadanie4.exceptions.NullArgException;
import tips.zadanie4.tips_zadanie4.exceptions.SourceDataLineException;
import tips.zadanie4.tips_zadanie4.exceptions.TargetDataLineException;

import javax.sound.sampled.*;
import javax.xml.transform.Source;
import java.io.IOException;
import java.io.InputStream;

public class SoundServer {
    private Server serverThread;
    private AudioFormat audioFormat;
    private SourceDataLine sourceDataLine;

    /*
        @ Constructor: SoundServer()

        @ Parameters:

        * float sampleRate      -> the number of samples per second
        * int sampleSizeInBits  -> the number of bits in each sample
        * int channels          -> the number of supported channels (1 for mono, 2 for stereo and so on)

        @ Returns: Object instance of class SoundServer

        @ Description: This method is used for creating a SoundServer object, and as a result SourceDataLine
        that will be used later, in other methods, for sound transmission between two different hosts (actually this
        class will function as exit point of sound from microphone, and other one: SoundClient will work as entry
        point for that sound).
     */

    public SoundServer(float sampleRate, int sampleSizeInBits, int channels) throws SourceDataLineException {
        try {
            audioFormat = new AudioFormat(sampleRate, sampleSizeInBits, channels, true, false);
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
        } catch (LineUnavailableException lineUnavailableException) {
            throw new SourceDataLineException("Podana linia komunikacyjna jest niedostępna z powodu ograniczeń zasobów; Powód:" + lineUnavailableException.getMessage());
        }
    }

    /*
        @ Getters: Used for getting audioFormat and sourceDataLine in current class' scope.
     */

    private AudioFormat getAudioFormat() {
        return audioFormat;
    }

    private SourceDataLine getSourceDataLine() {
        return sourceDataLine;
    }

    /*
        @ Setters: Used for changing parameters from outside this class' scope.
     */

    public void setAudioFormat(float sampleRate, int sampleSizeInBits, int channels) {
        this.audioFormat = new AudioFormat(sampleRate, sampleSizeInBits, channels, true, false);
    }

    public void setSourceDataLine() throws SourceDataLineException {
        try {
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
            this.sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
        } catch(LineUnavailableException lineUnavailableException) {
            throw new SourceDataLineException("Podana linia komunikacyjna jest niedostępna z powodu ograniczeń zasobów; Powód:" + lineUnavailableException.getMessage());
        }
    }

    /*
        @ Method: startSoundReceiving

        @ Parameters:

        * InputStream inputStream -> stream used for connection with other host - data is received from this stream.

        @ Returns: None

        @ Description: This method is used to receive sound from other host - constantly, until thread gets interrupted
        which happens when the communication is about to end.
     */

    public void startSoundReceiving(InputStream inputStream) throws SourceDataLineException {
        try {
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();
            serverThread = new Server(inputStream);
            serverThread.start();
        } catch (LineUnavailableException lineUnavailableException) {
            throw new SourceDataLineException("Podana linia komunikacyjna jest niedostępna z powodu ograniczeń zasobów; Powód:" + lineUnavailableException.getMessage());
        }
    }

    /*
        @ Method: stopSoundReceiving

        @ Parameters: None

        @ Returns: None

        @ Description: This method is used when ending communication in order to free all of used resources.
     */

    public void stopSoundReceiving() {
        try {
            serverThread.interrupt();
            sourceDataLine.stop();
            sourceDataLine.close();
        } catch (NullPointerException nullPointerException) {
            throw new NullArgException("Nie jest możliwe zakończenie połączenia, które się nie rozpoczęło.");
        }
    }

    /*
        @ Thread: Server

        @ Description: This thread will be responsible for constant receiving of sound from other host.
     */

    private class Server extends Thread {

        /*
            @ helperFlag: boolean value used for stopping while loop in run method. As a result
            it finishes current thread execution.
         */

        boolean helperFlag = true;

        /*
            @ inputStream: a stream used for reading audio data and putting it in sourceDataLine
            in order to make user hear, what the other side records.
         */

        private InputStream inputStream;

        /*
            @ Constructor: Server

            @ Parameters:

            * InputStream inputStream -> input stream responsible for
            reading data sent by other host.

            @ Returns: Server thread class instance.

            @ Description: This method is used for creating a Server thread class object, which
            will be responsible for getting all audio from socket and passing it through to speakers /
            headphones.
         */

        public Server(InputStream inputStream) {
            this.inputStream = inputStream;
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

            @ Description: This method is main method executed by this thread, and it is all about
            getting audio data from socket (through inputStream) and passing it to sourceDataLine
            in order to play it.
         */

        @Override
        public void run() {
            byte[] receivedData = new byte[1024];
            while (helperFlag) {
                try {
                    int count = inputStream.read(receivedData, 0, receivedData.length);
                    System.out.println("Received data size: " + count);
                    sourceDataLine.write(receivedData, 0 , receivedData.length);
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
