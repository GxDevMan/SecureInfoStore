module com.secinfostore.secureinfostore {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.persistence;
    requires java.sql;
    requires java.desktop;
    requires javafx.base;
    requires org.hibernate.orm.core;
    requires com.fasterxml.jackson.databind;
    requires java.naming;
    requires javafx.swing;


    opens com.secinfostore.secureinfostore to javafx.fxml;
    opens com.secinfostore.secureinfostore.model;
    exports com.secinfostore.secureinfostore;
}