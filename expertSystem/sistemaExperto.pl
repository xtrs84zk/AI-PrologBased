%% Muestra mas contenidos de las listas
:- set_prolog_flag(toplevel_print_options, [max_depth(100)]).

%% Permite predicados dinámicos en la base de conocimiento, asi las características
%% pueden ser actualizadas al tiempo de ejecucion
:- dynamic baseDeConocimiento/1.
:- dynamic kb/1.

%% Cargar la base de conocimiento y preguntas sobre la misma
:- consult('kb.pl').
:- consult('preguntas.pl').

%% Escribe una Fact en la base de conocimiento.
EscribirLineaALaBaseDeConocimiento(HiloDelArchivo, Fact) :-
  write(HiloDelArchivo, 'kb('),
  write(HiloDelArchivo, Fact),
  write(HiloDelArchivo, ').'),
  write(HiloDelArchivo, '\n').

%% Escribe todas las Facts en el archivo de la base de conocimiento.
EscribirALaBaseDeConocimiento :-
  open('kb.pl', write, HiloDelArchivo),
  forall(kb(Fact), EscribirLineaALaBaseDeConocimiento(HiloDelArchivo, Fact)),
  close(HiloDelArchivo).

%% Regresa todas las Facts de la base de conocimiento actual
listFacts(List) :-
  findall(X, kb(X), List).

%% Toma la lista de Facts y las realiza un assert para hacerlas activas en el programa actual
assertFacts([Fact]) :-
  assertz(Fact).
assertFacts([H|T]) :-
  assertz(H),
  assertFacts(T).

%% Carga todas las Facts y realiza un assert cuando el archivo es cargado
:- listFacts(List), assertFacts(List).

%% Aplica la conjuncion a cualquier predicado en la lista
%%
%% Ejemplo: `hasProperties([foo, bar, baz], X).` es equivalente a
%%          `foo(X), bar(X), baz(X).`
%%
%% Adaptado de:
%% http://stackoverflow.com/questions/10410082/list-of-predicates-in-prolog
hasProperties([], _).
hasProperties([P|Ps], X) :-
  call(P, X),
  hasProperties(Ps, X).

%% Recolecta todos los resultados del hazProperty en una lista
todosLosSImilares(List, Out) :-
  findall(X, hasProperties(List, X), Out).

%% Escribe todos las cadenas de la lista a la terminal en orden
mostrarLinea(LineaAMostrar) :-
  (
    LineaAMostrar = []    -> true;
    LineaAMostrar = nl    -> nl;
    LineaAMostrar = [A|B] -> mostrarLinea(A), mostrarLinea(B);
    otherwise    -> write(LineaAMostrar)
  ).

%% Despliega al usuario una linea para insersion. Normaliza la respuesta para ser en minuscula
%% de esta forma, no hay problema si escribe "si" o "SI" e, incluso "sI".
lectura(Entrada) :-
  write('> '),
  read_line_to_codes(user_input, Codes),
  atom_codes(RawEntrada, Codes),
  downcase_atom(RawEntrada, Entrada).

%% Concatena todas las cadenas en la lista en un sólo string de salida
concat(List, Salida) :-
  (
    List = []    -> Salida = '';
    List = [A|B] -> concat(B, Tail), string_concat(A, Tail, Salida)
  ).

%% En caso de encontrarse con un argumento sin predicción en la base de conocimiento actual,
%% actualiza la base de conocimiento para contener todas las Facts dadas en la lista
expandirBaseDeConocimiento([Pred], Actual) :-
  Pred =.. [Name, Val],
  NuevoPredicado =.. [Name, Val, Actual],
  assertz(kb(NuevoPredicado)).

expandirBaseDeConocimiento([H|T], Actual) :-
  expandirBaseDeConocimiento([H], Actual),
  expandirBaseDeConocimiento(T, Actual).

%% Intenta adivinar en lo que pensó el usuario
intentarAdivinar(RespuestaInferida, NumeroDePreguntas, ListaDeActivos) :-
  mostrarLinea([nl, '¿Estás pensando en  "', RespuestaInferida, '"?', nl]),
  lectura(Respuesta),
  (
    Respuesta = 'si' -> siPierdeElUsuario(NumeroDePreguntas, RespuestaInferida);
    Respuesta = 'sí' -> siPierdeElUsuario(NumeroDePreguntas, RespuestaInferida);
    Respuesta = 'no' -> siGanaElUsuario(ListaDeActivos)
  ).

%% Si un numero es menor a 1, la salida es reciproca a el, si no, da el mismo numero
invertirProporción(ProporcionDeEntrada, ProporcionDeSalida) :-
  (
    ProporcionDeEntrada < 1 -> ProporcionDeSalida is 1 / ProporcionDeEntrada;
    otherwise -> ProporcionDeSalida is ProporcionDeEntrada
  ).

%% Para una propiedad, calcula el radio entre el numero de entidades que  tienen la propiedad y el numero que no
calcularProporcion(Prop, Proporcion) :-
  PredicadosVerdaderos =.. [Prop, yes],
  PredicadosFalsos =.. [Prop, no],
  findall(X, call(PredicadosVerdaderos, X), Verdaderos),
  findall(Y, call(PredicadosFalsos, Y), Falsos),
  length(Verdaderos, NumPos),
  length(Falsos, NumNeg),
  ProporcionEnCrudo is NumPos / NumNeg,
  invertirProporción(ProporcionEnCrudo, Proporcion).

%% Toma la pregunta de la lista de preguntas que dividen las actuales entidades en dos sets de tamaño similar
seleccionarPregunta([[Pred, Pregunta]], MenorProporcion, MejorOpcion, NuevaList) :-
  MejorOpcion = [Pred, Pregunta],
  NuevaList = [],
  calcularProporcion(Pred, MenorProporcion).

seleccionarPregunta([[Pred, Pregunta]|Tail], MenorProporcion, MejorOpcion, List) :-
  calcularProporcion(Pred, Proporcion),
  seleccionarPregunta(Tail, NuevaMenorProporcion, NewMejorOpcion, NuevaList),
  (
    Proporcion < NuevaMenorProporcion ->
      MenorProporcion = Proporcion,
      List = Tail,
      MejorOpcion = [Pred, Pregunta];
    otherwise ->
      MenorProporcion = NuevaMenorProporcion,
      append(NuevaList, [[Pred, Pregunta]], List),
      MejorOpcion = NewMejorOpcion
  ).

seleccionarPregunta(List, MejorOpcion, OutList) :-
  seleccionarPregunta(List, _, MejorOpcion, OutList).

%% Utilidad interna predicate usada por 'acotarBaseDeConocimiento'
subAct(ListaDePreguntas, ListaDeActivos, NumeroDePreguntas) :-
  NuevoNumero is NumeroDePreguntas + 1,
  seleccionarPregunta(ListaDePreguntas, PredicadoDeLaPregunta, NuevaListaDePreguntas),
  cicloPrincipal(PredicadoDeLaPregunta, NuevaListaDePreguntas, ListaDeActivos, NuevoNumero).

%% Maneja el caso cuando no hay preguntas restantes
acotarBaseDeConocimiento(_, _, [], ListaDeActivos, _) :-
  mostrarLinea(['No se detectaron preguntas.', nl]),
  siGanaElUsuario(ListaDeActivos).

%% Actualiza la base de terminos actual, basado en la respuesta dada por el usuario
acotarBaseDeConocimiento(Respuesta, Pred, ListaDePreguntas, ListaDeActivos, NumeroDePreguntas) :-
  NuevoPredicado =.. [Pred, Respuesta],
  append(ListaDeActivos, [NuevoPredicado], NuevaListaDeActivos),
  todosLosSImilares(NuevaListaDeActivos, Resultados),
  length(Resultados, CantidadDeResultados),
  (
    CantidadDeResultados = 0 ->
      siGanaElUsuario(NuevaListaDeActivos);

    CantidadDeResultados = 1 ->
      [RespuestaInferida] = Resultados,
      intentarAdivinar(RespuestaInferida, NumeroDePreguntas, NuevaListaDeActivos);

    otherwise ->
      subAct(ListaDePreguntas, NuevaListaDeActivos, NumeroDePreguntas)
  ).

mostrarAyuda :-
  mostrarLinea([
    'Prolog n preguntas', nl, nl,
    'sí: Indica al juego que la respuesta es correcta.', nl,
    'no: Indica al juego que la respuesta es incorrecta.', nl,
    'quizá: Indica al juego que la respuesta es desconocida.', nl,
    'ayuda: Muestra este mensaje de ayuda.', nl,
    'salir: Sale del juego.', nl
  ]).

%% Llamado cuando el usuario sale del juego. Muestra un mensaje de despedida
alSalir :-
  mostrarLinea(['Hasta luego.', nl]),
  true.

%% Llamado cuando el usuario gana el juego. Pregunta al usuario por la cosa en la que estaba pensando
%%, para guardarlo en la base de conocimiento y usarla para mejorar la ejecucion futura.
siGanaElUsuario(ListaDeActivos) :-
  mostrarLinea([nl, '¡Vaya! Me doy, ¿Qué estabas pensando?', nl]),
  lectura(Actual),
  mostrarLinea([nl, 'Gracias, conoceré la respuesta para "', Actual, '" la próxima vez.', nl]),
  expandirBaseDeConocimiento(ListaDeActivos, Actual),
  EscribirALaBaseDeConocimiento,
  true.

%% Llamado cuando el sistema adivina correctamente
siPierdeElUsuario(NumeroDePreguntas, RespuestaInferida) :-
  mostrarLinea([nl, 'Averigüé que estás pensando en "', RespuestaInferida, '" con tan solo ',
    NumeroDePreguntas, ' preguntas.', nl, '¡Mejor suerte la próxima vez!']),
  true.

%% Llamado cuando el usuario da una respuesta invalida. Muestra el mensaje informandolo de ello
alRecibirUnaRespuestaInvalida(Response) :-
  mostrarLinea(['"', Response, '" no es una respuesta válida, por favor inserte "sí", "no" o "quizá".',
    nl, 'Para salir del juego, escriba "salir".', nl]).

%% Analiza la linea de entrada para realizar la accion apropiada
interpretar(Entrada, [Pred, Pregunta], ListaDePreguntas, ListaDeActivos, NumeroDePreguntas) :-
  (
    NumeroDePreguntas = 20 -> siGanaElUsuario(ListaDeActivos);

    Entrada = 'quit' -> alSalir;
    Entrada = 'stop' -> alSalir;
    Entrada = 'exit' -> alSalir;
    Entrada = 'salir' -> alSalir;

    Entrada = 'ayuda' ->
      mostrarAyuda,
      cicloPrincipal([Pred, Pregunta], ListaDePreguntas, ListaDeActivos, NumeroDePreguntas);

    Entrada = 'si' ->
      acotarBaseDeConocimiento(yes, Pred, ListaDePreguntas, ListaDeActivos, NumeroDePreguntas);

    %%Suelo usar tildes, esta línea es por si olvido no ponerla
    Entrada = 'sí' ->
      acotarBaseDeConocimiento(yes, Pred, ListaDePreguntas, ListaDeActivos, NumeroDePreguntas);

    Entrada = 'no' ->
      acotarBaseDeConocimiento(no, Pred, ListaDePreguntas, ListaDeActivos, NumeroDePreguntas);

    Entrada = 'quiza' ->
      subAct(ListaDePreguntas, ListaDeActivos, NumeroDePreguntas);

    %%Suelo usar tildes, esta línea es por si olvido no ponerla
    Entrada = 'quizá' ->
      subAct(ListaDePreguntas, ListaDeActivos, NumeroDePreguntas);

    otherwise ->
      alRecibirUnaRespuestaInvalida(Entrada),
      cicloPrincipal([Pred, Pregunta], ListaDePreguntas, ListaDeActivos, NumeroDePreguntas)
  ).

%% Itera entre las preguntas
cicloPrincipal([Pred, Pregunta], ListaDePreguntas, ListaDeActivos, NumeroDePreguntas) :-
  nl,
  mostrarLinea(['Pregunta ', NumeroDePreguntas, ': ', Pregunta, nl]),
  lectura(Entrada),
  interpretar(Entrada, [Pred, Pregunta], ListaDePreguntas, ListaDeActivos, NumeroDePreguntas).

%% Imprime el mensaje de bienvenida
mensajeDeBienvenida :-
  mostrarLinea(['Bienvenido!!!:D.', nl,
    'Intentaré adivinar que personaje de videojuegos estás pensando, pulsa enter para iniciar.', nl]).

%%comando para iniciar el sistema
iniciar :-
  mensajeDeBienvenida,
  read_line_to_codes(user_input, _),
  questions(ListaDePreguntas),
  seleccionarPregunta(ListaDePreguntas, PredicadoDeLaPregunta, NuevaListaDePreguntas),
  cicloPrincipal(PredicadoDeLaPregunta, NuevaListaDePreguntas, [], 1).
