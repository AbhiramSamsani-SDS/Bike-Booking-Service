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
public class Drivers {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int driverId;
	private String password;
	private String driverName;
	private BigDecimal balance = BigDecimal.ZERO;
	private int numberOfRides=0;
	private boolean availableStatus=true;
	private String email;
	private boolean active=true;
	private String profilePhoto;
	private int withdrawWrongAttempts;
	private LocalDateTime withdrawLockedUntil;
	private String withdrawChallengeQuestion;
	private Integer withdrawChallengeAnswer;
	
	public String getWithdrawChallengeQuestion() {
		return withdrawChallengeQuestion;
	}
	public void setWithdrawChallengeQuestion(String withdrawChallengeQuestion) {
		this.withdrawChallengeQuestion = withdrawChallengeQuestion;
	}
	public Integer getWithdrawChallengeAnswer() {
		return withdrawChallengeAnswer;
	}
	public void setWithdrawChallengeAnswer(Integer withdrawChallengeAnswer) {
		this.withdrawChallengeAnswer = withdrawChallengeAnswer;
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
	public int getWithdrawWrongAttempts() {
		return withdrawWrongAttempts;
	}
	public void setWithdrawWrongAttempts(int withdrawWrongAttempts) {
		this.withdrawWrongAttempts = withdrawWrongAttempts;
	}
	public LocalDateTime getWithdrawLockedUntil() {
		return withdrawLockedUntil;
	}
	public void setWithdrawLockedUntil(LocalDateTime withdrawLockedUntil) {
		this.withdrawLockedUntil = withdrawLockedUntil;
	}
	public boolean isAvailableStatus() {
		return availableStatus;
	}
	public void setAvailableStatus(boolean availableStatus) {
		this.availableStatus = availableStatus;
	}
	
	
	public int getNumberOfRides() {
		return numberOfRides;
	}
	public void setNumberOfRides(int numberOfRides) {
		this.numberOfRides = numberOfRides;
	}
	public int getDriverId() {
		return driverId;
	}
	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	
}

