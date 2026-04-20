package com.sopes1.ejercicio1;

/*
 * PROBLEMA: Estacionamiento Inteligente
 *
 * Simulación de un estacionamiento con:
 * - 20 espacios totales
 * - 5 espacios VIP (solo carros)
 * - Máximo 3 camiones simultáneos
 *
 * Se utilizan semáforos para controlar el acceso concurrente.
 */

public class Main {

    public static void main(String[] args) {

        /*
         * Se crean:
         * - 50 carros
         * - 10 camiones
         */

        Thread[] vehiculos = new Thread[60];

        // Crear carros
        for (int i = 0; i < 50; i++) {
            vehiculos[i] = new Vehiculo("carro");
        }

        // Crear camiones
        for (int i = 50; i < 60; i++) {
            vehiculos[i] = new Vehiculo("camion");
        }

        // Iniciar todos los hilos
        for (Thread v : vehiculos) {
            v.start();
        }

        // Esperar a que todos terminen
        for (Thread v : vehiculos) {
            try {
                v.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Simulación finalizada.");
    }
}