%%author Javier Sanchez

%%Predicados dinámicos
:- dynamic escenario/1.
:- dynamic reglas/2.
:- dynamic sobre/2.
:- dynamic estado/2.
:- dynamic tipo_de_objeto/2.
:- dynamic lectura/0.

%%Accediendo al complemento para procesar el lenguaje natural.
consult('naturalLanguajeProcessing.pl').

%%Cargando las reglas a la base de conocimiento, i guess
%%consult('rules.pl').

%%Procesando el lenguaje natural de las reglas
%%idk, procesar, i guess (?)

%%Procesando la entrada a la línea de comandos
lectura:- write('>'),
          readln(LINEA),
          writeln(LINEA).
          procesar(LINEA).
		  
%%Holy smokes, what else shall I do
