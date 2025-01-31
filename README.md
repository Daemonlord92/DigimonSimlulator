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

### Evolution Lines
The simulator includes multiple complete evolution lines, each with unique characteristics:

1. Classic Lines:
   - Agumon Line (Balanced): Botamon → Koromon → Agumon → Greymon → MetalGreymon → WarGreymon
   - Gabumon Line (Physical): Punimon → Tsunomon → Gabumon → Garurumon → WereGarurumon → MetalGarurumon
   - Patamon Line (Holy): Poyomon → Tokomon → Patamon → Angemon → MagnaAngemon → Seraphimon

2. Nature/Plant Line:
   - Palmon Line: Yuramon → Tanemon → Palmon → Togemon → Lillymon → Rosemon
   - Balanced evolution line focusing on nature-based abilities

3. Dark/Virus Line:
   - DemiDevimon Line: Zurumon → Pagumon → DemiDevimon → Devimon → Myotismon → VenomMyotismon
   - High aggression evolution path with powerful offensive capabilities

4. Machine/Android Line:
   - Hagurumon Line: MetalKoromon → Kapurimon → Hagurumon → Guardromon → Andromon → HiAndromon
   - Defensive evolution line with high health stats

5. Aquatic/Marine Line:
   - Gomamon Line: Pichimon → Bukamon → Gomamon → Ikkakumon → Zudomon → Plesiomon
   - Versatile evolution line with balanced stats

## Technical Features

### Visual Interface (JavaFX)
- Real-time visualization of the Digimon world
- Grid-based sector display with color-coded cells
- Interactive UI with tabbed interface for:
   - Sector information and grid visualization
   - Tribe status and management
   - Event logging and monitoring
- Custom styling for a retro digital aesthetic
- Real-time updates of Digimon positions and states

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

## Getting Started

### Prerequisites
- Java JDK 17 or higher
- JavaFX SDK 17.0.6 or higher
- Maven
- Minimum 4GB RAM recommended

### System Requirements
- Operating System: Windows 10/11, macOS 10.15+, or Linux
- Graphics: OpenGL 2.0 capable system
- Display: Minimum resolution of 1200x800 pixels

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/digimon-simulator.git
   ```
2. Navigate to the project directory:
   ```bash
   cd digimon-simulator
   ```
3. Ensure JavaFX SDK is properly installed and JAVA_HOME is set
4. Build with Maven:
   ```bash
   mvn clean install
   ```

### Running the Simulator
1. Launch from the command line:
   ```bash
   java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar target/digimon-simulator-1.0-SNAPSHOT.jar
   ```
   Or use the Maven plugin:
   ```bash
   mvn javafx:run
   ```
2. The simulation will automatically initialize with:
   - 100 regular Digimon
   - 10 Celestial Digimon
   - 10 sectors with interconnected grids
   - Full graphical interface

### User Interface Guide
1. Main Window
   - World Info: Current time, age, and global statistics
   - Sector View: Grid-based visualization of each sector
   - Event Log: Real-time updates of world events

2. Sector Tab
   - Click on cells to view detailed information
   - Digimon are represented as green circles
   - Buildings shown as blue squares with type indicators
   - Border cells in dark gray, blocked cells in red

3. Tribes Tab
   - List of all tribes and their current status
   - Member counts and territory information
   - Technology levels and military strength

4. Events Tab
   - Attack events in real-time
   - Political events and tribe interactions
   - Natural events and world changes

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

### Evolution Stages
- Fresh: Initial stage with basic stats
- In-Training: First evolution with slightly improved stats
- Rookie: Basic combat-capable form
- Champion: Significant power increase
- Ultimate: Advanced form with specialized abilities
- Mega: Final evolution with maximum potential

### Customizing the Interface
The visual appearance can be modified through the `styles.css` file:
```css
/* Example of customizing the interface */
.root {
    -fx-font-family: 'Courier New';
    -fx-background-color: #000000;
}
```

### Modifying World Behavior
- Adjust grid size in `Sector.java`
- Modify event probabilities in `EventSystem.java`
- Configure building types and effects in `Building.java`

## Troubleshooting
Common issues and solutions:
1. JavaFX not found
   ```bash
   Error: JavaFX runtime components are missing
   ```
   Solution: Ensure JavaFX SDK is properly installed and module path is correct

2. Display scaling issues
   - Windows: Adjust your display scaling settings
   - macOS: Use the recommended display resolution

## Contributing
Contributions are welcome! Please read our contributing guidelines before submitting pull requests.

## License
This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments
- The Digimon franchise for inspiration
- Contributors to the grid system and pathfinding algorithms
- Open source community for various utilities and tools