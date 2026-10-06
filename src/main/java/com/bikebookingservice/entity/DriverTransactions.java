package com.bikebookingservice.entity;

import com.bikebookingservice.enums.TransactionType;


import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class DriverTransactions {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int transactionId;
	private BigDecimal amount;
	
	@Enumerated(EnumType.STRING)
    private TransactionType transactionType;

	@ManyToOne
	@JoinColumn(name = "driver_id")
	private Drivers driver;

	@ManyToOne
    @JoinColumn(name = "ride_id", nullable = true)
	private Rides ride;
	
	
	public BigDecimal getAmount() {
		return amount;
	}
	 public void setAmount(BigDecimal amount) {
		 this.amount = amount;
	 }
	 public Drivers getDriver() {
		return driver;
	}
	 public void setDriver(Drivers driver) {
		 this.driver = driver;
	 }
	 public Rides getRide() {
		 return ride;
	 }
	 public void setRide(Rides ride) {
		 this.ride = ride;
	 }
	public int getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}
	public TransactionType getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	

}
