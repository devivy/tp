package tahub.contacts.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tahub.contacts.model.grade.GradingSystem;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GradingSystemStorageTest {
    private static final String TEST_FILE_PATH = "test_grades.json";
    private GradingSystemStorage storage;
    private GradingSystem gradingSystem;

    @BeforeEach
    void setUp() {
        storage = new GradingSystemStorage(TEST_FILE_PATH);
        gradingSystem = new GradingSystem();
        gradingSystem.addGrade("Midterm", 85.0);
        gradingSystem.addGrade("Final", 90.0);
        gradingSystem.setAssessmentWeight("Midterm", 0.4);
        gradingSystem.setAssessmentWeight("Final", 0.6);
    }

    @AfterEach
    void tearDown() {
        new File(TEST_FILE_PATH).delete();
    }

    @Test
    void testSaveAndLoadGradingSystem() throws IOException {
        storage.saveGradingSystem(gradingSystem);

        GradingSystem loadedSystem = storage.loadGradingSystem();

        assertEquals(85.0, loadedSystem.getGrade("Midterm"), 0.001);
        assertEquals(90.0, loadedSystem.getGrade("Final"), 0.001);
        assertEquals(88.0, loadedSystem.getOverallScore(), 0.001);
    }

    @Test
    void testLoadNonExistentFile() throws IOException {
        GradingSystem loadedSystem = storage.loadGradingSystem();
        assertEquals(-1.0, loadedSystem.getOverallScore(), 0.001);
    }
}
