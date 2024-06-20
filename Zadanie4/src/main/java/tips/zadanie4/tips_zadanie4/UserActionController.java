package tips.zadanie4.tips_zadanie4;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import tips.zadanie4.tips_zadanie4.audio.PlayAudioManager;
import tips.zadanie4.tips_zadanie4.audio.RecordAudioManager;
import tips.zadanie4.tips_zadanie4.audio.SoundClient;
import tips.zadanie4.tips_zadanie4.audio.SoundServer;
import tips.zadanie4.tips_zadanie4.exceptions.LineException;
import tips.zadanie4.tips_zadanie4.exceptions.LineWithGivenParametersNotSupported;
import tips.zadanie4.tips_zadanie4.exceptions.SourceDataLineException;
import tips.zadanie4.tips_zadanie4.exceptions.TargetDataLineException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class UserActionController {

    @FXML
    private Slider sampleRate;
    @FXML
    private Slider sampleSizeInBits;
    @FXML
    private ComboBox numberOfChannels;
    @FXML
    private Slider sampleRateForPlaying;
    @FXML
    private Slider sampleSizeInBitsForPlaying;
    @FXML
    private ComboBox numberOfChannelsForPlaying;
    @FXML
    private Button startRecordingButton;
    @FXML
    private Button finishRecordingButton;
    @FXML
    private Button startPlayButton;
    @FXML
    private Button replayButton;
    @FXML
    private Button finishPlayButton;
//    @FXML
//    private Button startConnectionButton;
//    @FXML
//    private Button endConnectionButton;
    @FXML
    private TextField valueForRecording;
    @FXML
    private TextField valueForPlaying;

    /*
        @ Above attributes are used for customizing GUI combo-boxes, disabling buttons and so on.
     */

    /*
        @ userInputSampleRate: Sample rate chosen by the user from combo-box. It is
        already converted to float, since it will be easier to create RecordAudioManager with it.
     */

    private float userInputSampleRate;

    /*
        @ getUserInputSampleRateForPlaying: Sample rate chosen by the user from combo-box. It is
        already converted to float, since it will be easier to create RecordAudioManager with it.
     */

    private float userInputSampleRateForPlaying;

    /*
        @ userInputSampleSizeInBits: Sample size in bits chosen by the user from combo-box. It is
        already converted to integer, since it will be easier to create RecordAudioManager with it.
     */

    private int userInputSampleSizeInBits;

    /*
        @ userInputSampleSizeInBitsForPlaying: Sample size in bits chosen by the user from combo-box. It is
        already converted to integer, since it will be easier to create RecordAudioManager with it.
     */

    private int userInputSampleSizeInBitsForPlaying;

    /*
        @ userInputNumberOfChannels: Number of channels chosen by the user from combo-box. It is
        already converted to integer, since it will be easier to create RecordAudioManager with it.
     */

    private int userInputNumberOfChannels;

    /*
        @ userInputNumberOfChannelsForPlaying: Number of channels chosen by the user from combo-box. It is
        already converted to integer, since it will be easier to create RecordAudioManager with it.
     */

    private int userInputNumberOfChannelsForPlaying;

    /*
        @ recordAudioManager: Variable holding recordAudioManager object
        that will be used later for recording sound.
     */

    private RecordAudioManager recordAudioManager;

    /*
        @ playAudioManager: Variable holding recordAudioManager object
        that will be used later for playing recorded sound.
     */

    private PlayAudioManager playAudioManager;

    private SoundClient soundClient;
    private SoundServer soundServer;

    private ServerSocket serverGeneralSocket;
    private Socket usedSocket;
    private String fileName;

    /*
        @ Method: initialize()

        @ Parameters: None

        @ Description: Method used for initializing combo-boxes content, disabling some button and so on.
     */

    @FXML
    public void initialize() {
        sampleRate.setMin(8000);
        sampleRate.setMax(192000);
        sampleRate.setValue(44100);

        sampleSizeInBits.setMin(8);
        sampleSizeInBits.setMax(32);
        sampleSizeInBits.setValue(16);
        sampleRate.valueProperty().addListener((observable, oldValue, newValue) -> {
            refreshValueForRecording();
        });

        sampleSizeInBits.valueProperty().addListener((observable, oldValue, newValue) -> {
            refreshValueForRecording();
        });

        // Initialize the sampleRateForPlaying slider
        sampleRateForPlaying.setMin(8000);
        sampleRateForPlaying.setMax(192000);
        sampleRateForPlaying.setValue(44100);
        sampleRateForPlaying.valueProperty().addListener((observable, oldValue, newValue) -> {
            refreshValueForPlaying();
        });

        // Initialize the sampleSizeInBitsForPlaying slider
        sampleSizeInBitsForPlaying.setMin(8);
        sampleSizeInBitsForPlaying.setMax(32);
        sampleSizeInBitsForPlaying.setValue(16);
        sampleSizeInBitsForPlaying.valueProperty().addListener((observable, oldValue, newValue) -> {
            refreshValueForPlaying();
        });

        numberOfChannels.getItems().setAll("1", "2");
        numberOfChannelsForPlaying.getItems().setAll("1", "2");
        numberOfChannels.getSelectionModel().select(0);
        numberOfChannelsForPlaying.getSelectionModel().select(0);

        finishRecordingButton.setDisable(true);
        finishPlayButton.setDisable(true);
//        endConnectionButton.setDisable(true);
        replayButton.setDisable(true);

        updateAllData();
        refreshValues();
    }

    private void updateSampleRate() {
        userInputSampleRate = (float) sampleRate.getValue();
    }

    private void updateSampleSizeInBits() {
        userInputSampleSizeInBits = (int) sampleSizeInBits.getValue();
    }

    private void updateNumberOfChannels() {
        String numberOfChannelsInString = numberOfChannels.getSelectionModel().getSelectedItem().toString();
        userInputNumberOfChannels = Integer.parseInt(numberOfChannelsInString);
    }

    private void updateSampleRateForPlaying() {
        userInputSampleRateForPlaying = (float) sampleRateForPlaying.getValue();
    }

    private void updateSampleSizeInBitsForPlaying() {
        userInputSampleSizeInBitsForPlaying = (int) sampleSizeInBitsForPlaying.getValue();
    }

    /*
        @ Method: updateNumberOfChannelsForPlaying()

        @ Parameters: None

        @ Description: This method is used for updating numberOfChannels value. It is called by
        updateAllData() method.
     */

    private void updateNumberOfChannelsForPlaying() {
        String numberOfChannelsInStringForPlaying = numberOfChannelsForPlaying.getSelectionModel().getSelectedItem().toString();
        userInputNumberOfChannelsForPlaying = Integer.parseInt(numberOfChannelsInStringForPlaying);
    }

    /*
        @ Method: updateAllData()

        @ Parameters: None

        @ Description: This method is used for updating all the data selected by
        the user from the combo-boxes.
     */

    private void updateAllData() {
        updateSampleRate();
        updateSampleSizeInBits();
        updateNumberOfChannels();
        updateSampleRateForPlaying();
        updateSampleSizeInBitsForPlaying();
        updateNumberOfChannelsForPlaying();
    }

    /*
        @ Method: showAuthors()

        @ Parameters: None

        @ Description: This method is used to show authors' data.
     */

    @FXML
    public void showAuthors() {
        showPupUpWindow("Autorzy programu", "Franciszek Pietrzycki & Jan Rzepkowski");
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
        @ Method: startRecordingSound()

        @ Parameters: None

        @ Description: This method is used for starting a recording of sound from user microphone.
     */

    @FXML
    public void startRecordingSound() {
        updateAllData();
        try {
            String fullFileName;
            File file;
            FileChooser fileChooser = new FileChooser();
            file = fileChooser.showSaveDialog(StageSetup.getStage());
            if (file != null) {
                fullFileName = file.getAbsolutePath(); // userInputSampleSizeInBits should be 16
                recordAudioManager = new RecordAudioManager(userInputSampleRate, 16, userInputNumberOfChannels);
                startRecordingButton.setDisable(true);
                finishRecordingButton.setDisable(false);
                recordAudioManager.recordSound(fullFileName);
            } else {
                throwAlert(Alert.AlertType.ERROR, "Bład", "Krytyczny błąd", "Nie podano nazwy pliku");
            }
        } catch (LineException lineException) {
            throwAlert(Alert.AlertType.ERROR, "Błąd", "Krytyczny błąd", "Powód: " + lineException.getMessage());
        } catch (LineWithGivenParametersNotSupported notSupported) {
            throwAlert(Alert.AlertType.ERROR, "Błąd", "Nieprawidłowy dobór parametrów", notSupported.getMessage());
        }
    }

    /*
        @ Method: stopRecordingSound()

        @ Parameters: None

        @ Description: This method is used for cutting user recorded sound.
     */

    @FXML
    public void stopRecordingSound() {
        try {
            startRecordingButton.setDisable(false);
            finishRecordingButton.setDisable(true);
            recordAudioManager.closeTargetDataLine();
        } catch (NullPointerException nullPtrExc) {
            throwAlert(Alert.AlertType.ERROR, "Błąd", "Krytyczny błąd", "Nagrywanie zostało już zakończone.");
        }
    }

    /*
        @ Method: startPlayingRecordedSound()

        @ Parameters: None

        @ Description: This method is responsible for recording sound, recorded by user microphone.
     */

    @FXML
    public void startPlayingRecordedSound() {
        File file;
        FileChooser fileChooser = new FileChooser();
        file = fileChooser.showOpenDialog(null);
        if (file != null) {
            fileName = file.getAbsolutePath();
            startPlayButton.setDisable(true);
            replayButton.setDisable(false);
            finishPlayButton.setDisable(false);
            replayRecordedSound();
        } else {
            throwAlert(Alert.AlertType.ERROR, "Bład", "Krytyczny błąd", "Nie podano nazwy pliku");
        }
    }

    @FXML
    public void replayRecordedSound() {
        playAudioManager = new PlayAudioManager();
        playAudioManager.playRecordedSound(fileName);
    }

    /*
        @ Method: finishPlayingRecordedSound()

        @ Parameters: None

        @ Description: This method is used for cutting played sound, before the end of it.
     */

    @FXML
    public void finishPlayingRecordedSound() {
        try {
            playAudioManager.closePlayedClip();
            startPlayButton.setDisable(false);
            replayButton.setDisable(true);
            finishPlayButton.setDisable(true);
        } catch (NullPointerException nullPtrExc) {
            throwAlert(Alert.AlertType.ERROR, "Błąd", "Krytyczny błąd", "Odtwarzanie nagrania zostało już zakończone");
        }
    }

    /*
        @ Method: serverFunctionInit

        @ Parameters: None

        @ Returns: None

        @ Description: This method is used for initialization of ServerSocket and assignment to Socket used for
        connecting hosts together. When communication is established, a popup window is display informing about this fact.
     */

    @FXML
    public void serverFunctionInit() {
        TextInputDialog textInput = new TextInputDialog();
        textInput.setTitle("Wprowadź dane");
        textInput.setHeaderText("Wprowadź dane potrzebne do nawiązania połączenia");
        textInput.setContentText("Wprowadź numer portu, na którym będzie działać serwer: ");
        String serverPortNumber = textInput.showAndWait().get();
        try {
            serverGeneralSocket = new ServerSocket(Integer.parseInt(serverPortNumber));
            usedSocket = serverGeneralSocket.accept();
            showPupUpWindow("Połączono", "Udało się nawiązać połączenie!");
        } catch(IOException ioException) {
            throwAlert(Alert.AlertType.ERROR, "Błąd", "Wystąpił błąd!", "Powód: " + ioException.getMessage());
        }
    }

    /*
        @ Method: clientFunctionInit

        @ Parameters: None

        @ Returns: None

        @ Description: This method is used for initialization of Socket which will be used, for
        communication with other host. When communication is established, a popup window is display
        informing about this fact.
     */

//    @FXML
//    public void clientFunctionInit() {
//        TextInputDialog textInput = new TextInputDialog();
//        textInput.setTitle("Wprowadź dane");
//        textInput.setHeaderText("Wprowadź adres IP serwera, z którym się chcesz połączyć");
//        textInput.setContentText("Adres IP: ");
//        String serverIpAddress = textInput.showAndWait().get();
//        textInput = new TextInputDialog();
//        textInput.setTitle("Wprowadź dane");
//        textInput.setHeaderText("Wprowadź numer portu, na którym działa serwera, z którym się chcesz połączyć");
//        textInput.setContentText("Numer portu: ");
//        String serverPortNumber = textInput.showAndWait().get();
//        try {
//            usedSocket = new Socket(serverIpAddress, Integer.parseInt(serverPortNumber));
//            showPupUpWindow("Połączono", "Udało się nawiązać połączenie!");
//        } catch(IOException ioException) {
//            throwAlert(Alert.AlertType.ERROR, "Błąd", "Wystąpił błąd!", "Powód: " + ioException.getMessage());
//        }
//    }

    /*
        @ Method: startConnection

        @ Parameters: None

        @ Returns: None

        @ Description: This method is used for starting actual communication between two sides, that is
        it takes microphone input from one side and transmit it to the other side.
     */

//    @FXML
//    private void startConnection() {
//        try {
//            OutputStream outputStream = usedSocket.getOutputStream();
//            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
//            dataOutputStream.writeInt(userInputSampleSizeInBits);
//            Thread.sleep(1000);
//            InputStream inputStream = usedSocket.getInputStream();
//            DataInputStream dataInputStream = new DataInputStream(inputStream);
//            userInputSampleSizeInBits = dataInputStream.readInt();
//            sampleSizeInBitsForPlaying.setValue(userInputSampleSizeInBits);
//            refreshValueForPlaying();
//            soundServer = new SoundServer(userInputSampleRateForPlaying, userInputSampleSizeInBitsForPlaying, userInputNumberOfChannelsForPlaying);
//            soundClient = new SoundClient(userInputSampleRate, userInputSampleSizeInBits, userInputNumberOfChannels);
//            soundClient.startSoundSending(usedSocket.getOutputStream());
//            soundServer.startSoundReceiving(usedSocket.getInputStream());
//            startConnectionButton.setDisable(true);
//            endConnectionButton.setDisable(false);
//        } catch (TargetDataLineException targetDataLineException) {
//            throwAlert(Alert.AlertType.ERROR, "Błąd", "Wystąpił błąd", targetDataLineException.getMessage());
//        } catch (SourceDataLineException sourceDataLineException) {
//            throwAlert(Alert.AlertType.ERROR, "Błąd", "Wystąpił błąd", sourceDataLineException.getMessage());
//        } catch (IOException ioException) {
//            throwAlert(Alert.AlertType.ERROR, "Błąd", "Wystąpił błąd", "Powód: " + ioException.getMessage());
//        } catch(Exception exception) {
//            throwAlert(Alert.AlertType.ERROR, "Błąd", "Wystąpił błąd", "Powód: " + exception.getMessage());
//        }
//    }

    /*
        @ Method: startConnection

        @ Parameters: None

        @ Returns: None

        @ Description: This method is used for ending actual communication between two sides, so that they no longer
        hear anything from each other.
     */

//    @FXML
//    public void endConnection() {
//        try {
//            soundServer.stopSoundReceiving();
//            soundClient.stopSoundSending();
//            usedSocket.close();
//            if (serverGeneralSocket != null) {
//                serverGeneralSocket.close();
//            }
//        } catch(IOException ioException) {
//            throwAlert(Alert.AlertType.ERROR, "Błąd", "Wystąpił błąd", "Powód: " + ioException.getMessage());
//        }
//        startConnectionButton.setDisable(false);
//        endConnectionButton.setDisable(true);
//    }

    @FXML
    public void refreshValues() {
        updateAllData();
        double snrValue = 20 * Math.log10(Math.pow(2, userInputSampleSizeInBits));
        valueForRecording.setText(String.valueOf(snrValue));
        snrValue = 20 * Math.log10(Math.pow(2, userInputSampleSizeInBitsForPlaying));
        valueForPlaying.setText(String.valueOf(snrValue));
    }

    @FXML
    public void refreshValueForRecording() {
        updateSampleSizeInBits();
        double snrValue = 20 * Math.log10(Math.pow(2, userInputSampleSizeInBits));
        valueForRecording.setText(String.valueOf(snrValue));
    }

    @FXML
    public void refreshValueForPlaying() {
        updateSampleSizeInBitsForPlaying();
        double snrValue = 20 * Math.log10(Math.pow(2, userInputSampleSizeInBitsForPlaying));
        valueForPlaying.setText(String.valueOf(snrValue));
    }
}