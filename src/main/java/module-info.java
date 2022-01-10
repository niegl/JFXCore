module flow.jfxcore {
    requires javafx.controls;
    requires javafx.fxml;
    requires log4j;
    requires org.apache.commons.lang3;
    requires org.apache.commons.io;
    requires cglib;

    opens flow.jfxcore.core to javafx.fxml, cglib;
    opens flow.example to cglib, javafx.fxml;

    exports flow.jfxcore.annotation;
    exports flow.jfxcore.core;
    exports flow.example;

}