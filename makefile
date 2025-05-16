SRC_DIR = src
BIN_DIR = bin

all: build run

build:
	$(shell mkdir -p $(BIN_DIR))
	javac -d $(BIN_DIR) $(SRC_DIR)/*.java
run:
	java -cp $(BIN_DIR) Main

clean:
	rm -rf $(BIN_DIR)