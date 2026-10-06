package com.bikebookingservice.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import com.bikebookingservice.entity.*;
import com.bikebookingservice.request.*;
import com.bikebookingservice.response.*;
import com.bikebookingservice.enums.*;
import com.bikebookingservice.exception.*;
import com.bikebookingservice.repository.*;

@Service
public class BikeService {
    
	@Autowired private UserRepository usersRepository;
    @Autowired private DriverRepository driversRepository;
    @Autowired private RideRepository ridesRepository;
    @Autowired private UserTransactionRepository userTransactionsRepository;
    @Autowired private DriverTransactionRepository driverTransactionsRepository;
    @Autowired private TaskScheduler taskScheduler;
    private final Random random = new Random();

    public Users createUser(CreateUserRequest request){
    	
        if(request.getUserName()==null||request.getUserName().isBlank()||request.getEmail()==null||request.getEmail().isBlank()||request.getPassword()==null||request.getPassword().length()<4) 
        	throw new IllegalArgumentException("Name, email and a password of at least 4 characters are required");
        
        if(usersRepository.existsByEmail(request.getEmail()))
        	throw new EmailAlreadyExistsException("Email already registered");
        
        Users u=new Users();
        u.setUserName(request.getUserName().trim()); 
        u.setEmail(request.getEmail().trim()); 
        u.setPassword(request.getPassword()); 
        return usersRepository.save(u);
    }
    
    public Drivers createDriver(CreateDriverRequest request){
    	
        if(request.getDriverName()==null||request.getDriverName().isBlank()||request.getEmail()==null||request.getEmail().isBlank()||request.getPassword()==null||request.getPassword().length()<4) 
        	throw new IllegalArgumentException("Name, email and a password of at least 4 characters are required");
        
        if(driversRepository.existsByEmail(request.getEmail())) 
        	throw new EmailAlreadyExistsException("Email already registered");
        
        Drivers d=new Drivers(); 
        d.setDriverName(request.getDriverName().trim());
        d.setEmail(request.getEmail().trim()); 
        d.setPassword(request.getPassword()); 
        return driversRepository.save(d);
    }
    

    
    public Users getUserDetails(Users user){ 
    	
    	Users x=usersRepository.findByUserIdAndPasswordAndActiveTrue(user.getUserId(),user.getPassword());
    	
    	if(x==null) 
    		throw new RecordNotFoundException("Invalid User ID or Password"); 
    	return x; 
    }
    
    public Drivers getDriverDetails(Drivers driver){ 
    	Drivers x=driversRepository.findByDriverIdAndPasswordAndActiveTrue(driver.getDriverId(),driver.getPassword());
    	
    	if(x==null) 
    		throw new RecordNotFoundException("Invalid Driver ID or Password"); 
    	
    	return x; 
    }
    
    private Users user(int id){
    	Users user=usersRepository.findById(id).orElseThrow(()->new RecordNotFoundException("User Not Found"));
    	
    	if(!user.isActive()) 
    		throw new RecordNotFoundException("User Account is inactive"); 
    	
    	return user; 
    }
    
    private Drivers driver(int id){ 
    	Drivers driver=driversRepository.findById(id).orElseThrow(()->new RecordNotFoundException("Driver Not Found")); 
    	
    	if(!driver.isActive()) 
    		throw new RecordNotFoundException("Driver Account is inactive"); 
    	
    	return driver; 
    }
    
    public BigDecimal getUserBalance(int id){ 
    	return user(id).getBalance(); 
    }
    
    public BigDecimal getDriverBalance(int id){
    	return driver(id).getBalance(); 
    }
    
    public Map<String,Object> getUserStats(int id){ 
    	user(id); 
    	return Map.of("totalRides",ridesRepository.countByUser_UserId(id),"availableDrivers",driversRepository.countByAvailableStatusTrueAndActiveTrue()); 
    }
    
    public Map<String,Object> getDriverStats(int id){
    	Drivers d=driver(id);
    	return Map.of("totalRides",ridesRepository.countByDriver_DriverId(id),"available",d.isAvailableStatus()); 
    }
    

    
    public Page<Rides> getUserRideHistory(int id,int page,int size){ 
    	user(id);
    	return ridesRepository.findByUser_UserId(id,PageRequest.of(Math.max(page,0),size,Sort.by(Sort.Direction.DESC,"rideId"))); 
    }
    
    public Page<Rides> getDriverRideHistoryPage(int id,int page,int size){
    	driver(id); 
    	return ridesRepository.findByDriver_DriverId(id,PageRequest.of(Math.max(page,0),size,Sort.by(Sort.Direction.DESC,"rideId"))); 
    }
    
    public Page<UserTransactions> getUserTransactionsHistory(int id,int page,int size){
    	user(id); 
    	return userTransactionsRepository.findByUser_UserId(id,PageRequest.of(Math.max(page,0),size,Sort.by(Sort.Direction.DESC,"transactionId")));
    }
    
    public Page<DriverTransactions> getDriverTransactionsPage(int id,int page,int size){
    	driver(id); 
    	return driverTransactionsRepository.findByDriver_DriverId(id,PageRequest.of(Math.max(page,0),size,Sort.by(Sort.Direction.DESC,"transactionId"))); 
    }


    
    public Rides bookRideRequest(BookRideRequest request){
    	
        Users user=usersRepository.findByUserIdAndPasswordAndActiveTrue(request.getUserId(),request.getPassword());
        
        if(user==null) 
        	throw new RecordNotFoundException("Invalid user session. Please login again");
        
        String from=request.getBoarding()==null?"":request.getBoarding().trim();
        String to=request.getDestination()==null?"":request.getDestination().trim();
        
        if(from.isEmpty()||to.isEmpty()) 
        	throw new IllegalArgumentException("Boarding and destination are required");
        
        if(from.equalsIgnoreCase(to)) 
        	throw new IllegalArgumentException("Boarding and destination must be different");
        
        if(ridesRepository.existsByUser_UserIdAndRideStatus(user.getUserId(),RideStatus.ONGOING)) 
        	throw new IllegalArgumentException("You already have an ongoing ride");
       
        BigDecimal fare = BigDecimal.valueOf(request.getBoarding().charAt(0)+request.getDestination().charAt(0)); 
   
        if(user.getBalance().compareTo(fare)<0) 
        	throw new InsufficientBalanceException("Insufficient wallet balance. Estimated fare is ₹"+fare);
        
        Drivers driver=driversRepository.findFirstByAvailableStatusTrueAndActiveTrue();
        
        if(driver==null) 
        	throw new DriversNotAvailableException("No drivers are available right now");
        
        driver.setAvailableStatus(false);
        driversRepository.save(driver); 
        
        user.setBalance(user.getBalance().subtract(fare)); 
        usersRepository.save(user);
        
        Rides ride=new Rides(); 
        ride.setBoarding(from);
        ride.setDestination(to); 
        ride.setDriver(driver); 
        ride.setUser(user);
        ride.setFare(fare);
        ride.setRideStatus(RideStatus.ONGOING); 
        ride.setTimeInSeconds(request.getBoarding().charAt(0)); 
        ride.setStartedAt(LocalDateTime.now());
        ride.setEstimatedCompletionTime(ride.getStartedAt().plusSeconds(ride.getTimeInSeconds()));
        ride=ridesRepository.save(ride);
        
        setUserTransaction(user,ride,null);
        final int rideId=ride.getRideId(); 
        final int driverId=driver.getDriverId();

        taskScheduler.schedule(() -> {

            Rides rr = ridesRepository.findById(rideId).orElse(null);
            Drivers dd = driversRepository.findById(driverId).orElse(null);

            if (rr != null && dd != null) {

                rr.setRideStatus(RideStatus.COMPLETED);
                ridesRepository.save(rr);

                dd.setAvailableStatus(true);
                dd.setBalance(dd.getBalance().add(rr.getFare()));
                dd.setNumberOfRides(dd.getNumberOfRides() + 1);

                driversRepository.save(dd);

                setDriverTransaction(dd, rr, rr.getFare());
            }

        }, Instant.now().plusSeconds(ride.getTimeInSeconds()));

        return ride;
    }
    
    private void setUserTransaction(Users user,Rides ride,BigDecimal amount){
    	UserTransactions t=new UserTransactions();
    	t.setUser(user); 
    	t.setRide(ride);
    	t.setAmount(ride!=null?ride.getFare():amount);
    	t.setTransactionType(ride!=null?TransactionType.DEBIT:TransactionType.CREDIT);
    	userTransactionsRepository.save(t); 
    }
    
    private void setDriverTransaction(Drivers driver,Rides ride,BigDecimal amount){ 
    	DriverTransactions t=new DriverTransactions();
    	t.setDriver(driver);
    	t.setRide(ride);
    	t.setAmount(amount);
    	t.setTransactionType(ride!=null?TransactionType.CREDIT:TransactionType.DEBIT);
    	driverTransactionsRepository.save(t); 
    }


    
    private MoneyChallengeResponse userChallenge(Users user){ 
    	int a=random.nextInt(9)+1,b=random.nextInt(9)+1;
    	user.setAddMoneyChallengeQuestion("What is "+a+" + "+b+"?"); 
    	user.setAddMoneyChallengeAnswer(a+b);
    	usersRepository.save(user); 
    	return MoneyChallengeResponse.active(user.getAddMoneyChallengeQuestion(),3-user.getAddMoneyWrongAttempts()); 
    }
    
    public MoneyChallengeResponse generateUserMoneyChallenge(int id){
    	Users user=user(id);
    	
    	if(user.getAddMoneyLockedUntill()!=null && user.getAddMoneyLockedUntill().isAfter(LocalDateTime.now()))
    		return MoneyChallengeResponse.locked(user.getAddMoneyLockedUntill());
    	
    	return userChallenge(user); 
    }
    
    public BigDecimal verifyUserMoneyChallenge(VerifyMoneyChallengeRequest request){
    	Users user=user(request.getUserId());
    	
    	if(request.getAmount()==null || request.getAmount().compareTo(BigDecimal.ZERO)<=0) 
    		throw new IllegalArgumentException("Enter a valid amount");
    	
    	if(user.getAddMoneyLockedUntill()!=null && user.getAddMoneyLockedUntill().isAfter(LocalDateTime.now())) 
    		throw new InsufficientBalanceException("Add Money is temporarily locked");
    	
    	if(user.getAddMoneyChallengeAnswer()==null || !user.getAddMoneyChallengeAnswer().equals(request.getAnswer())){
    		int n=user.getAddMoneyWrongAttempts()+1;
    		user.setAddMoneyWrongAttempts(n); 
    		if(n>=3){
    			user.setAddMoneyLockedUntill(LocalDateTime.now().plusHours(24));
    			user.setAddMoneyChallengeAnswer(null);
    			user.setAddMoneyChallengeQuestion(null);
    			usersRepository.save(user);
    			throw new InsufficientBalanceException("Three incorrect answers. Add Money is locked for 24 hours");
    		} 
    		usersRepository.save(user); 
    		throw new IllegalArgumentException("Incorrect answer. "+(3-n)+" attempts remaining"); 
    	} 
    	
    	user.setBalance(user.getBalance().add(request.getAmount()));
    	user.setAddMoneyWrongAttempts(0);
    	user.setAddMoneyLockedUntill(null);
    	user.setAddMoneyChallengeAnswer(null);
    	user.setAddMoneyChallengeQuestion(null);
    	usersRepository.save(user);
    	setUserTransaction(user,null,request.getAmount());
    	return user.getBalance(); 
    }

    
    
    private MoneyChallengeResponse driverChallenge(Drivers d){
    	int a=random.nextInt(9)+1,b=random.nextInt(9)+1;
    	d.setWithdrawChallengeQuestion("What is "+a+" + "+b+"?");
    	d.setWithdrawChallengeAnswer(a+b); driversRepository.save(d); 
    	return MoneyChallengeResponse.active(d.getWithdrawChallengeQuestion(),3-d.getWithdrawWrongAttempts()); }
    
    public MoneyChallengeResponse generateDriverWithdrawChallenge(int id){
    	Drivers d=driver(id); 
    	
    	if(d.getWithdrawLockedUntil()!=null&&d.getWithdrawLockedUntil().isAfter(LocalDateTime.now())) 
    		return MoneyChallengeResponse.locked(d.getWithdrawLockedUntil());
    	
    	return driverChallenge(d); 
    }
    
    public BigDecimal verifyDriverWithdrawChallenge(VerifyWithdrawChallengeRequest request){ 
    	Drivers driver=driver(request.getDriverId());
    	if(request.getAmount()==null||request.getAmount().compareTo(BigDecimal.ZERO)<=0) 
    		throw new IllegalArgumentException("Enter a valid amount"); 
    	
    	if(request.getAmount().compareTo(driver.getBalance())>0) 
    		throw new InsufficientBalanceException("Insufficient driver balance"); 
    	
    	if(driver.getWithdrawLockedUntil()!=null && driver.getWithdrawLockedUntil().isAfter(LocalDateTime.now())) 
    		throw new InsufficientBalanceException("Withdraw is temporarily locked");
    	
    	if(driver.getWithdrawChallengeAnswer()==null || !driver.getWithdrawChallengeAnswer().equals(request.getAnswer())){
    		int n=driver.getWithdrawWrongAttempts()+1;
    		driver.setWithdrawWrongAttempts(n);
    		if(n>=3){
    			driver.setWithdrawLockedUntil(LocalDateTime.now().plusHours(24));
    			driver.setWithdrawChallengeAnswer(null);
    			driver.setWithdrawChallengeQuestion(null);
    			driversRepository.save(driver);
    			throw new InsufficientBalanceException("Three incorrect answers. Withdraw is locked for 24 hours");
    		}
    		driversRepository.save(driver);
    		throw new IllegalArgumentException("Incorrect answer. "+(3-n)+" attempts remaining");
    	} 
    	driver.setBalance(driver.getBalance().subtract(request.getAmount()));
    	driver.setWithdrawWrongAttempts(0);
    	driver.setWithdrawLockedUntil(null);
    	driver.setWithdrawChallengeAnswer(null);
    	driver.setWithdrawChallengeQuestion(null);
    	driversRepository.save(driver);
    	setDriverTransaction(driver,null,request.getAmount());
    	return driver.getBalance();
    }


    
    public Users updateUserProfilePhoto(int id,String photo){
    	Users u=user(id);
    	u.setProfilePhoto(photo);
    	return usersRepository.save(u); 
    }
    
    public Drivers updateDriverProfilePhoto(int id,String photo){
    	Drivers d=driver(id);
    	d.setProfilePhoto(photo);
    	return driversRepository.save(d);
    }

    public void deleteUserAccount(int id){ 
    	Users u=user(id);
    	if(ridesRepository.existsByUser_UserIdAndRideStatus(id,RideStatus.ONGOING))
    		throw new IllegalArgumentException("Cannot delete account during an ongoing ride");
    	
    	u.setActive(false);
    	usersRepository.save(u);
    }
    
    public void deleteDriverAccount(int id){ 
    	Drivers d=driver(id);
    	if(ridesRepository.existsByDriver_DriverIdAndRideStatus(id,RideStatus.ONGOING))
    		throw new IllegalArgumentException("Cannot delete account during an ongoing ride");
    	
    	d.setActive(false);
    	d.setAvailableStatus(false);
    	driversRepository.save(d); 
    }
}
