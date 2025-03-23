package s25.cs151.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;

public class LandingPage {
    public static Scene createScene(Stage stage) {
        // Main container
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));

        // Title
        Label titleLabel = new Label("Office Hour Manager - Landing Page");
        titleLabel.setStyle("-fx-font-size: 24px;");

        // TableView to display CSV data
        TableView<OfficeHoursEntry> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Columns for TableView
        TableColumn<OfficeHoursEntry, String> semesterColumn = new TableColumn<>("Semester");
        semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));

        TableColumn<OfficeHoursEntry, String> yearColumn = new TableColumn<>("Year");
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));

        TableColumn<OfficeHoursEntry, String> daysColumn = new TableColumn<>("Days");
        daysColumn.setCellValueFactory(new PropertyValueFactory<>("days"));

        // Add columns to TableView
        tableView.getColumns().addAll(semesterColumn, yearColumn, daysColumn);

        // Load data from CSV into TableView
        ObservableList<OfficeHoursEntry> data = FXCollections.observableArrayList();
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
                if (parts.length == 3) {
                    String semester = parts[0];
                    String year = parts[1];
                    String days = parts[2].replace(";", ", ");

                    // Add data to the observable list
                    data.add(new OfficeHoursEntry(semester, year, days));
                }
            }
        } catch (IOException e) {
            // Show error message if CSV file cannot be read
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to read CSV file.");
            alert.showAndWait();
        }

        // Sort data by year (descending) and then by semester (descending)
        data.sort(Comparator
                .comparing(OfficeHoursEntry::getYear, Comparator.reverseOrder())
                .thenComparing(OfficeHoursEntry::getSemester, Comparator.reverseOrder())
        );

        // Set data to TableView
        tableView.setItems(data);

        // Date Picker
        DatePicker datePicker = new DatePicker(LocalDate.now());

        // Home Button
        Button homeButton = new Button("Go to Homepage");
        homeButton.setOnAction(e -> stage.setScene(HomePage.createScene(stage)));

        // Add all components to the main layout
        layout.getChildren().addAll(titleLabel, tableView, datePicker, homeButton);

        return new Scene(layout, 1250, 750);
    }

    // Data model class for TableView
    public static class OfficeHoursEntry {
        private final String semester;
        private final String year;
        private final String days;

        public OfficeHoursEntry(String semester, String year, String days) {
            this.semester = semester;
            this.year = year;
            this.days = days;
        }

        public String getSemester() {
            return semester;
        }

        public String getYear() {
            return year;
        }

        public String getDays() {
            return days;
        }
    }
}