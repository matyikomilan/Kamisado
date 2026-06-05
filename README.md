# Kamisado Board Game
A complete Java implementation of the strategy board game: **Kamisado**.
This project was developed as a university assignment to demonstrate object-oriented programming, software architecture, and unit testing.

## About the Project
Kamisado is a game played on an 8x8 board where players move colored towers. The core mechanic is that the color of the square a tower lands on, determines which tower the opponent must move next with. 

This project was built from scratch, adhering to the **MVC (Model-View-Controller)** design pattern to ensure a clean separation of game logic, the UI and the input handlers.

### Key Features
* **Game Logic:** All official Kamisado movement and winning rules.
* **Save/Load:** Players can save their current game state (`.KAM` files) and reload them later.
* **Architecture:** Clean MVC pattern.
* **Unit-testing:** Core logic components: Board, GameLogic and GameState are mostly covered with JUnit tests.
* **GUI:** using Java Swing/AWT.

## Project Structure
The source code is organized clearly into the following packages:
* `src/kamisado/model/` - Contains the data structures and pure game rules (`Board`, `Tower`, `GameLogic`, etc.).
* `src/kamisado/view/` - Contains the graphical components (`MainFrame`, `GamePanel`, `MenuPanel`).
* `src/kamisado/controller/` - Connects the UI events with the underlying model.
* `src/kamisado/tests/` - JUnit 4 test cases verifying the integrity of the game logic.

## Documentation
Detailed project documentation can be found in the `docs` folder, showcasing the software engineering process:
* **Specification** (`specifikacio.pdf`)
* **User Manual** (`felhasznaloi_kezikonyv.pdf`)
* **UML Class Diagrams**

## How to Run
1. Clone the repository.
2. Compile the `.java` files in the `src` directory.
3. Ensure the `pics` folder is in the same working directory as the compiled classes for assets to load properly.
4. Run the `MainFrame` class (or the designated main entry point) to start the game.