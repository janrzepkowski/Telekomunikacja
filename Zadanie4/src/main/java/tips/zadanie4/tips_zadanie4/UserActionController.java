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
    private ComboBox sampleRate;
    @FXML
    private ComboBox sampleSizeInBits;
    @FXML
    private ComboBox numberOfChannels;
    @FXML
    private ComboBox sampleRateForPlaying;
    @FXML
    private ComboBox sampleSizeInBitsForPlaying;
    @FXML
    private ComboBox numberOfChannelsForPlaying;
    @FXML
    private Button startRecordingButton;
    @FXML
    private Button finishRecordingButton;
    @FXML
    private Button startPlayButton;
    @FXML
    private Button finishPlayButton;
    @FXML
    private Button startConnectionButton;
    @FXML
    private Button endConnectionButton;
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

    /*
        @ Method: initialize()

        @ Parameters: None

        @ Description: Method used for initializing combo-boxes content, disabling some button and so on.
     */

    @FXML
    public void initialize() {
        sampleRate.getItems().addAll("8000", "11025", "16000", "22050", "32000", "44100", "48000", "88200", "96000", "176400", "192000");
        sampleSizeInBits.getItems().setAll("8", "16", "24", "32");
        numberOfChannels.getItems().setAll("1", "2");

        sampleRateForPlaying.getItems().addAll("8000", "11025", "16000", "22050", "32000", "44100", "48000", "88200", "96000", "176400", "192000");
        sampleSizeInBitsForPlaying.getItems().setAll("8", "16", "24", "32");
        numberOfChannelsForPlaying.getItems().setAll("1", "2");

        sampleRate.getSelectionModel().select(6);
        sampleSizeInBits.getSelectionModel().select(1);
        numberOfChannels.getSelectionModel().select(0);

        sampleRateForPlaying.getSelectionModel().select(6);
        sampleSizeInBitsForPlaying.getSelectionModel().select(1);
        numberOfChannelsForPlaying.getSelectionModel().select(0);

        finishRecordingButton.setDisable(true);
        finishPlayButton.setDisable(true);
        endConnectionButton.setDisable(true);

        updateAllData();
        refreshValues();
    }

    /*
        @ Method: updateSampleRate()

        @ Parameters: None

        @ Description: This method is used for updating sampleRate value. It is called by
        updateAllData() method.
     */

    private void updateSampleRate() {
        String sampleRateInString = sampleRate.getSelectionModel().getSelectedItem().toString();
        userInputSampleRate = Float.parseFloat(sampleRateInString);
    }

    /*
        @ Method: updateSampleSizeInBits()

        @ Parameters: None

        @ Description: This method is used for updating sampleSizeInBits value. It is called by
        updateAllData() method.
     */

    private void updateSampleSizeInBits() {
        String sampleSizeInBitsInString = sampleSizeInBits.getSelectionModel().getSelectedItem().toString();
        userInputSampleSizeInBits = Integer.parseInt(sampleSizeInBitsInString);
    }

    /*
        @ Method: updateNumberOfChannels()

        @ Parameters: None

        @ Description: This method is used for updating numberOfChannels value. It is called by
        updateAllData() method.
     */

    private void updateNumberOfChannels() {
        String numberOfChannelsInString = numberOfChannels.getSelectionModel().getSelectedItem().toString();
        userInputNumberOfChannels = Integer.parseInt(numberOfChannelsInString);
    }

    /*
        @ Method: updateSampleRateForPlaying()

        @ Parameters: None

        @ Description: This method is used for updating sampleRate value. It is called by
        updateAllData() method.
     */

    private void updateSampleRateForPlaying() {
        String sampleRateInStringForPlaying = sampleRateForPlaying.getSelectionModel().getSelectedItem().toString();
        userInputSampleRateForPlaying = Float.parseFloat(sampleRateInStringForPlaying);
    }

    /*
        @ Method: updateSampleSizeInBitsForPlaying()

        @ Parameters: None

        @ Description: This method is used for updating sampleSizeInBits value. It is called by
        updateAllData() method.
     */

    private void updateSampleSizeInBitsForPlaying() {
        String sampleSizeInBitsInStringForPlaying = sampleSizeInBitsForPlaying.getSelectionModel().getSelectedItem().toString();
        userInputSampleSizeInBitsForPlaying = Integer.parseInt(sampleSizeInBitsInStringForPlaying);
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
            FileChooser fileChooser = new FileChooser();
            fullFileName = fileChooser.showSaveDialog(StageSetup.getStage()).getAbsolutePath();
            if (fullFileName != null) {
                recordAudioManager = new RecordAudioManager(userInputSampleRate, userInputSampleSizeInBits, userInputNumberOfChannels);
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
        String fullFileName;
        FileChooser fileChooser = new FileChooser();
        fullFileName = fileChooser.showOpenDialog(StageSetup.getStage()).getAbsolutePath();
        if (fullFileName != null) {
            playAudioManager = new PlayAudioManager();
            startPlayButton.setDisable(true);
            finishPlayButton.setDisable(false);
            playAudioManager.playRecordedSound(fullFileName);
        } else {
            throwAlert(Alert.AlertType.ERROR, "Bład", "Krytyczny błąd", "Nie podano nazwy pliku");
        }
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

    @FXML
    public void clientFunctionInit() {
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
            usedSocket = new Socket(serverIpAddress, Integer.parseInt(serverPortNumber));
            showPupUpWindow("Połączono", "Udało się nawiązać połączenie!");
        } catch(IOException ioException) {
            throwAlert(Alert.AlertType.ERROR, "Błąd", "Wystąpił błąd!", "Powód: " + ioException.getMessage());
        }
    }

    /*
        @ Method: startConnection

        @ Parameters: None

        @ Returns: None

        @ Description: This method is used for starting actual communication between two sides, that is
        it takes microphone input from one side and transmit it to the other side.
     */

    @FXML
    private void startConnection() {
        try {
            OutputStream outputStream = usedSocket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeInt(userInputSampleSizeInBits);
            Thread.sleep(1000);
            InputStream inputStream = usedSocket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            userInputSampleSizeInBits = dataInputStream.readInt();
            sampleSizeInBitsForPlaying.setValue(String.valueOf(userInputSampleSizeInBits));
            refreshValueForPlaying();
            soundServer = new SoundServer(userInputSampleRateForPlaying, userInputSampleSizeInBitsForPlaying, userInputNumberOfChannelsForPlaying);
            soundClient = new SoundClient(userInputSampleRate, userInputSampleSizeInBits, userInputNumberOfChannels);
            soundClient.startSoundSending(usedSocket.getOutputStream());
            soundServer.startSoundReceiving(usedSocket.getInputStream());
            startConnectionButton.setDisable(true);
            endConnectionButton.setDisable(false);
        } catch (TargetDataLineException targetDataLineException) {
            throwAlert(Alert.AlertType.ERROR, "Błąd", "Wystąpił błąd", targetDataLineException.getMessage());
        } catch (SourceDataLineException sourceDataLineException) {
            throwAlert(Alert.AlertType.ERROR, "Błąd", "Wystąpił błąd", sourceDataLineException.getMessage());
        } catch (IOException ioException) {
            throwAlert(Alert.AlertType.ERROR, "Błąd", "Wystąpił błąd", "Powód: " + ioException.getMessage());
        } catch(Exception exception) {
            throwAlert(Alert.AlertType.ERROR, "Błąd", "Wystąpił błąd", "Powód: " + exception.getMessage());
        }
    }

    /*
        @ Method: startConnection

        @ Parameters: None

        @ Returns: None

        @ Description: This method is used for ending actual communication between two sides, so that they no longer
        hear anything from each other.
     */

    @FXML
    public void endConnection() {
        try {
            soundServer.stopSoundReceiving();
            soundClient.stopSoundSending();
            usedSocket.close();
            if (serverGeneralSocket != null) {
                serverGeneralSocket.close();
            }
        } catch(IOException ioException) {
            throwAlert(Alert.AlertType.ERROR, "Błąd", "Wystąpił błąd", "Powód: " + ioException.getMessage());
        }
        startConnectionButton.setDisable(false);
        endConnectionButton.setDisable(true);
    }

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