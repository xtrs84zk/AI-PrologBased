import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

public class ProcesarReglas {
    private static ArrayList<String> codigoDeReglasAlArchivoPl;

    public static void main(String[] args) {
        String rutaAGuardarElArchivoDeReglas = "/Users/xtrs84zk/Documents/AI/AI-PrologBased/expertSystem/reglas.pl";
        String rutaParaCargarElArchivoDeReglas = "/Users/xtrs84zk/Documents/AI/AI-PrologBased/expertSystem/reglas.txt";
        String rutaParaCargarElEscenario = "/Users/xtrs84zk/Documents/AI/AI-PrologBased/expertSystem/scenario.txt";
        String rutaParaGuardarElEscenario = "/Users/xtrs84zk/Documents/AI/AI-PrologBased/expertSystem/scenario.pl";
        //cargando las reglas y el escenario EnCrudo desde un archivo de texto
        ArrayList reglasEnCrudo;
        ArrayList<String> escenarioEnCrudo;
        codigoDeReglasAlArchivoPl = new ArrayList<>();
        ArrayList<String> codigoDeEscenarioAlArchivoPl = new ArrayList<>();
        try {
            //Cargando el archivo de reglas
            reglasEnCrudo = cargarUnArchivoDeTexto(rutaParaCargarElArchivoDeReglas);
            procesarReglasDesdeLenguajeNatural(reglasEnCrudo);
        } catch (Exception e) {
            System.err.println("No se pudo cargar el archivo de reglas.");
            return;
        }
        //guardando el archivo de reglas procesado a un .pl
        try {
            escribirElResultadoAUnArchivo(codigoDeReglasAlArchivoPl, rutaAGuardarElArchivoDeReglas);
            System.out.println("Se han procesado las reglas.");
        } catch (Exception f) {
            f.printStackTrace();
        }

        //cargando el archivo con el escenario
        try {
            escenarioEnCrudo = cargarUnArchivoDeTexto(rutaParaCargarElEscenario);
            int i = 0;
            String tmp;
            try {
                //procesando las reglas del escenario
                while (i < escenarioEnCrudo.size()) {
                    tmp = procesarEscena(escenarioEnCrudo.get(i));
                    if (!tmp.equals("")) {
                        codigoDeEscenarioAlArchivoPl.add(tmp);
                    } else {
                        codigoDeEscenarioAlArchivoPl.add("write('Error al definir escenario, linea: " + (i + 1) + "').");
                    }
                    i++;
                }
                //exportando el archivo del escenario
                try {
                    Collections.sort(codigoDeEscenarioAlArchivoPl);
                    escribirElResultadoAUnArchivo(codigoDeEscenarioAlArchivoPl, rutaParaGuardarElEscenario);
                    System.out.println("Se ha procesado el escenario.");
                } catch (Exception k) {
                    System.err.println("Hubo un error al guardar el archivo de escenario.");
                }
            } catch (Exception p) {
                p.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar el archivo de escenario.");
        }
        //guardando el escenario procesado a un .pl
    }

    /**
     * Procesando reglas del lenguaje natural
     *
     * @param lineasDelLenguajeNaturalAProcesar que son las líneas a procesar de lenguaje natural
     * @return null
     */
    private static void procesarReglasDesdeLenguajeNatural(ArrayList<String> lineasDelLenguajeNaturalAProcesar) {
        // if (lineasDelLenguajeNaturalAProcesar.get(0).equals("si")) {
        String reglaProcesada;
        why:
        for (int i = 1; i < lineasDelLenguajeNaturalAProcesar.size(); i++) {
            while (!lineasDelLenguajeNaturalAProcesar.get(i).equals("si")) {
                while (!lineasDelLenguajeNaturalAProcesar.get(i).equals("entonces")) {
                    //procesarRegla
                    //agregar la regla procesada a un .pl
                    reglaProcesada = procesarRegla(lineasDelLenguajeNaturalAProcesar.get(i));
                    if (reglaProcesada != null) {
                        if (reglaProcesada.equals("")) {
                            continue why;
                        }
                        if (reglaProcesada.charAt(reglaProcesada.length() - 1) != '-') {
                            reglaProcesada += ",";
                        }
                        codigoDeReglasAlArchivoPl.add(reglaProcesada);
                        continue why;
                    } else {
                        codigoDeReglasAlArchivoPl.add("write('Regla mal redactada, revisar línea: " + i + "'),");
                        continue why;
                    }
                }
                //se encontró el entonces
                while (!lineasDelLenguajeNaturalAProcesar.get(i).equals("si")) {
                    //procesarEntonces
                    reglaProcesada = procesarRegla(lineasDelLenguajeNaturalAProcesar.get(i));
                    if (reglaProcesada != null) {
                        if (!reglaProcesada.equals("")) {
                            codigoDeReglasAlArchivoPl.add(reglaProcesada + ",");
                        }
                    }
                    continue why;
                }
                //se cierra el bloque de código prolog
                codigoDeReglasAlArchivoPl.add("true. \n");
            }
            codigoDeReglasAlArchivoPl.add("true. \n");
        }
        codigoDeReglasAlArchivoPl.add("true. \n");
        //}
    }

    /**
     * Procesa una oración desde lenguaje natural de forma que ésta sea procesada fácilmente en prolog
     *
     * @param escena que es una línea de lenguaje natural
     * @return la línea en lenguaje prolog
     */
    private static String procesarEscena(String escena) {
        String[] escenaPorPalabras = escena.split(" ");
        int tipo = -1, tamano = -1;
        String tamanoReal = "";
        String tipoReal = "";
        String colorReal = "";
        int cantidadDePalabrasEnLaEscenaDescrita = escenaPorPalabras.length;
        switch (cantidadDePalabrasEnLaEscenaDescrita) {
            case 8:
                //existe un cubo azul grande sobre el piso
                if (escenaPorPalabras[0].equals("existe") && escenaPorPalabras[1].equals("un")
                        && escenaPorPalabras[5].equals("sobre") && escenaPorPalabras[6].equals("el")
                        && escenaPorPalabras[7].equals("piso")) {
                    //se busca la sintaxis en que escribió los atributos
                    if (esUnaFiguraValida(escenaPorPalabras[2])) {
                        tipoReal = escenaPorPalabras[2];
                        tipo = 2;
                    } else if (esUnaFiguraValida(escenaPorPalabras[4])) {
                        tipoReal = escenaPorPalabras[4];
                        tipo = 4;
                    } else if (esUnaFiguraValida(escenaPorPalabras[3])) {
                        tipo = 3;
                        tipoReal = escenaPorPalabras[tipo];
                    }
                    if (esUnTamanoValido(escenaPorPalabras[4])) {
                        tamanoReal = normalizarTamano(escenaPorPalabras[4]);
                        tamano = 4;
                    } else if (esUnTamanoValido(escenaPorPalabras[2])) {
                        tamanoReal = normalizarTamano(escenaPorPalabras[2]);
                        tamano = 2;
                    } else if (esUnTamanoValido(escenaPorPalabras[3])) {
                        tamano = 3;
                        tamanoReal = normalizarTamano(escenaPorPalabras[tamano]);
                    }
                    //el color se define como el tercer enunciado no encontrado
                    if ((tipo == 4 && tamano == 2) || tipo == 2 && tamano == 4) {
                        colorReal = escenaPorPalabras[3];
                    } else if ((tipo == 4 && tamano == 3) || (tamano == 4 && tipo == 3)) {
                        colorReal = escenaPorPalabras[2];
                    } else if (tipo == -1) {
                        if (tamano == -1) {
                            colorReal = escenaPorPalabras[3];
                        }
                    }
                    // 2 : tipo de gifura ; 4 : tamano de la figura ; 3 : color de la figura ;
                    return crearFigura(tipoReal, tamanoReal, colorReal, "");
                }
                //arriba del cilindro amarillo grande no existen objetos
                if (escenaPorPalabras[0].equals("arriba") && escenaPorPalabras[1].equals("del")
                        && escenaPorPalabras[5].equals("no") && escenaPorPalabras[6].equals("existen")
                        && escenaPorPalabras[7].equals("objetos")) {
                    tamano = encontrarTamanoValido(escenaPorPalabras, 2, 3, 4);
                    tipo = encontrarFiguraValida(escenaPorPalabras, 2, 3, 4);
                    if (tipo != -1 && tamano != -1) {
                        tipoReal = escenaPorPalabras[tipo];
                        tamanoReal = normalizarTamano(escenaPorPalabras[tamano]);
                        if ((tamano == 3 && tipo == 4) || tamano == 4 && tipo == 3) {
                            colorReal = escenaPorPalabras[2];
                        } else if ((tamano == 2 && tipo == 4) || (tamano == 4 && tipo == 2)) {
                            colorReal = escenaPorPalabras[3];
                        } else {
                            colorReal = escenaPorPalabras[4];
                        }
                    }
                    return "sobre(nada," + accederFigura(tipoReal, tamanoReal, colorReal) + ").";

                }
                break;
            case 11:
                //Arriba del cubo azul grande se encuentra el cubo rojo mediano
                if (escenaPorPalabras[0].equals("arriba") && escenaPorPalabras[1].equals("del")
                        && escenaPorPalabras[5].equals("se") && escenaPorPalabras[6].equals("encuentra")
                        && escenaPorPalabras[7].equals("el")) {
                    tamano = encontrarTamanoValido(escenaPorPalabras, 2, 3, 4);
                    if (tamano != -1) {
                        tamanoReal = normalizarTamano(escenaPorPalabras[tamano]);
                    }
                    tipo = encontrarFiguraValida(escenaPorPalabras, 2, 3, 4);
                    if (tipo != -1) {
                        tipoReal = escenaPorPalabras[tipo];
                    }
                    if ((tipo == 4 && tamano == 2) || tipo == 2 && tamano == 4) {
                        colorReal = escenaPorPalabras[3];
                    } else if ((tipo == 4 && tamano == 3) || (tamano == 4 && tipo == 3)) {
                        colorReal = escenaPorPalabras[2];
                    } else {
                        System.err.println("Error al buscar figura en línea '" + escena + "'");
                        return "";
                    }
                    String figura1 = accederFigura(tipoReal, tamanoReal, colorReal);
                    tamano = encontrarTamanoValido(escenaPorPalabras, 8, 9, 10);
                    tipo = encontrarFiguraValida(escenaPorPalabras, 8, 9, 10);
                    if (tipo != -1 && tamano != -1) {
                        tipoReal = escenaPorPalabras[tipo];
                        tamanoReal = normalizarTamano(escenaPorPalabras[tamano]);
                        if ((tamano == 8 && tipo == 9) || tamano == 9 && tipo == 8) {
                            colorReal = escenaPorPalabras[10];
                        } else if ((tamano == 10 && tipo == 9) || (tamano == 9 && tipo == 10)) {
                            colorReal = escenaPorPalabras[8];
                        } else {
                            colorReal = escenaPorPalabras[9];
                        }
                        return crearFigura(tipoReal, tamanoReal, colorReal, "", figura1);
                    }
                }
                //Arriba del cubo rojo mediana esta una caja chica amarilla cerrada
                else if (escenaPorPalabras[0].equals("arriba") && escenaPorPalabras[1].equals("del")
                        && (escenaPorPalabras[5].equals("está") || escenaPorPalabras[5].equals("esta"))
                        && escenaPorPalabras[6].equals("una")) {
                    tamano = encontrarTamanoValido(escenaPorPalabras, 2, 3, 4);
                    tipo = encontrarFiguraValida(escenaPorPalabras, 2, 3, 4);
                    if (tipo != -1 && tamano != -1) {
                        tipoReal = escenaPorPalabras[tipo];
                        tamanoReal = normalizarTamano(escenaPorPalabras[tamano]);
                        if ((tamano == 2 && tipo == 4) || tamano == 4 && tipo == 2) {
                            colorReal = escenaPorPalabras[3];
                        } else if ((tamano == 2 && tipo == 3) || (tamano == 3 && tipo == 2)) {
                            colorReal = escenaPorPalabras[4];
                        } else {
                            colorReal = escenaPorPalabras[2];
                        }
                        String figura1 = accederFigura(tipoReal, tamanoReal, colorReal);
                        tamano = encontrarTamanoValido(escenaPorPalabras, 7, 8, 9);
                        tipo = encontrarFiguraValida(escenaPorPalabras, 7, 8, 9);
                        boolean abierta = (escenaPorPalabras[10].equals("abierta") || escenaPorPalabras[10].equals("abierto"));
                        if (tipo != -1 && tamano != -1) {
                            tipoReal = escenaPorPalabras[tipo];
                            tamanoReal = normalizarTamano(escenaPorPalabras[tamano]);
                            if ((tamano == 7 && tipo == 9) || tamano == 9 && tipo == 7) {
                                colorReal = escenaPorPalabras[8];
                            } else if ((tamano == 7 && tipo == 8) || (tamano == 8 && tipo == 7)) {
                                colorReal = escenaPorPalabras[9];
                            } else {
                                colorReal = escenaPorPalabras[7];
                            }
                            return crearFigura(tipoReal, tamanoReal, colorReal, "", figura1, abierta);
                        }
                    }
                }
                break;
            case 9:
                //arriba de la caja chica amarilla no existen objetos
                if (escenaPorPalabras[0].equals("arriba") && escenaPorPalabras[1].equals("de")
                        && (escenaPorPalabras[2].equals("la") || escenaPorPalabras[6].equals("no"))
                        && escenaPorPalabras[7].equals("existen") && escenaPorPalabras[8].equals("objetos")) {
                    tamano = encontrarTamanoValido(escenaPorPalabras, 3, 4, 5);
                    tipo = encontrarFiguraValida(escenaPorPalabras, 3, 4, 5);
                    if (tipo != -1 && tamano != -1) {
                        tipoReal = escenaPorPalabras[tipo];
                        tamanoReal = normalizarTamano(escenaPorPalabras[tamano]);
                        if ((tamano == 3 && tipo == 5) || tamano == 5 && tipo == 3) {
                            colorReal = escenaPorPalabras[4];
                        } else if ((tamano == 5 && tipo == 4) || (tamano == 4 && tipo == 5)) {
                            colorReal = escenaPorPalabras[3];
                        } else {
                            colorReal = escenaPorPalabras[5];
                        }
                    }
                    return "sobre(nada," + accederFigura(tipoReal, tamanoReal, colorReal) + ").";
                }
                break;

        }
        return "";
    }

    /**
     * Verifica que el texto contenido en cada una de las tres opciones
     * sea un tamaño válido y regresa donde lo encontró
     * En caso de no encontrarlo, regresa un -1
     */
    private static int encontrarTamanoValido(String[] dondeBuscar, int opcion1, int opcion2, int opcion3) {
        if (esUnTamanoValido(dondeBuscar[opcion1])) {
            return opcion1;
        } else if (esUnTamanoValido(dondeBuscar[opcion2])) {
            return opcion2;
        } else if (esUnTamanoValido(dondeBuscar[opcion3])) {
            return opcion3;
        }
        return -1;
    }

    /**
     * Verifica que el texto contenido en alguna de las tres opciones
     * sea un tipo soportado de figura, y regresa donde lo encontró
     * en caso contrario, regresa -1
     */
    private static int encontrarFiguraValida(String[] dondeBuscar, int opcion1, int opcion2, int opcion3) {
        if (esUnaFiguraValida(dondeBuscar[opcion1])) {
            return opcion1;
        } else if (esUnaFiguraValida(dondeBuscar[opcion2])) {
            return opcion2;
        } else if (esUnaFiguraValida(dondeBuscar[opcion3])) {
            return opcion3;
        }
        return -1;
    }

    /**
     * Define un estándar para el tamano a utilizar en el código prolog
     * Por ejemplo, traduce chica a chico
     *
     * @param tamano que es el tamano a estandarizar
     * @return String estandarizado
     */
    private static String normalizarTamano(String tamano) {
        switch (tamano.charAt(0)) {
            case 'c':
                return "chico";
            case 'g':
                return "grande";
            case 'm':
                return "mediano";
        }
        //no debería haber errores en este punto, but, what gives
        //tamano mediano por defecto
        return "mediano";
    }

    /**
     * Verifica que la cadena proporcionada sea un tipo de figura válido
     *
     * @param figura que es el tipo de figura
     * @return true si es un tipo admitido
     */
    private static boolean esUnaFiguraValida(String figura) {
        if (figura.equals("cubo")) {
            return true;
        } else if (figura.equals("caja")) {
            return true;
        } else return figura.equals("cilindro");
    }

    /**
     * Verifica que el tamano sea válido
     *
     * @param tamano que es el tamano de la figura
     * @return true si es un tamano soportado
     */
    private static boolean esUnTamanoValido(String tamano) {
        if (tamano.equals("grande") || tamano.equals("chico") || tamano.equals("mediano")) {
            return true;
        }
        return tamano.equals("chica") || tamano.equals("mediana");
    }

    /**
     * Procesa los parámetros y los convierte a sintaxis Prolog
     *
     * @param tipo   de la figura
     * @param tamano de la figura
     * @param color  de la figura
     * @param X      o nombre de la figura
     * @return sintaxis en lenguaje Prolog
     */
    private static String crearFigura(String tipo, String tamano, String color, String X) {
        if (tipo.equals("")) {
            tipo = "cubo";
        }
        if (tamano.equals("")) {
            tamano = "mediano";
        }
        if (color.equals("")) {
            color = ("azul");
        }
        if (X.equals("")) {
            X = tipo + tamano + color;
        }
        return "tamano(" + X.toUpperCase() + "," + tamano + "). \n" +
                "tipo(" + X.toUpperCase() + "," + tipo + "). \n" +
                "color(" + X.toUpperCase() + "," + color + "). \n" +
                "sobre(" + X.toUpperCase() + ",piso). ";
    }

    /**
     * Procesa los parámetros y los convierte a sintaxis Prolog
     *
     * @param tipo   de la figura
     * @param tamano de la figura
     * @param color  de la figura
     * @param X      o nombre de la figura
     * @return sintaxis en lenguaje Prolog
     */
    private static String crearFigura(String tipo, String tamano, String color, String X, boolean abierta) {
        String estado = abierta ? "open" : "closed";
        if (tipo.equals("caja")) {
            if (tamano.equals("")) {
                tamano = "mediano";
            }
            if (color.equals("")) {
                color = ("azul");
            }
            if (X.equals("")) {
                X = tipo + tamano + color;
            }
            return "tamano(" + X.toUpperCase() + "," + tamano + "). \n" +
                    "tipo(" + X.toUpperCase() + "," + tipo + "). \n" +
                    "estado(" + X.toUpperCase() + "," + estado + "). \n" +
                    "color(" + X.toUpperCase() + "," + color + "). \n" +
                    "sobre(" + X.toUpperCase() + ",piso).";
        }
        return "tamano(" + X.toUpperCase() + "," + tamano + "). \n" +
                "tipo(" + X.toUpperCase() + "," + tipo + "). \n" +
                "color(" + X.toUpperCase() + "," + color + "). \n" +
                "sobre(" + X.toUpperCase() + ",piso).";
    }

    /**
     * Procesa los parámetros y los convierte a sintaxis Prolog
     *
     * @param tipo   de la figura
     * @param tamano de la figura
     * @param color  de la figura
     * @param X      o nombre de la figura
     * @return sintaxis en lenguaje Prolog
     */
    private static String crearFigura(String tipo, String tamano, String color, String X, String Y) {
        if (tipo.equals("")) {
            tipo = "cubo";
        }
        if (tamano.equals("")) {
            tamano = "mediano";
        }
        if (color.equals("")) {
            color = ("azul");
        }
        if (X.equals("")) {
            X = tipo + tamano + color;
        }
        return "tamano(" + X.toUpperCase() + "," + tamano + "). \n" +
                "tipo(" + X.toUpperCase() + "," + tipo + "). \n" +
                "color(" + X.toUpperCase() + "," + color + "). \n" +
                "sobre(" + X.toUpperCase() + "," + Y.toUpperCase() + ").";
    }

    /**
     * Procesa los parámetros y los convierte a sintaxis Prolog
     *
     * @param tipo   de la figura
     * @param tamano de la figura
     * @param color  de la figura
     * @param X      o nombre de la figura
     * @return sintaxis en lenguaje Prolog
     */
    private static String crearFigura(String tipo, String tamano, String color, String X, String Y, boolean abierta) {
        String estado = abierta ? "open" : "closed";
        if (tipo.equals("caja")) {
            if (tamano.equals("")) {
                tamano = "mediano";
            }
            if (color.equals("")) {
                color = ("azul");
            }
            if (X.equals("")) {
                X = tipo + tamano + color;
            }
            return "tamano(" + X.toUpperCase() + "," + tamano + "). \n" +
                    "tipo(" + X.toUpperCase() + "," + tipo + "). \n" +
                    "estado(" + X.toUpperCase() + "," + estado + "). \n" +
                    "color(" + X.toUpperCase() + "," + color + "). \n" +
                    "sobre(" + X.toUpperCase() + "," + Y.toUpperCase() + ").";
        }
        return "tamano(" + X.toUpperCase() + "," + tamano + "). \n" +
                "tipo(" + X.toUpperCase() + "," + tipo + "). \n" +
                "color(" + X.toUpperCase() + "," + color + "). \n" +
                "sobre(" + X.toUpperCase() + "," + Y.toUpperCase() + ").";
    }

    /**
     * Accede a la última variable creada con dichas características
     *
     * @param tipo   de la figura
     * @param tamano de la figura
     * @param color  de la figura
     * @return nombre de la figura
     */
    private static String accederFigura(String tipo, String tamano, String color) {
        return (tipo + tamano + color).toUpperCase();
    }

    /**
     * Se intenta procesar la regla de tal suerte que ésta sea traducida a prolog
     * Primeramente, identificando los componentes clave de la misma
     *
     * @param regla a identificar
     * @return codigo Prolog equivalente
     */
    private static String procesarRegla(String regla) {
        String[] reglaPorPalabras = regla.split(" ");
        int tamanoDeLaReglaEnPalabras;
        tamanoDeLaReglaEnPalabras = reglaPorPalabras.length;
        switch (tamanoDeLaReglaEnPalabras) {
            case 11:
                //la orden es quitar el objeto X sobre el objeto Y
                if (reglaPorPalabras[0].equals("la") && reglaPorPalabras[1].equals("orden")
                        && reglaPorPalabras[2].equals("es") && reglaPorPalabras[3].equals("quitar")
                        && reglaPorPalabras[4].equals("el") && reglaPorPalabras[5].equals("objeto")
                        && reglaPorPalabras[7].equals("sobre") && reglaPorPalabras[8].equals("el")
                        && reglaPorPalabras[9].equals("objeto")) {
                    return quitarProlog(reglaPorPalabras[6], reglaPorPalabras[10]) + ":-";

                }
                //la orden es mover el objeto X encima del objeto Y
                if (reglaPorPalabras[0].equals("la") && reglaPorPalabras[1].equals("orden")
                        && reglaPorPalabras[2].equals("es") && reglaPorPalabras[3].equals("mover")
                        && reglaPorPalabras[4].equals("el") && reglaPorPalabras[5].equals("objeto")
                        && (reglaPorPalabras[7].equals("sobre") || reglaPorPalabras[7].equals("encima"))
                        && (reglaPorPalabras[8].equals("el") || reglaPorPalabras[8].equals("del"))
                        && reglaPorPalabras[9].equals("objeto")) {
                    return moverProlog(reglaPorPalabras[6], reglaPorPalabras[10]) + ":-";
                }
                break;
            //la orden es quitar el objeto z del objeto y
            case 10:
                if (reglaPorPalabras[0].equals("la") && reglaPorPalabras[1].equals("orden")
                        && reglaPorPalabras[2].equals("es") && reglaPorPalabras[3].equals("quitar")
                        && reglaPorPalabras[4].equals("el") && reglaPorPalabras[5].equals("objeto")
                        && reglaPorPalabras[7].equals("del") && reglaPorPalabras[8].equals("objeto")) {
                    return quitarProlog(reglaPorPalabras[6], reglaPorPalabras[9]) + "";
                }
                break;
            case 8:
                //el objeto X está encima del objeto Y
                if (reglaPorPalabras[0].equals("el") && reglaPorPalabras[1].equals("objeto")
                        && reglaPorPalabras[3].equals("está") && reglaPorPalabras[4].equals("encima")
                        && reglaPorPalabras[5].equals("del") && reglaPorPalabras[6].equals("objeto")) {
                    return sobreProlog(reglaPorPalabras[2], reglaPorPalabras[7]);
                }
                //poner el objeto X sobre el objeto Y
                if (reglaPorPalabras[0].equals("poner") && reglaPorPalabras[1].equals("el")
                        && reglaPorPalabras[2].equals("objeto") && reglaPorPalabras[4].equals("sobre")
                        && reglaPorPalabras[5].equals("el") && reglaPorPalabras[6].equals("objeto")) {
                    return ponerEncima(reglaPorPalabras[3], reglaPorPalabras[7]);
                }
                break;
            case 4:
                //limpiar el objeto Y
                if (reglaPorPalabras[0].equals("limpiar") && reglaPorPalabras[1].equals("el")
                        && reglaPorPalabras[2].equals("objeto")) {
                    return limpiarProlog(reglaPorPalabras[3]);
                }
                //abrir la caja X
                if (reglaPorPalabras[0].equals("abrir") && reglaPorPalabras[1].equals("la")
                        && reglaPorPalabras[2].equals("caja")) {
                    return "retract(estado(" + reglaPorPalabras[3] + "," + "closed)), \n"
                            + "assert(estado(" + reglaPorPalabras[3] + "," + "open))";

                }
                break;
            case 7:
                //poner el objeto Y en el piso
                if (reglaPorPalabras[0].equals("poner") && reglaPorPalabras[1].equals("el")
                        && reglaPorPalabras[2].equals("objeto") && reglaPorPalabras[4].equals("en")
                        && reglaPorPalabras[5].equals("el") && reglaPorPalabras[6].equals("piso")) {
                    return moverAlPisoProlog(reglaPorPalabras[3]);
                }
                //La orden es abrir la caja X
                if (reglaPorPalabras[0].equals("la") && reglaPorPalabras[1].equals("orden")
                        && reglaPorPalabras[2].equals("es") && reglaPorPalabras[3].equals("abrir")
                        && reglaPorPalabras[4].equals("la") && reglaPorPalabras[5].equals("caja")) {
                    return abrirProlog(reglaPorPalabras[6]) + ":-";
                }
                break;
            case 5:
                //El objeto X está vacío
                if (reglaPorPalabras[0].equals("el") && reglaPorPalabras[1].equals("objeto")
                        && (reglaPorPalabras[3].equals("está") || reglaPorPalabras[3].equals("esta"))
                        && reglaPorPalabras[4].equals("vacío")) {
                    return verificacionDeObjetoVacioProlog(reglaPorPalabras[2]);
                }
                //la caja X está vacía
                if (reglaPorPalabras[0].equals("la") && reglaPorPalabras[1].equals("caja")
                        && (reglaPorPalabras[3].equals("está") || reglaPorPalabras[3].equals("esta"))
                        && (reglaPorPalabras[4].equals("vacía") || reglaPorPalabras[4].equals("vacia"))) {
                    return verificacionDeObjetoVacioProlog(reglaPorPalabras[2]);
                }
                //la caja X está cerrada
                if (reglaPorPalabras[0].equals("la") && reglaPorPalabras[1].equals("caja")
                        && (reglaPorPalabras[3].equals("está") || reglaPorPalabras[3].equals("esta"))
                        && (reglaPorPalabras[4].equals("cerrada"))) {
                    return verificacionDeCajaCerradaProlog(reglaPorPalabras[2]);
                }
                break;
            case 2:
                //Limpiar Y
                if (reglaPorPalabras[0].equals("limpiar")) {
                    return limpiarProlog(reglaPorPalabras[1]);
                }
                break;
            case 6:
                //Quitar el objeto X del piso
                if (reglaPorPalabras[0].equals("quitar") && reglaPorPalabras[1].equals("el")
                        && (reglaPorPalabras[2].equals("objeto")) && reglaPorPalabras[4].equals("del")
                        && reglaPorPalabras[5].equals("piso")) {
                    return "retract(sobre(" + reglaPorPalabras[3].toUpperCase() + ",piso)";
                }
            default:
                return "";
        }
        return "";
    }

    private static String abrirProlog(String X) {
        return "abrirCaja(" + X + ")";
    }
    private static String moverProlog(String X, String Y) {
        return "mover(" + X.toUpperCase() + "," + Y.toUpperCase() + ")";
    }

    private static String verificacionDeObjetoVacioProlog(String X) {
        return "sobre(nada" + "," + X.toUpperCase() + ")";
    }

    private static String verificacionDeCajaCerradaProlog(String X) {
        return "estado(" + X + "," + "closed)";
    }
    private static String ponerEncima(String X, String Y) {
        return "assert(sobre(" + X.toUpperCase() + "," + Y.toUpperCase() + "))";
    }

    private static String moverAlPisoProlog(String X) {
        return "assert(sobre(" + X.toUpperCase() + ",piso))";
    }

    private static String limpiarProlog(String X) {
        return "assert(sobre(nada," + X.toUpperCase() + "))";
    }

    private static String quitarProlog(String X, String Y) {
        return "quitar(" + X.toUpperCase() + "," + Y.toUpperCase() + ")";
    }

    private static String sobreProlog(String X, String Y) {
        return "sobre(" + X.toUpperCase() + "," + Y.toUpperCase() + ")";
    }

    /**
     * Prepara la línea leída para eliminar irregularidades
     *
     * @param linea a procesar
     * @return la línea procesada
     */
    private static String procesarLinea(String linea) {
        //asegurándome de que no haya espacios antes de la primer palabra o después de la última
        linea = eliminarEspaciosAlInicioYFinal(linea);
        return linea;
    }

    /**
     * Prepara una línea eliminando los espacios al inicio, final
     * y, en caso de presentarse dos o más consecutivos, los elimina
     * de tal forma que sólo sea uno
     *
     * @param linea a procesar
     * @return línea procesada
     */
    private static String eliminarEspaciosAlInicioYFinal(String linea) {
        linea += " ";
        String nuevaString = "";
        int cantidadDeEspacios = 0;
        why:
        for (int i = 0; i < linea.length(); i++) {
            //Eliminando espacios al final
            if (linea.charAt(i) == ' ') {
                cantidadDeEspacios++;
                continue;
            }
            //si la String de salida está vacía, se han eliminado los espacios del inicio
            //en caso contrario, se ignoran todos los posibles espacios entre palabras y sólo se inserta uno
            if (cantidadDeEspacios > 0 && !nuevaString.equals("")) {
                if (!(i == linea.length() - 1)) {
                    nuevaString += " ";
                }
            }
            //se concatena la siguiente letra a la String
            cantidadDeEspacios = 0;
            nuevaString += linea.charAt(i);
        }
        return nuevaString;
    }

    /**
     * Se encarga de leer un archivo previamente especifícado en codificación UTF-8
     *
     * @return ArrayList con el contenido del archivo
     * @throws IOException en caso de no poder acceder o leer el mismo.
     */
    private static ArrayList cargarUnArchivoDeTexto(String path) throws IOException {
        ArrayList<String> archivoDeTextoPorLineas;
        File archivoDelCodigo = new File(path);
        InputStreamReader input;
        input = new InputStreamReader(new FileInputStream(archivoDelCodigo), StandardCharsets.UTF_8);
        archivoDeTextoPorLineas = new ArrayList<>();
        String r;
        BufferedReader in = new BufferedReader(input);
        //el texto será leído en minúsculas
        while ((r = in.readLine()) != null) {
            archivoDeTextoPorLineas.add(procesarLinea(r.toLowerCase()));
        }
        in.close();
        return archivoDeTextoPorLineas;
    }

    /**
     * Recibe como parámetro un ArrayList y lo escribe a un archivo.
     *
     * @param listaAEscribir que contiene la información a escribir.
     * @throws IOException en caso de no poder escribir al archivo.
     */
    private static void escribirElResultadoAUnArchivo(ArrayList<String> listaAEscribir, String rutaDelArchivo) throws IOException {
        FileWriter writer = new FileWriter(rutaDelArchivo);
        for (String aListaAEscribir : listaAEscribir) {
            if (aListaAEscribir != null) {
                writer.write(aListaAEscribir + "\n");
            }
        }
        writer.close();
    }
}