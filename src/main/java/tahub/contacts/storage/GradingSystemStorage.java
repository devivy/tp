package tahub.contacts.storage;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import tahub.contacts.model.grade.GradingSystem;

/**
 * Handles the storage and retrieval of GradingSystem data(uses JSON format for data persistence).
 */
public class GradingSystemStorage {
    private final String filePath;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a new GradingSystemStorage with the specified file path.
     *
     * @param filePath The path where the grading system data will be stored.
     */
    public GradingSystemStorage(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Saves the given GradingSystem to a JSON file.
     *
     * @param gradingSystem The GradingSystem to be saved.
     * @throws IOException If there's an error writing to the file.
     */
    public void saveGradingSystem(GradingSystem gradingSystem) throws IOException {
        ObjectNode rootNode = objectMapper.createObjectNode();

        ObjectNode scoresNode = objectMapper.valueToTree(gradingSystem.getAllGrades());
        rootNode.set("scores", scoresNode);

        ObjectNode weightsNode = objectMapper.valueToTree(gradingSystem.getAllWeights());
        rootNode.set("weights", weightsNode);

        objectMapper.writeValue(new File(filePath), rootNode);
    }

    /**
     * Loads a GradingSystem from a JSON file.
     * If the file doesn't exist, returns a new, empty GradingSystem.
     *
     * @return A GradingSystem populated with data from the file, or a new GradingSystem if the file doesn't exist.
     * @throws IOException If there's an error reading from the file.
     */
    public GradingSystem loadGradingSystem() throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return new GradingSystem();
        }

        ObjectNode rootNode = (ObjectNode) objectMapper.readTree(file);
        GradingSystem gradingSystem = new GradingSystem();

        ObjectNode scoresNode = (ObjectNode) rootNode.get("scores");
        ObjectNode weightsNode = (ObjectNode) rootNode.get("weights");

        scoresNode.fields().forEachRemaining(entry ->
                                                     gradingSystem.addGrade(entry.getKey(),
                                                                            entry.getValue().asDouble()));

        weightsNode.fields().forEachRemaining(entry ->
                                                      gradingSystem.setAssessmentWeight(entry.getKey(),
                                                                                        entry.getValue().asDouble()));

        return gradingSystem;
    }
}