package tahub.contacts.logic.commands.attend;

import static java.util.Objects.requireNonNull;
import static tahub.contacts.logic.parser.CliSyntax.PREFIX_COURSE_CODE;
import static tahub.contacts.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;
import static tahub.contacts.logic.parser.CliSyntax.PREFIX_TUTORIAL;

import tahub.contacts.commons.util.ToStringBuilder;
import tahub.contacts.logic.Messages;
import tahub.contacts.logic.commands.Command;
import tahub.contacts.logic.commands.CommandResult;
import tahub.contacts.logic.commands.exceptions.CommandException;
import tahub.contacts.model.Model;
import tahub.contacts.model.course.Attendance;
import tahub.contacts.model.studentcourseassociation.StudentCourseAssociation;
import tahub.contacts.model.studentcourseassociation.StudentCourseAssociationList;
import tahub.contacts.model.studentcourseassociation.exceptions.ScaNotFoundException;

/**
 * Clears the attendance record of a particular student.
 */
public class AttendClearCommand extends Command {

    public static final String COMMAND_WORD = "attend-clear";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Clears the attendance records of "
            + "a student in a particular course and tutorial "
            + "group.\n"
            + "Parameters: "
            + PREFIX_MATRICULATION_NUMBER + "MATRICULATION NUMBER "
            + PREFIX_COURSE_CODE + "COURSE CODE "
            + PREFIX_TUTORIAL + "TUTORIAL ID\n"
            + "(All parameters need to already be created.)\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_MATRICULATION_NUMBER + "A0123456X "
            + PREFIX_COURSE_CODE + "CS1010 "
            + PREFIX_TUTORIAL + "T01 ";

    public static final String MESSAGE_SUCCESS = "Attendance records cleared for student %1$s";
    public static final String MESSAGE_NO_SCA_FOUND = "Student %1$s could not be found.";

    private final StudentCourseAssociation toFind;

    /**
     * Creates a CourseCommand to clear a student's attendance records.
     */
    public AttendClearCommand(StudentCourseAssociation targetSca) {
        requireNonNull(targetSca);
        this.toFind = targetSca;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        StudentCourseAssociationList scaList = model.getScaList();
        StudentCourseAssociation foundSca;

        try {
            foundSca = scaList.findMatch(toFind);
        } catch (ScaNotFoundException e) {
            throw new CommandException(String.format(MESSAGE_NO_SCA_FOUND, Messages.format(toFind)));
        }

        // attendance ops
        Attendance attendance = foundSca.getAttendance();
        requireNonNull(attendance);

        attendance.clear();

        // return success
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toFind)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AttendClearCommand otherAttendClearCommand)) {
            return false;
        }

        return toFind.equals(otherAttendClearCommand.toFind);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetSCA", toFind)
                .toString();
    }
}