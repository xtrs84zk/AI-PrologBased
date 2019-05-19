%% Muestra mas contenidos de las listas
:- set_prolog_flag(toplevel_print_options, [max_depth(100)]).

%% Permite predicados dinámicos en la base de conocimiento, asi las características
%% pueden ser actualizadas al tiempo de ejecucion
:- dynamic baseDeConocimiento/1.
:- dynamic kb/1.
:- dynamic start/0.
:- dynamic questions/1.


%% Cargar la base de conocimiento y preguntas sobre la misma
:- consult('kb.pl').
:- consult('preguntas.pl').

%% Escribe una Fact en la base de conocimiento.
writeKBLine(Stream, Fact) :-
  write(Stream, 'kb('),
  write(Stream, Fact),
  write(Stream, ').'),
  write(Stream, '\n').

%% Escribe todas las Facts en el archivo de la base de conocimiento.
writeKB :-
  open('kb.pl', write, Stream),
  forall(kb(Fact), writeKBLine(Stream, Fact)),
  close(Stream).

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
allMatches(List, Out) :-
  findall(X, hasProperties(List, X), Out).

%% Escribe todos las cadenas de la lista a la terminal en orden
writeLine(Line) :-
  (
    Line = []    -> true;
    Line = nl    -> nl;
    Line = [A|B] -> writeLine(A), writeLine(B);
    otherwise    -> write(Line)
  ).

%% Despliega al usuario una linea para insersion. Normaliza la respuesta para ser en minuscula
%% de esta forma, no hay problema si escribe "Yes" o "yes" o, incluso "yEs".
listen(Input) :-
  write('> '),
  read_line_to_codes(user_input, Codes),
  atom_codes(RawInput, Codes),
  downcase_atom(RawInput, Input).

%% Concatena todas las cadenas en la lista en un sólo string de salida
concat(List, Output) :-
  (
    List = []    -> Output = '';
    List = [A|B] -> concat(B, Tail), string_concat(A, Tail, Output)
  ).

%% En caso de encontrarse con un argumento sin predicción en la base de conocimiento actual,
%% actualiza la base de conocimiento para contener todas las Facts dadas en la lista
saveFacts([Pred], Actual) :-
  Pred =.. [Name, Val],
  NewPred =.. [Name, Val, Actual],
  assertz(kb(NewPred)).

saveFacts([H|T], Actual) :-
  saveFacts([H], Actual),
  saveFacts(T, Actual).

%% Intenta adivinar en lo que pensó el usuario
makeGuess(Guess, QuestionNum, ActiveList) :-
  writeLine([nl, '¿Estás pensando en  "', Guess, '"?', nl]),
  listen(Answer),
  (
    Answer = 'si' -> onLose(QuestionNum, Guess);
    Answer = 'sí' -> onLose(QuestionNum, Guess);
    Answer = 'no' -> onWin(ActiveList)
  ).

%% Si un numero es menor a 1, la salida es reciproca a el, si no, da el mismo numero
flipRatio(InRatio, OutRatio) :-
  (
    InRatio < 1 -> OutRatio is 1 / InRatio;
    otherwise -> OutRatio is InRatio
  ).

%% Para una propiedad, calcula el radio entre el numero de entidades que  tienen la propiedad y el numero que no
calcRatio(Prop, Ratio) :-
  YesPred =.. [Prop, yes],
  NoPred =.. [Prop, no],
  findall(X, call(YesPred, X), Positives),
  findall(Y, call(NoPred, Y), Negatives),
  length(Positives, NumPos),
  length(Negatives, NumNeg),
  RawRatio is NumPos / NumNeg,
  flipRatio(RawRatio, Ratio).

%% Toma la pregunta de la lista de preguntas que dividen las actuales entidades en dos sets de tamaño similar
pickQuestion([[Pred, Question]], LowestRatio, Best, NewList) :-
  Best = [Pred, Question],
  NewList = [],
  calcRatio(Pred, LowestRatio).

pickQuestion([[Pred, Question]|Tail], LowestRatio, Best, List) :-
  calcRatio(Pred, Ratio),
  pickQuestion(Tail, NewLowestRatio, NewBest, NewList),
  (
    Ratio < NewLowestRatio ->
      LowestRatio = Ratio,
      List = Tail,
      Best = [Pred, Question];
    otherwise ->
      LowestRatio = NewLowestRatio,
      append(NewList, [[Pred, Question]], List),
      Best = NewBest
  ).

pickQuestion(List, Best, OutList) :-
  pickQuestion(List, _, Best, OutList).

%% Utilidad interna predicate usada por 'update'
update1(QuestionList, ActiveList, QuestionNum) :-
  NewNum is QuestionNum + 1,
  pickQuestion(QuestionList, PredQuestion, NewQuestionList),
  botLoop(PredQuestion, NewQuestionList, ActiveList, NewNum).

%% Maneja el caso cuando no hay preguntas restantes
update(_, _, [], ActiveList, _) :-
  writeLine(['No se detectaron preguntas.', nl]),
  onWin(ActiveList).

%% Actualiza la base de terminos actual, basado en la respuesta dada por el usuario
update(Answer, Pred, QuestionList, ActiveList, QuestionNum) :-
  NewPred =.. [Pred, Answer],
  append(ActiveList, [NewPred], NewActiveList),
  allMatches(NewActiveList, Results),
  length(Results, NumResults),
  (
    NumResults = 0 ->
      onWin(NewActiveList);

    NumResults = 1 ->
      [Guess] = Results,
      makeGuess(Guess, QuestionNum, NewActiveList);

    otherwise ->
      update1(QuestionList, NewActiveList, QuestionNum)
  ).

showHelp :-
  writeLine([
    'Prolog n preguntas', nl, nl,
    'sí: Indica al juego que la respuesta es correcta.', nl,
    'no: Indica al juego que la respuesta es incorrecta.', nl,
    'quizá: Indica al juego que la respuesta es desconocida.', nl,
    'ayuda: Muestra este mensaje de ayuda.', nl,
    'salir: Sale del juego.', nl
  ]).

%% Llamado cuando el usuario sale del juego. Muestra un mensaje de despedida
onQuit :-
  writeLine(['Hasta luego.', nl]),
  true.

%% Llamado cuando el usuario gana el juego. Pregunta al usuario por la cosa en la que estaba pensando
%%, para guardarlo en la base de conocimiento y usarla para mejorar la ejecucion futura.
onWin(ActiveList) :-
  writeLine([nl, '¡Vaya! Me doy, ¿Qué estabas pensando?', nl]),
  listen(Actual),
  writeLine([nl, 'Gracias, conoceré la respuesta para "', Actual, '" la próxima vez.', nl]),
  saveFacts(ActiveList, Actual),
  writeKB,
  true.

%% Llamado cuando el sistema adivina correctamente
onLose(QuestionNum, Guess) :-
  writeLine([nl, 'Averigüé que estás pensando en "', Guess, '" con tan solo ',
    QuestionNum, ' preguntas.', nl, '¡Mejor suerte la próxima vez!']),
  true.

%% Llamado cuando el usuario da una respuesta invalida. Muestra el mensaje informandolo de ello
onInvalidResponse(Response) :-
  writeLine(['"', Response, '" no es una respuesta válida, por favor inserte "sí", "no" o "quizá".',
    nl, 'Para salir del juego, escriba "salir".', nl]).

%% Analiza la linea de entrada para realizar la accion apropiada
parse(Input, [Pred, Question], QuestionList, ActiveList, QuestionNum) :-
  (
    QuestionNum = 20 -> onWin(ActiveList);

    Input = 'quit' -> onQuit;
    Input = 'stop' -> onQuit;
    Input = 'exit' -> onQuit;
    Input = 'salir' -> onQuit;

    Input = 'ayuda' ->
      showHelp,
      botLoop([Pred, Question], QuestionList, ActiveList, QuestionNum);

    Input = 'si' ->
      update(yes, Pred, QuestionList, ActiveList, QuestionNum);

    %%Suelo usar tildes, esta línea es por si olvido no ponerla
    Input = 'sí' ->
      update(yes, Pred, QuestionList, ActiveList, QuestionNum);

    Input = 'no' ->
      update(no, Pred, QuestionList, ActiveList, QuestionNum);

    Input = 'quiza' ->
      update1(QuestionList, ActiveList, QuestionNum);

    %%Suelo usar tildes, esta línea es por si olvido no ponerla
    Input = 'quizá' ->
      update1(QuestionList, ActiveList, QuestionNum);

    otherwise ->
      onInvalidResponse(Input),
      botLoop([Pred, Question], QuestionList, ActiveList, QuestionNum)
  ).

%% Loop principal
botLoop([Pred, Question], QuestionList, ActiveList, QuestionNum) :-
  nl,
  writeLine(['Pregunta ', QuestionNum, ': ', Question, nl]),
  listen(Input),
  parse(Input, [Pred, Question], QuestionList, ActiveList, QuestionNum).

%% Imprime el mensaje de bienvenida
welcomeMessage :-
  writeLine(['Hola, soy un sistema experto.', nl,
    'Piensa en algo para que adivine, luego presiona enter.', nl]).

%%comando para iniciar el sistema
start :-
  welcomeMessage,
  read_line_to_codes(user_input, _),
  questions(QuestionList),
  pickQuestion(QuestionList, PredQuestion, NewQuestionList),
  botLoop(PredQuestion, NewQuestionList, [], 1).