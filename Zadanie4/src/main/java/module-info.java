module tips.zadanie4.tips_zadanie4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens tips.zadanie4.tips_zadanie4 to javafx.fxml;
    exports tips.zadanie4.tips_zadanie4;
}