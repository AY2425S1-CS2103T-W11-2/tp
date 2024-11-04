package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.TagList;

/**
 * Adds a new predefined tag.
 */
public class NewtagCommand extends UndoableCommand {
    public static final String COMMAND_WORD = "newtag";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Creates new tag(s) (case insensitive). Maximum of 50 alphanumeric characters, spaces, parenthesis "
            + "and apostrophes per tag.\n"
            + "Parameters: " + PREFIX_TAG + "TAG...\n"
            + "Example: " + COMMAND_WORD + " t/bride's side t/groom's side";

    public static final String MESSAGE_SUCCESS = "New tag(s) created.\n";
    public static final String MESSAGE_ALL_DUPLICATE = "The following tag(s) already exist:\n";

    public static final String MESSAGE_SOME_DUPLICATE = "Non-duplicate tag(s) has/have been created successfully.\n"
            + "The following tag(s) already exist(s):\n";

    public static final String MESSAGE_TOO_MANY_TAGS = "You are attempting to add more than "
            + TagList.MAXIMUM_TAGLIST_SIZE + " tags in total.\n"
            + "Please remove some using 'deletetag first'.\n";

    private final List<Tag> tags;


    /**
     * Constructs a NewtagCommand to add the specified {@code tags}.
     * @param tags The {@code List} of tags to be added.
     */
    public NewtagCommand(List<Tag> tags) {
        requireAllNonNull(tags);
        this.tags = tags;
    }

    /**
     * @throws CommandException if the number of defined tags in the system will exceed the maximum
     *      allowable number if the new tags were to be added.
     */
    private void validateTagListSize(Model model) throws CommandException {
        requireAllNonNull(model);
        requireAllNonNull(tags);
        if (!model.checkAcceptableTagListSize(tags.size())) {
            throw new CommandException(MESSAGE_TOO_MANY_TAGS);
        }
    }

    /**
     * Adds the tags to the model and checks for duplicates.
     * @param model The model to which tags will be added.
     */
    private Set<Tag> addTagsToModel(Model model) {
        requireAllNonNull(model);
        Set<Tag> tagsSuccessfullyAdded = model.addTags(tags);
        model.updateTagList();
        return tagsSuccessfullyAdded;
    }

    /**
     * Creates a CommandResult based on the success of adding tags to the model.
     *
     * @param tagsSuccessfullyAdded the tags that were successfully added.
     * @return The corresponding CommandResult.
     */
    private CommandResult createCommandResult(Set<Tag> tagsSuccessfullyAdded) {
        requireAllNonNull(tagsSuccessfullyAdded);
        List<Tag> tagsNotSuccessfullyAdded = new ArrayList<>(tags);
        tagsNotSuccessfullyAdded.removeAll(tagsSuccessfullyAdded);

        if (!tagsNotSuccessfullyAdded.isEmpty()) {
            if (tagsSuccessfullyAdded.isEmpty()) {
                return new CommandResult(MESSAGE_ALL_DUPLICATE + tagsNotSuccessfullyAdded);
            }
            return new CommandResult(MESSAGE_SOME_DUPLICATE + tagsNotSuccessfullyAdded);
        }
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireAllNonNull(model);
        validateTagListSize(model);
        Set<Tag> tagsSuccessfullyAdded = addTagsToModel(model);
        return createCommandResult(tagsSuccessfullyAdded);
    }

    @Override
    public void undo(Model model) {
        model.deleteTags(tags);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof NewtagCommand)) {
            return false;
        }

        NewtagCommand otherCommand = (NewtagCommand) other;
        return tags.equals(otherCommand.tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("tags", tags)
                .toString();
    }
}
