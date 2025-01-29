# Digimon Simulator

## Overview

The Digimon Simulator is a sophisticated Java-based project that creates a living, breathing digital world where Digimon creatures evolve, interact, and form complex societies. The simulator features a rich ecosystem of regular and celestial Digimon, complete with territorial dynamics, social structures, and technological advancement.

## Key Features

### Core Mechanics
- Dynamic life cycle simulation with birth, evolution, and rebirth systems
- Comprehensive attribute tracking including health, hunger, and aggression
- Multiple evolution paths supported through JSON configuration
- Grid-based movement and territorial control within sectors

### Social Systems
- Tribe formation and management
- Political interactions including alliances and conflicts
- Professional specialization system (Farmers, Builders, Soldiers, Scientists)
- Resource management and distribution

### World Features
- Grid-based sector system with interconnected territories
- Building placement and ownership mechanics
- Technology progression through four ages
- Natural and political event systems

### Special Entities
- Celestial Digimon system with unique behaviors
- Limited population of 10 Celestial Digimon
- Special abilities including healing and food provision
- Immunity to death and aging

## Project Structure
```
digimon-simulator/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── horrorcore/
│   │   │           ├── grid/              # Grid system components
│   │   │           ├── entities/          # Digimon and related classes
│   │   │           ├── systems/           # Game mechanics
│   │   │           └── gui/              # Visual interface
│   │   └── resources/
│   │       ├── digimon.json             # Digimon configurations
│   │       └── evolution_rules.json     # Evolution path definitions
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── horrorcore/
└── README.md
```

## Technical Features

### Grid System
- Each sector contains a grid of cells
- Cells can contain Digimon, buildings, or remain empty
- Pathfinding system for intelligent movement
- Border cells for sector transitions
- Building placement with proximity rules

### Building System
- Multiple building types with different functions
- Ownership tracking by tribes
- Influence radius mechanics
- Automatic cleanup on tribe dissolution

### Movement Mechanics
- Grid-based pathfinding within sectors
- Strategic movement toward owned buildings
- Sector transition through border cells
- Tribe-influenced movement patterns

## Getting Started

### Prerequisites
- Java JDK 17 or higher
- Maven
- Minimum 4GB RAM recommended

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/digimon-simulator.git
   ```
2. Navigate to the project directory:
   ```bash
   cd digimon-simulator
   ```
3. Build with Maven:
   ```bash
   mvn clean install
   ```

### Running the Simulator
1. Launch from the command line:
   ```bash
   java -jar target/digimon-simulator-1.0-SNAPSHOT.jar
   ```
2. The simulation will automatically initialize with:
    - 100 regular Digimon
    - 10 Celestial Digimon
    - 10 sectors with interconnected grids

## Configuration

### Adding New Digimon
1. Add stats to `digimon.json`:
   ```json
   {
     "name": "NewDigimon",
     "age": 0,
     "health": 100,
     "hunger": 50,
     "aggression": 30,
     "stage": "Rookie"
   }
   ```
2. Define evolution paths in `evolution_rules.json`:
   ```json
   {
     "NewDigimon": {
       "Rookie": "EvolvedForm:Champion"
     }
   }
   ```

### Modifying World Behavior
- Adjust grid size in `Sector.java`
- Modify event probabilities in `EventSystem.java`
- Configure building types and effects in `Building.java`

## Contributing
Contributions are welcome! Please read our contributing guidelines before submitting pull requests.

## License
This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments
- The Digimon franchise for inspiration
- Contributors to the grid system and pathfinding algorithms
- Open source community for various utilities and tools