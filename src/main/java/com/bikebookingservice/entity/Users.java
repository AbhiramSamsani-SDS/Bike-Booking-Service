package com.bikebookingservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Users {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int userId;
	private String userName;
	private String email;
	private String password;
	private BigDecimal balance = BigDecimal.ZERO;
	private boolean active = true;
	private String profilePhoto;
	private int addMoneyWrongAttempts;
	private LocalDateTime addMoneyLockedUntill;
	private String addMoneyChallengeQuestion;
	private Integer addMoneyChallengeAnswer;
	
	public String getAddMoneyChallengeQuestion() {
		return addMoneyChallengeQuestion;
	}
	public void setAddMoneyChallengeQuestion(String addMoneyChallengeQuestion) {
		this.addMoneyChallengeQuestion = addMoneyChallengeQuestion;
	}
	public Integer getAddMoneyChallengeAnswer() {
		return addMoneyChallengeAnswer;
	}
	public void setAddMoneyChallengeAnswer(Integer addMoneyChallengeAnswer) {
		this.addMoneyChallengeAnswer = addMoneyChallengeAnswer;
	}
	public int getUserId() {
		return userId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getProfilePhoto() {
		return profilePhoto;
	}
	public void setProfilePhoto(String profilePhoto) {
		this.profilePhoto = profilePhoto;
	}
	public int getAddMoneyWrongAttempts() {
		return addMoneyWrongAttempts;
	}
	public void setAddMoneyWrongAttempts(int addMoneyWrongAttempts) {
		this.addMoneyWrongAttempts = addMoneyWrongAttempts;
	}
	public LocalDateTime getAddMoneyLockedUntill() {
		return addMoneyLockedUntill;
	}
	public void setAddMoneyLockedUntill(LocalDateTime addMoneyLockedUntill) {
		this.addMoneyLockedUntill = addMoneyLockedUntill;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

}
