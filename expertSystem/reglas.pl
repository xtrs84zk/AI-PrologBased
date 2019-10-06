assert(sobre(nada,Y)),
true. 

quitar(X,Y):-
sobre(X,Y),
sobre(Z,X),
quitar(Z,X),
quitar(X,Y),
true. 

mover(X,Y):-
sobre(nada,X),
sobre(nada,Y),
retract(sobre(X,piso)),
assert(sobre(X,Y)),
true. 

abrirCaja(x):-
estado(X,closed),
sobre(nada,X),
retract(estado(X,closed)), 
assert(estado(X,open)),
true. 

true. 

