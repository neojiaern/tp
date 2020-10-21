package manager.card;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Card {
    private String question;
    private String answer;
    private int previousInterval;
    private LocalDate dueBy;
    private int rating;

    public Card(String question, String answer) {
        this.question = question;
        this.answer = answer;
        this.dueBy = null;
        this.previousInterval = 1;
    }

    public Card(String question, String answer, int previousInterval) {
        this.question = question;
        this.answer = answer;
        this.dueBy = null;
        this.previousInterval = previousInterval;
    }


    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return "[Q] " + question;
    }

    public String getAnswer() {
        return "[A] " + answer;
    }

    public LocalDate getDueBy() {
        return dueBy;
    }

    public int getPreviousInterval() {
        return previousInterval;
    }

    public void setPreviousInterval(int newInterval) {
        this.previousInterval = newInterval;
    }

    public void setDueBy(LocalDate newDueBy) {
        dueBy = newDueBy;
    }

    public String toString() {
        return "[Q] " + question + " | [A] " + answer;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
