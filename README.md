# Bike Booking Service

A full-stack Bike Booking application developed using Spring Boot, MySQL, HTML, CSS, and JavaScript.

The application provides separate functionality for users and drivers, including ride booking, wallet management, ride tracking, transaction history, and driver earnings.

## Features

### User Features

- User registration and login
- User profile and profile photo
- Check wallet balance
- Add money to wallet
- Security challenge for adding money
- Book bike rides
- View available drivers
- View ride fare
- Track ongoing ride completion time
- View ride history
- View transaction history
- Delete account
- Logout

### Driver Features

- Driver registration and login
- Driver profile and profile photo
- View current balance
- View availability status
- View assigned rides
- Track ongoing ride completion time
- Receive ride earnings
- Withdraw money
- Security challenge for withdrawals
- View ride history
- View transaction history
- Delete account
- Logout

## Technologies Used

### Backend

- Java
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate
- Maven

### Frontend

- HTML5
- CSS3
- JavaScript

### Database

- MySQL

### Tools

- Git
- GitHub
- Eclipse
- VS Code
- Postman

## Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/bikebookingservice/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── entity/
│   │       ├── enums/
│   │       ├── exception/
│   │       ├── repository/
│   │       ├── request/
│   │       ├── response/
│   │       └── service/
│   │
│   └── resources/
│       ├── static/
│       │   ├── css/
│       │   ├── js/
│       │   ├── index.html
│       │   ├── dashboard.html
│       │   └── driver-dashboard.html
│       │
│       └── application.properties
│
└── test/