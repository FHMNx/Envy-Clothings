
# Envy Clothings  
A Modern Online E-Commerce Clothing Shop Web Application

## Overview
Envy Clothings is a full-featured online e-commerce web application designed for selling clothing products efficiently and securely.  
The platform provides a smooth shopping experience for customers and a powerful management system for admins, including product handling, user authentication, order processing, and email verification.

## Configuration Setup
Before running the project, create a configuration file named:
```app.properties```
Add the following properties inside it:
```
db.url=jdbc:mysql://localhost:3306/envyclothings
db.username=your_database_username
db.password=your_database_password

mail.host=smtp_host_here
mail.port=smtp_port_here
mail.username=your_email_here
mail.password=your_email_password_here
app.mail=[noreply@envyclothings.com](mailto:noreply@envyclothings.com)
app.name=Envy Clothings

````

## How to Run

### 1. Clone the repository:
``` bash git clone https://github.com/your-username/EnvyClothings.git ```

### 2. Open the project
Open the project in **IntelliJ IDEA** (recommended) or any preferred Java IDE.

### 3. Configure the database
* Create a MySQL database named:
``` envyclothings ```
* Update the DB credentials inside `app.properties`.

### 4. Build and Run the project
Use **Maven** or your IDE's build tools to run the application.

## Technologies Used
### **Frontend**
* HTML5
* CSS3
* JavaScript
* GSAP
* Tailwind CSS
* Bootstrap

### **Backend**
* Java (JDK 17+)
* Hibernate (ORM)
* Jakarta EE
* Jersey (REST API)
* Jakarta Mail API
* Email Template Engine (RocketBase)

### **Database**
* MySQL
* HeidiSQL / MySQL Workbench

### **Build Tool**
* Maven

## Features

* User registration & login with email verification
* Secure password validation
* Session-based login management
* Add to cart, checkout flow
* Product listing & filtering
* Admin panel for managing products and users
* Email notifications (verification, alerts, etc.)
* RESTFUL API endpoints (Jersey + JSON communication)


## Author
**M.F.A. Fahman**
Java Institute for Advanced Technology

## License
This project is licensed under the **MIT License**.

