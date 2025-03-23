package s25.cs151.application;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class HomePage {
    private static Model model = new Model(); // Shared data model

    public static Scene createScene(Stage stage) {
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.TOP_CENTER);
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(10, 10, 10, 10));

        Label titleLabel = new Label("Office Hour Manager");
        titleLabel.setStyle("-fx-font-size: 48px;");
        GridPane.setHalignment(titleLabel, HPos.CENTER);
        pane.add(titleLabel, 0, 0, 2, 1);

        // Semester Dropdown
        Label semesterLabel = new Label("Semester:");
        semesterLabel.setStyle("-fx-font-size: 24px;");
        ComboBox<String> semesterDropdown = new ComboBox<>();
        semesterDropdown.getItems().addAll("Spring", "Summer", "Fall", "Winter");
        semesterDropdown.setValue("Spring"); // Default value
        semesterDropdown.setPrefWidth(250);
        pane.add(semesterLabel, 0, 1);
        pane.add(semesterDropdown, 0, 2);

        // Year Text Field
        Label yearLabel = new Label("Year:");
        yearLabel.setStyle("-fx-font-size: 24px;");
        TextField yearField = new TextField();
        yearField.setPrefWidth(250);
        yearField.setPromptText("Enter a 4-digit year (e.g., 2025)");
        pane.add(yearLabel, 1, 1);
        pane.add(yearField, 1, 2);

        // Days Checkboxes
        Label daysLabel = new Label("Days:");
        daysLabel.setStyle("-fx-font-size: 24px;");
        pane.add(daysLabel, 0, 3);

        CheckBox mondayCheckBox = new CheckBox("Monday");
        CheckBox tuesdayCheckBox = new CheckBox("Tuesday");
        CheckBox wednesdayCheckBox = new CheckBox("Wednesday");
        CheckBox thursdayCheckBox = new CheckBox("Thursday");
        CheckBox fridayCheckBox = new CheckBox("Friday");

        GridPane checkBoxPane = new GridPane();
        checkBoxPane.setHgap(10);
        checkBoxPane.setVgap(10);
        checkBoxPane.add(mondayCheckBox, 0, 0);
        checkBoxPane.add(tuesdayCheckBox, 1, 0);
        checkBoxPane.add(wednesdayCheckBox, 2, 0);
        checkBoxPane.add(thursdayCheckBox, 3, 0);
        checkBoxPane.add(fridayCheckBox, 4, 0);

        pane.add(checkBoxPane, 0, 4, 2, 1);

        // Save Button
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            // Save semester
            model.setSemester(semesterDropdown.getValue());

            // Save year
            model.setYear(yearField.getText());

            // Save selected days
            model.clearSelectedDays();
            if (mondayCheckBox.isSelected()) model.addSelectedDay("Monday");
            if (tuesdayCheckBox.isSelected()) model.addSelectedDay("Tuesday");
            if (wednesdayCheckBox.isSelected()) model.addSelectedDay("Wednesday");
            if (thursdayCheckBox.isSelected()) model.addSelectedDay("Thursday");
            if (fridayCheckBox.isSelected()) model.addSelectedDay("Friday");

            // Check if required fields have input, check for duplicates, save to CSV and show confirmation
            if (model.getYear() == null || model.getYear().isEmpty()){
                showAlert("Empty Entry", "Must enter a year.");
            }
            else if (model.getSelectedDays() == null || model.getSelectedDays().isEmpty()){
                showAlert("Empty Entry", "Must select a day.");
            }
            else if (!isDuplicate(model)) {
                showAlert("Duplicate Entry", "This entry already exists.");
            }
            else if(model.getSelectedDaysCount() > 1){
                showAlert("Only one entry allowed", "Please limit your entry count to one day.");
            }
            else {
                saveToCSV(model);
                showAlert("Success", "Data saved successfully!");
            }
        });
        pane.add(saveButton, 0, 5);

        // Back Button
        Button backButton = new Button("Back to Landing Page");
        backButton.setOnAction(e -> stage.setScene(LandingPage.createScene(stage)));
        pane.add(backButton, 1, 5);

        return new Scene(pane, 800, 600);
    }

    private static void saveToCSV(Model model) {
        String csvFile = "office_hours_data.csv";
        try (FileWriter writer = new FileWriter(csvFile, true)) { // Append mode
            // Write header if file is empty
            if (model.getSemester() != null && model.getYear() != null && !model.getSelectedDays().isEmpty()) {
                writer.append("Semester,Year,Days\n");
            }

            // Write data
            writer.append(model.getSemester()).append(",");
            writer.append(model.getYear()).append(",");
            writer.append(String.join(";", model.getSelectedDays())).append("\n");
        } catch (IOException e) {
            showAlert("Error", "Failed to save data to CSV file.");
            e.printStackTrace();
        }
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static Model getModel() {
        return model;
    }

    private static boolean isDuplicate(Model model) {
        try (BufferedReader reader = new BufferedReader(new FileReader("office_hours_data.csv"))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    // Skip the header line
                    isHeader = false;
                    continue;
                }
                // Split CSV line into components
                String[] parts = line.split(",");

                // Check duplicates of only semester and year combinations
                String semester = parts[0];
                String year = parts[1];

                if (model.getSemester().equals(semester) && model.getYear().equals(year)){
                    return false;
                }
            }
        } catch (IOException e) {
            Label errorLabel = new Label("Duplicate Entry.");
            errorLabel.setStyle("-fx-font-size: 16px;");
        }
        return true;
    }
}
