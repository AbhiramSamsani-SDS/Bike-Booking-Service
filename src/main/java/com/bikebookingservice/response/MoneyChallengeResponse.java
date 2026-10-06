package com.bikebookingservice.response;

import java.time.LocalDateTime;

public class MoneyChallengeResponse {
    private String question;
    private int remainingAttempts;
    private boolean locked;
    private LocalDateTime lockedUntil;

    public MoneyChallengeResponse(String question, int remainingAttempts, boolean locked, LocalDateTime lockedUntil) {
        this.question = question;
        this.remainingAttempts = remainingAttempts;
        this.locked = locked;
        this.lockedUntil = lockedUntil;
    }

    public static MoneyChallengeResponse active(String question, int remainingAttempts) {
        return new MoneyChallengeResponse(question, remainingAttempts, false, null);
    }

    public static MoneyChallengeResponse locked(LocalDateTime lockedUntil) {
        return new MoneyChallengeResponse(null, 0, true, lockedUntil);
    }

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public int getRemainingAttempts() {
		return remainingAttempts;
	}

	public void setRemainingAttempts(int remainingAttempts) {
		this.remainingAttempts = remainingAttempts;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public LocalDateTime getLockedUntil() {
		return lockedUntil;
	}

	public void setLockedUntil(LocalDateTime lockedUntil) {
		this.lockedUntil = lockedUntil;
	}

    
}
