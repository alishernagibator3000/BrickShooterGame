module com.example.brickshootergame {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.brickshootergame to javafx.fxml;
    exports com.example.brickshootergame;
}