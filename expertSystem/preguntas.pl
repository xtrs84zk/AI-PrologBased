%  Base de datos de preguntas
%  Cada pregunta se conforma de (Predicado, Mensaje).
%
%  -'Predicado' is un predicado usado para llenar la base de
%  conocimiento para solo incluir las respuestas que concuerden con las
%  del usuario.
%  -'Mensaje' es un mensaje que se le muestra al usuario para
%  preguntarle sobre una propiedad de la entidad que el sistema experto
%  esta pensando. Debe tener una respuesta binaria.

questions([
  [colorAmarillo,  '�Es color amarillo?'],
  [colorVerde,      '�Es color verde?'],
  [tipoElectrico,  '�Es tipo el�ctrico?'],
  [tipoRoca,  '�Es tipo roca?'],
  [tieneAlas,   '�Tiene alas?'],
  [esRelevanteEnLaMeta, '�Es relevante en la meta?'],
  [primerEvolucion,    '�Es la primer evoluci�n de una familia?'],
  [generacionUno,  '�Pertenece a la primera generaci�n?'],
  [generacionDos,  '�Pertenece a la segunda generaci�n?'],
  [legendario, '�Es un pok�mon legendario?']
]).
