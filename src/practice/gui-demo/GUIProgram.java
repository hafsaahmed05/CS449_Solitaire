package com.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GUIProgram extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create labels
        Label titleLabel = new Label("Temperature Converter");
        Label celsiusLabel = new Label("Celsius:");
        Label fahrenheitLabel = new Label("Fahrenheit:");
        Label resultLabel = new Label("");

        // Create text fields
        TextField celsiusField = new TextField();
        TextField fahrenheitField = new TextField();

        // Create buttons
        Button convertToFButton = new Button("Convert to Fahrenheit");
        Button convertToCButton = new Button("Convert to Celsius");

        // Set button actions
        convertToFButton.setOnAction(e -> {
            try {
                String celsiusText = celsiusField.getText();
                double celsius = Double.parseDouble(celsiusText);
                double fahrenheit = (celsius * 9.0/5.0) + 32;
                fahrenheitField.setText(String.format("%.2f", fahrenheit));
                resultLabel.setText(celsius + "°C = " + String.format("%.2f", fahrenheit) + "°F");
            } catch (NumberFormatException ex) {
                resultLabel.setText("Please enter a valid number!");
            }
        });

        convertToCButton.setOnAction(e -> {
            try {
                String fahrenheitText = fahrenheitField.getText();
                double fahrenheit = Double.parseDouble(fahrenheitText);
                double celsius = (fahrenheit - 32) * 5.0/9.0;
                celsiusField.setText(String.format("%.2f", celsius));
                resultLabel.setText(fahrenheit + "°F = " + String.format("%.2f", celsius) + "°C");
            } catch (NumberFormatException ex) {
                resultLabel.setText("Please enter a valid number!");
            }
        });

        // Create layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.getChildren().addAll(
            titleLabel,
            celsiusLabel,
            celsiusField,
            convertToFButton,
            fahrenheitLabel,
            fahrenheitField,
            convertToCButton,
            resultLabel
        );

        // Create scene and show stage
        Scene scene = new Scene(layout, 300, 350);
        primaryStage.setTitle("Temperature Converter");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}