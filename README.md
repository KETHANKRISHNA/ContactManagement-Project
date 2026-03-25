# Contact Management System

A Java-based Contact Management System featuring an in-memory CRUD engine and a modern JavaFX Graphical User Interface. It allows users to add, view, update, and delete contacts, and includes functionalities to export and import standard VCard (.vcf) files.

## Prerequisites
- Java JDK 17 or higher
- An IDE with built-in Maven support (IntelliJ IDEA, Eclipse, or VS Code) OR Apache Maven installed globally.

## How to Run & Database Setup

1. **Database Setup**: As per the application requirements, this application runs entirely **In-Memory** with no external persistence database. Data is stored sequentially in an `ArrayList` while the application is actively running. There is no SQL database to configure or initialize.

2. **Running via IDE (Recommended)**:
   - Open your favorite IDE (IntelliJ IDEA, Eclipse, etc.) and select **Import Project from Existing Sources**. Select the `pom.xml` file.
   - The IDE will download all required JavaFX dependencies automatically.
   - Wait for the project to index, then navigate to `src/main/java/com/shadowfox/contact/App.java`.
   - Run the `main` method in `App.java`. The IDE will automatically configure the required JavaFX modules and launch the application.

3. **Running via CLI (If Maven is installed)**:
   - Open a terminal in the root directory of this project.
   - Run the following command:
     ```bash
     mvn clean compile javafx:run
     ```

## Architecture Decisions
- **In-Memory Storage**: Used an `ArrayList` in `ContactManager` because the application prioritizes viewing and reading over frequent list modifications. An `ArrayList` is faster for lookups and iterations. We used a `HashSet` internally for fast O(1) duplicate checks on phone numbers.
- **JavaFX / No FXML**: The UI is built entirely in Java code without external `.fxml` files to keep the setup simple and programmatic, allowing strong data binding between the `Contact` POJO and the `TableView`.
- **Validation**: Strict RegEx constraints ensure dirty data doesn't get into the in-memory construct.
