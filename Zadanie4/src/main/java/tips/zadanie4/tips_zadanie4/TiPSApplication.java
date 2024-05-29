package tips.zadanie4.tips_zadanie4;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class TiPSApplication extends Application {

    /*
        @ Method: start()

        @ Parameters:

        * stage -> the main stage for the application.

        @ Description: This method is used as entry point of JavaFX application.
     */

    @Override
    public void start(Stage stage) throws IOException {
        StageSetup.buildStage(stage, "main-form.fxml","TiPS - Zadanie 4.");
    }

    /*
        @ Method: main()

        @ Parameters: None

        @ Description: Main method of this class, used for running application.
     */

    public static void main(String[] args) {
        launch();
    }
}