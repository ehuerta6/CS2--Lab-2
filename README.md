# Gacha Game in Java

## Overview
This Java implementation of a Gacha Game features a graphical user interface where players can collect heroes and battle against villains. The game combines elements of chance, strategy, and character progression in an engaging combat system.

## Features
- **Graphical User Interface**: Play through a user-friendly GUI built with Java Swing.
- **Character Collection**: Draw heroes from a gacha pool with different rarity levels.
- **Turn-Based Combat**: Engage in battles against villains with strategic gameplay.
- **Character Progression**: Level up heroes to increase their stats and abilities.
- **Dynamic Battle System**: Real-time updates of character health and actions during combat.
- **Villain Defeat Counter**: Tracks the number of villains defeated in a session.
- **Interactive Menus**: Includes main menu, character selection, and battle screens.
- **Multiple Classes**: Game logic split into separate classes for better organization and maintainability.

## Files
- `Main.java`: Entry point, handles the game flow and GUI initialization.
- `GachaGameBoard.java`: Core game logic, including battle system and GUI updates.
- `GachaCharacter.java`: Represents both heroes and villains with their attributes and methods.
- `GachaPool.java`: Manages the gacha drawing system and character pools.
- `BattleSystem.java`: Handles the combat logic between heroes and villains.
- `FileIO.java`: Manages file operations for saving and loading game data.

## How to Run
1. Clone the repository and navigate to the project directory.
2. Ensure all necessary resource files are in the correct directories.
3. Compile and run the Java program:
   ```
   javac Main.java GachaGameBoard.java GachaCharacter.java GachaPool.java BattleSystem.java FileIO.java
   java Main
   ```
4. Use the GUI to play the game, draw characters, and engage in battles.

## GUI Features
- Main menu with options to start a new game, load a saved game, or exit.
- Character drawing interface with animations and rarity displays.
- Interactive battle screen with health bars and action buttons.
- Character inventory and progression menu.
- End-battle dialog with options to continue or return to main menu.
- Visually appealing character portraits and battle backgrounds.

This project demonstrates proficiency in Java programming, GUI design with Swing, object-oriented programming principles, and implementing complex game systems while maintaining a user-friendly interface. It showcases skills in managing game state, balancing gameplay mechanics, and creating an engaging user experience.
