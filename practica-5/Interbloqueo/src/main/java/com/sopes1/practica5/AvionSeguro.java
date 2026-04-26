package com.sopes1.practica5;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/*
 * Representa un avion sin deadlock
 *
 * como evita el interbloqueo:
 *  1. ORDEN GLOBAL DE ADQUISICION: las acciones siempre se reservan en el mismo orden
 *     IZQUIERDA, GENERAL , DERECHA lo que rompe la espera circular
 * 2. trylock con TIMEOUT si el avion no logra reservar las secciones que necesita
 *    libera las que ya tomo y reintenta, asi se rompe la retencion y espera
 */
public class AvionSeguro implements Runnable {

    // Identificador del avion
    private final String nombre;

    // Bandera para saber si es despegue o aterrisaje
    private final boolean enTierra;

    // Referencia al aeropuerto para acceder a las pistas
    private final Aeropuerto aeropuerto;

    private final Random random = new Random();

    public AvionSeguro(String nombre, boolean enTierra, Aeropuerto aeropuerto) {
        this.nombre = nombre;
        this.enTierra = enTierra;
        this.aeropuerto = aeropuerto;
    }

    @Override
    public void run() {
        try {
            if (enTierra){
                ejecutarDespegueSeguro();
            } else {
                ejecutarAterrizajeSeguro();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Despegue seguro reserva las 3 secciones en orden
    // si no logra todas libera y reintenta
    private void ejecutarDespegueSeguro() throws InterruptedException {
        boolean despegueExitoso = false;

        while (!despegueExitoso) {

            // selecciona una pista aleatoria
            int numeroPista = random.nextInt(2) + 1;

            // obtiene las secciones de la pista en orden
            List<SeccionPista> seccionesOrdenadas = obtenerSeccionesEnOrden(numeroPista);

            aeropuerto.actualizarEstado(nombre, "Intentando reservar pista " + numeroPista + " para despegue");

            // Registro de los locks que se adquirieron, para liberarlos si se requiere
            List<ReentrantLock> locksAdquiridos = new ArrayList();
            boolean todosAdquiridos = true;

            try{
                // intentar adquirir los 3 locks en orden
                for (SeccionPista seccionPista : seccionesOrdenadas) {
                    ReentrantLock lock = seccionPista.getLock();
                    if (lock.tryLock(1, TimeUnit.SECONDS)) {
                        locksAdquiridos.add(lock);
                        seccionPista.setAvionOcupante(nombre);
                        aeropuerto.actualizarEstado(nombre, " Reservo " + seccionPista);
                    } else {
                        // no se puede obtener liberar locks
                        todosAdquiridos = false;
                        aeropuerto.actualizarEstado(nombre, " No pudo reservar " + seccionPista + " liberando y reintentando");
                        break;
                    }
                }


                if (todosAdquiridos) {
                    // ETAPA 1: simula el tiempo de preparacion antes del despegue
                    aeropuerto.actualizarEstado(nombre, " Preparando despegue en pista " + numeroPista);
                    Thread.sleep(500 + random.nextInt(1000));

                    // ETAPA 2: realizar el despegue
                    aeropuerto.actualizarEstado(nombre, "DESPEGADO por pista " + numeroPista);
                    Thread.sleep(500);


                    // marcar las secciones como libres
                    for (SeccionPista seccionPista : seccionesOrdenadas) {
                        seccionPista.setAvionOcupante(null);
                    }
                    aeropuerto.actualizarEstado(nombre, " EN EL AIRE - pistas liberadas");
                    despegueExitoso = true;
                }
            }finally {

                // libera los locks que se adquirieron
                for (ReentrantLock lock : locksAdquiridos) {
                    lock.unlock();
                }
            }
            if (!despegueExitoso) {
                // esperar antes de reintentar para evitar livelock
                Thread.sleep(200 + random.nextInt(300));
            }
        }
    }

    // Reserva toda la pista en orden con trylock y timeout
    private void ejecutarAterrizajeSeguro() throws InterruptedException {
        boolean aterrizajeExitoso = false;

        while (!aterrizajeExitoso) {
            int numeroPista = random.nextInt(2) + 1;
            List<SeccionPista> seccionesOrdendas = obtenerSeccionesEnOrden(numeroPista);

            aeropuerto.actualizarEstado(nombre, "Aproximandose intentando aterrizar en la pista " + numeroPista);

            List<ReentrantLock> locksAdquiridos = new ArrayList();
            boolean todosAdquiridos = true;

            try {
                // intenta adquirir los 3 locks en orden
                for (SeccionPista seccionPista : seccionesOrdendas) {
                    ReentrantLock lock = seccionPista.getLock();
                    if (lock.tryLock(1, TimeUnit.SECONDS)) {
                        locksAdquiridos.add(lock);
                        seccionPista.setAvionOcupante(nombre);
                        aeropuerto.actualizarEstado(nombre, " Reservo " + seccionPista);
                    } else {
                        // no se puede obtener el lock libera y reitenta
                        todosAdquiridos = false;
                        aeropuerto.actualizarEstado(nombre, "Pista " + seccionPista + " ocupada, reintentando");
                        break;
                    }
                }

                if (todosAdquiridos) {

                    // ETAPA 1: simula tiempo de preparacion para el despegue
                    aeropuerto.actualizarEstado(nombre, "ATERRIZANDO en pista" + numeroPista);
                    Thread.sleep(700);

                    for (SeccionPista seccionPista : seccionesOrdendas) {
                        seccionPista.setAvionOcupante(null);
                    }
                    aeropuerto.actualizarEstado(nombre, " ATERRIZAJE COMPLETO - pistas liberadas");
                    aterrizajeExitoso = true;
                }
            } finally {
                for (ReentrantLock lock : locksAdquiridos) {
                    lock.unlock();
                }
            }
            if (!aterrizajeExitoso) {
                Thread.sleep(200 + random.nextInt(300));
            }
        }
    }

    // Devuelve las secciones de la pista en un orden global
    private List<SeccionPista> obtenerSeccionesEnOrden(int numeroPista) throws InterruptedException {
        List<SeccionPista> ordenadas  = new ArrayList();

        ordenadas.add(aeropuerto.getSeccion(numeroPista, Aeropuerto.IZQUIERDA));
        ordenadas.add(aeropuerto.getSeccion(numeroPista, Aeropuerto.GENERAL));
        ordenadas.add(aeropuerto.getSeccion(numeroPista, Aeropuerto.DERECHA));
        return ordenadas;
    }
}
