package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.RsvpStatus;
import seedu.address.model.person.RsvpedPredicate;
import seedu.address.model.person.TagContainsKeywordsPredicate;
import seedu.address.model.tag.Tag;


/**
 * Filters the address book by a given prefix and displays the filtered list to the user
 * Users can only filter by one field at a time
 */
public class FilterCommand extends UndoableCommand {
    public static final String COMMAND_WORD = "filter";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Filters the displayed list with the given criteria\n"
            + "Parameters: [s/RSVPSTATUS] [t/TAG]...\n" + "At least one parameter must be provided.\n"
            + "Example: " + COMMAND_WORD + " s/1 t/bride's side";
    public static final String MESSAGE_TAG_NOT_CREATED = "Tag(s) must be created first using 'newtag' command: ";
    public static final String MESSAGE_TAG_FILTER_ALREADY_EXISTS = "The following tag(s) are already being filtered: ";
    public static final String MESSAGE_EXISTING_STATUS_FILTERED = "The following RSVP status(es) are not filtered "
            + "as a RSVP status currently being filtered. Clear existing filter(s) first: ";
    public static final String MESSAGE_STATUS_ALREADY_BEING_FILTERED = "The following RSVP status(es) are already "
            + "being filtered: ";

    private final Set<Tag> tagSet;
    private final Set<RsvpStatus> statusSet;
    private final Set<Predicate<Person>> predicateSet = new HashSet<>();
    private Predicate<Person> previousPredicate;
    private final Set<Tag> validTagSet = new HashSet<>();
    private final Set<RsvpStatus> validStatusSet = new HashSet<>();
    private final Set<Tag> invalidTagSet = new HashSet<>();
    private final Set<Tag> missingTagSet = new HashSet<>();
    private final Set<RsvpStatus> invalidStatusSet = new HashSet<>();
    private final Set<RsvpStatus> duplicateStatusSet = new HashSet<>();

    private final StringBuilder successMessage = new StringBuilder();
    private final StringBuilder failureMessage = new StringBuilder();
    private final StringBuilder finalMessage = new StringBuilder();

    private Predicate<Person> predicate = new Predicate<Person>() {
        @Override
        public boolean test(Person person) {
            return true;
        }
    };

    /**
     * Creates a FilterCommand with the given set of tags and RSVP statuses to filter by.
     */
    public FilterCommand(Set<Tag> tagSet, Set<RsvpStatus> statusSet) {
        requireNonNull(tagSet);
        requireNonNull(statusSet);
        this.tagSet = tagSet;
        this.statusSet = statusSet;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        previousPredicate = model.getCurrentPredicate();

        for (Tag tag: tagSet) {
            if (!model.hasTag(tag)) {
                missingTagSet.add(tag);
            } else if (model.checkTagFilterAlreadyExists(tag)) {
                invalidTagSet.add(tag);
            } else {
                validTagSet.add(tag);
            }
        }

        for (RsvpStatus status: statusSet) {
            if (model.checkStatusFilterAlreadyExists()) {
                if (model.checkSpecificStatusFilterAlreadyExists(status)) {
                    duplicateStatusSet.add(status);
                } else {
                    invalidStatusSet.add(status);
                }
            } else {
                validStatusSet.add(status);
            }
        }

        predicateSet.addAll(validTagSet.stream().map(TagContainsKeywordsPredicate::new).toList());
        predicateSet.addAll(validStatusSet.stream().map(RsvpedPredicate::new).toList());

        for (Predicate<Person> predicateToAdd: predicateSet) {
            predicate = predicate.and(predicateToAdd);
        }

        model.addTagFilters(validTagSet);
        model.addStatusFilters(validStatusSet);
        model.updateFilteredPersonList(predicate);


        updateSuccessMessage(model);
        updateFailedFiltersMessage();

        finalMessage.append(successMessage);
        finalMessage.append(failureMessage);

        return new CommandResult(finalMessage.toString().trim());
    }

    private void updateSuccessMessage(Model model) {
        if (!validTagSet.isEmpty() || !validStatusSet.isEmpty()) {
            successMessage.append(String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW,
                    model.getFilteredPersonList().size()));
            successMessage.append("\n");
        }

    }

    private void updateFailedFiltersMessage() {
        if (!missingTagSet.isEmpty()) {
            failureMessage.append(MESSAGE_TAG_NOT_CREATED).append(missingTagSet.stream()
                    .map(Tag::toString).collect(Collectors.joining(", "))).append("\n");
        }

        if (!invalidTagSet.isEmpty()) {
            failureMessage.append(MESSAGE_TAG_FILTER_ALREADY_EXISTS).append(invalidTagSet.stream()
                    .map(Tag::toString).collect(Collectors.joining(", "))).append("\n");
        }

        if (!invalidStatusSet.isEmpty()) {
            failureMessage.append(MESSAGE_EXISTING_STATUS_FILTERED).append(invalidStatusSet.stream()
                    .map(RsvpStatus::getFilterFormat).collect(Collectors.joining(", "))).append("\n");
        }

        if (!duplicateStatusSet.isEmpty()) {
            failureMessage.append(MESSAGE_STATUS_ALREADY_BEING_FILTERED).append(duplicateStatusSet.stream()
                    .map(RsvpStatus::getFilterFormat).collect(Collectors.joining(", "))).append("\n");
        }
    }

    @Override
    public void undo(Model model) {
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        System.out.println("removing: " + validStatusSet + validTagSet);
        model.removeFilters(validTagSet, validStatusSet);
        if (previousPredicate != null) {
            model.updateFilteredPersonList(previousPredicate);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FilterCommand)) {
            return false;
        }

        FilterCommand otherFilterCommand = (FilterCommand) other;
        return this.tagSet.equals(otherFilterCommand.tagSet) && this.statusSet.equals(otherFilterCommand.statusSet);
    }
}
