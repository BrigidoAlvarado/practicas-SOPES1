package com.sopes1.practica5;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Representa al aeropuerto y sus 2 pistas
 * Tiene metodos para imprimir el estado del sistema
 *
 * Cada pista tiene 3 secciones: IZQUIERDA, GENERAL y DERECHA
 * Existen 6 recursos en total que los aviones intentan reservar
 */
public class Aeropuerto {

    // Nombres de las secciones
    public static final String IZQUIERDA = "IZQUIERDA";
    public static final String GENERAL = "GENERAL";
    public static final String DERECHA = "DERECHA";

    // Lista de 6 secciones 3 por cada una de las 2 pistas
    private final List<SeccionPista> secciones = new ArrayList<>();

    // Mapa para registrar el estado actual de los avines
    // Claves (clave: nombre_avion, valor: estado)
    // Se usa concurrent map porque lo modifican varios hilos a la vez
    private final Map<String, String> estadoAviones = new ConcurrentHashMap<>();

    // Incializar las secciones por cada una de las 2 pistas
    public Aeropuerto() {
        for (int pista = 1; pista <= 2; pista++) {
            secciones.add( new SeccionPista(pista, IZQUIERDA));
            secciones.add( new SeccionPista(pista, GENERAL));
            secciones.add( new SeccionPista(pista, DERECHA));
        }
    }

    // Devuelve las 3 secciones de una pista especifica
    public List<SeccionPista> getSeccionesPista(int numeroPista) {
        List<SeccionPista> resultado = new ArrayList<>();
        for (SeccionPista seccion : secciones) {
            if(seccion.getNumeroPista() == numeroPista){
                resultado.add(seccion);
            }
        }
        return resultado;
    }

    // Obtiene una seccion especifica por numero de pista y nombre
    public SeccionPista getSeccion(int numeroPista, String nombreSeccion) {
        for (SeccionPista seccion : secciones) {
            if(seccion.getNumeroPista() == numeroPista && seccion.getNombreSeccion().equals(nombreSeccion)){
                return seccion;
            }
        }
        return null;
    }

    // Actualiza el estado de una avion y reimprime el estado global
    // EL bloque sincronizado garantiza que las impresiones no se intercalen
    public synchronized void actualizarEstado(String avion, String estado){
        estadoAviones.put(avion, estado);
        imprimmirEstadoGlobal(avion, estado);
    }

    // Imprime el estado actual de los aviones y de las secciones de la pista
    private void  imprimmirEstadoGlobal(String avionEvente, String evento){
        StringBuilder sb = new StringBuilder();
        sb.append("\n=============== EVENTO: ").append(avionEvente)
                .append(" -> ").append(evento).append(" =============\n");

        sb.append(">> Estado de Aviones:\n");
        for (Map.Entry<String, String> entry : estadoAviones.entrySet()) {
            sb.append("   - ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        sb.append(">> Estado de Pistas:\n");
        for (SeccionPista s : secciones) {
            String ocupante = s.getAvionOcupante();
            sb.append("   - ").append(s.toString()).append(": ")
                    .append(ocupante == null ? "LIBRE" : "OCUPADA por " + ocupante).append("\n");
        }
        sb.append("=====================================================\n");
        System.out.print(sb.toString());
    }
}
