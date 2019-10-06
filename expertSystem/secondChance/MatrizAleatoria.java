package secondChance;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;


public class MatrizAleatoria {
    private static int[][] matriz;

    public static void main(String[] args) {
        Random rd = new Random();
        Scanner sc = new Scanner(System.in);
        String resultadoParcial = "";
        String resultadoTotalMayor = "", resultadoTotalMenor = "";

        /*System.out.println("Ingresar el numero de COLUMNAS");
        int columnas = sc.nextInt();
        System.out.println("Ingresar el numero de FILAS");
        int filas = sc.nextInt();*/


        System.out.print("Ingrese la cantidad de elementos por lado: ");
        int cantidadDeElementosPorLado = sc.nextInt();


        matriz = new int[cantidadDeElementosPorLado][cantidadDeElementosPorLado];

        for (int f = 0; f < matriz.length; f++) {
            java.util.Arrays.setAll(matriz[f], c -> rd.nextInt(99999));
        }

        //Imprimiendo la matriz
        System.out.println();
        System.out.println(Arrays.deepToString(matriz).replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));


        //Realizando la suma de cada columna
        System.out.println();
        int mayorResultado = sumarColumna(0);
        int menorResultado = sumarColumna(0);
        for (int i = 0; i < matriz.length; i++) {
            //método burbuja
            if (mayorResultado < sumarColumna(i)) {
                mayorResultado = sumarColumna(i);
                resultadoTotalMayor = "La columna con mayor suma es " + i + " con " + sumarColumna(i) + " por total. ";
            }
            //burbuja inversa
            if (menorResultado > sumarColumna(i)) {
                menorResultado = sumarColumna(i);
                resultadoTotalMenor = "La columna con menor suma es #" + i + " con " + sumarColumna(i) + " por total. ";
            }
            resultadoParcial += "La suma en la columna #" + i + " es : " + sumarColumna(i) + "\n";
        }


        //Mostrando los resultados
        //System.out.println(resultadoParcial);
        System.out.println(resultadoTotalMenor);
        //System.out.println(resultadoTotalMayor);
    }

    /**
     * Método que suma una columna de la matriz global
     *
     * @param i que es la posición en que se encuentra la columna
     * @return sumaColumna que es la sumatoria de la columna actual
     */
    private static int sumarColumna(int i) {
        int sumaColumna = 0;
        for (int j = 0; j < matriz.length; j++) {
            sumaColumna += matriz[j][i];
        }
        return sumaColumna;
    }
}