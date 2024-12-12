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

    opens com.secinfostore.controller;
    opens com.secinfostore.exception;
    opens com.secinfostore.model;
    opens com.secinfostore.util;

    exports com.secinfostore;
    exports com.secinfostore.controller;
    exports com.secinfostore.controller.interfaces;
    opens com.secinfostore.controller.interfaces;
    exports com.secinfostore.controller.components;
    opens com.secinfostore.controller.components;
}