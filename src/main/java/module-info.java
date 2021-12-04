module com.onionskinstudio.flipbookanimator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.sql;


    opens com.onionskinstudio.flipbookanimator.ui to javafx.fxml;
    exports com.onionskinstudio.flipbookanimator.ui;
    exports com.onionskinstudio.flipbookanimator;
    opens com.onionskinstudio.flipbookanimator to javafx.fxml;
}