/* This program tests basic type declarations. */

PROGRAM X;
  TYPE x = ARRAY 5 OF INTEGER;
  TYPE y = x;
  TYPE z = RECORD
    a: INTEGER;
    b: y;
  END;
END X.
