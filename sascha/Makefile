build-data:
	javac StreamData.java
	java StreamData

build-main:
	javac Main.java

play: build-data build-main
	java StreamData | java Player

play-without-data-build: build-main
	java StreamData | java Player
