package com.okta.spring.example.models;

public class RegisteringUser {
    public enum WorkflowStage {prereg,enroll,verify,welcome}

    private String userIdentifier;
    private String email;
    private WorkflowStage workflowStage = WorkflowStage.prereg;
    private char[] password;
    private String verificationCode;

    public String getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public WorkflowStage getWorkflowStage() {
        return workflowStage;
    }

    public void setWorkflowStage(WorkflowStage workflowStage) {
        this.workflowStage = workflowStage;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
