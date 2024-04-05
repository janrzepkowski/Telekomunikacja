import org.junit.jupiter.api.Test;
import tips.zadanie3.tips_zadanie3.model.HuffmanCoding;
import tips.zadanie3.tips_zadanie3.utils.Converter;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HuffmanCodingTest {

    @Test
    public void HuffmanCodingTest() {
        byte[] textToEncode = "............AAAAAAAAAAA".getBytes(StandardCharsets.UTF_8);
        HuffmanCoding huffmanCode = new HuffmanCoding();
        Map<Short, Integer> occurrenceMap = huffmanCode.findOccurrenceMap(textToEncode);
        byte[] encodedMessage = huffmanCode.encodeWithHuffmanEncoding(textToEncode, occurrenceMap);
        System.out.println(new String(Converter.convertAsciiToHexadecimal(encodedMessage), StandardCharsets.US_ASCII));
        byte[] decodedMessage = huffmanCode.decodeWithHuffmanEncoding(encodedMessage, occurrenceMap);
        System.out.println(new String(decodedMessage, StandardCharsets.UTF_8));
    }

    @Test
    public void HuffmanCodingTestWithBinary() {
        byte[] textToEncode = Converter.convertHexadecimalToAscii("FFD8FFE00010".getBytes(StandardCharsets.US_ASCII));
        HuffmanCoding huffmanCode = new HuffmanCoding();
        Map<Short, Integer> occurrenceMap = huffmanCode.findOccurrenceMap(textToEncode);
        byte[] encodedMessage = huffmanCode.encodeWithHuffmanEncoding(textToEncode, occurrenceMap);
        System.out.println(new String(Converter.convertAsciiToHexadecimal(encodedMessage), StandardCharsets.US_ASCII));
        byte[] decodedMessage = huffmanCode.decodeWithHuffmanEncoding(encodedMessage, occurrenceMap);
        System.out.println(new String(Converter.convertAsciiToHexadecimal(decodedMessage), StandardCharsets.US_ASCII));

    }
}
