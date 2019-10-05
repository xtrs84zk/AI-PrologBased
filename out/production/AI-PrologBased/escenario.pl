quitar(X,Y):-
sobre(X,Y),
sobre(nada,X),
assert(sobre(X,piso),
retract(sobre(nada,Y),
true. 

quitar(X,Y):-
sobre(X,Y),
sobre(Z,X),
quitar(Z,X),
true. 

mover(X,Y):-
sobre(nada,X),
sobre(nada,Y),
retract(sobre(nada,Y),
assert(sobre(X,Y),
true. 

