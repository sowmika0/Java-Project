package com.temenos.arc.security.authenticationserver.common;


public class UserSecret {
    private String question; 
    private String answer;
    
    public UserSecret(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
    
    public String getAnswer() {
        return answer;
    }
    
    public String getQuestion() {
        return question;
    }    
}
