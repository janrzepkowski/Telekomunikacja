package tips.zadanie3.tips_zadanie3;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import tips.zadanie3.tips_zadanie3.exceptions.IOManagerReadException;
import tips.zadanie3.tips_zadanie3.exceptions.IOManagerWriteException;
import tips.zadanie3.tips_zadanie3.model.HuffmanCoding;
import tips.zadanie3.tips_zadanie3.utils.Converter;
import tips.zadanie3.tips_zadanie3.utils.IOManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;


public class UserActionController {

    /*
        @ Attributes:
     */
    @FXML
    private TextArea decodedTextArea;
    @FXML
    private TextArea encodedTextArea;
    @FXML
    private CheckBox isBinaryContent;
    @FXML
    private CheckBox encodedContent;
    @FXML
    private ComboBox chooseFunction;
    @FXML
    private Button startButton;
    @FXML
    private Button finishButton;

    /*
        Comment: Above attributes are used mainly for styling combo-boxes, disabling buttons
        and so on.
     */

    /*
        @ serverFunctionality: Attribute used for holding information if
        application functions as server of as a client.
     */

    private boolean serverFunctionality;

    /*
        @ clientSocket: This variable is used for holding Socket object
        that is required for communication with the other side of connection, but
        from the client side.
     */

    private Socket clientSocket;

    /*
        @ serverSocket: This variable is used for holding Socket object
        that is required for communication with the other side of connection, but
        from the server side.
     */

    private ServerSocket serverSocket;

    /*
        @ serverSideSocket: Actual socket object, used in communication.
     */

    private Socket serverSideSocket;

    /*
        @ outputStream: Variable used for holding outputStream
        opened object instance.
     */

    private OutputStream outputStream;

    /*
        @ inputStream: Variable used for holding inputStream
        opened object instance.
     */

    private InputStream inputStream;

    /*
        @ objectOutputStream: Variable used for holding objectOutputStream
        opened object instance.
     */

    private ObjectOutputStream objectOutputStream;

    /*
        @ objectInputStream: Variable used for holding objectInputStream
        opened object instance.
     */

    private ObjectInputStream objectInputStream;

    // Used for decoding.
    private Map<Short, Integer> occurrenceMap;

    /*
        @ Methods:
     */

    /*
        @ Method: initialize()

        @ Parameters: None

        @ Description: This method is used to include some options in combo-boxes in GUI, and for
        making some buttons disabled.
        Started automatically by JavaFX app.
     */

    @FXML
    public void initialize() {
        chooseFunction.getItems().addAll("Klient", "Serwer");
        chooseFunction.getSelectionModel().selectFirst();
        serverFunctionality = false;
        finishButton.setDisable(true);
    }

    /*
        @ Method: showAuthors()

        @ Parameters: None

        @ Description: This method is used to show authors' data.
     */

    @FXML
    public void showAuthors() {
        showPupUpWindow("Autorzy programu", "Franek Pietrzycki & Jan Rzepkowski");
    }

    /*
        @ Method: throwAlert()

        @ Parameters:

        * typeOfAlert   -> error of thrown alert
        * title         -> title of the window
        * header        -> header message inside the window
        * content       -> content inside the window - in most of the cases it is the reason for
        a certain exception being thrown

        @ Description: This method is used mainly to communicate to the user some problems
        that can occur through inappropriate use of the program.
     */

    private void throwAlert(Alert.AlertType typeOfAlert, String title, String header, String content) {
        Alert alert = new Alert(typeOfAlert);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }

    /*
        @ Method: showPupUpWindow()

        @ Parameters:

        * title -> title of the popup window.
        * content -> actual content of the popup - containing in this case authors' data.

        @ Description: Method for showing a windows with some text.
     */

    private void showPupUpWindow(String title, String content) {
        Dialog<String> popUpWin = new Dialog<>();
        popUpWin.setTitle(title);
        popUpWin.setContentText(content);
        ButtonType closeWindow = new ButtonType("Zamknij", ButtonBar.ButtonData.CANCEL_CLOSE);
        popUpWin.getDialogPane().getButtonTypes().add(closeWindow);
        popUpWin.show();
    }

    /*
        @ Method: encodeText()

        @ Parameters: None

        @ Description: Method that is used for encoding text inserted into decodedTextArea. It also uses
        isBinaryContent checkbox to determine if input text is binary representation of a file or not.
     */

    @FXML
    public void encodeText() {
        byte[] textToEncode;
        if (isBinaryContent.isSelected()) {
            textToEncode = Converter.convertHexadecimalToAscii(decodedTextArea.getText().getBytes(StandardCharsets.US_ASCII));
        } else {
            textToEncode = decodedTextArea.getText().getBytes(StandardCharsets.UTF_8);
        }
        HuffmanCoding huffmanCode = new HuffmanCoding();
        occurrenceMap = huffmanCode.findOccurrenceMap(textToEncode);
        byte[] encodedMessage = huffmanCode.encodeWithHuffmanEncoding(textToEncode, occurrenceMap);
        encodedTextArea.setText(new String(Converter.convertAsciiToHexadecimal(encodedMessage), StandardCharsets.US_ASCII));
    }

    /*
        @ Method: decodeText()

        @ Parameters: None

        @ Description: Method that is used for decoding text inserted into encodedTextArea. It also uses
        isBinaryContent checkbox to determine if input text is binary representation of a file or not.
     */

    @FXML
    public void decodeText() {
        byte[] textToDecode = Converter.convertHexadecimalToAscii(encodedTextArea.getText().getBytes(StandardCharsets.US_ASCII));
        HuffmanCoding huffmanCode = new HuffmanCoding();
        byte[] decodedMessage = huffmanCode.decodeWithHuffmanEncoding(textToDecode, occurrenceMap);
        if (isBinaryContent.isSelected()) {
            decodedTextArea.setText(new String(Converter.convertAsciiToHexadecimal(decodedMessage), StandardCharsets.US_ASCII));
        } else {
            decodedTextArea.setText(new String(decodedMessage, StandardCharsets.UTF_8));
        }
    }

    /*
        @ Method: readDecodedFromAFile()

        @ Parameters: None

        @ Description: Method used to read content from a file - uses isBinaryContent to see if file to be input from
        the user is binary or not. Besides it uses encodedContent - which reads it immediately into encodedTextArea.
     */

    @FXML
    public void readDecodedFromAFile() {
        String pathToFile;
        FileChooser chooseFile = new FileChooser();
        pathToFile = chooseFile.showOpenDialog(StageSetup.getStage()).getAbsolutePath();
        if (!pathToFile.isEmpty() && pathToFile != null) {
            try {
                byte[] someTextFromFile = IOManager.readBytesFromAFile(pathToFile);
                if (isBinaryContent.isSelected() && encodedContent.isSelected()) {
                    encodedTextArea.setText(new String(Converter.convertAsciiToHexadecimal(someTextFromFile), StandardCharsets.US_ASCII));
                } else if (isBinaryContent.isSelected() && !encodedContent.isSelected()) {
                    decodedTextArea.setText(new String(Converter.convertAsciiToHexadecimal(someTextFromFile), StandardCharsets.US_ASCII));
                } else if (!isBinaryContent.isSelected() && encodedContent.isSelected()) {
                    encodedTextArea.setText(new String(Converter.convertAsciiToHexadecimal(someTextFromFile), StandardCharsets.US_ASCII));
                } else {
                    decodedTextArea.setText(new String(someTextFromFile, StandardCharsets.UTF_8));
                }
            } catch (IOManagerReadException readExc) {
                String title = "Błąd";
                String header = "Błąd operacji odczytu z pliku: " + pathToFile;
                String content = readExc.getMessage();
                throwAlert(Alert.AlertType.ERROR, title, header, content);
            }
        }
    }

    /*
        @ Method: saveDecodedToAFile()

        @ Parameters: None

        @ Description: Method used to save content from a file - uses isBinaryContent to see if file to be input to
        the file is binary or not. Besides it uses encodedContent - which saves it from encodedTextArea.
     */

    @FXML
    public void saveDecodedToAFile() {
        FileChooser chooseFile = new FileChooser();
        File file = chooseFile.showSaveDialog(StageSetup.getStage());
        if (file != null) {
            byte[] textFromInput;
            if (isBinaryContent.isSelected() && encodedContent.isSelected()) {
                textFromInput = Converter.convertHexadecimalToAscii(encodedTextArea.getText().getBytes(StandardCharsets.US_ASCII));
            } else if (isBinaryContent.isSelected() && !encodedContent.isSelected()) {
                textFromInput = Converter.convertHexadecimalToAscii(decodedTextArea.getText().getBytes(StandardCharsets.US_ASCII));
            } else if (!isBinaryContent.isSelected() && encodedContent.isSelected()) {
                textFromInput = Converter.convertHexadecimalToAscii(encodedContent.getText().getBytes(StandardCharsets.US_ASCII));
            } else {
                textFromInput = decodedTextArea.getText().getBytes(StandardCharsets.UTF_8);
            }
            try {
                IOManager.writeBytesToAFile(file.getAbsolutePath(), textFromInput);
            } catch (IOManagerWriteException writeExc) {
                String title = "Błąd";
                String header = "Błąd operacji zapisu pliku: " + file.getName();
                String content = writeExc.getMessage();
                throwAlert(Alert.AlertType.ERROR, title, header, content);
            }
        }
    }

    /*
        @ Method: sendTextToOtherHost()

        @ Parameters: None

        @ Description: Method used for sending content from decodedTextArea to host on the other side of connection.
     */

    @FXML
    public void sendTextToOtherHost() {
        try {
            if (serverFunctionality) {
                outputStream = serverSideSocket.getOutputStream();
            } else {
                outputStream = clientSocket.getOutputStream();
            }
            objectOutputStream = new ObjectOutputStream(outputStream);
            HuffmanCoding huffmanCode = new HuffmanCoding();
            byte[] textToEncode = null;
            if (isBinaryContent.isSelected()) {
                textToEncode = Converter.convertHexadecimalToAscii(decodedTextArea.getText().getBytes(StandardCharsets.US_ASCII));
            } else {
                textToEncode = decodedTextArea.getText().getBytes(StandardCharsets.UTF_8);
            }
            occurrenceMap = huffmanCode.findOccurrenceMap(textToEncode);
            byte[] encodedText = huffmanCode.encodeWithHuffmanEncoding(textToEncode, occurrenceMap);
            encodedTextArea.setText(new String(Converter.convertAsciiToHexadecimal(encodedText), StandardCharsets.US_ASCII));
            objectOutputStream.writeObject(occurrenceMap);
            objectOutputStream.writeObject(encodedText);
            objectOutputStream.flush();
        } catch (IOException ioException) {
            throwAlert(Alert.AlertType.ERROR, "Błąd", "Krytyczny błąd", "Nie udało się przesłać danych do serwera.");
        }
    }

    /*
        @ Method: startOperation()

        @ Parameters: None

        @ Description: Method used for starting content transfer from one host the second one - it requires user being
        on the other side of connection to click appropriate button to receive the data.
     */

    @FXML
    public void startOperation() {
        if (!serverFunctionality) {
            TextInputDialog textInput = new TextInputDialog();
            textInput.setTitle("Wprowadź dane");
            textInput.setHeaderText("Wprowadź adres IP serwera, z którym się chcesz połączyć");
            textInput.setContentText("Adres IP: ");
            String serverIpAddress = textInput.showAndWait().get();
            textInput = new TextInputDialog();
            textInput.setTitle("Wprowadź dane");
            textInput.setHeaderText("Wprowadź numer portu, na którym działa serwera, z którym się chcesz połączyć");
            textInput.setContentText("Numer portu: ");
            String serverPortNumber = textInput.showAndWait().get();

            try {
                clientSocket = new Socket(serverIpAddress, Integer.parseInt(serverPortNumber));
                startButton.setDisable(true);
                finishButton.setDisable(false);
            } catch (IOException ioException) {
                throwAlert(Alert.AlertType.ERROR, "Błąd", "Krytyczny błąd", "Nie powiodło się otworzenie gniazda: " + serverIpAddress + ":" + serverPortNumber);
            } catch (IllegalArgumentException argExc) {
                throwAlert(Alert.AlertType.ERROR, "Błąd", "Krytyczny błąd", "Podany numer portu tj. " + serverPortNumber + " znajduje się poza zakresem dopuszczalnych numerów portów.");
            }
        } else {
            TextInputDialog textInput = new TextInputDialog();
            textInput.setTitle("Wprowadź dane");
            textInput.setHeaderText("Wprowadź dane potrzebne do nawiązania połączenia");
            textInput.setContentText("Wprowadź numer portu, na którym będzie działać serwer: ");
            String serverPortNumber = textInput.showAndWait().get();

            try {
                serverSocket = new ServerSocket(Integer.parseInt(serverPortNumber));
                startButton.setDisable(true);
                finishButton.setDisable(false);
                serverSideSocket = serverSocket.accept();
            } catch (IOException ioException) {
                throwAlert(Alert.AlertType.ERROR, "Błąd", "Krytyczny błąd", "Nie powiodło się otworzenie portu o numerze: " + serverPortNumber);
            } catch (IllegalArgumentException argExc) {
                throwAlert(Alert.AlertType.ERROR, "Błąd", "Krytyczny błąd", "Podany numer portu tj. " + serverPortNumber + " znajduje się poza zakresem dopuszczalnych numerów portów.");
            }
        }
    }

    /*
        @ Method: finishOperation()

        @ Parameters: None

        @ Description: Method used for closing connection between two hosts.
     */

    @FXML
    public void finishOperation() {
        try {
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (serverFunctionality && serverSocket != null) {
                serverSocket.close();
            } else if (!serverFunctionality && clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException ioException) {
            throwAlert(Alert.AlertType.ERROR, "Błąd", "Błąd krytyczny", "Nie powiodło się zamknięcie gniazda.");
        }
        startButton.setDisable(false);
        finishButton.setDisable(true);
    }

    /*
        @ Method: changeFunction()

        @ Parameters: None

        @ Description: This method is used from changing boolean value of serverFunctionality every time user
        changes the mode of functionality.
     */

    @FXML
    public void changeFunction() {
        finishOperation();
        String function = chooseFunction.getSelectionModel().getSelectedItem().toString();
        if (function.equals("Klient")) {
            serverFunctionality = false;
        } else {
            serverFunctionality = true;
        }
        startButton.setDisable(false);
        finishButton.setDisable(true);
    }

    /*
        @ Method: receiveDataFromSecondHost()

        @ Parameters: None

        @ Description: Method used for receiving data from the host on the other side of the connection.
     */

    @FXML
    public void receiveDataFromSecondHost() {
        try {
            if (serverFunctionality) {
                inputStream = serverSideSocket.getInputStream();
            } else {
                inputStream = clientSocket.getInputStream();
            };
            objectInputStream = new ObjectInputStream(inputStream);
            occurrenceMap = (Map<Short, Integer>) objectInputStream.readObject();
            byte[] savedContent = (byte[]) objectInputStream.readObject();
            encodedTextArea.setText(new String(Converter.convertAsciiToHexadecimal(savedContent), StandardCharsets.US_ASCII));
            HuffmanCoding huffmanCode = new HuffmanCoding();
            byte[] decodedText = huffmanCode.decodeWithHuffmanEncoding(savedContent, occurrenceMap);
            if (isBinaryContent.isSelected()) {
                decodedTextArea.setText(new String(Converter.convertAsciiToHexadecimal(decodedText), StandardCharsets.US_ASCII));
            } else {
                decodedTextArea.setText(new String(decodedText, StandardCharsets.UTF_8));
            }
        } catch (IOException ioException) {
            throwAlert(Alert.AlertType.ERROR, "Błąd", "Krytyczny błąd", "Nie udało się odebrać danych od klienta.");
        } catch (ClassNotFoundException cnfExc) {
            throwAlert(Alert.AlertType.ERROR, "Błąd", "Krytyczny błąd", "Nie ma takiej klasy.");
        }
    }
}