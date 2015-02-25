/* NOTE: This program shouldn't fully compile, just parse correctly. */
PROGRAM ExhaustiveCorrectTest;
    CONST TYPE VAR a : b;
    VAR c : d; e, f, g, h : i;
    VAR a,b,c : ARRAY (1 + 2*3*(x[1,2].asdf[2*(1+3)])) OF ARRAY
        12*3+2 MOD 34*x[2].f OF RECORD
            one, two: a; three: ARRAY 2 OF RECORD
                blarg : foo;
            END;
        END;
    CONST CurrentDoctor = 12; TomBaker = 4; MattSmith = 11;
        a = -65;
        b = 12 * 12 + 8 - 72 - (-11);
    TYPE
        foo = baz;
        baz = foo;
        bar = ARRAY 12 OF foo;
BEGIN
    READ HelloWorld;
    WRITE +2 + 11 * 98 MOD 87 DIV 41 + (x[1*3+5+y.z[2]].f);
    IF x = 1 THEN
        WRITE 1;
        WRITE 2
    ELSE IF x # 2 THEN
        WRITE 5;
        WRITE 67
    ELSE IF x < 87 THEN
        WRITE 9
    ELSE IF x > 9 THEN
        WRITE 21
    ELSE IF x <= 82 THEN
        WRITE 98
    ELSE IF x >= 23 THEN
        WRITE 72
    ELSE
        WRITE 0 DIV 0
    END END END END END END;
    REPEAT
        WRITE 93
    UNTIL 1 = 1 END;
    WHILE 1 = 1 DO
        WRITE 97
    END;
    IF 1 = 1 THEN
        WRITE 5
    END;
    x := 7
END AnotherIdentifier.
