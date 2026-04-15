//
// Created by brigido on 30/3/26.
//

// Ejercicio 1 - Sincronización Forzada
// Simula un sistema de pedidos de dos sucursales que se turnan
// mediante un sistema de turnos.
// Demuestra la falla crítica del algoritmo 1:
// Si S1 deja de operar S2 queda bloqueada indefinidamente.

#include <stdio.h>
#include <pthread.h>
#include <unistd.h>
#include <stdbool.h>

// Variable compartida que controla el turno activo:
// turno = 0 -> le corresponde operar a S1
// turno = 1 -> le corresponde operar a S2
int turno = 0;


// Número de operaciones que realiza cada sucursal
// S1 hace menos para provocar el bloqueo en S2
const int ITER_S1 = 3;
const int ITER_S2 = 5;


// Hilo: sucursal 1:
// Representa a la sucursal 1 realiza pedidos
// Entra en mantenimiento para bloquear a la sucursal 2
void *sucursal1(void *arg) {
    for (int i = 0; i < ITER_S1; i++) {
        printf("S1: Quiere hacer pedido (iteración %d)\n", i);

        // Espera, S1 solo puede operar cuando turno = 0
        // El sistema le asigna turno
        while (turno != 0);

        // S1 realiza pedidos
        printf("S1: Procesando Pedido...\n");
        sleep(1);

        // Finalizar operacion y habilitar turno de S2
        printf("S1: Finaliza y cede turno a S2...\n\n");

        turno = 1;
    }


    // Al terminar las iteraciones S1 entra en mantenimiento
    // Bloquea el turno de S2
    printf("=============================================\n");
    printf("     S1: EN MANTENIMIENTO (YA NO OPERA)\n");
    printf("=============================================\n");

    return NULL;
};

// Hilo Sucursal 2:
// Representa a la sucursal 2
// Realiza pedidos en cada uno de sus iteracioes
// Si esta bloqueda envia una advertencia y no puede continuar
void *sucursal2(void *arg) {

    // Bandera mostrar el mensaje de aviso 1 vez
    bool aviso = false;

    for (int i = 0; i < ITER_S2; i++) {
        printf("S2: Quiere hacer pedido (iteración %d)\n", i);

        // Espera activa S2 solo hace pedidos cuando turno = 1
        while ( turno != 1 ) {
            if ( i >= ITER_S1 && !aviso ) {
                printf("\nS2 BLOQUEADA: S1 no cede el turno\n\n");
                aviso = true;
            }
        }

        // S2 realiza pedidos
        printf("S2: Procesando Pedido...\n");
        sleep(1);

        // S2 Finaliza su operación y habilita el turno de S1
        printf("S2: Finaliza y cede turno a S1\n\n");
        turno = 0;
    }

    // S2 Termina sus operaciones
    printf("=================================\n");
    printf("S2: TERMINO SUS OPERACIONES\n");
    printf("=================================\n");

    return NULL;
}


// Main
// Inicializa los hilos de las sucursales
// Lanza los hilos de las sucursales
// Espera a que los hilos terminen
int main() {
    pthread_t t1, t2;

    printf("=== SINCRONIZACION: SINCRONIZACION FORZADA ===\n");

    // Crear hilo para la sucursal 1 inicia turnos
    pthread_create(&t1, NULL, sucursal1, NULL);

    // Crear el hilo para la sucursal 2 espera turno
    pthread_create(&t2, NULL, sucursal2, NULL);

    // Esperar finalización de hilos
    pthread_join(t1, NULL);
    pthread_join(t2, NULL);

    return 0;
}