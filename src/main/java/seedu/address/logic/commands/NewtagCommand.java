package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.TagList;

/**
 * Adds a new predefined tag.
 */
public class NewtagCommand extends Command {
    public static final String COMMAND_WORD = "newtag";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Creates new tag(s) (case insensitive). Maximum of 50 alphanumeric characters, spaces, parenthesis "
            + "and apostrophes per tag.\n"
            + "Parameters: " + PREFIX_TAG + "TAG...\n"
            + "Example: " + COMMAND_WORD + " t/bride's side t/groom's side";

    public static final String MESSAGE_SUCCESS = "New tag(s) created: ";
    public static final String MESSAGE_DUPLICATE = "Some tag(s) already exists.\n";
    public static final String MESSAGE_TAGLIST_PREFIX = "Your tags: ";
    public static final String MESSAGE_TOO_MANY_TAGS = "You have more than " + TagList.MAXIMUM_TAGLIST_SIZE
            + " tags.\nPlease remove some using 'deletetag'.\n";

    private final List<Tag> tags;


    /**
     * @param tags The {@code List} of tags to be added.
     */
    public NewtagCommand(List<Tag> tags) {
        requireAllNonNull(tags);
        this.tags = tags;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireAllNonNull(model);
        if (!model.checkAcceptableTagListSize(tags.size())) {
            throw new CommandException(MESSAGE_TOO_MANY_TAGS);
        }

        boolean isSuccessful = model.addTags(tags);

        if (!isSuccessful) {
            throw new CommandException(MESSAGE_DUPLICATE);
        }
        String successMessage = MESSAGE_SUCCESS + tags + "\n";

        model.updateTagList();

        String currentTags = MESSAGE_TAGLIST_PREFIX + model.getTagList();
        return new CommandResult(successMessage + currentTags);
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
