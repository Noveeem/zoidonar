package com.example.zoidonarapp;

public class modelQuestion {
    public String question, answer;

    public modelQuestion() {

    }

    public modelQuestion(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
