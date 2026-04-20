package com.sopes1.ejercicio1;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.Semaphore;

public class Estacionamiento
{
    // Semáforos
    static Semaphore espacios = new Semaphore(20); // espacios totales
    static Semaphore vip = new Semaphore(5);       // espacios VIP
    static Semaphore camiones = new Semaphore(3);  // máximo 3 camiones
    static Semaphore mutex = new Semaphore(1);     // exclusión mutua

    // Variables compartidas
    static int carrosDentro = 0;
    static int camionesDentro = 0;
    static int esperando = 0;
    static int desistieron = 0;

    // Método para escribir logs
    public static void log(String mensaje) {
        try (FileWriter fw = new FileWriter("log-ejercicio-1.txt", true)) {
            fw.write(LocalDateTime.now() + " - " + mensaje + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
