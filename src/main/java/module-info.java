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
    requires com.google.api.client.json.gson;
    // JSON 解析
    // sql
    requires java.sql;
    //Hikari
    requires com.zaxxer.hikari;
    requires com.fasterxml.jackson.core;
    requires org.checkerframework.checker.qual;
    //JSON
    requires com.fasterxml.jackson.databind;
    requires jdk.compiler; // 添加 Jackson 的依賴
    opens org to javafx.fxml;
    exports org;
    exports org.Task;
    opens org.Task to javafx.fxml;
    exports org.TeamTask;
    opens org.TeamTask to javafx.fxml;
    exports org.Node;
    opens org.Node to javafx.fxml;
}