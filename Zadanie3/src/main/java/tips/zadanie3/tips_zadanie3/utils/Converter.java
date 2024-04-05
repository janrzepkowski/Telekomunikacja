package tips.zadanie3.tips_zadanie3.utils;

import java.nio.charset.StandardCharsets;

public class Converter {

    /*
        @ Attribute: HEX_ARRAY

        @ Use: This array of bytes is used for conversion of bytes to HEX characters.
     */

    public static byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.UTF_8);

    /*
        @ Method: convertHexadecimalToAscii()

        @ Parameters:

        * inputArrayOfHexadecimal -> Byte array containing hexadecimal symbols, that later will
        be converted to bytes -> 2 hexadecimal symbol = 1 ascii code.

        @ Description: This method is used to convert byte array containing hexadecimal symbols (it codes) to
        byte array containing ascii codes.
     */

    public static byte[] convertHexadecimalToAscii(byte[] inputArrayOfHexadecimal) {
        byte[] outputArrayOfAscii = new byte[inputArrayOfHexadecimal.length / 2];
        for (int i = 0; i < inputArrayOfHexadecimal.length; i += 2) {
            outputArrayOfAscii[i / 2] = (byte) ((getHexadecimalDigitValue(inputArrayOfHexadecimal[i]) << 4)
                    | getHexadecimalDigitValue(inputArrayOfHexadecimal[i + 1]));
        }
        return outputArrayOfAscii;
    }

     /*
        @ Method: convertAsciiToHexadecimal()

        @ Parameters:

        * inputArrayOfAscii -> Byte array containing ascii codes that later will be converted to
        hexadecimal symbols.

        @ Description: This method is used for converting byte array containing ascii codes to byte
        array containing hexadecimal symbols.
     */

    public static byte[] convertAsciiToHexadecimal(byte[] inputArrayOfAscii) {
        byte[] outputArrayOfHexadecimal = new byte[inputArrayOfAscii.length * 2];
        for (int i = 0; i < inputArrayOfAscii.length; i++) {
            int charValue = inputArrayOfAscii[i] & 0xFF;
            outputArrayOfHexadecimal[2 * i] = HEX_ARRAY[charValue >> 4];
            outputArrayOfHexadecimal[2 * i + 1] = HEX_ARRAY[charValue & 0x0F];
        }
        return outputArrayOfHexadecimal;
    }

    /*
        @ Method: getHexadecimalDigitValue()

        @ Parameters:

        * hexDigit -> byte containing byte value of hexadecimal symbol

        @ Description: This method is used for getting decimal number from byte representing
        hexadecimal symbol.
     */

    private static int getHexadecimalDigitValue(byte hexDigit) {
        for (int i = 0; i < HEX_ARRAY.length; i++) {
            if (hexDigit == HEX_ARRAY[i]) {
                return i;
            }
        }
        return -1;
    }

    /*
        @ Method: convertStringToEightBitBinary()

        @ Parameters:

        * inputStringByteArray -> byte array containing ascii codes for characters - either from user input or
        from input from a file.

        @ Description: This method is used for converting array of bytes, containing ascii codes to byte
        array that contains bit values for that codes - that means that result byte array is eight times bigger.
     */

    public static byte[] convertStringToEightBitBinary(byte[] inputStringByteArray) {
        byte[] outputBinaryByteArray = new byte[inputStringByteArray.length * 8];
        for (int i = 0; i < inputStringByteArray.length; i++) {
            byte byteValue = inputStringByteArray[i];
            for (int j = 0; j < 8; j++) {
                if ((byteValue & 0x80) != 0) {
                    outputBinaryByteArray[i * 8 + j] = 1;
                } else {
                    outputBinaryByteArray[i * 8 + j] = 0;
                }
                byteValue <<= 1;
            }
        }
        return outputBinaryByteArray;
    }

    /*
        @ Method: convertEightBitBinaryToString()

        @ Parameters:

        * inputBinaryByteArray -> byte array containing in each byte bit value - eight bits represent a
        byte - that is after a conversion - from binary to byte.

        @ Description: This method is used for conversion from byte array containing bits to
        byte array containing ascii codes representing bytes.
     */

    public static byte[] convertEightBitBinaryToString(byte[] inputBinaryByteArray) {
        byte[] outputStringByteArray = new byte[inputBinaryByteArray.length / 8];
        for (int i = 0; i < outputStringByteArray.length; i++) {
            byte byteValue = 0;
            for (int j = 0 ; j < 8; j++) {
                byteValue = (byte) ((byteValue << 1) | inputBinaryByteArray[i * 8 + j]);
            }
            outputStringByteArray[i] = byteValue;
        }
        return outputStringByteArray;
    }

    /*
        @ Method: convertNumValuesToStringCodes()

        @ Parameters:

        * inputNumByteArray -> this method is used for converting array of ascii codes (for decimal digits from 0
        to 9) to their numerical values.

        @ Description: This method is used for converting ascii code byte array to byte array containing numerical
        values - that goal is achieved by subtracting from each ascii code an ascii code for 0.
     */

    public static byte[] convertNumValuesToStringCodes(byte[] inputNumByteArray) {
        byte[] outputStringCodesArray = new byte[inputNumByteArray.length];
        for (int i = 0; i < inputNumByteArray.length; i++) {
            outputStringCodesArray[i] = (byte) (inputNumByteArray[i] + 0x30);
        }
        return outputStringCodesArray;
    }

    /*
        @ Method: convertStringCodesToNumValues()

        @ Parameters:

        * inputStringByteArray -> byte array containing numerical values of decimal digits.

        @ Description: This method is used for converting numerical values to ascii code representing them.
     */

    public static byte[] convertStringCodesToNumValues(byte[] inputStringByteArray) {
        byte[] outputNumValuesArray = new byte[inputStringByteArray.length];
        for (int i = 0; i < inputStringByteArray.length; i++) {
            outputNumValuesArray[i] = (byte) (inputStringByteArray[i] - 0x30);
        }
        return outputNumValuesArray;
    }
}
