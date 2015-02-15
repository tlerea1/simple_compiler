CFLAGS=-std=c99 -Wextra -pedantic

all: cat
	javac cat.java

clean:
	rm cat
	rm cat.class
	rm cs328-assignment-1-tlerea1.tar.gz

tar:
	tar -cvf cs328-assignment-1-tlerea1.tar cat.c cat.py cat.java Makefile README
	gzip cs328-assignment-1-tlerea1.tar
