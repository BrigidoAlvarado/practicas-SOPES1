package com.sopes1.practica5;

import java.util.concurrent.locks.ReentrantLock;

/*
* Representa una seccion fisica de una pista del aeropuerto
* Cada pista (Norte-Sur y Este-Oeste) esta dividida en 3 secciones
* IZQUIERDA, GENERAL y DERECHA
*
*
 */
public class SeccionPista {

    // Identificador de la pista a la que pertenece esta seccion
    // 1 = Norte-Sur, 2 = Este-Oeste
    private final int numeroPista;

    // Nombre de la seccion IZQUIERDA, GENERAL o DERECHA
    private final String nombreSeccion;

    // Para romper la condición de retención y espera liberando recursos si no logra adquirir todoas
    private final ReentrantLock lock = new ReentrantLock();

    // Indica el avión que tiene reservada la sección
    private volatile String avionOcupante = null;

    // inicializa la pista con un numero y nombre
    public SeccionPista(int numeroPista, String nombreSeccion) {
        this.numeroPista = numeroPista;
        this.nombreSeccion = nombreSeccion;
    }

    // GETTERS
    public int getNumeroPista() {
        return numeroPista;
    }

    public String getNombreSeccion() {
        return nombreSeccion;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public String getAvionOcupante() {
        return avionOcupante;
    }


    // Marca la sección como ocupada por avión
    public void setAvionOcupante(String avionOcupante) {
        this.avionOcupante = avionOcupante;
    }

    // Para una impresión legible
    @Override
    public String toString(){
        return "Pista" + numeroPista + "-" + nombreSeccion;
    }
}
