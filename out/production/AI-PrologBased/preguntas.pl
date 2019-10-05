% Base de datos de preguntas % Cada pregunta se conforma de (Predicado, Mensaje).
%%-'Predicado' es un predicado usado para llenar la base de conocimiento para sólo incluir las respuestas que concuerden con las % del usuario.
%%-'Mensaje' es un mensaje que se le muestra al usuario para % preguntarle sobre una propiedad de la entidad que el sistema experto % esta pensando. Debe tener una respuesta binaria.
questions([
  [legendario, '¿Es un pokémon legendario?'],
  [colorAmarillo,  '¿Es color amarillo?'],
  [colorVerde,      '¿Es color verde?'],
  [tipoElectrico,  '¿Es tipo eléctrico?'],
  [tipoRoca,  '¿Es tipo roca?'],
  [tieneAlas,   '¿Tiene alas?'],
  [esRelevanteEnLaMeta, '¿Es relevante en la meta?'],
  [primerEvolucion,    '¿Es la primer evolución de una familia?'],
  [generacionUno,  '¿Pertenece a la primera generación?'],
  [generacionDos,  '¿Pertenece a la segunda generación?']
]).
