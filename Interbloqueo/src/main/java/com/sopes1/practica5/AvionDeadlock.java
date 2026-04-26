package com.sopes1.practica5;


import java.util.List;
import java.util.Random;

/*
 * El avion reserva una seccion inicial aleatoria
 * Mantiene la seccion e intenta reservar las secciones restantes en un orden que puede variar
 *
 * Se provoca que distintos aviones adquieran los recursos en orden distinto
 * generando una espera circuloar y por lo tanto un interbloqueo
 */
public class AvionDeadLock implements Runnable {

    // Identificador del avion
    private final String nombre;

    // Bandera para saber si es despegue o aterrisaje
    private final boolean enTierra;

    // Referencia al aeropuerto para acceder a las pistas
    private final Aeropuerto aeropuerto;

    private final Random random = new Random();

    public AvionDeadLock(String nombre, boolean enTierra, Aeropuerto aeropuerto) {
        this.nombre = nombre;
        this.enTierra = enTierra;
        this.aeropuerto = aeropuerto;
    }

    @Override
    public void run(){
        try{
            if(this.enTierra){
                ejecutarDespegueConDeadlock();
            } else {
                ejecutarAterrizajeConDeadLock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Logica de despegue que puede provocar un deadlock (etapa 1 + etapa 2)
    // Etapa 1: el avion escoge una pista y una seccion al azar y la reserva
    // Etapa 2: Intenta reservar las dos secciones restantes de esa pista para despegar
    private void ejecutarDespegueConDeadlock() throws InterruptedException {

        // selceccinoar una pista aletoria
        int numeroPista = random.nextInt(2) + 1;

        // obtiene las seccions de la pista elegida
        List<SeccionPista> seccionesPIsta = aeropuerto.getSeccionesPista(numeroPista);

        // selecciona una seccion incial aleatoria entre las 3
        SeccionPista seccionInicial = seccionesPIsta.get(random.nextInt(seccionesPIsta.size()));

        aeropuerto.actualizarEstado(nombre, "Solicitanto posicion inicial en " + seccionInicial);

        // ETAPA 1: el avion reservar la seccion incial syncronized = exclusion mutua
        synchronized (seccionInicial) {
            seccionInicial.setAvionOcupante(nombre);
            aeropuerto.actualizarEstado(nombre, "Posicionado en " + seccionInicial + " Etapa 1 preparandose");

            // ETAPA 2: intenta reservar las otras manteniedo la primera
            // el orden de adquiscion depende de las lista pero como la seccion fue aleatoria
            // distintos aviones llegan en orden distinto
            for (SeccionPista otra:  seccionesPIsta) {
                if (otra != seccionInicial) {
                    aeropuerto.actualizarEstado(nombre, "Esperando reservar " + otra);

                    // se anidan los syncronized
                    // el avion retiene el primero
                    // el segundo causa el deadlock
                    synchronized (otra) {
                        otra.setAvionOcupante(nombre);
                        aeropuerto.actualizarEstado(nombre, " Reservada " + otra);
                    }
                }
            }

            // si llega si bloqueos simula el despegue
            aeropuerto.actualizarEstado(nombre, "DESPEGADO por pista " + numeroPista);
            Thread.sleep(500);

            // libera las secciones de la pista, el avion ya esta en el aire
            for (SeccionPista seccion:  seccionesPIsta) {
                seccion.setAvionOcupante(null);
            }
            aeropuerto.actualizarEstado(nombre, "EN EL AIRE - pistas liberadas");
        }

    }

    // Logia de aterrizaje tambien crea un deadlock
    // El avion intenta reservar las 3 secciones
    // el orden depende la lista, si se comabina con los avines en tierra pueda generar un deadlock
    private void ejecutarAterrizajeConDeadLock() throws InterruptedException {
        int numeroPista = random.nextInt(2) + 1;
        List<SeccionPista> seccionesPista = aeropuerto.getSeccionesPista(numeroPista);

        aeropuerto.actualizarEstado(nombre, "Aproximandose para aterrizar en pista " + numeroPista);

        // Reserva las secciones de manera anidada, mantiene una y pide la siguiente
        synchronized (seccionesPista.get(0)) {
            seccionesPista.get(0).setAvionOcupante(nombre);
            aeropuerto.actualizarEstado(nombre, "Reservo " + seccionesPista.get(0) + " para aterrizar");
            Thread.sleep(200);

            synchronized (seccionesPista.get(1)) {
                seccionesPista.get(1).setAvionOcupante(nombre);
                aeropuerto.actualizarEstado(nombre, "Reservo " + seccionesPista.get(0) );
                Thread.sleep(200);

                synchronized (seccionesPista.get(2)) {
                    seccionesPista.get(2).setAvionOcupante(nombre);
                    aeropuerto.actualizarEstado(nombre, "ATERRIZADO en pista " + numeroPista );

                    // Libera recursos al terminar el aterrizaje
                    for (SeccionPista seccion:  seccionesPista) {
                        seccion.setAvionOcupante(null);
                    }
                    aeropuerto.actualizarEstado(nombre, "ATERRIZAJE COMPLETO - pistas liberadas");
                }
            }
        }
    }
}
