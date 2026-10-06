package com.bikebookingservice.controller;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import com.bikebookingservice.service.BikeService;
import com.bikebookingservice.entity.*;
import com.bikebookingservice.request.*;
import com.bikebookingservice.response.*;

@RestController
@RequestMapping("/bike")
public class BikeController {
	
    @Autowired 
    private BikeService service;

    @PostMapping("/accounts/user") 
    public UserLoginResponse createUser(@RequestBody CreateUserRequest request){
    	return new UserLoginResponse(service.createUser(request)); 
    }
    
    @PostMapping("/accounts/driver")
    public DriverLoginResponse createDriver(@RequestBody CreateDriverRequest request){
    	return new DriverLoginResponse(service.createDriver(request)); 
    }
    
    @PostMapping("/userLogin") 
    public UserLoginResponse userLogin(@RequestBody Users user){
    	return new UserLoginResponse(service.getUserDetails(user)); 
    }
    
    @PostMapping("/driverLogin") 
    public DriverLoginResponse driverLogin(@RequestBody Drivers driver){ 
    	return new DriverLoginResponse(service.getDriverDetails(driver)); 
    }
    

    
    @GetMapping("/userLogin/checkBalance/{id}") 
    public BigDecimal userBalance(@PathVariable int id){ 
    	return service.getUserBalance(id); 
    }
    
    @GetMapping("/driverLogin/checkBalance/{id}") 
    public BigDecimal driverBalance(@PathVariable int id){ 
    	return service.getDriverBalance(id); 
    }
    
    @GetMapping("/user/stats/{id}") 
    public Map<String,Object> userStats(@PathVariable int id){ 
    	return service.getUserStats(id); 
    }
    
    @GetMapping("/driver/stats/{id}") 
    public Map<String,Object> driverStats(@PathVariable int id){ 
    	return service.getDriverStats(id);
    }

    

    @PostMapping("/userLogin/bookRide")
    public Rides book(@RequestBody BookRideRequest request){ 
    	return service.bookRideRequest(request); 
    }
    
    @GetMapping("/user/add-money/challenge/{id}") 
    public MoneyChallengeResponse addChallenge(@PathVariable int id){
    	return service.generateUserMoneyChallenge(id); 
    }
    
    @PostMapping("/user/add-money/verify") 
    public BigDecimal addVerify(@RequestBody VerifyMoneyChallengeRequest r){ 
    	return service.verifyUserMoneyChallenge(r); 
    }
    
    @GetMapping("/driver/withdraw/challenge/{id}")
    public MoneyChallengeResponse withdrawChallenge(@PathVariable int id){ 
    	return service.generateDriverWithdrawChallenge(id);
    }
    
    @PostMapping("/driver/withdraw/verify") 
    public BigDecimal withdrawVerify(@RequestBody VerifyWithdrawChallengeRequest r){ 
    	return service.verifyDriverWithdrawChallenge(r); 
    }


    
    @GetMapping("/userLogin/rideHistory/{id}")
    public Page<Rides> userRides(@PathVariable int id,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="5") int size){ 
    	return service.getUserRideHistory(id,page,size); 
    }
    
    @GetMapping("/driverLogin/rideHistory/{id}") 
    public Page<Rides> driverRides(@PathVariable int id,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="5") int size){ 
    	return service.getDriverRideHistoryPage(id,page,size); 
    }
    
    @GetMapping("/userLogin/transactionHistory/{id}") 
    public Page<UserTransactions> userTx(@PathVariable int id,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="5") int size){ 
    	return service.getUserTransactionsHistory(id,page,size); 
    }
    
    @GetMapping("/driverLogin/transactionHistory/{id}") 
    public Page<DriverTransactions> driverTx(@PathVariable int id,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="5") int size){
    	return service.getDriverTransactionsPage(id,page,size); 
    }


    
    @PutMapping("/user/profile-photo/{id}")
    public UserLoginResponse userPhoto(@PathVariable int id,@RequestBody Map<String,String> r){ 
    	return new UserLoginResponse(service.updateUserProfilePhoto(id,r.get("profilePhoto"))); 
    }
    
    @PutMapping("/driver/profile-photo/{id}")
    public DriverLoginResponse driverPhoto(@PathVariable int id,@RequestBody Map<String,String> r){ 
    	return new DriverLoginResponse(service.updateDriverProfilePhoto(id,r.get("profilePhoto")));
    }
    
    @DeleteMapping("/user/{id}") 
    public String deleteUser(@PathVariable int id){ 
    	service.deleteUserAccount(id); 
    	return "User account deleted successfully"; 
    }
    
    @DeleteMapping("/driver/account/{id}")
    public String deleteDriver(@PathVariable int id){ 
    	service.deleteDriverAccount(id);
    	return "Driver account deleted successfully";
    }
}
