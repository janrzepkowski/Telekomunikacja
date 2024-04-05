package tips.zadanie3.tips_zadanie3.utils;

import tips.zadanie3.tips_zadanie3.exceptions.IOManagerReadException;
import tips.zadanie3.tips_zadanie3.exceptions.IOManagerWriteException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class IOManager {

    /*
        @ Method: readBytesFromAFile()

        @ Parameters:

        * fileName -> Name of the file, which data should be read from.

        @ Description: This method is used to read data from a file with given fileName.
        Read data is stored as bytes in inputMessageInByteRepresentation.
     */

    public static byte[] readBytesFromAFile(String fileName) throws IOManagerReadException {
        try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
            return fileInputStream.readAllBytes();
        } catch (IOException ioException) {
            throw new IOManagerReadException("Error while reading data from a file: " + fileName);
        }
    }

    /*
        @ Method: writeBytesToAFile()

        @ Parameters:

        * fileName                  -> Name of the file, which data should be written to.
        * arrayOfBytesToWrite       -> Array of bytes to be written to a file with given fileName.

        @ Description: This method is used for writing data to a file with given, as a parameter, name.
     */

    public static void writeBytesToAFile(String fileName, byte[] arrayOfBytesToWrite) throws IOManagerWriteException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            fileOutputStream.write(arrayOfBytesToWrite);
        } catch(IOException ioException) {
            throw new IOManagerWriteException("Error while writing data to a file: " + fileName);
        }
    }
}
