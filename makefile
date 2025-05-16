SRC_DIR = src
BIN_DIR = bin
JAVAFX = ../javafx-sdk-21.0.7/lib/
MODULES = javafx.controls,javafx.fxml,javafx.graphics

all: build run

build:
	$(shell mkdir -p $(BIN_DIR))
	javac --module-path $(JAVAFX) --add-modules $(MODULES) -d bin src/*.java
run:
	java -cp $(BIN_DIR) Main

clean:
	@mvn clean

help:
	@echo   Available commands:
	@echo   all          - Compile and run the project
	@echo   build        - Compile the project 
	@echo   run          - Run the application
	@echo   clean        - Remove compiled files
	@echo   help         - Display this help