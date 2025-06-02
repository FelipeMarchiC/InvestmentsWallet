# Investments Wallet
Investments Wallet is a full-stack application designed to help users manage and track their personal investments. It allows for registering new investments, viewing a dashboard, and checking the history of investments.

**Application Features:** 
* **Dashboard Page:** View a summary of your investments. 
* **Assets Page:** Register new investments. 
* **Wallet Page:** See all your active investments and their history.

## How to execute?
### Option 1: Running Frontend and Backend Separately
### Backend
1. Navigate to the `backend` folder.
2. Update Maven packages and build the project: ```mvn clean install ``` 
3. Run the application (e.g., using the Spring Boot Maven plugin): ```mvn spring-boot:run ``` 
Alternatively, you can open the `backend` folder in an IDE like IntelliJ IDEA, let it resolve Maven dependencies, and run the `DemoAuthAppApplication` main class.
### Frontend
1. Navigate to the `frontend` folder. 
2. Install the necessary packages: ```npm install ``` 
3. Run the frontend development server: ```npm run dev ```

### Option 2: Running with Docker Compose

This method will build and run both the frontend and backend services in isolated Docker containers.

**Prerequisites:**
* Docker installed and running on your machine.
* Docker Compose installed (usually comes with Docker Desktop).

**Instructions:**
1.  **Build and Run with Docker Compose:**
    Navigate to the root directory of your project (where the `docker-compose.yml` file is located) in your terminal and run:
    ```bash
    docker-compose up --build
    ```

2.  **Stopping the Application:**
    To stop all services defined in the `docker-compose.yml`, open a new terminal in the same directory and run:
    ```bash
    docker-compose down
    ```
    This will stop and remove the containers.

## How to use this application
After successfully executing one of the options above, open your browser and navigate to: [http://localhost:5173/](http://localhost:5173/)

You can create a new account or use the following pre-configured test account: 
* **Login:** `fabio.nothing@snow.com` 
* **Password:** `123123`
