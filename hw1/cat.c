#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>

int main(void) {
	int buf_size = 4096;
	char buf[buf_size];
	size_t count;
	while ((count = read(0, buf, buf_size)) > 0) {
		write(1, buf, count);
	}
}
