# <h1 align="center">Tugas Besar 2 IF2211 Strategi Algoritma</h1>
<h2 align="center">Semester II tahun 2024/2025</h2>
<h3 align="center">Recipe Search in Little Alchemy 2 Using BFS and DFS</h3>

<p align="center">
  <img src="doc/main.png" alt="Main" width="700">
</p>

## Table of Contents
- [Description](#description)
- [DFS, BFS, & Bidirectional Search](#algorithms-implemented)
- [Program Structure](#program-structure)
- [Requirements & Installation](#requirements--installation)
- [Author](#author)
- [References](#references)

## Resources
- [Backend Repository](https://github.com/Ferdin-Arsenic/Tubes2_BE_Bolang)
- [Frontent Repository](https://github.com/azfaradhi/Tubes2_FE_Bolang)
- [Youtube Demo](https://www.youtube.com/watch?v=Iq1JeTXSvfU)

## Description
This program is a web application for a Little Alchemy 2 element recipe finder. The application utilizes graph traversal methods such as Breadth-First Search, Depth-First Search, and Bidirectional Search to look for elements required to create your targe element. The resulting recipe will be visualized, with a live update visualization available as an option. 

## Algorithms Implemented
### 1. BFS
Breadth-First Search is implemented using search queues to keep track of nodes to visit, once the queues contain only basic elements, the recipe tree is saved.

### 2. DFS
Depth-First Search is implemented using recursion calls, where each valid nodes are added on to the tree, and each recipe elements will then be processed through recursion.

### 3. Bidirectional
Bidirectional Search is done using BFS in two directions, forward search that starts with 4 basic elements, and backward search that starts at the target element. Once both directions meet, the nodes are combined to form the recipe tree

## Program Structure

## Usage
### - Dependencies -
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

###  - Installation -
#### Clone the Repository

```bash
git clone https://github.com/grwna/Tucil3_13523035_13523117
```
---

### 🚀 Running the Application
Before compiling and running the application. Make sure to change `JAVAFX` path in makefile to your JavaFX installation path.

Compile using `make build`

Run using `make run`

And use `make help` to see other available commands. 

## Author
| **NIM**  | **Nama Anggota**               | **Github** |
| -------- | ------------------------------ | ---------- |
| 13523035 | M. Rayhan Farrukh              | [grwna](https://github.com/grwna) |
| 13523117 | Ferdin Arsenarendra Purtadi    | [Ferdin-Arsenic](https://github.com/Ferdin-Arsenic) |

## References
- [Spesifikasi Tugas Besar 2 Stima 2024/2025](https://docs.google.com/document/d/1aQB5USxfUCBfHmYjKl2wV5WdMBzDEyojE5yxvBO3pvc/edit?tab=t.0)
- [Slide Kuliah IF2211 2024/2025 Algoritma BFS dan DFS (Bagian 1)](https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/13-BFS-DFS-(2025)-Bagian1.pdf)
- [Slide Kuliah IF2211 2024/2025 Algoritma BFS dan DFS (Bagian 2)](https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/14-BFS-DFS-(2025)-Bagian2.pdf)
- [Little Alchemy 2 Fandom](https://little-alchemy.fandom.com/wiki/Elements_(Little_Alchemy_2))
- [Golang Documentation](https://go.dev/doc/)