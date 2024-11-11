package tahub.contacts.ui;

import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import tahub.contacts.commons.core.LogsCenter;
import tahub.contacts.logic.Logic;
import tahub.contacts.model.person.Person;
import tahub.contacts.model.course.AttendanceSession;
import tahub.contacts.model.studentcourseassociation.StudentCourseAssociation;

/**
 * A JavaFX window that displays and manages attendance information for a student's courses.
 */
public class AttendanceWindow extends UiPart<Stage> {
    private static final Logger logger = LogsCenter.getLogger(AttendanceWindow.class);
    private static final String FXML = "AttendanceWindow.fxml";

    private final Logic logic;
    private final Person person;
    private StudentCourseAssociation currentSca;
    private ListChangeListener<StudentCourseAssociation> scaListChangeListener;

    @FXML
    private ComboBox<StudentCourseAssociation> courseComboBox;
    @FXML
    private Label courseCode;
    @FXML
    private Label courseName;
    @FXML
    private Label tutorialCode;
    @FXML
    private Label tutorialName;
    @FXML
    private ListView<AttendanceSession> attendanceListView;
    @FXML
    private Label attendancePercentage;
    @FXML
    private Label attendanceCount;

    /**
     * Creates a new AttendanceWindow.
     */
    public AttendanceWindow(Person person, Logic logic, Stage root) {
        super(FXML, root);
        this.logic = logic;
        this.person = person;
        attendanceListView.setCellFactory(listView -> new AttendanceListCell());
        setupScaListChangeListener();
        setupCourseComboBox();

        // Add listener to the stage's showing property to refresh when shown
        root.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                refreshDisplay();
            }
        });
    }

    /**
     * Creates a new AttendanceWindow with a new Stage.
     */
    public AttendanceWindow(Person person, Logic logic) {
        this(person, logic, new Stage());
    }

    /**
     * Sets up a listener to monitor changes in the SCA list.
     */
    private void setupScaListChangeListener() {
        scaListChangeListener = change -> {
            while (change.next()) {
                if (change.wasUpdated() || change.wasAdded() || change.wasRemoved()) {
                    // Use Platform.runLater to ensure UI updates happen on the JavaFX Application Thread
                    if (getRoot().isShowing()) {
                        javafx.application.Platform.runLater(this::refreshDisplay);
                    }
                }
            }
        };

        // Add the listener to the observable list
        logic.getStudentScas(person).asUnmodifiableObservableList()
                .addListener(scaListChangeListener);
    }

    /**
     * Sets up the course selection ComboBox.
     */
    private void setupCourseComboBox() {
        ObservableList<StudentCourseAssociation> scaList = this.logic.getStudentScas(person)
                .getByMatric(person.getMatricNumber().value);

        courseComboBox.setItems(scaList);

        courseComboBox.setCellFactory(lv -> new javafx.scene.control.ListCell<StudentCourseAssociation>() {
            @Override
            protected void updateItem(StudentCourseAssociation sca, boolean empty) {
                super.updateItem(sca, empty);
                setText(empty || sca == null ? null :
                                sca.getCourse().courseCode + ": " + sca.getCourse().courseName);
            }
        });

        courseComboBox.setButtonCell(new javafx.scene.control.ListCell<StudentCourseAssociation>() {
            @Override
            protected void updateItem(StudentCourseAssociation sca, boolean empty) {
                super.updateItem(sca, empty);
                setText(empty || sca == null ? null :
                                sca.getCourse().courseCode + ": " + sca.getCourse().courseName);
            }
        });

        courseComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentSca = newValue;
                refreshDisplay();
            }
        });

        if (!scaList.isEmpty()) {
            courseComboBox.getSelectionModel().select(0);
            currentSca = scaList.get(0);
            refreshDisplay();
        } else {
            showNoCourseMessage();
        }
    }

    /**
     * Refreshes the display with current data.
     */
    private void refreshDisplay() {
        if (currentSca != null) {
            // Get the latest version of the current SCA
            ObservableList<StudentCourseAssociation> currentList =
                    logic.getStudentScas(person).getByMatric(person.getMatricNumber().value);

            StudentCourseAssociation latestSca = currentList.stream()
                    .filter(sca -> sca.isSameSca(currentSca))
                    .findFirst()
                    .orElse(currentSca);

            // Update currentSca with the latest version
            currentSca = latestSca;

            courseCode.setText(String.valueOf(latestSca.getCourse().courseCode));
            courseName.setText(String.valueOf(latestSca.getCourse().courseName));
            tutorialCode.setText(latestSca.getTutorial().getTutorialId());
            tutorialName.setText(String.valueOf(latestSca.getTutorial().getCourse().courseName));

            attendanceListView.setItems(FXCollections.observableArrayList(
                    latestSca.getAttendance().getAttendanceList()));

            int attended = latestSca.getAttendance().getAttendanceAttendedCount();
            int total = latestSca.getAttendance().getAttendanceTotalCount();
            double percentage = total == 0 ? 0 : (double) attended / total * 100;

            attendanceCount.setText(String.format("%d/%d sessions", attended, total));
            attendancePercentage.setText(String.format("%.1f%%", percentage));
        }
    }

    private void showNoCourseMessage() {
        courseCode.setText("");
        courseName.setText("Student " + person.getName() + " has no course associations.");
        tutorialCode.setText("");
        tutorialName.setText("");
        attendanceCount.setText("0/0 sessions");
        attendancePercentage.setText("0%");
        attendanceListView.setItems(FXCollections.observableArrayList());
    }

    @FXML
    private void handleMarkPresent() {
        if (currentSca != null) {
            currentSca.getAttendance().addAttendedLesson();
            refreshDisplay();
        }
    }

    @FXML
    private void handleMarkAbsent() {
        if (currentSca != null) {
            currentSca.getAttendance().addAbsentLesson();
            refreshDisplay();
        }
    }

    public void show() {
        logger.fine("Showing attendance window.");
        getRoot().show();
        getRoot().centerOnScreen();
    }

    public boolean isShowing() {
        return getRoot().isShowing();
    }

    public void hide() {
        getRoot().hide();
    }

    public void focus() {
        getRoot().requestFocus();
    }

    /**
     * Cleanup method to remove listeners when the window is closed.
     */
    public void cleanup() {
        if (scaListChangeListener != null) {
            logic.getStudentScas(person).asUnmodifiableObservableList()
                    .removeListener(scaListChangeListener);
        }
    }
}
