# Investments Wallet
Investments Wallet is a full-stack application designed to help users manage and track their personal investments. It allows for registering new investments, viewing a dashboard, and checking the history of investments.
## How to execute ?
### Backend
1. Navigate to the `backend` folder.
2. Update Maven packages and build the project: ```mvn clean install ``` 
3. Run the application (e.g., using the Spring Boot Maven plugin): ```mvn spring-boot:run ``` 
Alternatively, you can open the `backend` folder in an IDE like IntelliJ IDEA, let it resolve Maven dependencies, and run the `DemoAuthAppApplication` main class.
### Frontend
1. Navigate to the `frontend` folder. 
2. Install the necessary packages: ```npm install ``` 
3. Run the frontend development server: ```npm run dev ```
## How to use this application
After successfully executing both the frontend and backend projects, open your browser and navigate to: [http://localhost:5173/](http://localhost:5173/)

You can create a new account or use the following pre-configured test account: 
* **Login:** `fabio.nothing@snow.com` 
* **Password:** `123123`

**Application Features:** 
* **Dashboard Page:** View a summary of your investments. 
* **Assets Page:** Register new investments. 
* **Wallet Page:** See all your active investments and their history.
