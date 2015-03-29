PROGRAM X;
CONST a=6;
VAR b : INTEGER;
TYPE ra=ARRAY 5 OF INTEGER;
TYPE rec=RECORD
    a : INTEGER;
    b : INTEGER;
    END;
VAR c,d : ra;
VAR e,f : rec;
BEGIN
b := a + 14;
c[2] := b;
WRITE d[2];
d := c;
WRITE b;
WRITE c[2];
WRITE d[2];
e.a := 15;
e.b := c[2];
f := e;
e.a := 15 DIV (b - 20);
WRITE f.a;
f.a := 105;
WRITE e.a;
c[4] := 15
END X.

