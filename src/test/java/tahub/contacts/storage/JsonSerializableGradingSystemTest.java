package tahub.contacts.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import tahub.contacts.commons.exceptions.IllegalValueException;
import tahub.contacts.model.grade.GradingSystem;

/**
 * Unit tests for {@link JsonSerializableGradingSystem}.
 */
public class JsonSerializableGradingSystemTest {

    private static final JsonAdaptedGrade VALID_GRADE = new JsonAdaptedGrade("Midterm",
                                                                             85.0, 0.4);
    private static final JsonAdaptedGrade VALID_GRADE_2 = new JsonAdaptedGrade("Final",
                                                                               90.0, 0.6);

    @Test
    public void toModelType_validGradingSystem_returnsGradingSystem() throws Exception {
        List<JsonAdaptedGrade> grades = new ArrayList<>();
        grades.add(VALID_GRADE);
        grades.add(VALID_GRADE_2);
        JsonSerializableGradingSystem gradingSystem = new JsonSerializableGradingSystem(grades);

        GradingSystem modelGradingSystem = gradingSystem.toModelType();
        assertEquals(85.0, modelGradingSystem.getGrade("Midterm"));
        assertEquals(90.0, modelGradingSystem.getGrade("Final"));
        assertEquals(0.4, modelGradingSystem.getAllWeights().get("Midterm"));
        assertEquals(0.6, modelGradingSystem.getAllWeights().get("Final"));
    }

    @Test
    public void toModelType_invalidGrade_throwsIllegalValueException() {
        List<JsonAdaptedGrade> grades = new ArrayList<>();
        grades.add(new JsonAdaptedGrade("", 85.0, 0.4)); // Invalid assessment name
        JsonSerializableGradingSystem gradingSystem = new JsonSerializableGradingSystem(grades);
        assertThrows(IllegalValueException.class, gradingSystem::toModelType);
    }
}
