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

    opens com.secinfostore.secureinfostore.controller;
    opens com.secinfostore.secureinfostore.exception;
    opens com.secinfostore.secureinfostore.model;
    opens com.secinfostore.secureinfostore.util;

    exports com.secinfostore.secureinfostore;
    exports com.secinfostore.secureinfostore.controller;
}