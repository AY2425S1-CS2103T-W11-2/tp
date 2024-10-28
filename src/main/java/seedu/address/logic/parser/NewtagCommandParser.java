package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.tag.Tag.isValidTagName;

import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.commands.NewtagCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new NewtagCommand object.
 */
public class NewtagCommandParser implements Parser<NewtagCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the NewtagCommand
     * and returns a NewtagCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format.
     */
    public NewtagCommand parse(String args) throws ParseException {
        String lowerCaseArguments = args.toLowerCase();
        ArgumentMultimap tokenisedArguments = ArgumentTokenizer.tokenize(lowerCaseArguments, PREFIX_TAG);
        List<String> arguments = tokenisedArguments.getAllValues(PREFIX_TAG);
        List<Tag> tagsToAdd = new ArrayList<>();

        if (arguments.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, NewtagCommand.MESSAGE_USAGE));
        }

        // Create and return the NewtagCommand.
        for (String argument : arguments) {
            if (!isValidTagName(argument)) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, NewtagCommand.MESSAGE_USAGE));
            }

            Tag tag = new Tag(argument);
            tagsToAdd.add(tag);
        }

        return new NewtagCommand(tagsToAdd);
    }
}
