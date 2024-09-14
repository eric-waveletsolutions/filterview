# Build and Run Instructions for Speed Graph Application

This guide explains how to build and run the Speed Graph with Adjustable Raised Cosine Filter application on different platforms: **Arch Linux**, **Ubuntu**, and **Windows**.

## Prerequisites

Before proceeding, ensure you have the following installed:
- **Java Development Kit (JDK)** 17 or higher
- **Maven** (for handling dependencies and building the project)
- **Git** (for cloning the repository)

---

## Arch Linux

### 1. Install Dependencies
You can install the necessary dependencies using `pacman`:
```bash
sudo pacman -S jdk17-openjdk maven git
```

## 2. Clone the Repository and Build
```bash
git clone https://github.com/eric-waveletsolutions/speedgraph.git
cd speedgraph
mvn clean install
```

## 3. Run the Application
```bash
mvn javafx:run

```

## Ubuntu
### 1. Install Dependencies

You can install the required packages using apt:

```bash
sudo apt update
sudo apt install openjdk-17-jdk maven git
```

### 2. Clone the Repository and Build
```bash
git clone https://github.com/eric-waveletsolutions/speedgraph.git
cd speedgraph
mvn clean install
```

### 3. Run the Application
```bash
mvn javafx:run
```

## Windows
### 1. Install Dependencies

1. Install Java 17: Download and install the JDK from the Oracle website or using AdoptOpenJDK.

2. Install Maven: Download Maven from the Apache Maven website, extract it, and set it up in your system's PATH.

3. Install Git: Download and install Git from the Git website.

### 2. Clone the Repository and Build

Open the terminal (or Git Bash) and run:
```bash
git clone https://github.com/eric-waveletsolutions/speedgraph.git
cd speedgraph
mvn clean install
```

### 3. Run the Application
```bash
mvn javafx:run
```
