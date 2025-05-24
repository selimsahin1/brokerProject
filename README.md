# brokerProject

## Project Description

This project is a broker application. Users can perform stock trading operations, create and manage orders.

## Technologies Used

- Java 17+
- Spring Boot
- Maven
- SQL (database)
- JUnit (for testing)

## Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/selimsahin1/brokerProject.git
    ```
2. Go to the project directory:
    ```bash
    cd brokerProject
    ```
3. Install Maven dependencies:
    ```bash
    mvn clean install
    ```
4. Start the application:
    ```bash
    mvn spring-boot:run
    ```
5. The application will run at `http://localhost:8080` by default.

## Features

- Create buy/sell orders
- Cancel orders
- Order matching
- User asset management

## Running Tests

To run tests:
```bash
mvn test