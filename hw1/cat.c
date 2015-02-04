#include <stdlib.h>
#include <stdio.h>

int main(void) {
	int buf_size = 4096;
	char *buf;
	size_t count;
	while ((buf = fgetln(stdin, &count)) != NULL) {
		fwrite(buf, 1, count, stdout);
	}
}
