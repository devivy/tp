package seedu.address.logic.parser;

import seedu.address.logic.commands.GetGradeCommand;
import seedu.address.logic.parser.exceptions.ParseException;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

public class GetGradeCommandParser implements Parser<GetGradeCommand> {

    @Override
    public GetGradeCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, GetGradeCommand.MESSAGE_USAGE));
        }

        return new GetGradeCommand(trimmedArgs);
    }
}