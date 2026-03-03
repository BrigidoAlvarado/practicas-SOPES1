//
// Created by brigido on 3/3/26.
//
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>

/// Saldo de la cuenta GTQ
int saldoCuenta = 100;

/// ïndices para la información de los hilos
int cantidadTransaccionIndx = 0;
int tiempoEsperaIndx = 1;

/// Información de los hilos
int const infoHilo[4][2] = {
    // 202230251
    {3, 1,},
    {0+2, 2},
    {2,  1},
    {5,  2}
};

void *calculoTransacción( void *arg) {
    int tmpResultado = 0;
    int idHilo  = *((int *) arg);
    free(arg);

    for (int i = 0; i < 20; i++) {
        // Espererar antes de la transacción
        sleep(infoHilo[idHilo][tiempoEsperaIndx]);

        if (idHilo < 2) {
            // Depositos
            tmpResultado = saldoCuenta + infoHilo[idHilo][cantidadTransaccionIndx];
            saldoCuenta = tmpResultado;
            printf("Nuevo deposito, cantidad:%d saldo actual:%d\n", infoHilo[idHilo][cantidadTransaccionIndx], saldoCuenta);
        } else {
            // Retiros
            tmpResultado = saldoCuenta - infoHilo[idHilo][cantidadTransaccionIndx];
            if (tmpResultado < 0) {
                printf("\n!! No se puede realizar la transacción, requerido:%d saldo:%d\n", infoHilo[idHilo][cantidadTransaccionIndx], saldoCuenta);
            } else {
                saldoCuenta = tmpResultado;
                printf("Nuevo retiro, cantidad:%d saldo actual:%d\n", infoHilo[idHilo][cantidadTransaccionIndx], saldoCuenta);
            }
        }
    }

    pthread_exit(NULL);
}

int main() {
    pthread_t hilo[4];

    // Creación de hilos
    for (int i = 0; i < 4; i++) {
        int *idHilo = malloc(sizeof(int));
        *idHilo = i;

        if ( pthread_create( &hilo[i], NULL, calculoTransacción, idHilo) != 0 ) {
            printf("\nError al crear el hilo: %d\n", i+1);
            free(idHilo);
        }
    }

    // Espera de hilos
    for (int i = 0; i < 4; i++) {
        if ( pthread_join(hilo[i], NULL) != 0 ) {
            printf("\nError al esperar el hilo: %d\n", i+1);
        }
    }

    printf("\n-----------------------------------------\n");
    printf("Saldo final de la cuenta: %d\n", saldoCuenta);
    printf("-----------------------------------------\n");
}