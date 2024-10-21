package tahub.contacts.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import tahub.contacts.commons.exceptions.IllegalValueException;

/**
 * Jackson-friendly version of a grade entry.
 */
class JsonAdaptedGrade {

    private final String assessmentName;
    private final double score;
    private final double weight;

    /**
     * Constructs a {@code JsonAdaptedGrade} with the given grade details.
     */
    @JsonCreator
    public JsonAdaptedGrade(@JsonProperty("assessmentName") String assessmentName,
                            @JsonProperty("score") double score,
                            @JsonProperty("weight") double weight) {
        this.assessmentName = assessmentName;
        this.score = score;
        this.weight = weight;
    }

    public String getAssessmentName() {
        return assessmentName;
    }

    public double getScore() {
        return score;
    }

    public double getWeight() {
        return weight;
    }

    /**
     * Converts this Jackson-friendly adapted grade object into the model's grade data.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted grade.
     */
    public void toModelType(tahub.contacts.model.grade.GradingSystem gradingSystem) throws IllegalValueException {
        if (assessmentName == null || assessmentName.trim().isEmpty()) {
            throw new IllegalValueException("Assessment name cannot be null or empty.");
        }
        if (score < 0 || score > 100) {
            throw new IllegalValueException("Score must be between 0 and 100.");
        }
        if (weight < 0 || weight > 1) {
            throw new IllegalValueException("Weight must be between 0 and 1.");
        }

        gradingSystem.addGrade(assessmentName, score);
        gradingSystem.setAssessmentWeight(assessmentName, weight);
    }
}
