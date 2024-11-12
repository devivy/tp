package tahub.contacts.logic.commands.person;

import static java.util.Objects.requireNonNull;
import static tahub.contacts.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static tahub.contacts.logic.parser.CliSyntax.PREFIX_EMAIL;
import static tahub.contacts.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;
import static tahub.contacts.logic.parser.CliSyntax.PREFIX_NAME;
import static tahub.contacts.logic.parser.CliSyntax.PREFIX_PHONE;
import static tahub.contacts.logic.parser.CliSyntax.PREFIX_TAG;
import static tahub.contacts.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import tahub.contacts.commons.util.CollectionUtil;
import tahub.contacts.commons.util.ToStringBuilder;
import tahub.contacts.logic.Messages;
import tahub.contacts.logic.commands.Command;
import tahub.contacts.logic.commands.CommandResult;
import tahub.contacts.logic.commands.exceptions.CommandException;
import tahub.contacts.model.Model;
import tahub.contacts.model.person.Address;
import tahub.contacts.model.person.Email;
import tahub.contacts.model.person.MatriculationNumber;
import tahub.contacts.model.person.Name;
import tahub.contacts.model.person.Person;
import tahub.contacts.model.person.Phone;
import tahub.contacts.model.studentcourseassociation.StudentCourseAssociation;
import tahub.contacts.model.studentcourseassociation.StudentCourseAssociationList;
import tahub.contacts.model.tag.Tag;

/**
 * Edits the details of an existing person in the address book.
 */
public class PersonEditCommand extends Command {

    public static final String COMMAND_WORD = "person-edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the matriculation number. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: MATRICULATION_NUMBER (must be matriculation number of an existing student) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_MATRICULATION_NUMBER + "A1234567M "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";

    private final MatriculationNumber matriculationNumber;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param matriculationNumber of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public PersonEditCommand(MatriculationNumber matriculationNumber, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(matriculationNumber);
        requireNonNull(editPersonDescriptor);

        this.matriculationNumber = matriculationNumber;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (!containsStudentInPersonList(lastShownList, matriculationNumber)) {
            throw new CommandException(Messages.MESSAGE_PERSON_NOT_FOUND);
        }

        Person personToEdit = getStudentFromPersonList(lastShownList, matriculationNumber);
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        // Get all SCAs associated with the person before editing
        StudentCourseAssociationList scaList = model.getStudentScaList();
        List<StudentCourseAssociation> personScas = scaList.get(personToEdit);

        // Update the person
        model.setPerson(personToEdit, editedPerson);

        // Update all SCAs with the edited person immediately
        for (StudentCourseAssociation oldSca : personScas) {
            StudentCourseAssociation newSca = new StudentCourseAssociation(
                    editedPerson,
                    oldSca.getCourse(),
                    oldSca.getTutorial(),
                    oldSca.getAttendance()
            );
            model.setStudentCourseAssociation(oldSca, newSca);
        }

        // Force an immediate update of the filtered list
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // Return with a flag indicating this was a person edit command
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)));
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());

        return new Person(personToEdit.getMatricNumber(), updatedName,
                updatedPhone, updatedEmail, updatedAddress, updatedTags);
    }

    private static boolean containsStudentInPersonList(List<Person> personList,
                                                       MatriculationNumber matriculationNumber) {
        return personList.stream().anyMatch(person -> person.getMatricNumber().equals(matriculationNumber));
    }

    private static Person getStudentFromPersonList(List<Person> personList, MatriculationNumber matriculationNumber) {
        return personList.stream().filter(person -> person.getMatricNumber()
                .equals(matriculationNumber)).findFirst().get();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonEditCommand)) {
            return false;
        }

        PersonEditCommand otherPersonEditCommand = (PersonEditCommand) other;
        return matriculationNumber.equals(otherPersonEditCommand.matriculationNumber)
                && editPersonDescriptor.equals(otherPersonEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("matriculationNumber", matriculationNumber)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Set<Tag> tags;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            EditPersonDescriptor otherEditPersonDescriptor = (EditPersonDescriptor) other;
            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(address, otherEditPersonDescriptor.address)
                    && Objects.equals(tags, otherEditPersonDescriptor.tags);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("tags", tags)
                    .toString();
        }
    }
}