package com.sopes1.ejercicio1;

import java.util.Random;

public class Vehiculo extends Thread {
    private final String tipo;
    private final Random random = new Random();

    public Vehiculo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public void run() {

        try {

            // El vehículo entra a la fila de espera
            Estacionamiento.mutex.acquire();
            Estacionamiento.esperando++;
            Estacionamiento.mutex.release();

            boolean entro = false;

            // Tiempo máximo de espera (3 segundos)
            long inicio = System.currentTimeMillis();

            while (!entro && (System.currentTimeMillis() - inicio) < 3000) {

                if (tipo.equals("carro")) {

                    // Intentar adquirir un espacio total
                    if (Estacionamiento.espacios.tryAcquire()) {

                        // Intentar usar VIP si hay disponible
                        boolean usoVIP = Estacionamiento.vip.tryAcquire();

                        Estacionamiento.mutex.acquire();
                        Estacionamiento.carrosDentro++;
                        Estacionamiento.esperando--;
                        Estacionamiento.mutex.release();

                        Estacionamiento.log("ENTRADA CARRO | Dentro: C="
                                + Estacionamiento.carrosDentro + " T="
                                + Estacionamiento.camionesDentro);

                        entro = true;

                        // Permanecer dentro (tiempo aleatorio)
                        Thread.sleep(random.nextInt(3000) + 1000);

                        // Salida
                        Estacionamiento.espacios.release();
                        if (usoVIP) Estacionamiento.vip.release();

                        Estacionamiento.mutex.acquire();
                        Estacionamiento.carrosDentro--;
                        Estacionamiento.mutex.release();

                        Estacionamiento.log("SALIDA CARRO | Dentro: C="
                                + Estacionamiento.carrosDentro + " T="
                                + Estacionamiento.camionesDentro);
                    }

                } else { // CAMIÓN

                    // Intentar cumplir condiciones:
                    // - 2 espacios disponibles
                    // - cupo de camiones
                    if (Estacionamiento.espacios.availablePermits() >= 2 &&
                            Estacionamiento.camiones.tryAcquire()) {

                        if (Estacionamiento.espacios.tryAcquire(2)) {

                            Estacionamiento.mutex.acquire();
                            Estacionamiento.camionesDentro++;
                            Estacionamiento.esperando--;
                            Estacionamiento.mutex.release();

                            Estacionamiento.log("ENTRADA CAMION | Dentro: C="
                                    + Estacionamiento.carrosDentro + " T="
                                    + Estacionamiento.camionesDentro);

                            entro = true;

                            // Permanecer dentro
                            Thread.sleep(random.nextInt(4000) + 2000);

                            // Salida
                            Estacionamiento.espacios.release(2);
                            Estacionamiento.camiones.release();

                            Estacionamiento.mutex.acquire();
                            Estacionamiento.camionesDentro--;
                            Estacionamiento.mutex.release();

                            Estacionamiento.log("SALIDA CAMION | Dentro: C="
                                    + Estacionamiento.carrosDentro + " T="
                                    + Estacionamiento.camionesDentro);
                        } else {
                            Estacionamiento.camiones.release();
                        }
                    }
                }

                Thread.sleep(200); // pequeña espera antes de reintentar
            }

            // Si no logró entrar desiste
            if (!entro) {
                Estacionamiento.mutex.acquire();
                Estacionamiento.desistieron++;
                Estacionamiento.esperando--;
                Estacionamiento.mutex.release();

                Estacionamiento.log("DESISTE " + tipo.toUpperCase());
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
