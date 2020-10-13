package parser;

import access.Access;
import commands.*;

import exception.IncorrectAccessLevelException;
import exception.InvalidFileFormatException;
import exception.InvalidInputException;
import storage.Storage;
import ui.Ui;


public class Parser {
    private static final String QUESTION_ANSWER_PREFIX = " \\| ";
    private static final String QUESTION_PREFIX = "q:";
    private static final String ANSWER_PREFIX = "a:";

    private static final String ADMIN_LEVEL = "admin";
    private static final String MODULE_LEVEL = "module";
    private static final String CHAPTER_LEVEL = "chapter";

    public static Command parse(String fullCommand, Access access)
            throws InvalidInputException, IncorrectAccessLevelException {
        String[] commandTypeAndArgs = splitCommandTypeAndArgs(fullCommand);
        String commandType = commandTypeAndArgs[0].trim().toLowerCase();
        String commandArgs = commandTypeAndArgs[1].trim();

        switch (commandType) {
        case ListCommand.COMMAND_WORD:
            return prepareList(commandArgs);
        case AddCommand.COMMAND_WORD:
            return prepareAdd(commandArgs);
        case RemoveCommand.COMMAND_WORD:
            return prepareRemove(commandArgs);
        case ReviseCommand.COMMAND_WORD:
            return prepareRevise(commandArgs);
        case ExitCommand.COMMAND_WORD:
            return prepareExit(commandArgs);
        case HelpCommand.COMMAND_WORD:
            return prepareHelp(commandArgs);
        case AddModuleCommand.COMMAND_WORD:
            return prepareAddModule(commandArgs);
        case AddChapterCommand.COMMAND_WORD:
            return prepareAddChapter(commandArgs);
        case BackModuleCommand.COMMAND_WORD:
            return prepareBackModule(commandArgs);
        case BackChapterCommand.COMMAND_WORD:
            return prepareBackChapter(commandArgs);
        case GoModuleCommand.COMMAND_WORD:
            return prepareGoModule(commandArgs);
        case GoChapterCommand.COMMAND_WORD:
            return prepareGoChapter(commandArgs);
        case EditCommand.COMMAND_WORD:
            return prepareEdit(commandArgs, access);
        case ListDueCommand.COMMAND_WORD:
            return prepareListDue(commandArgs);
        default:
            throw new InvalidInputException();
        }
    }

    private static Command prepareGoChapter(String commandArgs) throws InvalidInputException {
        if (commandArgs.isEmpty()) {
            throw new InvalidInputException();
        }
        return new GoChapterCommand(commandArgs);
    }

    private static Command prepareGoModule(String commandArgs) throws InvalidInputException {
        if (commandArgs.isEmpty()) {
            throw new InvalidInputException();
        }
        return new GoModuleCommand(commandArgs);
    }

    private static Command prepareBackChapter(String commandArgs) throws InvalidInputException {
        if (!commandArgs.isEmpty()) {
            throw new InvalidInputException();
        }
        return new BackChapterCommand();
    }

    private static Command prepareBackModule(String commandArgs) throws InvalidInputException {
        if (!commandArgs.isEmpty()) {
            throw new InvalidInputException();
        }
        return new BackModuleCommand();
    }

    private static Command prepareAddChapter(String commandArgs) throws InvalidInputException {
        if (commandArgs.isEmpty()) {
            throw new InvalidInputException();
        }
        return new AddChapterCommand(commandArgs);
    }

    private static Command prepareAddModule(String commandArgs) throws InvalidInputException {
        if (commandArgs.isEmpty()) {
            throw new InvalidInputException();
        }
        return new AddModuleCommand(commandArgs);
    }

    private static String[] splitCommandTypeAndArgs(String userCommand) {
        String[] commandTypeAndParams = userCommand.trim().split(" ", 2);
        if (commandTypeAndParams.length != 2) {
            commandTypeAndParams = new String[]{commandTypeAndParams[0], ""};
        }
        return commandTypeAndParams;
    }

    private static Command prepareList(String commandArgs) throws InvalidInputException {
        if (!commandArgs.isEmpty()) {
            throw new InvalidInputException("There should not be any arguments for list.");
        }
        return new ListCommand();
    }

    private static Command prepareAdd(String commandArgs) throws InvalidInputException {
        try {
            String[] args = commandArgs.split(QUESTION_ANSWER_PREFIX, 2);
            String question = parseQuestion(args[0]);
            String answer = parseAnswer(args[1]);
            if (question.isEmpty() || answer.isEmpty()) {
                throw new InvalidInputException();
            }
            return new AddCommand(question, answer);
        } catch (IndexOutOfBoundsException | InvalidInputException e) {
            throw new InvalidInputException();
        }
    }

    private static Command prepareRemove(String commandArgs) throws InvalidInputException {
        int removeIndex;
        if (commandArgs.isEmpty()) {
            throw new InvalidInputException();
        }
        try {
            removeIndex = Integer.parseInt(commandArgs) - 1;
        } catch (NumberFormatException e) {
            throw new InvalidInputException();
        }
        return new RemoveCommand(removeIndex);
    }

    private static Command prepareEdit(String commandArgs, Access access)
            throws InvalidInputException, IncorrectAccessLevelException {
        if (access.isChapterLevel()) {
            return prepareEditCard(commandArgs);
        } else {
            throw new IncorrectAccessLevelException("Edit command can only be called at admin, "
                    + "module and chapter level.\n");
        }
    }

    private static Command prepareEditCard(String commandArgs) throws InvalidInputException {
        try {
            String[] args = commandArgs.split(" ", 2);
            if (args[0].trim().isEmpty()) {
                throw new InvalidInputException("The flashcard number is missing.\n"
                        + EditCommand.MESSAGE_USAGE);
            }

            int editIndex = Integer.parseInt(args[0].trim()) - 1;

            String[] questionAndAnswer = args[1].trim().split(QUESTION_ANSWER_PREFIX, 2);
            String question = parseQuestion(questionAndAnswer[0]);
            String answer = parseAnswer(questionAndAnswer[1]);

            if (question.isEmpty() && answer.isEmpty()) {
                throw new InvalidInputException("The content for question and answer are both empty.\n"
                        + EditCommand.MESSAGE_USAGE);
            }

            return new EditCommand(editIndex, question, answer, CHAPTER_LEVEL);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("The flashcard number needs to be an integer.\n"
                    + EditCommand.MESSAGE_USAGE);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidInputException("The format for the edit command is incorrect.\n"
                    + EditCommand.MESSAGE_USAGE);
        }
    }

    private static String parseQuestion(String arg) throws InvalidInputException {
        if (!(arg.trim().toLowerCase().startsWith(QUESTION_PREFIX))) {
            throw new InvalidInputException("There needs to be a \"q:\" prefix before the question.\n"
                    + "Example: " + AddCommand.COMMAND_WORD + AddCommand.CARD_PARAMETERS + "\n"
                    + "         " + EditCommand.COMMAND_WORD + EditCommand.CARD_PARAMETERS);
        }

        return arg.substring(2).trim();
    }

    private static String parseAnswer(String arg) throws InvalidInputException {
        if (!(arg.trim().toLowerCase().startsWith(ANSWER_PREFIX))) {
            throw new InvalidInputException("There needs to be a \"a:\" prefix before the answer.\n"
                    + "Example: " + AddCommand.COMMAND_WORD + AddCommand.CARD_PARAMETERS + "\n"
                    + "         " + EditCommand.COMMAND_WORD + EditCommand.CARD_PARAMETERS);
        }

        return arg.substring(2).trim();
    }

    private static Command prepareRevise(String commandArgs) throws InvalidInputException {
        if (commandArgs.isEmpty()) {
            throw new InvalidInputException();
        }
        return new ReviseCommand(commandArgs);
    }

    private static Command prepareExit(String commandArgs) throws InvalidInputException {
        if (!commandArgs.isEmpty()) {
            throw new InvalidInputException();
        }
        return new ExitCommand();
    }

    private static Command prepareHelp(String commandArgs) throws InvalidInputException {
        if (!commandArgs.isEmpty()) {
            throw new InvalidInputException();
        }
        return new HelpCommand();
    }

    public static String parseQuestionInFile(String arg) throws InvalidFileFormatException {
        if (!(arg.trim().startsWith(Storage.QUESTION_PREFIX))) {
            throw new InvalidFileFormatException();
        }

        String question = arg.substring(3).trim();
        if (question.isEmpty()) {
            throw new InvalidFileFormatException();
        }

        return question;
    }

    public static String parseAnswerInFile(String arg) throws InvalidFileFormatException {
        if (!(arg.trim().startsWith(Storage.ANSWER_PREFIX))) {
            throw new InvalidFileFormatException();
        }

        String answer = arg.substring(3).trim();
        if (answer.isEmpty()) {
            throw new InvalidFileFormatException();
        }

        return answer;
    }

    public static boolean chooseNewDeckRating() {
        System.out.println("Would you like to rate this new Chapter?");
        Ui ratingUi = new Ui();
        String userChoice = ratingUi.readCommand();
        while (!userChoice.equalsIgnoreCase("Y") && !userChoice.equalsIgnoreCase("N")) {
            userChoice = ratingUi.readCommand();
        }
        return userChoice.equalsIgnoreCase("Y");
    }

    public static boolean validDeckRating(String rating) {
        switch (rating.toUpperCase()) {
        case "E":
        case "M":
        case "H":
            return true;
        default:
            return false;
        }
    }

    public static String getChoiceOfNewDeckRating() {
        System.out.println("Please rate this new Chapter!");
        System.out.println("You have the options of: Easy(E), Medium(M) or Hard(H)");
        System.out.println("Would your choice be E, M or H?");
        Ui ratingUi = new Ui();
        String userChoice = ratingUi.readCommand();
        while (!validDeckRating(userChoice)) {
            userChoice = ratingUi.readCommand();
        }
        return userChoice.toUpperCase();
    }

    private static Command prepareListDue(String commandArgs) throws InvalidInputException {
        if (!commandArgs.isEmpty()) {
            throw new InvalidInputException("There should not be any arguments for list.");
        }
        return new ListCommand();
    }

}


