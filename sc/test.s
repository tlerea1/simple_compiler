/* This program tests basic type declarations. */

PROGRAM X;
  TYPE x = ARRAY 55 OF INTEGER;
  TYPE y = x;
  TYPE z = RECORD
    a: INTEGER;
    b: y;
  END;
	CONST a = 6;
	VAR first : INTEGER;
	VAR second : INTEGER;
	VAR third : x;
	VAR forth : x;
	BEGIN
		third := forth;
		third := forth;
		a := forth;
		first := forth;
		first := 6 + 5;
		IF first - 1 > a + 2 THEN
			WRITE 52
		ELSE
			WRITE 76 DIV 3
		END;
		WHILE first <= 6 DO
			first := first + 1
		END
END X.
