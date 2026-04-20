package com.sopes1.ejercicio2;

import java.util.concurrent.Semaphore;

public class Mesa {
    // 5 tenedores (uno entre cada par de estudiantes)
    public static Semaphore[] tenedores = new Semaphore[5];

    // Semáforo para evitar deadlock (máximo 4 estudiantes intentando comer)
    public static Semaphore mesero = new Semaphore(4);

    // Estado de los tenedores (true = libre, false = ocupado)
    public static boolean[] estadoTenedores = new boolean[5];

    // Mutex para impresión y control de estado
    public static Semaphore mutex = new Semaphore(1);

    static {
        for (int i = 0; i < 5; i++) {
            tenedores[i] = new Semaphore(1);
            estadoTenedores[i] = true;
        }
    }

    // Método para imprimir estado
    public static void imprimirEstado(String accion, int id) {
        try {
            mutex.acquire();

            System.out.println("Estudiante " + id + " " + accion);

            System.out.print("Tenedores: ");
            for (int i = 0; i < 5; i++) {
                System.out.print("[T" + i + ": " + (estadoTenedores[i] ? "Libre" : "Ocupado") + "] ");
            }
            System.out.println("\n----------------------------------");

            mutex.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
