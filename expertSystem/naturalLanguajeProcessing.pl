%%Procesamiento de lenguaje natural, prolog; 
%%La idea de este código es ser lo más modular posible
%%
%Procesando Ûrdenes que involucren crear elementos.
%Crea el cubo c sobre el piso
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

%crea el cubo a sobre el cubo b
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
                                                        write('Se creÛ el '),
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
                                                        write('Se creÛ el '),
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
                                              write('Se creÛ el cubo '),
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
                                              write('Se creÛ el cubo '),
                                              write(SUJETO),
                                              write(' sobre '),
                                              writeln(OBJETO).
                                              
 %Procesando Ûrdenes que involucren quitar elementos.
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
										
%%qué ocurre si el campo está vacío
procesar([]):- writeln('El campo está vacío.'), true.
