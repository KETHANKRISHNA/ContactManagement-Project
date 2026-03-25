package com.shadowfox.contact;

import com.shadowfox.contact.model.Contact;
import com.shadowfox.contact.service.ContactManager;
import com.shadowfox.contact.util.VCardUtil;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class App extends Application {

    private ContactManager contactManager;
    private ObservableList<Contact> observableContacts;
    private TableView<Contact> table;

    // UI Controls for Form
    private TextField nameInput;
    private TextField phoneInput;
    private TextField emailInput;
    private TextField searchInput;
    
    // Track selected contact for editing
    private String selectedPhoneNumber = null;

    @Override
    public void start(Stage primaryStage) {
        contactManager = new ContactManager();
        observableContacts = FXCollections.observableArrayList(contactManager.getAllContacts());

        primaryStage.setTitle("Contact Management System");

        // --- Layout ---
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // --- Top: Search Bar ---
        HBox topBox = new HBox(10);
        topBox.setPadding(new Insets(0, 0, 10, 0));
        searchInput = new TextField();
        searchInput.setPromptText("Search by Name...");
        Button searchBtn = new Button("Search");
        Button clearSearchBtn = new Button("Clear Search");
        
        searchBtn.setOnAction(e -> doSearch());
        clearSearchBtn.setOnAction(e -> {
            searchInput.clear();
            refreshTable();
        });
        
        topBox.getChildren().addAll(new Label("Search:"), searchInput, searchBtn, clearSearchBtn);
        root.setTop(topBox);

        // --- Center: Table ---
        table = new TableView<>();
        table.setItems(observableContacts);

        TableColumn<Contact, String> nameCol = new TableColumn<>("Name");
        nameCol.setMinWidth(150);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Contact, String> phoneCol = new TableColumn<>("Phone Number");
        phoneCol.setMinWidth(150);
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        TableColumn<Contact, String> emailCol = new TableColumn<>("Email Address");
        emailCol.setMinWidth(200);
        emailCol.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));

        table.getColumns().addAll(nameCol, phoneCol, emailCol);

        // Double-click to Edit row binding
        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && table.getSelectionModel().getSelectedItem() != null) {
                Contact selected = table.getSelectionModel().getSelectedItem();
                populateForm(selected);
            }
        });

        root.setCenter(table);

        // --- Right: Form & Actions ---
        VBox rightBox = new VBox(10);
        rightBox.setPadding(new Insets(0, 0, 0, 10));

        Label formTitle = new Label("Contact Details");
        formTitle.setStyle("-fx-font-weight: bold;");

        nameInput = new TextField();
        nameInput.setPromptText("Name");
        
        phoneInput = new TextField();
        phoneInput.setPromptText("Phone Number");
        
        emailInput = new TextField();
        emailInput.setPromptText("Email Address");

        Button saveBtn = new Button("Save / Update");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> saveContact());

        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setOnAction(e -> deleteContact());

        Button clearFormBtn = new Button("Clear Form");
        clearFormBtn.setMaxWidth(Double.MAX_VALUE);
        clearFormBtn.setOnAction(e -> clearForm());
        
        Separator separator = new Separator();

        Button importBtn = new Button("Import VCard");
        importBtn.setMaxWidth(Double.MAX_VALUE);
        importBtn.setOnAction(e -> importVCard(primaryStage));

        Button exportBtn = new Button("Export VCard");
        exportBtn.setMaxWidth(Double.MAX_VALUE);
        exportBtn.setOnAction(e -> exportVCard(primaryStage));

        rightBox.getChildren().addAll(
                formTitle, nameInput, phoneInput, emailInput, 
                saveBtn, deleteBtn, clearFormBtn, 
                separator, importBtn, exportBtn
        );

        root.setRight(rightBox);

        // --- Show Scene ---
        Scene scene = new Scene(root, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void refreshTable() {
        observableContacts.setAll(contactManager.getAllContacts());
    }

    private void doSearch() {
        String term = searchInput.getText();
        List<Contact> results = contactManager.searchContacts(term);
        observableContacts.setAll(results);
    }

    private void populateForm(Contact contact) {
        nameInput.setText(contact.getName());
        phoneInput.setText(contact.getPhoneNumber());
        emailInput.setText(contact.getEmailAddress());
        selectedPhoneNumber = contact.getPhoneNumber();
    }

    private void clearForm() {
        nameInput.clear();
        phoneInput.clear();
        emailInput.clear();
        selectedPhoneNumber = null;
        table.getSelectionModel().clearSelection();
    }

    private void saveContact() {
        String name = nameInput.getText();
        String phone = phoneInput.getText();
        String email = emailInput.getText();

        Contact newContact = new Contact(name, phone, email);

        try {
            if (selectedPhoneNumber == null) {
                // Add new
                contactManager.addContact(newContact);
            } else {
                // Update existing
                contactManager.updateContact(selectedPhoneNumber, newContact);
            }
            refreshTable();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Contact saved successfully.");
        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", ex.getMessage());
        }
    }

    private void deleteContact() {
        Contact selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a contact to delete.");
            return;
        }

        boolean success = contactManager.deleteContact(selected.getPhoneNumber());
        if (success) {
            refreshTable();
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete contact.");
        }
    }

    private void importVCard(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open VCard File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("VCard Files", "*.vcf"));
        File file = fileChooser.showOpenDialog(stage);
        
        if (file != null) {
            try {
                Contact contact = VCardUtil.importFromVCard(file);
                
                try {
                    contactManager.addContact(contact);
                    refreshTable();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "VCard imported successfully.");
                } catch (IllegalArgumentException ex) {
                    showAlert(Alert.AlertType.ERROR, "Import Error", "Contact already exists or validation failed: " + ex.getMessage());
                }

            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Import Error", "Failed to parse VCard: " + ex.getMessage());
            }
        }
    }

    private void exportVCard(Stage stage) {
        Contact selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a contact to export.");
            return;
        }

        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select Export Directory");
        File dir = dirChooser.showDialog(stage);

        if (dir != null) {
            try {
                VCardUtil.exportToVCard(selected, dir);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Contact exported to " + dir.getAbsolutePath());
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Export Error", "Failed to export VCard: " + ex.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        // Required for JavaFX application lifecycle
        // Note: Run this via Maven: mvn javafx:run
        launch(args);
    }
}
