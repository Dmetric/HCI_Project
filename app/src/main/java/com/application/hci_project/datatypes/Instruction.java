package com.application.hci_project.datatypes;

import java.io.Serializable;

public class Instruction implements Serializable {
    private String description;
    private Boolean hasTimer;
    private Integer timer=0;

    public Instruction(String description) {
        this.description = description;
        this.hasTimer = false;
    }

    public Instruction(String description, Integer timer) {
        this.description = description;
        this.timer = timer;
        this.hasTimer = true;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getHasTimer() {
        return hasTimer;
    }

    public void setHasTimer(Boolean hasTimer) {
        this.hasTimer = hasTimer;
    }

    public Integer getTimer() {
        return timer;
    }

    public void setTimer(Integer timer) {
        this.timer = timer;
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "description='" + description + '\'' +
                ", hasTimer=" + hasTimer +
                ", timer=" + timer +
                '}';
    }
}
