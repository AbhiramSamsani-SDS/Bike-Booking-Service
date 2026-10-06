package com.bikebookingservice.response;

import com.bikebookingservice.entity.Users;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class UserLoginResponse {
	
	private int userId;
	private String userName;
	private String email;
	private BigDecimal balance;
	private String profilePhoto;
	
	public UserLoginResponse(Users user) {
		super();
		this.userId = user.getUserId();
		this.userName = user.getUserName();
		this.email = user.getEmail();
		this.balance = user.getBalance();
		this.profilePhoto = user.getProfilePhoto();
	}

	public int getUserId() {
		return userId;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getProfilePhoto() {
		return profilePhoto;
	}

	public void setProfilePhoto(String profilePhoto) {
		this.profilePhoto = profilePhoto;
	}
	
	

}
