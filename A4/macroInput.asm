MACRO
M1      &X,     &Y,     &A
MOVER   &A,     &X
ADD     &A,     ='1'
MOVER   &A,     &Y
ADD     &A,     ='5'
MEND
START
M1      10,     20,     CREG
MOVER   AREG,   50
MACRO
M2      &P,     &Q,     &U,    &V
MOVER   &U,     &P
MOVER   &V,     &Q
ADD     &U,     ='15'
ADD     &V,     ='10'
MEND
M2      30,     40,     AREG,  DREG
STOP
END
