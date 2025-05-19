# <h1 align="center">Tugas Besar 2 IF2211 Strategi Algoritma</h1>
<h2 align="center">Semester II tahun 2024/2025</h2>
<h3 align="center">Rush Hour Puzzle Solver with Pathfinding Algorithms</h3>

<p align="center">
  <img src="doc/gambar.png" alt="Main" width="400">
</p>

## Table of Contents
- [Description](#-description)
- [Features](#-features)
- [Algorithms](#-algorithms)
- [Heuristics](#-heuristics)
- [Program Structure](#program-structure)
- [Usage](#usage)
- [Author](#author)
- [References](#references)

## Resources
- [Backend Repository](https://github.com/Ferdin-Arsenic/Tubes2_BE_Bolang)
- [Frontent Repository](https://github.com/azfaradhi/Tubes2_FE_Bolang)
- [Youtube Demo](https://www.youtube.com/watch?v=Iq1JeTXSvfU)

## 📖 Description
This program is a Java application which is a solver for Rush Hour Puzzle. The application utilizes pathfinding algorithms such as A*, UCS, BFS and more, to look for valid solutions for a specific puzzle configuration. The resulting solution will be animated, with the ability to save the results into a text file. 

## ✨ Features
* asdasd

## 🧠 Algorithms
### 1. BFS
Breadth-First Search is implemented using search queues to keep track of nodes to visit, once the queues contain only basic elements, the recipe tree is saved.

### 2. DFS
Depth-First Search is implemented using recursion calls, where each valid nodes are added on to the tree, and each recipe elements will then be processed through recursion.

### 3. Bidirectional
Bidirectional Search is done using BFS in two directions, forward search that starts with 4 basic elements, and backward search that starts at the target element. Once both directions meet, the nodes are combined to form the recipe tree

## 🔢 Heuristics


## Program Structure
```
├── bin
├── doc
│   └── LaporanTucil3_035_117
├── LICENSE
├── makefile
├── pom.xml
├── README.md
├── src
│   ├── algorithm
│   │   ├── heuristic
│   │   │   ├── BlockingCarsHeuristic.java
│   │   │   ├── CombinedHeuristic.java
│   │   │   ├── Heuristic.java
│   │   │   └── ManhattanToExitHeuristic.java
│   │   └── pathfinding
│   │       ├── AStar.java
│   │       ├── GreedyBestFirst.java
│   │       ├── HillClimbing.java
│   │       ├── IDDFS.java
│   │       ├── Pathfinder.java
│   │       └── UCS.java
│   ├── Main.java
│   ├── model
│   │   ├── Board.java
│   │   ├── Piece.java
│   │   ├── Position.java
│   │   └── State.java
│   ├── parser
│   │   └── InputParser.java
│   ├── test
│   │   └── java
│   ├── ui
│   │   ├── GUIHelper.java
│   │   └── GUI.java
│   └── utils
│       ├── Direction.java
│       └── OutputWriter.java
└── test
```


## 🚀  Usage
### Dependencies
#### 1. Java Development Kit 
* **Version** JDK 17 or newer
* **Installation:** Get from [Oracle](https://www.oracle.com/id/java/technologies/downloads/).

#### 2. Apache Maven
* **Version:** Maven 3.6.x or newer.
* **Installation:** 
    * Option 1: Download from [official Maven website](https://maven.apache.org/download.cgi).
    * Option 2: Install with package manager (ex. `apt install maven`).

#### 3. JavaFX SDK
* **Version:** JavaFX SDK 21.0.7 .
* **Installation:**
    1.  Go to the [Gluon website (JavaFX SDK download page)](https://gluonhq.com/products/javafx/).
    2.  Download the JavaFX SDK (NOT the JMODS) for your specific operating system (Windows, macOS, Linux) and architecture.
    3.  Extract the downloaded ZIP/TAR.GZ file to a stable location on your computer (e.g., `C:\javafx-sdk-21.0.7` on Windows, or `/opt/javafx-sdk-21.0.7` on Linux/macOS).

---

###  Installation
#### Clone the Repository

```bash
git clone https://github.com/grwna/Tucil3_13523035_13523117
                        or 
git clone git@github.com:grwna/Tucil3_13523035_13523117.git
```
---

### Running the Application
**Before compiling and running the application. Make sure to change `JAVAFX` path in makefile to your JavaFX installation path.**

Compile using `make build`

Run using `make run`

And use `make help` to see other available commands. 

## Author
| **NIM**  | **Nama Anggota**               | **Github** |
| -------- | ------------------------------ | ---------- |
| 13523035 | M. Rayhan Farrukh              | [grwna](https://github.com/grwna) |
| 13523117 | Ferdin Arsenarendra Purtadi    | [Ferdin-Arsenic](https://github.com/Ferdin-Arsenic) |

## References
- [Spesifikasi Tugas Kecil 3 Stima 2024/2025](https://docs.google.com/document/d/1NXyjtIHs2_tWDD37MYtc0VhWtoU2wIH8A95ImttmMXk/edit?tab=t.0)
- [Slide Kuliah IF2211 2024/2025 Penentuan Rute (Bagian 1)](https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/21-Route-Planning-(2025)-Bagian1.pdf)
- [Slide Kuliah IF2211 2024/2025 Penentuan Rute (Bagian 2)](https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/22-Route-Planning-(2025)-Bagian2.pdf)