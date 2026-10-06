package com.bikebookingservice.request;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class VerifyWithdrawChallengeRequest {
    private int driverId;
    private int answer;
    private BigDecimal amount;
    
	public int getDriverId() {
		return driverId;
	}
	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}
	public int getAnswer() {
		return answer;
	}
	public void setAnswer(int answer) {
		this.answer = answer;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
    
}
