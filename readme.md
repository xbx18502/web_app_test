# XM Blog Setup Guide

## Introduction

This guide provides step-by-step instructions to set up and launch the **XM Blog** application on your local machine. The application consists of a Spring Boot backend and a Vue.js frontend.

## Prerequisites

Before proceeding, ensure you have the following installed on your system:

- **Homebrew**: Package manager for macOS. [Install Homebrew](https://brew.sh/)
- **MySQL**: Relational database management system.
- **Java Development Kit (JDK)**: Version 1.8
- **Node.js and npm**: JavaScript runtime and package manager. [Download Node.js](https://nodejs.org/)
- **Maven**: Build automation tool for Java projects. [Install Maven](https://maven.apache.org/install.html)

## How to Launch

Follow the steps below to set up and run the XM Blog application.

### 2. Create a New Database

Create a new MySQL database named `xm-blog` with the character set `utf8mb4` and collation `utf8mb4_unicode_ci`.

1. **Access MySQL Command-Line Interface**:

    ```bash
    mysql -u your_username -p
    ```

    *Replace `your_username` with your MySQL username. You will be prompted to enter your password.*

2. **Create the Database**:

    ```sql
    CREATE DATABASE `xm-blog` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    ```

3. **Exit MySQL**:

    ```sql
    EXIT;
    ```

### 3. Import the Database Schema

Load the database schema from the `xm-blog.sql` file into the newly created `xm-blog` database.

1. **Navigate to the Directory Containing `xm-blog.sql`**:

    ```bash
    cd path/to/your/sql/file
    ```

2. **Import the Schema**:

    ```bash
    mysql -u your_username -p xm-blog < xm-blog.sql
    ```

    *Replace `your_username` with your MySQL username. You will be prompted to enter your password.*

### 4. Configure the Spring Boot Project

Set up the Spring Boot backend project to use JDK 1.8.

1. **Open the Project in Your IDE**:

    Open the `springboot` project in your preferred Integrated Development Environment (IDE) such as IntelliJ IDEA or Eclipse.

2. **Set JDK Version to 1.8**:

    - **IntelliJ IDEA**:
        - Go to `File` > `Project Structure` > `Project`.
        - Set the `Project SDK` to JDK 1.8.
        - Set the `Project language level` to `8 - Lambdas, type annotations, etc.`

    - **Eclipse**:
        - Right-click on the project > `Properties` > `Java Build Path` > `Libraries`.
        - Ensure that JDK 1.8 is selected.
        - Go to `Java Compiler` and set the `Compiler compliance level` to `1.8`.

3. **Verify Maven Configuration**:

    Ensure that the `pom.xml` is set to use Java 1.8.

    ```xml
    <properties>
        <java.version>1.8</java.version>
    </properties>
    ```

### 5. Edit `application.yml` for Database Connection

Configure the Spring Boot application to connect to the `xm-blog` MySQL database.

1. **Locate `application.yml`**:

    The `application.yml` file is typically located in the `src/main/resources` directory of your Spring Boot project.

2. **Update Database Connection Settings**:

    ```yaml
    spring:
      datasource:
        url: jdbc:mysql://localhost:3306/xm-blog?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=UTC
        username: your_username
        password: your_password
      jpa:
        hibernate:
          ddl-auto: update
        show-sql: true
    ```

    - **Parameters**:
        - `url`: JDBC URL for the MySQL database.
        - `username`: Your MySQL username.
        - `password`: Your MySQL password.
        - `ddl-auto`: Setting to `update` allows Hibernate to automatically update the database schema.
        - `show-sql`: Enables logging of SQL statements.

    *Replace `your_username` and `your_password` with your actual MySQL credentials.*

### 6. Launch the Applications

This step involves launching both the Spring Boot backend and the Vue.js frontend.

#### 6.1 Launch Spring Boot Application

1. **Navigate to the Spring Boot Project Directory**:

    ```bash
    cd path/to/springboot
    ```

2. **Build and Run the Application**:

    - **Using Maven Wrapper**:

        ```bash
        ./mvnw spring-boot:run
        ```

    - **Using Maven Installed on Your System**:

        ```bash
        mvn spring-boot:run
        ```

    *Ensure you are in the root directory of the Spring Boot project when executing the above command.*

3. **Verify the Backend is Running**:

    By default, Spring Boot runs on port `8080`. You should see logs indicating that the application has started successfully.

#### 6.2 Set Up the Vue.js Frontend

1. **Navigate to the Vue.js Project Directory**:

    ```bash
    cd vue
    ```

2. **Install Dependencies**:

    ```bash
    npm install
    ```

    *This command installs all necessary packages listed in `package.json`.*

#### 6.3 Launch the Vue.js Application

1. **Start the Development Server**:

    ```bash
    npm run serve
    ```

2. **Access the Frontend Application**:

    Once the development server is running, you can access the Vue.js application by navigating to `http://localhost:8080` (or the port specified in the terminal output) in your web browser.

## Accessing the Application

With both the Spring Boot backend and the Vue.js frontend running:

- **Backend API**: Accessible at `http://localhost:8080` (default port).
- **Frontend Interface**: Accessible at `http://localhost:8080` or the port specified by the Vue.js development server.

*Note: If both the backend and frontend are set to use the same port (`8080`), you may need to configure one of them to use a different port to avoid conflicts.*

## Troubleshooting

- **MySQL Issues**:
    - Ensure MySQL is running.
    - Verify that the credentials in `application.yml` are correct.
    - Check if the `xm-blog` database exists and is properly configured.

- **Port Conflicts**:
    - If port `8080` is already in use, you can change the port in the Spring Boot `application.yml`:

        ```yaml
        server:
          port: 8081
        ```

    - For Vue.js, you can change the port by modifying the `package.json` scripts or using environment variables.

- **Dependency Errors**:
    - For the backend, ensure all Maven dependencies are downloaded by running:

        ```bash
        mvn clean install
        ```

    - For the frontend, ensure all npm packages are installed by running:

        ```bash
        npm install
        ```

- **Application Not Starting**:
    - Check the console logs for any error messages.
    - Ensure that the Java version is set to 1.8.
    - Verify that all environment variables and configurations are correctly set.

## Contributing

Contributions are welcome! Please follow these steps to contribute:

1. **Fork the Repository**.
2. **Create a New Branch**:

    ```bash
    git checkout -b feature/YourFeatureName
    ```

3. **Make Your Changes**.
4. **Commit Your Changes**:

    ```bash
    git commit -m "Add Your Feature"
    ```

5. **Push to the Branch**:

    ```bash
    git push origin feature/YourFeatureName
    ```

6. **Create a Pull Request**.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
