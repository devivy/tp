package tahub.contacts.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import tahub.contacts.commons.exceptions.IllegalValueException;
import tahub.contacts.model.grade.GradingSystem;

/**
 * Unit tests for {@link JsonSerializableGradingSystem}.
 */
public class JsonSerializableGradingSystemTest {

    private static final JsonAdaptedGrade VALID_GRADE = new JsonAdaptedGrade("Midterm", 85.0, 0.4);
    private static final JsonAdaptedGrade VALID_GRADE_2 = new JsonAdaptedGrade("Final", 90.0, 0.6);

    /**
     * Test for successful conversion of JsonSerializableGradingSystem to model type.
     */
    @Test
    public void toModelType_validGradingSystem_success() throws Exception {
        List<JsonAdaptedGrade> gradeList = new ArrayList<>();
        gradeList.add(VALID_GRADE);
        gradeList.add(VALID_GRADE_2);
        JsonSerializableGradingSystem gradingSystem = new JsonSerializableGradingSystem(gradeList);

        GradingSystem modelGradingSystem = gradingSystem.toModelType();
        assertEquals(85.0, modelGradingSystem.getGrade("Midterm"));
        assertEquals(90.0, modelGradingSystem.getGrade("Final"));
        assertEquals(0.4, modelGradingSystem.getAllWeights().get("Midterm"));
        assertEquals(0.6, modelGradingSystem.getAllWeights().get("Final"));
    }

    /**
     * Test for successful conversion of GradingSystem to JsonSerializableGradingSystem.
     */
    @Test
    public void constructor_validGradingSystem_success() {
        GradingSystem gradingSystem = new GradingSystem();
        gradingSystem.addGrade("Midterm", 85.0);
        gradingSystem.setAssessmentWeight("Midterm", 0.4);
        gradingSystem.addGrade("Final", 90.0);
        gradingSystem.setAssessmentWeight("Final", 0.6);

        JsonSerializableGradingSystem jsonGradingSystem = new JsonSerializableGradingSystem(gradingSystem);

        assertEquals(2, jsonGradingSystem.grades.size());
        assertTrue(jsonGradingSystem.grades.stream().anyMatch(grade ->
                                                                      grade.getAssessmentName().equals("Midterm")
                                                                              && grade.getScore() == 85.0
                                                                              && grade.getWeight() == 0.4));
        assertTrue(jsonGradingSystem.grades.stream().anyMatch(grade ->
                                                                      grade.getAssessmentName().equals("Final")
                                                                              && grade.getScore() == 90.0
                                                                              && grade.getWeight() == 0.6));
    }

    /**
     * Test for IllegalValueException when there's an invalid grade in the system.
     */
    @Test
    public void toModelType_invalidGrade_throwsIllegalValueException() {
        List<JsonAdaptedGrade> gradeList = new ArrayList<>();
        gradeList.add(new JsonAdaptedGrade("", 85.0, 0.4)); // Invalid assessment name
        JsonSerializableGradingSystem gradingSystem = new JsonSerializableGradingSystem(gradeList);

        assertThrows(IllegalValueException.class, gradingSystem::toModelType);
    }
}
