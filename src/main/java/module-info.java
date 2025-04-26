module org{
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;
    requires javafx.media;
    requires com.google.api.client;
    requires com.google.api.client.json.jackson2;
    requires java.net.http;
    requires java.logging;
    requires jdk.httpserver;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires google.api.client;
    // Google API 相關
    requires com.google.api.client.auth;
    requires com.google.api.client.extensions.java6.auth;
    requires com.google.api.client.extensions.jetty.auth;
    requires google.api.services.calendar.v3.rev411;
    // JSON 解析
    requires java.sql;
    requires com.fasterxml.jackson.core;
    opens org to javafx.fxml;
    exports org;
}