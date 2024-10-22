package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.commands.DeleteTagCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new DeleteTagCommand object.
 */
public class DeleteTagCommandParser implements Parser<DeleteTagCommand> {
    public static final int MAX_LENGTH = 50;
    public static final String VALIDATION_REGEX = "[\\p{Alnum}'() ]+";

    /**
     * Checks if a given string (trimmed) is a valid argument
     * for the DeleteTagCommand class.
     * @param argument the argument to be checked.
     * @return true if it is valid, and false otherwise.
     */
    public boolean isValidArgument(String argument) {
        boolean isEmpty = argument.isEmpty();
        boolean isTooLong = argument.length() > MAX_LENGTH;
        boolean isValidCharacters = argument.matches(VALIDATION_REGEX);
        if (isEmpty || isTooLong || !isValidCharacters) {
            return false;
        }
        return true;
    }

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteTagCommand
     * and returns a DeleteTagCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public DeleteTagCommand parse(String args) throws ParseException {
        String lowerCaseArguments = args.toLowerCase();
        ArgumentMultimap tokenisedArguments = ArgumentTokenizer.tokenize(lowerCaseArguments, PREFIX_TAG);
        List<String> arguments = tokenisedArguments.getAllValues(PREFIX_TAG);
        List<Tag> tagsToDelete = new ArrayList<>();

        if (arguments.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteTagCommand.MESSAGE_USAGE));
        }

        for (String argument : arguments) {
            if (!isValidArgument(argument)) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteTagCommand.MESSAGE_USAGE));
            }

            Tag tag = new Tag(argument);
            tagsToDelete.add(tag);
        }

        return new DeleteTagCommand(tagsToDelete);
    }
}
