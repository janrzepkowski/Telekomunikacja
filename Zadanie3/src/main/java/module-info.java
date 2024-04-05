module tips.zadanie3.tips_zadanie3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens tips.zadanie3.tips_zadanie3 to javafx.fxml;
    exports tips.zadanie3.tips_zadanie3;
    exports tips.zadanie3.tips_zadanie3.utils;
    exports tips.zadanie3.tips_zadanie3.exceptions;
    exports tips.zadanie3.tips_zadanie3.model;
}