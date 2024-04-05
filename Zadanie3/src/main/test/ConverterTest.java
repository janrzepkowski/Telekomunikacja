import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import tips.zadanie3.tips_zadanie3.utils.Converter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ConverterTest {
    @Test
    public void convertAsciiToHexadecimalTest() {
        byte[] inputAsciiArray = "Some text on the input.".getBytes(StandardCharsets.UTF_8);
        byte[] expectedOutputHexadecimalArray = "536F6D652074657874206F6E2074686520696E7075742E".getBytes(StandardCharsets.UTF_8);
        byte[] actualOutputHexadecimalArray = Converter.convertAsciiToHexadecimal(inputAsciiArray);
        assertArrayEquals(expectedOutputHexadecimalArray, actualOutputHexadecimalArray);
    }

    @Test
    public void convertHexadecimalToAsciiTest() {
        byte[] inputHexadecimalArray = "536F6D652074657874206F6E2074686520696E7075742E".getBytes(StandardCharsets.UTF_8);
        byte[] expectedAsciiArray = "Some text on the input.".getBytes(StandardCharsets.UTF_8);
        byte[] actualOutputAscii = Converter.convertHexadecimalToAscii(inputHexadecimalArray);
        assertArrayEquals(expectedAsciiArray, actualOutputAscii);
    }

    @Test
    public void convertStringToEightBitBinaryTest() {
        byte[] inputString = "Some text to the tests.".getBytes(StandardCharsets.UTF_8);
        byte[] expectedOutputBinary = "0101001101101111011011010110010100100000011101000110010101111000011101000010000001110100011011110010000001110100011010000110010100100000011101000110010101110011011101000111001100101110".getBytes(StandardCharsets.UTF_8);
        byte[] actualOutputBinary = Converter.convertStringToEightBitBinary(inputString);
        assertArrayEquals(Converter.convertStringCodesToNumValues(expectedOutputBinary), actualOutputBinary);
    }

    @Test
    public void convertEightBitBinaryToStringTest() {
        byte[] inputBinary = "0101001101101111011011010110010100100000011101000110010101111000011101000010000001110100011011110010000001110100011010000110010100100000011101000110010101110011011101000111001100101110".getBytes(StandardCharsets.UTF_8);
        byte[] expectedOutputString = "Some text to the tests.".getBytes(StandardCharsets.UTF_8);
        byte[] actualOutputString = Converter.convertEightBitBinaryToString(Converter.convertStringCodesToNumValues(inputBinary));
        assertArrayEquals(expectedOutputString, actualOutputString);
    }

    @Test
    public void convertAsciiCodesToNumValuesTest() {
        byte[] inputAsciiCodeArray = "0010111011001001".getBytes(StandardCharsets.UTF_8);
        byte[] expectedNumValuesOutputArray = {0, 0, 1, 0, 1, 1, 1,  0, 1, 1, 0, 0, 1, 0, 0, 1};
        byte[] actualNumValuesOutputArray = Converter.convertStringCodesToNumValues(inputAsciiCodeArray);
        assertArrayEquals(expectedNumValuesOutputArray, actualNumValuesOutputArray);
    }

    @Test
    public void convertNumValuesToAsciiCodesTest() {
        byte[] inputNumValuesArray = {0, 0, 1, 0, 1, 1, 1,  0, 1, 1, 0, 0, 1, 0, 0, 1};
        byte[] expectedAsciiCodeOutputArray = "0010111011001001".getBytes(StandardCharsets.UTF_8);
        byte[] actualAsciiCodeOutputArray = Converter.convertNumValuesToStringCodes(inputNumValuesArray);
        assertArrayEquals(expectedAsciiCodeOutputArray, actualAsciiCodeOutputArray);
    }
}
