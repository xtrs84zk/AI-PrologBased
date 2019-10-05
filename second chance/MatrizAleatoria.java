import java.util.Random;
import java.util.Scanner;

public class MatrizAleatoria {
    private static int[][] matriz;

    public static void main(String[] args) {
        Random rd = new Random();
        Scanner sc = new Scanner(System.in);
        System.out.println("Ingresar el numero de COLUMNAS");
        int columnas = sc.nextInt();
        System.out.println("Ingresar el numero de FILAS");
        int filas = sc.nextInt();
        matriz = new int[filas][columnas];

        for (int f = 0; f < matriz.length; f++)
        {
            java.util.Arrays.setAll(matriz[f], c -> rd.nextInt(99999));

        }

        for(int f=0;f<matriz.length;f++) {
            for(int c=0;c<matriz[f].length;c++) {
                System.out.print(matriz[f][c]+" ");
            }
            System.out.println();
        }


    }
}