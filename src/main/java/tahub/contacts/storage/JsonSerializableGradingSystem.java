package tahub.contacts.storage;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import tahub.contacts.commons.exceptions.IllegalValueException;
import tahub.contacts.model.grade.GradingSystem;

/**
 * An Immutable GradingSystem that is serializable to JSON format.
 */
@JsonRootName(value = "gradingsystem")
class JsonSerializableGradingSystem {

    final List<JsonAdaptedGrade> grades = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableGradingSystem} with the given grades.
     */
    @JsonCreator
    public JsonSerializableGradingSystem(@JsonProperty("grades") List<JsonAdaptedGrade> grades) {
        this.grades.addAll(grades);
    }

    /**
     * Converts a given {@code GradingSystem} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableGradingSystem}.
     */
    public JsonSerializableGradingSystem(GradingSystem source) {
        source.getAllGrades().forEach((assessmentName, score) -> {
            double weight = source.getAllWeights().getOrDefault(assessmentName, 1.0);
            grades.add(new JsonAdaptedGrade(assessmentName, score, weight));
        });
    }

    /**
     * Converts this grading system into the model's {@code GradingSystem} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public GradingSystem toModelType() throws IllegalValueException {
        GradingSystem gradingSystem = new GradingSystem();
        for (JsonAdaptedGrade jsonAdaptedGrade : grades) {
            jsonAdaptedGrade.toModelType(gradingSystem);
        }
        return gradingSystem;
    }
}
