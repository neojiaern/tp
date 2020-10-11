package commands;

import access.Access;
import manager.chapter.CardList;
import storage.Storage;
import ui.Ui;

public class ExitCommand extends Command {
    public static final String COMMAND_WORD = "exit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Exits the program. \n"
            + "Example: " + COMMAND_WORD + "\n";

    @Override
    public void execute(CardList cards, Ui ui, Access access, Storage storage) {
        ui.showExit();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}