
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Main {
    private static ArrayList<String> codigoAlArchivoPl;

    public static void main(String[] args) {
        String rutaAGuardarElArchivo = "/Users/xtrs84zk/Documents/AI/AI-PrologBased/expertSystem/escenario.pl";
        //cargando el codigo desde un archivo de texto
        ArrayList codigo;
        codigoAlArchivoPl = new ArrayList<>();
        try {
            codigo = cargarUnArchivoDeTexto();
            procesarReglasDesdeLenguajeNatural(codigo);
        } catch (Exception e) {
            System.err.println("Error al encontrar el archivo.");
        }
        try {
            escribirElResultadoAUnArchivo(codigoAlArchivoPl, rutaAGuardarElArchivo);
        } catch (Exception f) {
            f.printStackTrace();
        }
    }

    /**
     * Procesando reglas del lenguaje natural
     *
     * @param lineasDelLenguajeNaturalAProcesar que son las líneas a procesar de lenguaje natural
     * @return null
     */
    private static void procesarReglasDesdeLenguajeNatural(ArrayList<String> lineasDelLenguajeNaturalAProcesar) {
        if (lineasDelLenguajeNaturalAProcesar.get(0).equals("si")) {
            String reglaProcesada = "";
            why:
            for (int i = 1; i < lineasDelLenguajeNaturalAProcesar.size(); i++) {
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
                        codigoAlArchivoPl.add("write('Regla mal redactada, revisar línea: " + i + "').");
                        continue why;
                    }
                }
                //se encontró el entonces
                while (i < lineasDelLenguajeNaturalAProcesar.size() && !lineasDelLenguajeNaturalAProcesar.get(i).equals("si")) {
                    //procesarEntonces
                    reglaProcesada = procesarRegla(lineasDelLenguajeNaturalAProcesar.get(i));
                    if (reglaProcesada != null) {
                        if (reglaProcesada.equals("")) {
                            continue why;
                        }
                        codigoAlArchivoPl.add(reglaProcesada + ",");
                    }
                    continue why;
                }
                //se cierra el bloque de código prolog
                codigoAlArchivoPl.add("true.");
            }
        }
    }

    private static String procesarRegla(String regla) {
        String X;
        String Y;
        String[] regl = regla.split(" ");
        if (regl.length < 1 || regla.equals("\\s")) {
            return "";
        }
            if (regl[0].equals("la")) {
                if (regl[1].equals("orden")) {
                    if (regl[2].equals("es")) {
                        //Se ha aceptado una gramática "La orden es"
                        if (regl[3].equals("quitar") || regl[3].equals("remover") || regl[3].equals("retirar") || regl[3].equals("quita") || regl[3].equals("retira")) {
                            //cómo procesar el quitado
                            if (regl[4].equals("el")) {
                                if (regl[5].equals("objeto")) {
                                    //agregar el nombre de la variable que venga en la regla por si acaso
                                    X = regl[6];
                                    //continua la interpretación buscando un sobre o de
                                    if (regl[7].equals("sobre") || regl[7].equals("de")) {
                                        if (regl[8].equals("el")) {
                                            if (regl[9].equals("objeto")) {
                                                Y = regl[10];
                                                //agregar eso a prolog
                                                return "quitar(" + X + "," + Y + "):-";
                                                //quitar(Y,X):-
                                            }
                                        }
                                    } else if (regl[7].equals("del")) {
                                        if (regl[8].equals("objeto")) {
                                            Y = regl[9];
                                            return "quitar(" + X + "," + Y + "),";
                                        }
                                    }
                                }
                            }
                        } else if (regl[3].equals("poner") || regl[3].equals("colocar") || regl[3].equals("pon") || regl[3].equals("coloca") || regl[3].equals("crea") || regl[3].equals("crear")) {
                            //cómo poner el objeto
                            return "aquí va la línea que crea el objeto.";
                        }
                    }
                }
            } else if (regl[0].equals("el")) {
                if (regl[1].equals("objeto")) {
                    X = regl[2];
                    if (regl[3].equals("está")) {
                        if (regl[4].equals("encima") || regl[4].equals("sobre")) {
                            if (regl[5].equals("del") || regl[5].equals("de")) {
                                if (regl[6].equals("objeto")) {
                                    Y = regl[7];
                                    //regresar la linea en prolog
                                    return "sobre(" + X + "," + Y + ")";
                                }
                            }
                        } else if (regl[4].equals("vacío") || regl[4].equals("solo")) {
                            return "sobre(nada," + X + ")";
                        }
                    }
                }
            } else if (regl[0].equals("poner")) {
                if (regl[1].equals("el")) {
                    if (regl[2].equals("objeto")) {
                        X = regl[3];
                        if (regl[4].equals("en")) {
                            if (regl[5].equals("el")) {
                                if (regl[6].equals("piso")) {
                                    return "assert(" + X + ",piso)";
                                }
                            }
                        }
                    }
                }

            } else if (regl[0].equals("limpiar")) {
                if (regl[1].equals("el")) {
                    if (regl[2].equals("objeto")) {
                        Y = regl[3];
                        return "retract(sobre(X," + Y + "), assert(sobre(nada," + Y + ")";
                    }
                }
        }
        return null;
    }

    private static String procesarLinea(String linea) {
        //asegurándome de que no haya espacios antes de la primer palabra o después de la última
        //linea = eliminarEspaciosAlInicioYFinal(linea);
        //linea = "'" + linea + "'.";
        return linea;
    }

    private static String eliminarEspaciosAlInicioYFinal(String linea) {
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
                } else {
                    nuevaString += "";
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
    private static ArrayList cargarUnArchivoDeTexto() throws IOException {
        ArrayList<String> archivoDeTextoPorLineas;
        File archivoDelCodigo = new File("/Users/xtrs84zk/Documents/AI/AI-PrologBased/expertSystem/escenario.txt");
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
