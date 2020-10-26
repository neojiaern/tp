package commands;

import access.Access;
import common.KajiLog;
import exception.ExclusionFileException;
import exception.IncorrectAccessLevelException;
import exception.InvalidFileFormatException;
import exception.InvalidInputException;
import manager.chapter.Chapter;
import manager.module.ChapterList;
import storage.Storage;
import ui.Ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class RemoveChapterCommand extends RemoveCommand {
    private static Logger logger = KajiLog.getLogger(RemoveChapterCommand.class.getName());

    public static final String MESSAGE_SUCCESS_CHAPTER = "The chapter <%1$s> has been removed.\n";
    public static final String MESSAGE_REMAINING_CHAPTER = "You currently have %1$d chapter(s) in this module.";
    public static final String MESSAGE_INVALID_INDEX_CHAPTER = "The chapter is not found, please try again.";

    private final int removeIndex;

    public RemoveChapterCommand(int removeIndex) {
        this.removeIndex = removeIndex;
    }

    @Override
    public void execute(Ui ui, Access access, Storage storage) throws IOException {
        String result = removeChapter(access, storage);
        ui.showToUser(result);
    }

    private String removeChapter(Access access, Storage storage) throws IOException {
        assert access.isModuleLevel() : "Not module level";
        StringBuilder result = new StringBuilder();
        try {
            ChapterList chapters = access.getModule().getChapters();
            ArrayList<Chapter> allChapters = chapters.getAllChapters();
            Chapter chapter = allChapters.get(removeIndex);
            File directory = new File(storage.getFilePath() + "/" + access.getModule()
                    + "/" + chapter.toString() + ".txt");
            logger.info("Deleting chapter: " + chapter.toString());
            boolean isRemoved = storage.deleteDirectory(directory);
            boolean isRemovedFromDue = storage.removeChapterFromDue(
                    access.getModule().toString(), chapter.toString());
            if (!isRemoved && !isRemovedFromDue) {
                throw new IOException("There was a problem deleting chapter in directory.");
            }
            result.append(String.format(MESSAGE_SUCCESS_CHAPTER, chapter.toString()));
            allChapters.remove(removeIndex);
            result.append(String.format(MESSAGE_REMAINING_CHAPTER, allChapters.size()));
            logger.info("Chapter: " + chapter.toString() + " successfully deleted.");
        } catch (IndexOutOfBoundsException e) {
            result.append(MESSAGE_INVALID_INDEX_CHAPTER);
        }
        return result.toString();
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
