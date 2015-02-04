#include <stdlib.h>
#include <stdio.h>

int main(void) {
	int buf_size = 4096;
	int count = 0;
	char buf[buf_size];
	while ((count = read(stdin, buf, buf_size)) > 0) {
		write(stdout, buf, count);
	}
}
