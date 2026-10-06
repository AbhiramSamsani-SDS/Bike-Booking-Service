package com.bikebookingservice.response;

import com.bikebookingservice.entity.Drivers;

import java.math.BigDecimal;

public class DriverLoginResponse {
    private int driverId;
    private String driverName;
    private String email;
    private BigDecimal balance;
    private int numberOfRides;
    private boolean availableStatus;
    private String profilePhoto;

    public DriverLoginResponse(Drivers driver) {
        this.driverId = driver.getDriverId();
        this.driverName = driver.getDriverName();
        this.email = driver.getEmail();
        this.balance = driver.getBalance();
        this.numberOfRides = driver.getNumberOfRides();
        this.availableStatus = driver.isAvailableStatus();
        this.profilePhoto = driver.getProfilePhoto();
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

	public int getNumberOfRides() {
		return numberOfRides;
	}

	public void setNumberOfRides(int numberOfRides) {
		this.numberOfRides = numberOfRides;
	}

	public boolean isAvailableStatus() {
		return availableStatus;
	}

	public void setAvailableStatus(boolean availableStatus) {
		this.availableStatus = availableStatus;
	}

	public String getProfilePhoto() {
		return profilePhoto;
	}

	public void setProfilePhoto(String profilePhoto) {
		this.profilePhoto = profilePhoto;
	}
    
    
}
