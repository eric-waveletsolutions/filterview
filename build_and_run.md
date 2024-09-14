# Build and Run Instructions for FilterView Application

This guide explains how to build and run the FilterView with Adjustable Raised Cosine Filter application on different platforms: **Arch Linux**, **Ubuntu**, and **Windows**.

## Prerequisites

Before proceeding, ensure you have the following installed:
- **Java Development Kit (JDK)** 17 or higher
- **Git** (for cloning the repository)

> **Note**: You do not need to install Maven manually. The project includes the Maven Wrapper, which will handle everything.

---

## Arch Linux

### 1. Install Dependencies
You can install the necessary dependencies using `pacman`:
```bash
sudo pacman -S jdk17-openjdk maven git
```

## 2. Clone the Repository and Build
```bash
git clone https://github.com/eric-waveletsolutions/filterview.git
cd filterview
./mvnw clean install
```

## 3. Run the Application
```bash
./mvnw javafx:run
```

## 4. Generate Javadocs
```bash
./mvnw clean install -Pfull-build
```

## 5. View Javadocs
Once generated, the Javadocs can be found in the `target/apidocs/` directory. Open the `index.html` file in a browser:
```bash
firefox target/apidocs/index.html
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
git clone https://github.com/eric-waveletsolutions/filterview.git
cd filterview
./mvnw clean install
```

### 3. Run the Application
```bash
./mvnw javafx:run
```

## 4. Generate Javadocs
```bash
./mvnw clean install -Pfull-build
```

## 5. View Javadocs
Once generated, the Javadocs can be found in the `target/apidocs/` directory. Open the `index.html` file in a browser:
```bash
firefox target/apidocs/index.html
```

## Windows
### 1. Install Dependencies

1. Install Java 17: Download and install the JDK from the Oracle website or using AdoptOpenJDK.

2. Install Maven: Download Maven from the Apache Maven website, extract it, and set it up in your system's PATH.

3. Install Git: Download and install Git from the Git website.

### 2. Clone the Repository and Build

Open the terminal (or Git Bash) and run:
```bash
git clone https://github.com/eric-waveletsolutions/filterview.git
cd filterview
mvnw.cmd clean install
```

### 3. Run the Application
```bash
mvnw.cmd javafx:run
```

## 4. Generate Javadocs
```bash
mvnw.cmd clean install -Pfull-build
```

## 5. View Javadocs
Once generated, the Javadocs can be found in the `target/apidocs/` directory. Open the `index.html` file in your browser by navigating to the directory and opening the file.
