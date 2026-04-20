package com.sopes1.ejercicio2;

import java.util.Random;

public class Estudiante extends Thread {
    private final int id;
    private final int izquierda;
    private final int derecha;
    private final Random random = new Random();

    public Estudiante(int id) {
        this.id = id;
        this.izquierda = id;
        this.derecha = (id + 1) % 5;
    }

    @Override
    public void run() {

        try {

            for (int i = 0; i < 3; i++) {

                // Estudiar
                Mesa.imprimirEstado("está estudiando", id);
                Thread.sleep(random.nextInt(1000) + 500);

                // Pedir permiso al mesero
                Mesa.mesero.acquire();

                // Tomar tenedores
                Mesa.tenedores[izquierda].acquire();
                Mesa.estadoTenedores[izquierda] = false;

                Mesa.tenedores[derecha].acquire();
                Mesa.estadoTenedores[derecha] = false;

                Mesa.imprimirEstado("está COMIENDO", id);

                // Comer
                Thread.sleep(random.nextInt(1000) + 1000);

                // Soltar tenedores
                Mesa.tenedores[izquierda].release();
                Mesa.estadoTenedores[izquierda] = true;

                Mesa.tenedores[derecha].release();
                Mesa.estadoTenedores[derecha] = true;

                Mesa.imprimirEstado("terminó de comer", id);

                // Liberar al mesero
                Mesa.mesero.release();
            }

            Mesa.imprimirEstado("ha terminado sus 3 comidas y se retira", id);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
