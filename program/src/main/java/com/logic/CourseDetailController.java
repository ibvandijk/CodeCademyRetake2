package com.logic;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import java.io.IOException;

import com.datastorage.CourseDAO;
import com.domain.Course;
import com.domain.Module; 

public class CourseDetailController {

    @FXML
    private Label lblCourseName, lblCourseNumber, lblSubject, lblIntroductionText, lblDifficulty;
    @FXML
    private TableView<Module> tvModules;
    @FXML
    private TableColumn<Module, String> colModuleName, colModuleDescription;
    @FXML
    private Label lblModuleTitle, lblModuleVersion, lblModuleDescription, lblContactPersonName, lblContactPersonEmail, lblAverageProgress;
    @FXML
    private Label lblStudentsCompleted;
    @FXML
    private ComboBox<String> cbDestinationCourseNames;

    @FXML
    private ComboBox<String> cbModuleNames;

    @FXML
    private Button btnAddModule, btnDeleteModule, btnMoveModule;

    @FXML
    private Label lblMaleStudents, lblFemaleStudents;

    private Course selectedCourse;

    @FXML
    void handleButtonAction(ActionEvent event) throws IOException {
        if (event.getSource() == btnAddModule) {
            addModuleToCourse();
        } 
        if (event.getSource() == btnDeleteModule) {
            removeModuleFromCourse();
        } 
        if (event.getSource() == btnMoveModule) {
        Module selectedModule = tvModules.getSelectionModel().getSelectedItem();
        if (selectedModule != null) {
            showMoveModuleDialog(selectedModule.getModuleTitle());
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Selecteer eerst een module om te verplaatsen.");
            alert.showAndWait();
        }
    }
    }

    public void initialize() {
        configureTableView();
        setupModuleSelection();
        cbModuleNames.setItems(FXCollections.observableArrayList(CourseDAO.getModuleNames()));
    }

    private void setupModuleSelection() {
        tvModules.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateModuleDetails(newSelection);
            }
        });
    }

    public void setSelectedCourse(Course course) {
        this.selectedCourse = course;
        displayCourseDetails();
    
        ObservableList<Module> modules = CourseDAO.getModulesForCourse(selectedCourse.getCourseName());
        tvModules.setItems(modules);
    
        int studentsCompleted = CourseDAO.getStudentsCompletedForCourse(selectedCourse.getCourseName());
        lblStudentsCompleted.setText(String.valueOf(studentsCompleted));

        int maleStudents = CourseDAO.getStudentsByGenderForCourse(course.getCourseName(), "M");
        lblMaleStudents.setText("Mannelijke Studenten: " + maleStudents);
    
        int femaleStudents = CourseDAO.getStudentsByGenderForCourse(course.getCourseName(), "F");
        lblFemaleStudents.setText("Vrouwelijke Studenten: " + femaleStudents);
    }
    
    private void configureTableView() {
        System.out.println("Fill up Course Details table");

        colModuleName.setCellValueFactory(new PropertyValueFactory<>("moduleTitle"));
        colModuleDescription.setCellValueFactory(new PropertyValueFactory<>("moduleDescription"));
    }

    private void displayCourseDetails() {
        System.out.println("Fill up Course Details side");

        lblCourseName.setText(selectedCourse.getCourseName());
        lblCourseNumber.setText(String.valueOf(selectedCourse.getCourseNumber()));
        lblSubject.setText(selectedCourse.getSubject());
        lblIntroductionText.setText(selectedCourse.getIntroductionText());
        lblDifficulty.setText(selectedCourse.getDifficulty().name()); 
    }

    private void updateModuleDetails(Module module) {
        System.out.println("Fill up Module Details table");
    
        lblModuleTitle.setText(module.getModuleTitle());
        lblModuleVersion.setText(module.getVersion());
        lblModuleDescription.setText(module.getModuleDescription());
        lblContactPersonName.setText(module.getContactPersonName());
        lblContactPersonEmail.setText(module.getContactPersonEmail());
    
        double averageProgress = CourseDAO.getAverageProgressPercentageForModule(module.getModuleTitle());
        
        lblAverageProgress.setText(String.format("%.2f%%", averageProgress));
    }

    private void addModuleToCourse() {
        System.out.println("Add Module to Course called");

        String courseName = selectedCourse.getCourseName();
        String selectedModule = cbModuleNames.getSelectionModel().getSelectedItem();

        CourseDAO.addModuletoCourse(courseName, selectedModule);

        ObservableList<Module> modules = CourseDAO.getModulesForCourse(selectedCourse.getCourseName());
        cbModuleNames.setItems(FXCollections.observableArrayList(CourseDAO.getModuleNames()));
        tvModules.setItems(modules);
    }

    private void removeModuleFromCourse() {
        System.out.println("Remove Module from Course called");
    
        String selectedModule = tvModules.getSelectionModel().getSelectedItem().getModuleTitle();       
    
        CourseDAO.removeModuleFromCourse(selectedModule);
    
        ObservableList<Module> modules = CourseDAO.getModulesForCourse(selectedCourse.getCourseName());
        cbModuleNames.setItems(FXCollections.observableArrayList(CourseDAO.getModuleNames()));
        tvModules.setItems(modules);
    }

    private void moveModuleToAnotherCourse(String moduleTitle, String destinationCourse) {
        try {
            CourseDAO.moveModuleToCourse(moduleTitle, destinationCourse);
    
            ObservableList<Module> modules = CourseDAO.getModulesForCourse(selectedCourse.getCourseName());
            tvModules.setItems(modules);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Module succesvol verplaatst naar " + destinationCourse + ".");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Fout bij het verplaatsen van de module: " + e.getMessage());
            alert.showAndWait();
        }
    }    
    
    private void showMoveModuleDialog(String selectedModule) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Module Verplaatsen");
        dialog.setHeaderText("Selecteer de bestemmingscursus voor de module: " + selectedModule);
    
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    
        ComboBox<String> cbDestinationCourses = new ComboBox<>();
        if (selectedCourse != null) {
            cbDestinationCourses.setItems(FXCollections.observableArrayList(
                CourseDAO.getCourseNamesExcluding(selectedCourse.getCourseName())));
        }
    
        VBox vbox = new VBox(new Label("Bestemmingscursus:"), cbDestinationCourses);
        vbox.setSpacing(10);
    
        dialogPane.setContent(vbox);
    
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return cbDestinationCourses.getSelectionModel().getSelectedItem();
            }
            return null;
        });
    
        dialog.showAndWait().ifPresent((String destinationCourse) -> {
            if (destinationCourse != null && !destinationCourse.isEmpty()) {
                moveModuleToAnotherCourse(selectedModule, destinationCourse);
            }
        });
    }
    

}