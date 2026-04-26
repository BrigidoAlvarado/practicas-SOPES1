package com.sopes1.practica5;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/* Clase principal
 * Permit escoger entre escenario seguro
 * y escenario con deadlock
 */
public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        try{
            System.out.println("=====================================================");
            System.out.println("   SIMULADOR DE AEROPUERTO - PRÁCTICA No. 5");
            System.out.println("   Sistemas Operativos 1");
            System.out.println("=====================================================");

            // Lectura validada de los parámetros de la simulación
            int totalAviones = leerEnteroPositivo(sc, "Ingrese el numero total de aviones a simular: ");
            int avionesTierra = leerEnteroNoNegativo(sc, "Ingrese cantidad de aviones en tierra (despegue): ");
            int avionesAire = leerEnteroNoNegativo(sc, "Ingrese cantidad de aviones en el aire (aterrizaje): ");

            // Validación: la suma debe coincidir con el total
            if (avionesTierra + avionesAire != totalAviones) {
                System.out.println("ERROR: La suma de aviones en tierra y en el aire debe igualar al total.");
                return;
            }

            // Selección del escenario a ejecutar
            System.out.println("\nSeleccione el escenario:");
            System.out.println("  1) Escenario CON DEADLOCK (uso incorrecto de synchronized)");
            System.out.println("  2) Escenario CORRECTO (sin deadlock)");
            int escenario = leerEnteroEnRango(sc, "Opción: ", 1, 2);

            // Crear aeropuerto con 6 seccines de pista
            Aeropuerto aeropuerto = new Aeropuerto();
            List<Thread> hilos = new ArrayList();

            // Creacion de los hilos
            for (int i = 1; i <= avionesTierra; i++) {
                String nombre = "AvionTierra-" + i;
                Runnable avion = (escenario == 1)
                        ? new AvionDeadlock(nombre, true, aeropuerto)
                        : new AvionSeguro(nombre, true, aeropuerto);
                hilos.add(new Thread(avion, nombre));
            }

            for (int i = 1; i <= avionesAire; i++) {
                String nombre = "AvionAire-" + i;
                Runnable avion = (escenario == 1)
                        ? new AvionDeadlock(nombre, false, aeropuerto)
                        : new AvionSeguro(nombre, false, aeropuerto);
                hilos.add(new Thread(avion, nombre));
            }

            // Inicio de los hilos
            System.out.println("\n>>> Iniciando simulacion con " + totalAviones + " aviones <<<\n");
            for (Thread t : hilos) {
                t.start();
            }

            // Esperar a que los hilos terminen
            for (Thread t : hilos) {
                // Le damos un timeout de 15 segundos para detectar deadlock visualmente
                t.join(15000);
            }

            boolean hayBloqueados = false;
            for (Thread t : hilos) {
                if (t.isAlive()) {
                    hayBloqueados = true;
                    System.out.println(">>> Hilo bloqueado (posible deadlock): " + t.getName());
                }
            }

            if (hayBloqueados) {
                System.out.println("\n*** DEADLOCK DETECTADO: el sistema quedo congelado. ***");
                System.out.println("*** Cierre la aplicación manualmente (Ctrl+C). ***");
            } else {
                System.out.println("\n>>> Simulación finalizada correctamente. <<<");
            }

        }  catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("La simulacion fue interrumpida.");
        } catch (Exception e) {
            System.out.println("Ocurrio un error inesperado: " + e.getMessage());
        } finally {
            sc.close();
        }
    }

    public static int leerEnteroPositivo(Scanner sc, String msg){
        while(true){
            System.out.println(msg);
            try{
                int valor = sc.nextInt();
                if ( valor > 0 ) return valor;

                System.out.println("Debe ingresar un numero entero mayor a cero");
            } catch ( InputMismatchException e ){
                System.out.println("Entrada ivalida ingresar otro valor");
                sc.next();
            }
        }
    }

    private static int leerEnteroNoNegativo(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                int valor = sc.nextInt();
                if (valor >= 0) return valor;
                System.out.println("Debe ingresar un numero mayor o igual a cero.");
            } catch (InputMismatchException e) {
                System.out.println("Entrada invalida, debe ingresar un numero entero.");
                sc.next();
            }
        }
    }

    private static int leerEnteroEnRango(Scanner sc, String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje);
            try {
                int valor = sc.nextInt();
                if (valor >= min && valor <= max) return valor;
                System.out.println("Debe ingresar un numero entre " + min + " y " + max + ".");
            } catch (InputMismatchException e) {
                System.out.println("Entrada invalida, debe ingresar un numero entero.");
                sc.next();
            }
        }
    }
}
