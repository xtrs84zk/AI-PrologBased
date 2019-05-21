import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ProcesarReglas {
    private static ArrayList<String> codigoDeReglasAlArchivoPl, codigoDeEscenarioAlArchivoPl;
    private static ArrayList<String> codigoDelEscenario;
    private static int cantidadDeCubosAnonimos = 0;

    public static void main(String[] args) {
        String rutaAGuardarElArchivoDeReglas = "/Users/xtrs84zk/Documents/AI/AI-PrologBased/expertSystem/reglas.pl";
        String rutaParaCargarElArchivoDeReglas = "/Users/xtrs84zk/Documents/AI/AI-PrologBased/expertSystem/reglas.txt";
        String rutaParaCargarElEscenario = "/Users/xtrs84zk/Documents/AI/AI-PrologBased/expertSystem/scenario.txt";
        String rutaParaGuardarElEscenario = "/Users/xtrs84zk/Documents/AI/AI-PrologBased/expertSystem/scenario.pl";
        //cargando las reglas y el escenario EnCrudo desde un archivo de texto
        ArrayList reglasEnCrudo;
        ArrayList<String> escenarioEnCrudo = new ArrayList<String>();
        codigoDeReglasAlArchivoPl = new ArrayList<>();
        codigoDeEscenarioAlArchivoPl = new ArrayList<>();
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
                        codigoDeEscenarioAlArchivoPl.add("write('Error al definir escenario, linea: " + i + ".");
                    }
                    i++;
                }
                //exportando el archivo del escenario
                try {
                    escribirElResultadoAUnArchivo(codigoDeEscenarioAlArchivoPl, rutaParaGuardarElEscenario);
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
        String reglaProcesada = "";
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

    private static String procesarEscena(String escena) {
        String[] escenaPorPalabras = escena.split(" ");
        int tipo = -1, tamaño = -1;
        String tamañoReal = "";
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
                    if (esUnTamañoValido(escenaPorPalabras[4])) {
                        tamañoReal = normalizarTamaño(escenaPorPalabras[4]);
                        tamaño = 4;
                    } else if (esUnTamañoValido(escenaPorPalabras[2])) {
                        tamañoReal = normalizarTamaño(escenaPorPalabras[2]);
                        tamaño = 2;
                    } else if (esUnTamañoValido(escenaPorPalabras[3])) {
                        tamaño = 3;
                        tamañoReal = normalizarTamaño(escenaPorPalabras[tamaño]);
                    }
                    //el color se define como el tercer enunciado no encontrado
                    if ((tipo == 4 && tamaño == 2) || tipo == 2 && tamaño == 4) {
                        colorReal = escenaPorPalabras[3];
                    } else if ((tipo == 4 && tamaño == 3) || (tamaño == 4 && tipo == 3)) {
                        colorReal = escenaPorPalabras[2];
                    } else if (tipo == -1) {
                        switch (tamaño) {
                            case -1:
                                colorReal = escenaPorPalabras[3];
                                break;
                        }
                    }
                    // 2 : tipo de gifura ; 4 : tamaño de la figura ; 3 : color de la figura ;
                    return crearFigura(tipoReal, tamañoReal, colorReal, "");
                }
                break;

        }
        return escena;
    }

    private static String normalizarTamaño(String tamaño) {
        switch (tamaño.charAt(0)) {
            case 'c':
                return "chico";
            case 'g':
                return "grande";
            case 'm':
                return "mediano";
        }
        //no debería haber errores en este punto, but, what gives
        //tamaño mediano por defecto
        return "mediano";
    }

    private static boolean esUnaFiguraValida(String figura) {
        if (figura.equals("cubo")) {
            return true;
        } else if (figura.equals("caja")) {
            return true;
        } else return figura.equals("cilindro");
    }

    private static boolean esUnTamañoValido(String tamaño) {
        if (tamaño.equals("grande") || tamaño.equals("chico") || tamaño.equals("mediano")) {
            return true;
        }
        return tamaño.equals("chica") || tamaño.equals("mediana");
    }


    private static String crearFigura(String tipo, String tamaño, String color, String X) {
        if (tipo.equals("")) {
            tipo = "cubo";
        }
        if (tamaño.equals("")) {
            tamaño.equals("chico");
        }
        if (color.equals("")) {
            color = ("azul");
        }
        if (X.equals("")) {
            X = tipo + tamaño + color + ++cantidadDeCubosAnonimos;
        }
        return "tamaño(" + X.toUpperCase() + "," + "). \n" +
                "tipo(" + X.toUpperCase() + "," + tipo + "). \n" +
                "color(" + X.toUpperCase() + "," + color + "). \n" +
                "sobre(" + X.toUpperCase() + ",piso). \n";
    }

    private static String procesarRegla(String regla) {
        String[] reglaPorPalabras = regla.split(" ");
        int tamañoDeLaReglaEnPalabras;
        tamañoDeLaReglaEnPalabras = reglaPorPalabras.length;
        switch (tamañoDeLaReglaEnPalabras) {
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
            //limpiar el objeto Y
            case 4:
                if (reglaPorPalabras[0].equals("limpiar") && reglaPorPalabras[1].equals("el")
                        && reglaPorPalabras[2].equals("objeto")) {
                    return limpiarProlog(reglaPorPalabras[3]);
                }
                break;
            case 7:
                //poner el objeto Y en el piso
                if (reglaPorPalabras[0].equals("poner") && reglaPorPalabras[1].equals("el")
                        && reglaPorPalabras[2].equals("objeto") && reglaPorPalabras[4].equals("en")
                        && reglaPorPalabras[5].equals("el") && reglaPorPalabras[6].equals("piso")) {
                    return moverAlPisoProlog(reglaPorPalabras[3]);
                }
                break;
            //El objeto X está vacío
            case 5:
                if (reglaPorPalabras[0].equals("el") && reglaPorPalabras[1].equals("objeto")
                        && (reglaPorPalabras[3].equals("está") || reglaPorPalabras[3].equals("esta")) && reglaPorPalabras[4].equals("vacío")) {
                    return verificacionDeObjetoVacioProlog(reglaPorPalabras[2]);
                }
                break;
            case 2:
                //Limpiar Y
                if (reglaPorPalabras[0].equals("limpiar")) {
                    return limpiarProlog(reglaPorPalabras[1]);
                }
            default:
                return "";
        }
        return "";
    }

    private static String moverProlog(String X, String Y) {
        return "mover(" + X.toUpperCase() + "," + Y.toUpperCase() + ")";
    }

    private static String verificacionDeObjetoVacioProlog(String X) {
        return "sobre(nada" + "," + X.toUpperCase() + ")";
    }

    private static String ponerEncima(String X, String Y) {
        return "assert(sobre(" + X.toUpperCase() + "," + Y.toUpperCase() + ")";
    }

    private static String moverAlPisoProlog(String X) {
        return "assert(sobre(" + X.toUpperCase() + ",piso)";
    }

    private static String limpiarProlog(String X) {
        return "retract(sobre(nada," + X.toUpperCase() + ")";
    }

    private static String quitarProlog(String X, String Y) {
        return "quitar(" + X.toUpperCase() + "," + Y.toUpperCase() + ")";
    }

    private static String sobreProlog(String X, String Y) {
        return "sobre(" + X.toUpperCase() + "," + Y.toUpperCase() + ")";
    }

    private static String procesarLinea(String linea) {
        //asegurándome de que no haya espacios antes de la primer palabra o después de la última
        linea = eliminarEspaciosAlInicioYFinal(linea);
        return linea;
    }

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
