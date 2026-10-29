# Car Insurance Management System (J1.L.P0038)

A robust, enterprise-grade console application for managing cars and insurance statements, built in Java adhering to **Computational Thinking (CT)** methodology and strict **MVC (Model-View-Controller)** architecture.

---

## 📌 Overview

This project was developed for **LAB211 - Core Java Lab (300 LOCs)**. It emphasizes clean software architecture, object-oriented design principles (encapsulation, abstraction, single inheritance, and interface polymorphism), strict separation of concerns, and defensive data persistence.

### Key Highlights
- **Strict MVC Separation**: Controller layer is 100% decoupled from View / Console I/O (`System.out`, `Scanner`).
- **Optimal Time Complexity**: Leverages `Map<String, Car>` and `Map<String, InsuranceStatement>` for $O(1)$ key lookups, and a `HashSet` lookup strategy for $O(N + M)$ uninsured car reporting.
- **Data Integrity & Consistency**: Enforces referential integrity guards (preventing deletion of insured vehicles) and domain-level fee invariant validation.
- **Transactional Two-Phase Persistence**: File save operations utilize staged temporary files (`.tmp`) and verified backups (`.bak`) to guarantee atomic writes with zero risk of partial commits or corrupted data.

---

## 🏛️ System Architecture & Package Structure

The project follows a standard 4-tier MVC layout:

```text
src/
├── dto/                       # Data Transfer Objects & Interfaces
│   ├── Car.java               # Car domain model
│   ├── InsuranceStatement.java# Insurance statement domain model
│   ├── ICrud.java             # Generic CRUD base interface
│   ├── ICarManager.java       # Interface for car business operations
│   ├── IInsuranceManager.java  # Interface for insurance operations
│   └── IMenu.java             # Interface for menu abstraction
│
├── controller/                # Business logic & collection manipulation
│   ├── CarController.java     # Manages Map<String, Car> and validation rules
│   ├── InsuranceController.java# Manages Map<String, InsuranceStatement> & fee logic
│   └── Menu.java              # Concrete console menu model
│
├── view/                      # User Interface & Console interaction
│   ├── Main.java              # Application entry point & dependency wiring
│   ├── InsuranceAppView.java  # User interaction flow, validation loop, UI screens
│   └── TablePrinter.java      # Formatted tabular display utility
│
└── utils/                     # Shared static utilities
    ├── InputUtils.java        # Robust console input reading & Enter-key handling
    ├── ValidationUtils.java   # Regular expressions & format validation rules
    └── FileUtils.java         # Atomic file I/O operations (Object serialization)
```

---

## 🚀 Features Checklist

| # | Function | Description | Business Rules & Invariants |
|---|---|---|---|
| **1** | **Add a Car** | Register a new vehicle | Validates plate format (`^[0-9]{2}[A-Z][0-9]-[0-9]{3}\.[0-9]{2}$`), unique plate check, owner name length (2-35 chars), valid registration date, value > 999, vehicle type ∈ {5, 7, 9}. |
| **2** | **Find a Car** | Lookup car by license plate | Case-insensitive lookup. Displays detailed car specs or `"Unregistered vehicle"` if not found. |
| **3** | **Update a Car** | Modify car details | Primary key (license plate) is immutable. Pressing `Enter` preserves existing values. |
| **4** | **Delete a Car** | Remove a car record | **Referential Integrity Guard**: Rejects deletion if car is associated with any insurance statement. Requires explicit `(Y/N)` confirmation. |
| **5** | **Add an Insurance** | Issue an insurance contract | Unique 4-digit ID (`^[0-9]{4}$`), car existence check, period ∈ {12, 24, 36 months}. Auto-computes fee: 12 mos = 25%, 24 mos = 40%, 36 mos = 45% of vehicle value. |
| **6** | **List Insurances by Year** | Query insurance statements | Filters by establishment year. Supports sorting by Insurance ID, Date, Plate, or Period in either `ASC` or `DESC` order. |
| **7** | **Report Uninsured Cars** | Identify cars without coverage | Uses $O(N + M)$ set lookup. Supports sorting by Plate, Owner, Registration Date, or Type in `ASC` or `DESC` order. |
| **8** | **Save Data** | Persist data to binary storage | Atomic two-phase commit to `carInfo.dat` and `insurances.dat` with automatic rollback on error. |
| **9** | **Load Data** | Load data from binary files | Validates both files concurrently before committing to in-memory state. |
| **10** | **Quit** | Graceful application exit | Confirms user intention and prompts to save if unsaved changes exist (`isChanged` flag). |

---

## 💡 Computational Thinking & OOP Principles

1. **Encapsulation**: All entity attributes are strictly `private`. Controllers return unmodifiable map views (`Collections.unmodifiableMap`) to avoid unauthorized state mutations from external layers.
2. **Key Immutability**: Primary key fields (`licensePlate`, `insuranceId`) cannot be altered during update operations.
3. **Polymorphism & Interface Inheritance**: View and Main components interact strictly through interface contracts (`ICarManager`, `IInsuranceManager`, `IMenu`). Manager interfaces extend the generic `ICrud<T, K>` interface.
4. **Referential Integrity**: Domain controllers use a functional predicate guard (`setDeletionGuard(plate -> !insuranceManager.isCarInsured(plate))`) to enforce data consistency before deleting entities.
5. **Atomic Persistence**:
   - Both datasets are written to separate temporary files (`.tmp`) first.
   - Verified backups (`.bak`) of existing files are created.
   - Files are committed in sequence. If any step fails, immediate rollback occurs, preventing split-brain or partial file corruption.

---

## 🛠️ Requirements & Setup

### Prerequisites
- **Java Development Kit (JDK)**: JDK 8 or higher (JDK 11, 17, or 21 compatible).
- **IDE** *(Optional)*: Apache NetBeans 12+ / NetBeans 8.2, IntelliJ IDEA, Eclipse, or VS Code.

### Running with NetBeans
1. Open NetBeans.
2. Select **File** > **Open Project...**
3. Navigate to and select this project directory.
4. Right-click the project > **Run** (or press `F6`).

### Running from Command Line (Terminal / Command Prompt)

1. **Compile all Java sources**:
   ```bash
   javac -encoding UTF-8 -d bin src/dto/*.java src/controller/*.java src/utils/*.java src/view/*.java
   ```

2. **Run the application**:
   ```bash
   java -cp bin view.Main
   ```

*(Or use Apache Ant if installed: `ant compile` followed by `ant run`)*

---

## 📄 Storage Files
- `carInfo.dat`: Binary serialized storage for car records.
- `insurances.dat`: Binary serialized storage for insurance statement records.
