#include <stdlib.h>
#include <stdio.h>

int main(void) {
	int buf_size = 4096;
	char *buf;
	size_t count;
	while ((count = read(stdin, buf, buf_size)) != 0) {
		fwrite(buf, 1, count, stdout);
	}
}
