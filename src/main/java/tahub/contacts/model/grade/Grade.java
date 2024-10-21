package tahub.contacts.model.grade;

import java.util.Objects;

/**
 * Represents a Grade in the grading system.
 * A grade consists of an assessment name, a score percentage, and a weight.
 * This class is immutable to ensure consistency of grade data.
 */
public class Grade {
    private final String assessmentName;
    private final double scorePercentage;
    private final double weight;

    /**
     * Constructs a new {@code Grade} instance.
     *
     * @param assessmentName The name of the assessment. Must not be null or empty.
     * @param scorePercentage The score as a percentage. Must be between 0 and 100, inclusive.
     * @param weight The weight of this grade in the overall grading scheme. Must be between 0 and 1, inclusive.
     * @throws IllegalArgumentException if any of the parameters are invalid.
     */
    public Grade(String assessmentName, double scorePercentage, double weight) {
        this.assessmentName = assessmentName;
        this.scorePercentage = scorePercentage;
        this.weight = weight;
    }

    /**
     * Returns the name of the assessment.
     *
     * @return The assessment name.
     */
    public String getAssessmentName() {
        return assessmentName;
    }

    /**
     * Returns the score as a percentage.
     *
     * @return The score percentage, a value between 0 and 100.
     */
    public double getScorePercentage() {
        return scorePercentage;
    }

    /**
     * Returns the weight of this grade.
     *
     * @return The weight of the grade, a value between 0 and 1.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * The equality is based on the assessment name, score percentage, and weight.
     *
     * @param o the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grade grade = (Grade) o;
        return Double.compare(grade.scorePercentage, scorePercentage) == 0 &&
                Double.compare(grade.weight, weight) == 0 &&
                Objects.equals(assessmentName, grade.assessmentName);
    }

    /**
     * Returns a hash code value for the object.
     * This method is supported for the benefit of hash tables such as those provided by HashMap.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(assessmentName, scorePercentage, weight);
    }

    /**
     * Returns a string representation of the Grade.
     *
     * @return a string representation of the Grade.
     */
    @Override
    public String toString() {
        return String.format("Grade{assessmentName='%s', scorePercentage=%.2f, weight=%.2f}",
                             assessmentName, scorePercentage, weight);
    }
}
