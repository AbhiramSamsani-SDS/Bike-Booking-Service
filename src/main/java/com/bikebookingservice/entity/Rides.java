package com.bikebookingservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.bikebookingservice.enums.RideStatus;
import jakarta.persistence.*;

@Entity
public class Rides {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int rideId;
    private BigDecimal fare;
    private String boarding;
    private String destination;
    private int timeInSeconds;
    private LocalDateTime startedAt;
    private LocalDateTime estimatedCompletionTime;

    @Enumerated(EnumType.STRING)
    private RideStatus rideStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Drivers driver;

    public int getRideId() { 
    	return rideId; 
    }
    public void setRideId(int rideId) {
    	this.rideId = rideId; 
    }
    public BigDecimal getFare() { 
    	return fare; 
    }
    public void setFare(BigDecimal fare) { 
    	this.fare = fare; 
    }
    public String getBoarding() { 
    	return boarding;
    }
    public void setBoarding(String boarding) { 
    	this.boarding = boarding; 
    }
    public String getDestination() {
    	return destination; 
    }
    public void setDestination(String destination) { 
    	this.destination = destination; 
    }
    public int getTimeInSeconds() { 
    	return timeInSeconds; 
    }
    public void setTimeInSeconds(int timeInSeconds) { 
    	this.timeInSeconds = timeInSeconds; 
    }
    public LocalDateTime getStartedAt() { 
    	return startedAt; 
    }
    public void setStartedAt(LocalDateTime startedAt) { 
    	this.startedAt = startedAt; 
    }
    public LocalDateTime getEstimatedCompletionTime() { 
    	return estimatedCompletionTime; 
    }
    public void setEstimatedCompletionTime(LocalDateTime estimatedCompletionTime) { 
    	this.estimatedCompletionTime = estimatedCompletionTime; 
    }
    public RideStatus getRideStatus() { 
    	return rideStatus; 
    }
    public void setRideStatus(RideStatus rideStatus) { 
    	this.rideStatus = rideStatus; 
    }
    public Users getUser() { 
    	return user; 
    }
    public void setUser(Users user) { 
    	this.user = user; 
    }
    public Drivers getDriver() { 
    	return driver; 
    }
    public void setDriver(Drivers driver) { 
    	this.driver = driver; 
    }
}
