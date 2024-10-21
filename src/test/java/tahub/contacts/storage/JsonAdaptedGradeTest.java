package tahub.contacts.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import tahub.contacts.commons.exceptions.IllegalValueException;
import tahub.contacts.model.grade.GradingSystem;

/**
 * Unit tests for {@link JsonAdaptedGrade}.
 */
public class JsonAdaptedGradeTest {

    private static final String VALID_ASSESSMENT_NAME = "Midterm";
    private static final double VALID_SCORE = 85.0;
    private static final double VALID_WEIGHT = 0.4;

    private static final String INVALID_ASSESSMENT_NAME = "";
    private static final double INVALID_SCORE_LOW = -1.0;
    private static final double INVALID_SCORE_HIGH = 101.0;
    private static final double INVALID_WEIGHT_LOW = -0.1;
    private static final double INVALID_WEIGHT_HIGH = 1.1;

    /**
     * Test for successful conversion of JsonAdaptedGrade to model type.
     */
    @Test
    public void toModelType_validGradeDetails_success() throws Exception {
        JsonAdaptedGrade grade = new JsonAdaptedGrade(VALID_ASSESSMENT_NAME, VALID_SCORE, VALID_WEIGHT);
        GradingSystem gradingSystem = new GradingSystem();
        grade.toModelType(gradingSystem);
        assertEquals(VALID_SCORE, gradingSystem.getGrade(VALID_ASSESSMENT_NAME));
        assertEquals(VALID_WEIGHT, gradingSystem.getAllWeights().get(VALID_ASSESSMENT_NAME));
    }

    /**
     * Test for IllegalValueException when assessment name is invalid.
     */
    @Test
    public void toModelType_invalidAssessmentName_throwsIllegalValueException() {
        JsonAdaptedGrade grade = new JsonAdaptedGrade(INVALID_ASSESSMENT_NAME, VALID_SCORE, VALID_WEIGHT);
        GradingSystem gradingSystem = new GradingSystem();
        assertThrows(IllegalValueException.class, () -> grade.toModelType(gradingSystem));
    }

    /**
     * Test for IllegalValueException when score is too low.
     */
    @Test
    public void toModelType_invalidScoreLow_throwsIllegalValueException() {
        JsonAdaptedGrade grade = new JsonAdaptedGrade(VALID_ASSESSMENT_NAME, INVALID_SCORE_LOW, VALID_WEIGHT);
        GradingSystem gradingSystem = new GradingSystem();
        assertThrows(IllegalValueException.class, () -> grade.toModelType(gradingSystem));
    }

    /**
     * Test for IllegalValueException when score is too high.
     */
    @Test
    public void toModelType_invalidScoreHigh_throwsIllegalValueException() {
        JsonAdaptedGrade grade = new JsonAdaptedGrade(VALID_ASSESSMENT_NAME, INVALID_SCORE_HIGH, VALID_WEIGHT);
        GradingSystem gradingSystem = new GradingSystem();
        assertThrows(IllegalValueException.class, () -> grade.toModelType(gradingSystem));
    }

    /**
     * Test for IllegalValueException when weight is too low.
     */
    @Test
    public void toModelType_invalidWeightLow_throwsIllegalValueException() {
        JsonAdaptedGrade grade = new JsonAdaptedGrade(VALID_ASSESSMENT_NAME, VALID_SCORE, INVALID_WEIGHT_LOW);
        GradingSystem gradingSystem = new GradingSystem();
        assertThrows(IllegalValueException.class, () -> grade.toModelType(gradingSystem));
    }

    /**
     * Test for IllegalValueException when weight is too high.
     */
    @Test
    public void toModelType_invalidWeightHigh_throwsIllegalValueException() {
        JsonAdaptedGrade grade = new JsonAdaptedGrade(VALID_ASSESSMENT_NAME, VALID_SCORE, INVALID_WEIGHT_HIGH);
        GradingSystem gradingSystem = new GradingSystem();
        assertThrows(IllegalValueException.class, () -> grade.toModelType(gradingSystem));
    }
}
