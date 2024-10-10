package seedu.address.logic.commands;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

public class AddGradeCommand extends Command {

    public static final String COMMAND_WORD = "addgrade";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a grade for a student. "
            + "Parameters: NAME SCORE "
            + "Example: " + COMMAND_WORD + " John Doe 85.5";

    public static final String MESSAGE_SUCCESS = "New grade added: %1$s";

    private final String studentName;
    private final double score;

    public AddGradeCommand(String studentName, double score) {
        this.studentName = studentName;
        this.score = score;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        model.addStudentGrade(studentName, score);
        return new CommandResult(String.format(MESSAGE_SUCCESS, studentName));
    }
}
