package com.bikebookingservice.request;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class VerifyMoneyChallengeRequest {
    private int userId;
    private int answer;
    private BigDecimal amount;
    
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
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
