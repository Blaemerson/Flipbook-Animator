module com.onionskin.onionskin.flipbookanimator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;


    exports com.onionskin.onionskin.flipbookanimator;
    exports com.onionskin.onionskin.flipbookanimator.ui;
    opens com.onionskin.onionskin.flipbookanimator.ui;
    opens com.onionskin.onionskin.flipbookanimator;

}