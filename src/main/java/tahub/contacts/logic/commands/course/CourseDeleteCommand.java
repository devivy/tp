package tahub.contacts.logic.commands.course;

import static java.util.Objects.requireNonNull;
import static tahub.contacts.logic.parser.CliSyntax.PREFIX_COURSE_CODE;
import static tahub.contacts.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import tahub.contacts.commons.util.ToStringBuilder;
import tahub.contacts.logic.Messages;
import tahub.contacts.logic.commands.Command;
import tahub.contacts.logic.commands.CommandResult;
import tahub.contacts.logic.commands.exceptions.CommandException;
import tahub.contacts.model.Model;
import tahub.contacts.model.course.Course;
import tahub.contacts.model.course.CourseCode;
import tahub.contacts.model.course.UniqueCourseList;
import tahub.contacts.model.person.Person;
import tahub.contacts.model.studentcourseassociation.StudentCourseAssociation;
import tahub.contacts.model.studentcourseassociation.StudentCourseAssociationList;

import java.util.HashSet;
import java.util.Set;

public class CourseDeleteCommand extends Command {

    public static final String COMMAND_WORD = "course-delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the course identified by its course code.\n"
            + "Parameters: " + PREFIX_COURSE_CODE + "COURSE_CODE (must be course code of an existing course)\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_COURSE_CODE + "CS1101S ";

    public static final String MESSAGE_DELETE_COURSE_SUCCESS = "Deleted Course: %1$s";

    private final CourseCode courseCode;

    public CourseDeleteCommand(CourseCode courseCode) {
        requireNonNull(courseCode);
        this.courseCode = courseCode;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        UniqueCourseList courseList = model.getCourseList();

        if (courseList.containsCourseWithCourseCode(courseCode)) {
            throw new CommandException(Messages.MESSAGE_NO_EXISTING_COURSE);
        }

        Course courseToDelete = courseList.getCourseWithCourseCode(courseCode);

        // Get all persons affected by this course deletion
        Set<Person> affectedPersons = new HashSet<>();
        StudentCourseAssociationList scaList = model.getScaList();

        // First collect all affected persons
        for (StudentCourseAssociation sca : scaList.get()) {
            if (sca.getCourse().equals(courseToDelete)) {
                affectedPersons.add(sca.getStudent());
            }
        }

        // Remove all SCAs for this course
        for (StudentCourseAssociation sca : scaList.get()) {
            if (sca.getCourse().equals(courseToDelete)) {
                model.deleteSca(sca);
            }
        }

        // Delete the course
        model.deleteCourse(courseToDelete);

        // Force refresh of all affected persons
        for (Person person : affectedPersons) {
            model.setPerson(person, person);
        }

        // Final UI refresh
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(String.format(MESSAGE_DELETE_COURSE_SUCCESS,
                                               Messages.format(courseToDelete)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CourseDeleteCommand)) {
            return false;
        }

        CourseDeleteCommand otherCourseDeleteCommand = (CourseDeleteCommand) other;
        return courseCode.equals(otherCourseDeleteCommand.courseCode);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("courseCode", courseCode)
                .toString();
    }
}
