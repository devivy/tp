package tahub.contacts.storage;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import tahub.contacts.model.grade.GradingSystem;

public class GradingSystemStorage {
    private final String filePath;
    private final ObjectMapper objectMapper;

    public GradingSystemStorage(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
    }

    public void saveGradingSystem(GradingSystem gradingSystem) throws IOException {
        ObjectNode rootNode = objectMapper.createObjectNode();

        ObjectNode scoresNode = objectMapper.valueToTree(gradingSystem.getAllGrades());
        rootNode.set("scores", scoresNode);

        ObjectNode weightsNode = objectMapper.valueToTree(gradingSystem.getAllWeights());
        rootNode.set("weights", weightsNode);

        objectMapper.writeValue(new File(filePath), rootNode);
    }

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
