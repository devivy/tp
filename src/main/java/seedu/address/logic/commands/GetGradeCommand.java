package seedu.address.logic.commands;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

public class GetGradeCommand extends Command {

    public static final String COMMAND_WORD = "getgrade";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Retrieves the grade for a student. "
            + "Parameters: NAME "
            + "Example: " + COMMAND_WORD + " John Doe";

    public static final String MESSAGE_SUCCESS = "Grade for %1$s: %2$s";

    private final String studentName;

    public GetGradeCommand(String studentName) {
        this.studentName = studentName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        String letterGrade = model.getLetterGrade(studentName);
        double score = model.getStudentScore(studentName);
        return new CommandResult(String.format(MESSAGE_SUCCESS, studentName, letterGrade + " (" + score + ")"));
    }
}