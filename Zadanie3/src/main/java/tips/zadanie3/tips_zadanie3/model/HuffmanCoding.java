package tips.zadanie3.tips_zadanie3.model;

import tips.zadanie3.tips_zadanie3.utils.Converter;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class HuffmanCoding {

    /*
        @ Method: encodeWithHuffmanEncoding()

        @ Parameters:

        * notEncodedInputArray  -> byte array containing message to encode.
        * occurrenceMap         -> map containing mappings: character -> number of its occurrences in
        the encoded text.

        @ Description: This method is used for encoding message with Huffman encoding. This goal is
        achieved by:

        * building binary tree, based on number of characters and their occurrences in the encoded
        text.

        * then encoding map is found - which maps character to string containing sequence of moves to
        go from the root node of built binary tree to a leaf, containing a certain char.

        * then for every ascii code in the array - we get the string being encoding of that ascii code - effectively
        making its size smaller.

        * at the end of the process we append zeros at the end - in order to make entire string divisible by 8.

        * then we convert the string to byte array, and the num values, and then to ascii codes, further lowering used
        amount of data by 8 times.
     */

    public byte[] encodeWithHuffmanEncoding(byte[] notEncodedInputArray, Map<Short, Integer> occurrenceMap) {
        // Find occurrence map -> mapping char value to number of this chars in the text. (That happens outside
        // this method.)
        // Build binary tree based on found occurrence map.
        Node rootNode = buildHuffmanTree(occurrenceMap);
        // Finding encoding map -> structure that maps character to the exact string encoding it (
        // string is ofc series of 0 and 1 - so it is binary, since it will be easier to navigate the tree.
        Map<Short, String> encodingMap = findEncodingMap(occurrenceMap, rootNode);
        // For every character in input byte array we find exact encoding and append it to stringBuilder.
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < notEncodedInputArray.length; i++) {
            stringBuilder.append(encodingMap.get((short) notEncodedInputArray[i]));
        }
        // Adding some zeros at the end to make entire string length divisible by 8.
        byte numberOfAppendedZeros = 0;
        if ((stringBuilder.toString().length() & 7) != 0) {
            while ((stringBuilder.toString().length() & 7) != 0) {
                stringBuilder.append("0");
                numberOfAppendedZeros++;
            }
        }
        // To make it work for every length of string in stringBuilder we have to add additional byte, containing
        // information about appended zeros.
        // TODO: Do not forget about this added byte when decoding.
        for (int i = 0; i < 8; i++) {
            if ((numberOfAppendedZeros & 0x80) != 0) {
                stringBuilder.append("1");
            } else {
                stringBuilder.append("0");
            }
            numberOfAppendedZeros <<= 1;
        }
        // Since stringBuilder contains sequence of 0 and 1 we have to transform it to ascii code representation
        // to reduce the amount of data transferred.
        return Converter.convertEightBitBinaryToString(Converter.convertStringCodesToNumValues(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII)));
    }

    /*
        @ Method: decodeWithHuffmanEncoding()

        @ Parameters:

        * encodedInputArray     -> byte array containing characters from previous encoding with Huffman encoding
        * occurrenceMap         -> map containing mappings: character -> number of times it appears in the
        original text.

        @ Description:
     */

    public byte[] decodeWithHuffmanEncoding(byte[] encodedInputArray, Map<Short, Integer> occurrenceMap) {
        List<Byte> decodedInputList = new ArrayList<>();
        // Transforming encodedInputArray into byte array containing binary code - that is 0 and 1.
        byte[] binaryInputArray = Converter.convertStringToEightBitBinary(encodedInputArray);
        // We need to see how many zeros were appended while encoding the message and remove them.
        byte numberOfAppendedZeros = 0;
        for (int i = 0; i < 8; i++) {
            numberOfAppendedZeros += (binaryInputArray[binaryInputArray.length - (i + 1)] << i);
        }
        byte[] actualEncodedArray = new byte[binaryInputArray.length - (numberOfAppendedZeros + 8)];
        System.arraycopy(binaryInputArray, 0, actualEncodedArray, 0, actualEncodedArray.length);
        // Build binary tree based on transferred occurrenceMap, so that decoding is possible.
        Node rootNodeHolder = buildHuffmanTree(occurrenceMap);
        Node rootNode = rootNodeHolder;
        // We go with next values in the array, that indicate direction to reach certain encode character.
        for (int i = 0; i < actualEncodedArray.length; i++) {
            if (actualEncodedArray[i] == 0) {
                rootNode = rootNode.getLeftChildNode();
            } else {
                rootNode = rootNode.getRightChildNode();
            }
            if (rootNode.isLeaf()) {
                decodedInputList.add((byte) rootNode.getCharacter());
                rootNode = rootNodeHolder;
            }
        }
        // We convert used list to array.
        byte[] decodedInputArray = new byte[decodedInputList.size()];
        for (int i = 0; i < decodedInputArray.length; i++) {
            decodedInputArray[i] = decodedInputList.get(i);
        }
        return decodedInputArray;
    }

    /*
        @ Method: buildHuffmanTree()

        @ Parameters:

        * occurrenceMap -> map containing mappings: character -> number of times it appears in the
        original text.

        @ Description: This method is used for building Huffman binary tree, based on occurrence map.
        For every character we create a node, with parent, left child and right child as null-s, then adding them
        to priority queue which is sorted base of characterImportance, and at the end building it by creating a new node
        and appending two nodes with the smallest importance - the one with the lower importance as the left child.
     */

    private Node buildHuffmanTree(Map<Short, Integer> occurrenceMap) {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
        // For every mapping we have to create a node and put it inside priorityQueue.
        // Since Node implements Comparable<Node>, collection sorts itself when adding next node. (Ordering according
        // to natural ordering - by compareTo method).
        Node newNode;
        Object[] arrayOfKeys = occurrenceMap.keySet().toArray();
        for (int i = 0; i < occurrenceMap.size(); i++) {
            newNode = new Node((short) arrayOfKeys[i], occurrenceMap.get((short) arrayOfKeys[i]));
            priorityQueue.add(newNode);
        }
        while (priorityQueue.size() != 1) {
            Node leftChildNode = priorityQueue.poll();
            Node rightChildNode = priorityQueue.poll();
            Node newParentNode = new Node((short) 128, leftChildNode.getImportanceValue() + rightChildNode.getImportanceValue(),
                    leftChildNode, rightChildNode);
            leftChildNode.setParentNode(newParentNode);
            rightChildNode.setParentNode(newParentNode);
            priorityQueue.add(newParentNode);
        }
        return priorityQueue.poll();
    }

    /*
        @ Method: findOccurrenceMap()

        @ Parameters:

        * notEncodedInputArray -> array of bytes containing ascii codes of characters
        in the original message.

        @ Description: This method is used for creating an occurrence map - it simply counts
        amount of times a given character appears in a given array of bytes.
     */

    public Map<Short, Integer> findOccurrenceMap(byte[] notEncodedInputArray) {
        Map<Short, Integer> occurrenceMap = new HashMap<>();
        for (int i = 0; i < notEncodedInputArray.length; i++) {
            if (occurrenceMap.containsKey((short) notEncodedInputArray[i])) {
                int prevValue = occurrenceMap.get((short) notEncodedInputArray[i]);
                occurrenceMap.replace((short) notEncodedInputArray[i], prevValue + 1);
            } else {
                occurrenceMap.put((short) notEncodedInputArray[i], 1);
            }
        }
        return occurrenceMap;
    }

    /*
        @ Method: findEncodingMap()

        @ Parameters:

        * occurrenceMap -> map containing mappings: character -> number of times it appears in the
        original text.
        * rootNode      -> root node of a build binary Huffman tree.

        @ Description: This method is used for finding encoding map - a map containing mappings -> character
        and their encoding in Huffman encoding scheme.

        In order to fulfill this goal this method utilizes findLeafWithGivenCharValue and
        findEncodingForGivenChar.
     */

    private Map<Short, String> findEncodingMap(Map<Short, Integer> occurrenceMap, Node rootNode) {
        Map<Short, String> encodingMap = new HashMap<>();
        Object[] arrayOfKeys = occurrenceMap.keySet().toArray();
        // For every char in occurrence map we find exact encoding and add its mapping to the map.
        for (int i = 0; i < occurrenceMap.size(); i++) {
            String sequence = findEncodingForGivenChar(findLeafWithGivenCharValue((short) arrayOfKeys[i], rootNode));
            encodingMap.put((short) arrayOfKeys[i], sequence);
        }
        return encodingMap;
    }

    /*
        @ Method: findLeafWithGivenCharValue()

        @ Parameters:

        * charValue -> ascii code value of a character to be found in Huffman binary tree.
        * rootNode  -> root node of a constructed binary Huffman tree.

        @ Description: This method is used finding a node, containing an ascii code
        for a given character. It utilizes inorder tree walk for finding nodes.
     */

    private Node findLeafWithGivenCharValue(short charValue, Node rootNode) {
        if (rootNode.getCharacter() != charValue) {
            Node resultNode;
            Node leftChild = rootNode.getLeftChildNode();
            if (leftChild != null) {
                if (leftChild.getCharacter() == charValue) {
                    return leftChild;
                } else {
                    resultNode = findLeafWithGivenCharValue(charValue, leftChild);
                    if (resultNode != null) {
                        return resultNode;
                    }
                }
            }
            Node rightChild = rootNode.getRightChildNode();
            if (rightChild != null) {
                if (rightChild.getCharacter() == charValue) {
                    return rightChild;
                } else {
                    resultNode = findLeafWithGivenCharValue(charValue, rightChild);
                    if (resultNode != null) {
                        return resultNode;
                    }
                }
            }
            return null;
        } else {
            return rootNode; // This case is quite impossible in this implementation of tree.
        }
    }

    /*
        @ Method: findEncodingForGivenChar()

        @ Parameters:

        * foundNode -> node containing ascii code of a certain character, which encoding in
        Huffman encoding scheme must be found for.

        @ Description: This method find string of ones and zeros - that is a sequence of moves - to go from
        root node to a child node with a certain char ascii code. Zero is equivalent to going to the left child
        node, while one is for going to the right child node.
     */

    private String findEncodingForGivenChar(Node foundNode) {
        StringBuilder stringBuilder = new StringBuilder();
        Node upperNode;
        while(!foundNode.isRootNode()) {
            upperNode = foundNode.getParentNode();
            if (upperNode.getLeftChildNode() == foundNode) {
                stringBuilder.append("0");
            } else {
                stringBuilder.append("1");
            }
            foundNode = upperNode;
        }
        // String in stringBuilder is reversed since we go from the bottom to the top.
        return stringBuilder.reverse().toString();
    }
}
