package tahub.contacts.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tahub.contacts.model.grade.GradingSystem;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for GradingSystemStorage.
 * This class tests the functionality of saving and loading GradingSystem data.
 */
class GradingSystemStorageTest {
    private static final String TEST_FILE_PATH = "test_grades.json";
    private GradingSystemStorage storage;
    private GradingSystem gradingSystem;

    /**
     * Set up the test environment before each test.
     * Initializes a new GradingSystemStorage and GradingSystem with sample data.
     */
    @BeforeEach
    void setUp() {
        storage = new GradingSystemStorage(TEST_FILE_PATH);
        gradingSystem = new GradingSystem();
        gradingSystem.addGrade("Midterm", 85.0);
        gradingSystem.addGrade("Final", 90.0);
        gradingSystem.setAssessmentWeight("Midterm", 0.4);
        gradingSystem.setAssessmentWeight("Final", 0.6);
    }

    /**
     * Clean up the test environment after each test.
     * Deletes the test file created during the test.
     */
    @AfterEach
    void tearDown() {
        new File(TEST_FILE_PATH).delete();
    }

    /**
     * Test saving and loading a GradingSystem.
     * Verifies that the loaded system matches the original system.
     *
     * @throws IOException if there's an error in file operations
     */
    @Test
    void testSaveAndLoadGradingSystem() throws IOException {
        storage.saveGradingSystem(gradingSystem);

        GradingSystem loadedSystem = storage.loadGradingSystem();

        assertEquals(85.0, loadedSystem.getGrade("Midterm"), 0.001);
        assertEquals(90.0, loadedSystem.getGrade("Final"), 0.001);
        assertEquals(88.0, loadedSystem.getOverallScore(), 0.001);
    }

    /**
     * Test loading from a non-existent file.
     * Verifies that a new, empty GradingSystem is created when the file doesn't exist.
     *
     * @throws IOException if there's an error in file operations
     */
    @Test
    void testLoadNonExistentFile() throws IOException {
        GradingSystem loadedSystem = storage.loadGradingSystem();
        assertEquals(-1.0, loadedSystem.getOverallScore(), 0.001);
    }
}
