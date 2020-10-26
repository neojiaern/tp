package commands;

import access.Access;
import common.KajiLog;
import exception.ExclusionFileException;
import exception.IncorrectAccessLevelException;
import exception.InvalidFileFormatException;
import exception.InvalidInputException;
import manager.card.Card;
import manager.chapter.CardList;
import storage.Storage;
import ui.Ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class RemoveCardCommand extends RemoveCommand {
    private static Logger logger = KajiLog.getLogger(RemoveCardCommand.class.getName());

    public static final String MESSAGE_SUCCESS_FLASHCARD = "The following flashcard has been removed:\n";
    public static final String MESSAGE_REMAINING_FLASHCARD = "You currently have %1$d card(s) in this chapter.";
    public static final String MESSAGE_INVALID_INDEX_FLASHCARD = "The flashcard is not found, please try again.";

    private final int removeIndex;

    public RemoveCardCommand(int removeIndex) {
        this.removeIndex = removeIndex;
    }

    @Override
    public void execute(Ui ui, Access access, Storage storage) throws IOException {
        String result = removeCard(access, storage);
        ui.showToUser(result);
    }

    private String removeCard(Access access, Storage storage) throws IOException {
        assert access.isChapterLevel() : "Not chapter level";
        StringBuilder result = new StringBuilder();
        try {
            CardList cards = access.getChapter().getCards();
            ArrayList<Card> allCards = cards.getAllCards();
            Card card = allCards.get(removeIndex);
            logger.info("Deleting flashcard: " + card.toString());
            cards.removeCard(removeIndex);
            result.append(MESSAGE_SUCCESS_FLASHCARD + card.toString() + "\n"
                    + String.format(MESSAGE_REMAINING_FLASHCARD, cards.getCardCount()));
            storage.saveCards(cards, access.getModule().getModuleName(), access.getChapter().getChapterName());
            logger.info("Flashcard successfully deleted.");
        } catch (IndexOutOfBoundsException e) {
            logger.info(MESSAGE_INVALID_INDEX_FLASHCARD);
            result.append(MESSAGE_INVALID_INDEX_FLASHCARD);
        }
        return result.toString();
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
