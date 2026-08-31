# IITM-CCW Hosteldine

## Overview

This is a spring-boot application used to manage the activities of the CCW-Hosteldine department of IITM.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Developer Usage](#developer-usage)
- [Running Tests](#running-tests)
- [Contributing](#contributing)
- [License](#license)

## Prerequisites

Before you begin, ensure you have the following installed:

- [Java 21](https://www.oracle.com/in/java/technologies/downloads/#java21)
- [PostgreSQL 16](https://www.postgresql.org/download/)
- [Project Lombok](https://projectlombok.org/)
- [Mapstruct](https://mapstruct.org/documentation/ide-support/)

## Installation

1. **Clone the repository**:

    ```bash
    git clone https://github.com/gsai79/iKollege-Refactoring.git
    cd your-repo
    ```

2. **Build the project**:

    ```bash
    mvn clean install
    ```
  
3. **Set up Project Lombok**:

   - **IntelliJ**:
     - Install the Project Lombok plugin from here [Lombok for IntelliJ](https://plugins.jetbrains.com/plugin/6317-lombok)
   - **STS**:
     - Follow the instructions in this site [Lombok for STS](https://projectlombok.org/setup/eclipse)
   
4. **Set up Mapstruct**:

   - **IntelliJ**:
     - Install the Mapstruct support plugin from here [Mapstruct Support for IntelliJ](https://plugins.jetbrains.com/plugin/10036-mapstruct-support)
   - **STS**:
     - Follow the instructions in this site [Mapstruct Support for STS](https://mapstruct.org/documentation/ide-support/)
   
5. **Set up your property file**:

   - Create a replication of the `application.properties` with your name for example `application-name.properties`.
   - Remove all the parameters and keep only those which you want to change.

6. **Set up the PostgreSQL database**:

   - Create a new PostgreSQL database:

       ```sql
       CREATE DATABASE iitm_dev;
       ```

   - Get the dev DB from the IT team.
   - Import the DB to the created DB.
     - Windows:
       - Open `SQLShell` application.
       - Enter the Postgres credentials like the `host`, `DB name`, `port`, `username` and `password`.
       - Once inside, enter the following command to start the import:
       > \i C:/path/to/your/sql/file.sql
     - Linux:
       - Open Terminal.
       - Login in as postgresql by using the following command
       ```bash
       su postgres
       ```
       - Enter the following command to import the DB to the desired DB you created.
       ```bash
       iitm_dev < /path/to/your/sql/file.sql
       ```
   - In the property file that you created, modify the postgres DB url with the proper `Host`, `Port number` and `DB Name`. 
   - Modify the keys `spring.datasource.username` and `spring.datasource.password` with the proper username and password.
   ```properties
       spring.datasource.url=jdbc:postgresql://localhost:5434/iitm
       spring.datasource.username=postgres
       spring.datasource.password=password
   ```
    - sd
   
7. **Run the application**:
   ```bash
       mvn spring-boot:run
   ```

## Configuration

Configuration settings can be found in the `src/main/resources/application.properties` file. Here's
an example:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/iitm_dev
spring.datasource.username=your-username
spring.datasource.password=your-password
spring.jpa.hibernate.ddl-auto=update
```

## Developer Usage

Elements to follow during development process.

1. **Naming conventions to follow**:
   - File name syntax for UI elements js, css, html and image assets
     - `first-second-4.js` | all small | hyphen to separate words and numbers.
   - Package, method, function, non-static variables' Names in both front end files and in Java. 
     - `firstSecond1` | start with small | cap letter for each new word.
   - Java Class Names
     - `FirstSecond1.java` | start with cap | cap letter for each new word. Avoid numbering in class names if possible.
   - Static variables names
     - `FIRST_4_SECOND` | all cap | underscore to separate words and numbers.

2. **Checks to follow when creating a new Entity Class for existing table**:
   - Check for all the column names for spelling corrections
   - Check if the column name has a prefix in the format `<data type>_<table code>_<column name>`. For example: Table: `event_master` Existing Column: `n_em_event_name` Expected: `event_name`.
   - Should have these 5 columns with these names only: `created_by` `created_at` `modified_by` `modified_at` `active_flag`. If any of these columns are named differently for example, in case of `modifed_at` it has `last_changed_at` or `updated_at`.
   - Spelling mistakes in the table name.
   - For all the above points, prepare the `alter` scripts and update it in the [structure_change.sql](structure_change.sql) file in the root folder.
   - Create the Entity class with the suffix `Entity` in the package `com.iitm.hosteldine.generated.model`. Eg. For the table `event_master`, the class name should be `EventMasterEntity`
   - Once the Entity class is created, extend it to the class [CommonEntity](src%2Fmain%2Fjava%2Fcom%2Fiitm%2Fhosteldine%2Fmodel%2FCommonEntity.java).
   - Check for any spelling errors in the variable names.
   - Remove the common 5 variables `createdAt` `createdBy` `modifiedAt` `modifiedBy` and `activeFlag` as they are already configured in the super class [CommonEntity](src%2Fmain%2Fjava%2Fcom%2Fiitm%2Fhosteldine%2Fmodel%2FCommonEntity.java).
   - Add the following annotations: `@Getter`, `@Setter`
   - In the `@Entity` annotation, remove the name if auto-generated.

3. **Procedure to create DTO, Mapper and Repository files**:
   - Once the Entity class is created, open the [CreateDTOFiles](src%2Fmain%2Fjava%2Fcom%2Fiitm%2Fhosteldine%2Futil%2FCreateDTOFiles.java) class.
   - In the `include` variable, add the Entity class names for which you intend to create the DTO, Mapper and Repository files.
   - Run the class file `CreateDTOFiles`.
   - You can see the respective files under the package `com.iitm.hosteldine.generated`.
   - Check for any code corrections if necessary.
   - Move all the files to the appropriate packages before starting to use the classes and interfaces.

4. **Checks to follow during code commit**:
   - Merge the latest code from the Development branch.
   - Make sure the newly created packages and class names are within the mentioned naming convention.
   - If a flow is complete, make sure there are no lint errors/warnings in your Java code(Errors/warnings in front end files like HTML, JS and CSS can be ignored). Check for spelling mistakes throughout the code you worked on, including the comment sections.
   - Go to [pom.xml](pom.xml). In the application's version tag `<version>`, it has 3 numbers separated by a dot. Increment the last number by 1. Eg: `<version>1.34.23456</version>`, increment the number `23456` to `23457`.
   - This will be reflected in the footer section in the application which will help detect and resolve bugs and retest them more efficiently.
   - If you are commenting a function which was in use, but now it is not going to be used in the future, at the start of the comment, mention the current application version and the reason for commenting it. eg:
     ```
        /*#1.23.4567 - commented reason if needed.*/
        /*function oldMenthod() {
            ...
        }*/
       ```
   - If the function remains commented for a long time, it will be removed permanently.
   - Make sure there are no unused methods that you have created during the development. If you have, please remove it before committing or at least add the version tag and comment it.
   - Once all the checks are complete and the version number incremented, commit and push your code to your branch and create a PR in the GITHUB for code review.

