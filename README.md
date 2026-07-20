# Smart Emergency Response & Hospital Management System

A DSA semester project simulating an ambulance dispatch and single-hospital
emergency management system. Ambulances navigate a city road network
(graph) to reach patients and transport them to the hospital, where they
are triaged and treated based on urgency.

## Core Data Structures Used
- **Graph** — city road network (adjacency list), Dijkstra's shortest path, BFS reachability
- **Min-Heap (custom Priority Queue)** — triage queue, ambulance dispatch priority
- **Queue (FIFO, linked list based)** — waiting room, pending emergency calls
- **Doubly Linked List** — ambulance fleet, doctor list, event log
- **Stack** — undo last dispatch, route backtracking
- **Hash Map** — O(1) lookups for patients, ambulances, doctors

## Project Structure
- `model/` — data entities (Patient, Ambulance, Doctor, Hospital, RoadNode, RoadEdge)
- `datastructures/` — custom-built data structures (MinHeap, LinkedQueue, DoublyLinkedList, CustomStack, Graph, HashMapTable)
- `algorithms/` — Dijkstra, BFS, Round Robin scheduler
- `management/` — business logic (DispatchManager, RoutePlanner, TriageManager, DoctorManager, HospitalManager)
- `simulation/` — simulation engine (SimulationClock, EventGenerator, TrafficUpdater)
- `logging/` — EventLogger, StatsTracker
- `util/` — IDGenerator, Constants

## How to Run

### GUI version (recommended demo)
The `gui/` package contains a fully working Swing-based visualization:
a live city map with ambulances moving along routes, a triage queue,
doctor/ambulance status, and an event log — all driven by the real
MinHeap, Graph, Dijkstra, and LinkedQueue implementations in
`datastructures/` and `algorithms/`.

**In IntelliJ:** open the project folder, let it index, then right-click
`src/main/java/com/elevate/emergency/GuiMain.java` -> **Run
'GuiMain.main()'**. No JavaFX SDK or extra module setup needed — Swing
ships with the JDK.

**From the command line:**
```bash
javac -d out $(find src/main/java -name "*.java")
java -cp out com.elevate.emergency.GuiMain
```

**Toolbar controls:**
- **+ New Patient** — manually enter a single patient's name, location, and urgency
- **Report Incident** — report a multi-casualty incident: pick a location,
  a severity scale (Minor/Moderate/Major/Mass Casualty), and how many
  people are affected. Creates that many patients at once, skews their
  urgency harsher as scale increases, and immediately runs a priority
  dispatch pass so multiple idle ambulances respond at once to the most
  critical victims first (replaces the old "random emergency" button)
- **Pause / Resume** — freezes/resumes the simulation clock
- **Enable/Disable Manual Control** — when ON, nothing happens
  automatically: no background emergencies, no auto-dispatch, no
  auto-assignment of doctors. Every action must go through the intake
  forms or the Manage tab. Ambulances already en route still finish
  their trip physically either way
- **Reset** — restarts with a fresh controller
- **Light Mode / Dark Mode** — toggles the whole UI's color theme
- **Auto emergency rate slider** — now shows a live numeric readout
  (events/sec) next to it; has no effect while Manual Control is on

**"Live" tab (right panel):** current mode + rate readout, stats, triage
queue (critical patients pulse red; shows a clear placeholder message
instead of blank space when empty), ambulance/doctor status, event log.

**"Manage" tab (right panel):**
- **Doctor Roster** — add a doctor (name + specialty) or remove a selected
  idle doctor
- **Manual Ambulance Dispatch** — pick a specific pending call and a
  specific idle ambulance, dispatch them together
- **Manual Doctor Assignment** — pick a specific waiting patient and a
  specific available doctor, assign them together

**"Report" tab (right panel):** a full table of every patient treated
this session — ID, name, urgency, which doctor treated them, that
doctor's specialty, response time, treatment time, and discharge time —
plus an **Export Report (CSV)** button that saves it to a file you
choose via a save dialog.

Note: in Automatic mode, if a doctor is free the moment a patient reaches
triage, they're assigned before you can intervene manually — that's
intentional (a real ER wouldn't leave a patient waiting if staff were
free). The triage queue visibly fills up during busy periods, especially
after a Mass Casualty incident outpaces available doctors. Switch on
Manual Control if you want to see calls and patients queue up and assign
everything yourself.

### Console version
```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.elevate.emergency.Main"
```

Or without Maven:
```bash
javac -d out $(find src/main/java -name "*.java")
java -cp out com.elevate.emergency.Main
```
Note: `Main.java` is still scaffolding (TODOs) for the console/report
version — the GUI's `SimController` is the fully working implementation.

## How to Test
```bash
mvn test
```

## Author
Mushaf Ali — COMSATS University Islamabad, Abbottabad Campus
DSA Semester Project — Spring 2026
