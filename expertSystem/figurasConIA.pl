%%author Javier Sanchez

%%Predicados dinámicos
:- dynamic escenario/1.
:- dynamic reglas/2.
:- dynamic sobre/2.
:- dynamic tipo/2.
:- dynamic estado/2.
:- dynamic color/2.
:- dynamic tipo_de_objeto/2.
:- dynamic lectura/0.
:- dynamic writeln/1.
%%cargando escenario y reglas
:- include('scenario').
:- include('reglas').

%%Procesando la entrada a la línea de comandos
lectura:- write('> '),
          readln(LINEA),
          procesar(LINEA).
		  
%%guess i'll remove modules
escribirLinea(Line):- (
      Line = []    -> true;
      Line = nl    -> nl;
      Line = [A|B] -> escribirLinea(A), escribirLinea(B);
      otherwise    -> write(Line)
    ).

%%las figuras se crearán en los métodos siguientes
%%la información suficiente se está otorgando
crear(TipoDeFigura,Size,FiguraNueva,Como,RespectoA):- not(sobre(FiguraNueva,_)),
									not(dentro(FiguraNueva,_)),
									member(Como,[sobre,encima,arriba]),
									member(Size,[chico,mediano,grande,chica,mediana]),
									(
									TipoDeFigura = cubo -> crearCubo(FiguraNueva,RespectoA,Size);
									otherwise -> escribirLinea('Tipo no permitido.')
									).
													 
									
%%no se está otorgando suficiente información, se procede a inferir
%%por motivos personales, será un cubo
crear(FiguraNueva):- crearCubo(FiguraNueva).									
									
%%los cubos se crearán en varios métodos debajo, redirijir aquí
%%Cubo que va sobre el piso -posibilidad 1 : por defecto
crearCubo(NuevoCubo):- not(sobre(CuboDebajo,_)),
					   %%wait
					   escribirLinea([nl,'Se creó el cubo ',NuevoCubo,' sobre el piso.'],nl).
					   
%%Cubo que va sobre el piso -posibilidad 2 : se especificó crearle sobre el piso
crearCubo(NuevoCubo,CuboDebajo,Size):- member(CuboDebajo,[piso,suelo,firme]).
								  
crearCubo(NuevoCubo,CuboDebajo,Size):- sobre(CuboDebajo,_),
								  sobre(nada,CuboDebajo),
								  assert(tipo_de_objeto(NuevoCubo,cubo)),
								  assert(shapeSize(NuevoCubo,Size)),
								  assert(sobre(NuevoCubo,CuboDebajo)),
								  asser(sobre(nada,NuevoCubo)),
								  escribirLinea([nl,'Se creó el cubo ',NuevoCubo,' sobre el cubo ',CuboDebajo,'.']).
								  
%%Procesamiento de lenguaje natural, prolog; 
%%La idea de este código es ser lo más modular posible
%Procesando órdenes que involucren crear elementos.

%%Crea el cubo c sobre el piso
					
procesar([V,ART1,ADJ,SUJETO,PREPOSICION,ART2,OBJETO]):- member(V,[crea,crear,define,coloca]),
                                                        member(ART1,[el,la]),
                                                        member(ADJ,[cubo,piramide,cilindro,caja,esfera]),
                                                        member(PREPOSICION,[sobre,encima,arriba]),
                                                        member(ART2,[el,la]),
                                                        not(sobre(SUJETO,_)),
                                                        assert(tipo_de_objeto(SUJETO,ADJ)),
                                                        assert(sobre(SUJETO,OBJETO)),
                                                        assert(sobre(nada,SUJETO)),
                                                        write('Se creó el '),
                                                        write(ADJ),
                                                        write(' '),
                                                        write(SUJETO),
                                                        write(' sobre '),
                                                        writeln(OBJETO).
														
%%Existe un cubo b sobre a
procesar([V,ART1,ADJ,SUJETO,PREPOSICION,OBJETO]):-member(V,[existe,hay,veo]),
													   member(ART1,[un,una]),
													   member(ADJ,[cubo,piramide,cilindro,caja,esfera]),
													   member(PREPOSICION,[sobre]),
													   not(sobre(SUJETO,_)),
													   sobre(nada,a).
													   %%crear el cubo b sobre a

%crea el cubo a sobre el cubo b

%%Existe un cubo llamado c
procesar([V,ART1,ADJ,LLAMADO,SUJETO]):- member(V,[existe,hay,veo]),
										member(ART1,[un,una]),
										member(ADJ,[cubo,piramide,cilindro,caja,esfera]),
										not(sobre(SUJETO,_)),
										crearCubo(SUJETO).
										
procesar([V,ART1,ADJ,SUJETO,PREPOSICION,ART2,ADJ2,OBJETO]):- member(V,[crea,crear,define,coloca]),
                                                        member(ART1,[el,la]),
                                                        member(ADJ,[cubo,piramide,cilindro,caja,esfera]),
                                                        member(PREPOSICION,[sobre,encima,arriba]),
                                                        member(ART2,[el,la]),
                                                        not(sobre(SUJETO,_)),
                                                        retract(sobre(nada,OBJETO)), %se elimina la cl·usula en que hay nada sobre el objeto
                                                        assert(tipo_de_objeto(SUJETO,ADJ)),
                                                        assert(sobre(SUJETO,OBJETO)),
                                                        assert(sobre(nada,SUJETO)),
                                                        write('Se creó el '),
                                                        write(ADJ),
                                                        write(' '),
                                                        write(SUJETO),
                                                        write(' sobre '),
                                                        writeln(OBJETO).

%crea el cubo a sobre b
procesar([V,ART1,ADJ,SUJETO,PREPOSICION,OBJETO]):- member(V,[crea,crear,define,coloca]),
                                                        member(ART1,[el,la]),
                                                        member(ADJ,[cubo,piramide,cilindro,caja,esfera]),
                                                        member(PREPOSICION,[sobre,encima,arriba]),
                                                        not(sobre(SUJETO,_)),
                                                        retract(sobre(nada,OBJETO)), %se elimina la cl·usula en que hay nada sobre el objeto
                                                        assert(tipo_de_objeto(SUJETO,ADJ)),
                                                        assert(sobre(SUJETO,OBJETO)),
                                                        assert(sobre(nada,SUJETO)),
                                                        write('Se creó el '),
                                                        write(ADJ),
                                                        write(' '),
                                                        write(SUJETO),
                                                        write(' sobre '),
                                                        writeln(OBJETO).

%Cuando no se proporciona un par·metro sobre quÈ figura es, se asume un cubo.
%crea a sobre el piso
procesar([V,SUJETO,PREPOSICION,ART,OBJETO]):- member(V,[crea,crear,define,coloca]),
                                              not(sobre(SUJETO,_)),
                                              member(PREPOSICION,[sobre,encima,arriba]),
                                              member(ART,[el,la]),
                                              assert(tipo_de_objeto(SUJETO,cubo)),
                                              assert(sobre(SUJETO,OBJETO)),
                                              assert(sobre(nada,SUJETO)),
                                              write('Se creó el cubo '),
                                              write(SUJETO),
                                              write(' sobre '),
                                              writeln(OBJETO).
 %crea b sobre a
 procesar([V,SUJETO,PREPOSICION,OBJETO]):- member(V,[crear,crea,define,coloca]),
                                              not(sobre(SUJETO,_)), %se valida que no exista b
                                              sobre(nada,OBJETO), %se valida que estÈ a y que estÈ libre
                                              member(PREPOSICION,[sobre,encima,arriba]),
                                              assert(tipo_de_objeto(SUJETO,cubo)), %por defecto, sujeto ser· un cubo
                                              assert(sobre(SUJETO,OBJETO)), %se crea el sujeto sobre el objeto
                                              assert(sobre(nada,SUJETO)),
                                              retract(sobre(nada,OBJETO)), %se elimina la cl·usula en que hay nada sobre el objeto
                                              write('Se creó el cubo '),
                                              write(SUJETO),
                                              write(' sobre '),
                                              writeln(OBJETO).
                                              
 %Procesando órdenes que involucren quitar elementos.
 %retirar a de b
 procesar([V,SUJETO,PREPOSICION,OBJETO]):- member(V,[quitar,quita,remover,retirar,retira]),
                                              member(PREPOSICION,[sobre,encima,arriba,de]),
                                              quitar(SUJETO,OBJETO).
                                              

                                              
 %quitar el cubo a sobre b ; retira el cubo a de b
 procesar([V,ART1,ADJ,SUJETO,PREPOSICION,OBJETO]):- member(V,[quitar,quita,remover,retirar]),
                                              member(PREPOSICION,[sobre,encima,arriba,de]),
                                              quitar(SUJETO,OBJETO).
                                              
 %coloca a sobre b
 procesar([V,SUJETO,PREPOSICION,OBJETO]):- member(V,[coloca,colocar,pon]),
                                           member(PREPOSICION,[sobre,encima,arriba,de]),
                                           colocar(SUJETO,OBJETO).

%coloca el cubo a sobre el cubo b
procesar([V,ART1,ADJ,SUJETO,PREPOSICION,ART2,ADJ2,OBJETO]):- member(V,[coloca,colocar,pon]),
                                                        member(ART1,[el,la]),
                                                        member(ADJ,[cubo,piramide,cilindro,caja,esfera]),
                                                        member(PREPOSICION,[sobre,encima,arriba]),
                                                        member(ART2,[el,la]),
                                                        colocar(SUJETO,OBJETO).



%tumbar todos al piso
procesar([V,CANT,PREPOSICION,OBJETO]):- member(V,[tumbar,tumba,tira]),
                                        member(CANT,[todos,todo]),
                                        member(PREPOSICION,[al,en,a]),
                                        member(OBJETO,[piso]),
                                        tumbar_todos_al_piso.
                                        
%colocar todos sobre a
procesar([V,CANT,PREPOSICION,X]):- member(V,[coloca,colocar,pon]),
                                        member(CANT,[todos,todo]),
                                        member(PREPOSICION,[sobre, en]),
                                        apilar(X).
										
%%si no coincide con alguna de las anteriores, se asume que es una lista de predicados.
%%esto es meramente experimental, proceder con precaución										
procesar([X|R]):- procesar(X),procesar(R).
										
procesar(X):- escribirLinea([nl,'por alguna razón, falló al procesar, la línea era: "',X,'"']).
										
%%qué ocurre si el campo está vacío
procesar([]):- writeln('El campo está vacío.'), true.
