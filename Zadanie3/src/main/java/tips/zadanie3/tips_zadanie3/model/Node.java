package tips.zadanie3.tips_zadanie3.model;

public class Node implements Comparable<Node> {

    /*
        @ character: byte representing ascii code of a character
     */

    private final short character;

    /*
        @ importanceValue: amount of times a given character appears in the text, given by
        the user.
     */

    private final int importanceValue;

    /*
        @ parentNode: Node being parent for this one. Its main goal is to
        be able to construct a binary tree.
     */

    private Node parentNode;

    /*
        @ leftChildNode: Attribute containing the node on the left -> this one
        is reached by going to the left from the root node - that means appending 0 in the Huffman coding.
     */

    private Node leftChildNode;

    /*
        @ rightChildNode: Attribute containing the node on the right -> this one
        is reached by going to the right from the root node - that means appending 1 in the Huffman coding.
     */

    private Node rightChildNode;

    /*
        @ Constructors: Self-explanatory.
     */

    public Node(short character, int importanceValue) {
        this.character = character;
        this.importanceValue = importanceValue;
    }

    public Node(short character, int importanceValue, Node leftChildNode, Node rightChildNode) {
        this.character = character;
        this.importanceValue = importanceValue;
        this.leftChildNode = leftChildNode;
        this.rightChildNode = rightChildNode;
    }

    /*
        @ Getters: Self-explanatory.
     */

    public Node getParentNode() {
        return parentNode;
    }

    public Node getLeftChildNode() {
        return leftChildNode;
    }

    public Node getRightChildNode() {
        return rightChildNode;
    }

    public short getCharacter() {
        return character;
    }

    public int getImportanceValue() {
        return importanceValue;
    }

    /*
        @ Setters:
     */

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    public boolean isLeaf() {
        return this.getLeftChildNode() == null && this.getRightChildNode() == null;
    }

    public boolean isRootNode() {
        return this.getParentNode() == null;
    }

    @Override
    public int compareTo(Node otherNode) {
        return this.getImportanceValue() - otherNode.getImportanceValue();
    }
}
