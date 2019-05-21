import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ProcesarReglas {
    private static ArrayList<String> codigoAlArchivoPl;
    private static ArrayList<String> codigoDelEscenario;
    private static int cantidadDeCubosAnonimos = 0;

    public static void main(String[] args) {
        String rutaAGuardarElArchivoDeReglas = "/Users/xtrs84zk/Documents/AI/AI-PrologBased/expertSystem/reglas.pl";
        String rutaParaCargarElArchivoDeReglas = "/Users/xtrs84zk/Documents/AI/AI-PrologBased/expertSystem/reglas.txt";
        //cargando el codigo desde un archivo de texto
        ArrayList codigo;
        codigoAlArchivoPl = new ArrayList<>();
        try {
            //Cargando el archivo de reglas
            codigo = cargarUnArchivoDeTexto(rutaParaCargarElArchivoDeReglas);
            procesarReglasDesdeLenguajeNatural(codigo);
        } catch (Exception e) {
            System.err.println("Error al encontrar el archivo.");
            return;
        }
        //guardando el archivo de reglas procesado a un .pl
        try {
            escribirElResultadoAUnArchivo(codigoAlArchivoPl, rutaAGuardarElArchivoDeReglas);
        } catch (Exception f) {
            f.printStackTrace();
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
                        codigoAlArchivoPl.add(reglaProcesada);
                        continue why;
                    } else {
                        codigoAlArchivoPl.add("write('Regla mal redactada, revisar línea: " + i + "'),");
                        continue why;
                    }
                }
                //se encontró el entonces
                while (!lineasDelLenguajeNaturalAProcesar.get(i).equals("si")) {
                    //procesarEntonces
                    reglaProcesada = procesarRegla(lineasDelLenguajeNaturalAProcesar.get(i));
                    if (reglaProcesada != null) {
                        if (!reglaProcesada.equals("")) {
                            codigoAlArchivoPl.add(reglaProcesada + ",");
                        }
                    }
                    continue why;
                }
                //se cierra el bloque de código prolog
                codigoAlArchivoPl.add("true. \n");
            }
            codigoAlArchivoPl.add("true. \n");
        }
        codigoAlArchivoPl.add("true. \n");
        //}
    }

    private static String procesarEscena(String escena) {
        String[] escenaPorPalabras = escena.split(" ");
        int cantidadDePalabrasEnLaEscenaDescrita = escenaPorPalabras.length;
        switch (cantidadDePalabrasEnLaEscenaDescrita) {
            case 8:
                //existe un cubo azul grande sobre el piso
                if (escenaPorPalabras[0].equals("existe") && escenaPorPalabras[1].equals("un")
                        && esUnaFiguraValida(escenaPorPalabras[2])
                        && esUnTamañoValido(escenaPorPalabras[4])
                        && escenaPorPalabras[5].equals("sobre") && escenaPorPalabras[6].equals("el")
                        && escenaPorPalabras[7].equals("piso")) {
                    return crearFigura(escenaPorPalabras[2], escenaPorPalabras[4], escenaPorPalabras[3], "");
                }
                break;
        }
        return escena;
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
            X = tipo + tamaño + color + cantidadDeCubosAnonimos;
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
