SRC_DIR = src
BIN_DIR = bin
JAVAFX = ../javafx-sdk-21.0.7/lib/
MODULES = javafx.controls,javafx.fxml,javafx.graphics

all: build run

build:
	@mvn compile
run:
	@java --module-path $(JAVAFX) --add-modules $(MODULES) -cp $(BIN_DIR)/classes Main

run-ui:
	@java --module-path $(JAVAFX) --add-modules $(MODULES) -cp $(BIN_DIR)/classes Main --gui

clean:
	@mvn clean

help:
	@echo   Available commands:
	@echo   all          - Compile and run the project
	@echo   build        - Compile the project 
	@echo   run          - Run the application
	@echo   clean        - Remove compiled files
	@echo   help         - Display this help