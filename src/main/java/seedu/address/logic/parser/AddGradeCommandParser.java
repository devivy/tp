package seedu.address.logic.parser;

import java.util.Arrays;
import seedu.address.logic.commands.AddGradeCommand;
import seedu.address.logic.parser.exceptions.ParseException;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

public class AddGradeCommandParser implements Parser<AddGradeCommand> {

    @Override
    public AddGradeCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        String[] nameAndScore = trimmedArgs.split("\\s+");

        if (nameAndScore.length < 2) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddGradeCommand.MESSAGE_USAGE));
        }

        String studentName = String.join(" ", Arrays.copyOfRange(nameAndScore, 0, nameAndScore.length - 1));
        try {
            double score = Double.parseDouble(nameAndScore[nameAndScore.length - 1]);
            return new AddGradeCommand(studentName, score);
        } catch (NumberFormatException e) {
            throw new ParseException("Score must be a valid number.");
        }
    }
}