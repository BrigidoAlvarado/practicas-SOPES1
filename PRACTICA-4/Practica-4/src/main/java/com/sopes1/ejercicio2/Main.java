package com.sopes1.ejercicio2;

/*
 * PROBLEMA: La Mesa
 *
 * 5 estudiantes comparten 5 tenedores.
 * Cada estudiante necesita 2 tenedores para comer.
 *
 * SOLUCIÓN:
 * Se utiliza un semáforo adicional (mesero) para evitar deadlock.
 * Solo 4 estudiantes pueden intentar tomar tenedores al mismo tiempo.
 */

public class Main {

    public static void main(String[] args) {

        /*
         * Se crean 5 estudiantes (hilos)
         */

        Estudiante[] estudiantes = new Estudiante[5];

        for (int i = 0; i < 5; i++) {
            estudiantes[i] = new Estudiante(i);
            estudiantes[i].start();
        }

        // Esperar a que todos terminen
        for (int i = 0; i < 5; i++) {
            try {
                estudiantes[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Todos los estudiantes han terminado.");
    }
}