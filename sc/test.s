PROGRAM X;
VAR a,b,d:INTEGER;
VAR c : BOOLEAN;
BEGIN
a := 5;
b := 4;
c := TRUE;

IF a < b THEN
    WRITE a
ELSEIF b < a THEN
    WRITE b
ELSE
    WRITE 15
END;
IF c THEN
    WRITE 10
END;
IF (NOT c) AND (c OR c) THEN
    WRITE 7
END;
c := (c = FALSE) AND (b MOD d = 7);
WRITE (c)
END X.