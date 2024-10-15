# Picit App (Work in progress)

Project Hogeschool Utrecht, een prototype van een retail supermarkt App.

The **Picit App** is a product management system developed to help customers and employees efficiently manage products and their availability. It features user interfaces for both employees (to manage stock, products, etc.) and customers (to view products and their availability).

## Inhoud

1. [Configuratie](#configuratie)
2. [Installatiehandleiding](#installatiehandleiding)
3. [Packages Project (beheerd door Maven)](#packages-project-beheerd-door-maven)

---

## Configuratie

Voordat je de applicatie kunt uitvoeren, moet je ervoor zorgen dat alle benodigde configuratiebestanden aanwezig zijn.

1. **secret.properties** - Dit bestand moet zich in `src/main/resources/com/pickmin/config/` bevinden. Dit bestand bevat configuraties zoals project paden.
2. **Vertalingsbestanden** - Vertalingsbestanden bevinden zich in de map `src/main/resources/com/pickmin/translation/`. Voeg hier extra vertalingen toe als dat nodig is.
3. **JSON-data** - data bestanden staan in de `src/main/resources/com/pickmin/data/` map. Voeg hier de juiste JSON-bestanden toe.

---

## Installatiehandleiding

### Vereisten:

-   **Java** (versie 22.0.2 of hoger)
-   **Maven** (voor build- en dependency-management)

### Steps to Build the Project

1. **Clone the repository**.
2. **Navigate to the project directory in the terminal**.
3. **Build the project using Maven**:
    - To compile the project:
        ```bash
        mvn clean compile
        ```
    - To compile and build the project (with testing):
        ```bash
        mvn clean install
        ```
    - If you need to skip tests:
        ```bash
        mvn clean install -DskipTests
        ```
    - if you need to create a JAR file (is created in the `target`-folder):
       ```bash
       mvn clean package
       ```

### Running the Application
To run the application using Maven:
```bash
mvn javafx:run
```
To run the application using vscode:
- open the project in the editor. 
- open the main class and using the `run java` button on the top right. (`run code` setting with the button doesn't work correctly)

The main class is `com.pickmin.App`, and this will launch the JavaFX GUI for the Picit App.

---

## Packages Project (beheerd door Maven)
- **JavaFX** Voor de grafische interface.
   - **javafx-controls (13)**: Biedt JavaFX UI elementen, zoals buttons, textFields, and comboBoxes.
   - **javafx-fxml (13)**: Voor het laden van FXML-bestanden en het bouwen van gebruikersinterfaces.
- **ControlsFX**: Biedt extra JavaFX UI elementen, zoals `CheckComboBox`.
- **org.json**: Voor het verwerken van JSON data.
- **Spring Security**: voor beveiligingsfunctionaliteiten zoals wachtwoord hashing

These dependencies are configured in the `pom.xml` file.