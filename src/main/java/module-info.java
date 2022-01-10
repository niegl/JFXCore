module flow.jfxcore {
    requires javafx.fxml;
    requires log4j;
    requires org.apache.commons.lang3;
    requires org.apache.commons.io;
    requires cglib;
    requires javafx.graphics;
    requires javafx.controls;

    opens flow.jfxcore.core to javafx.fxml, cglib;
    opens flow.example to cglib, javafx.fxml;

    exports flow.jfxcore.annotation;
    exports flow.jfxcore.core;
    exports flow.jfxcore.stage;

    exports flow.example;

}