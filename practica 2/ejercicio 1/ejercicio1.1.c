//
// Created by brigido on 1/3/26.
//
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;

/////////////////////////////////////////////////
/// Índices para los nombres de los hilos
/////////////////////////////////////////////////
#define productorTrigoIndex 0
#define productorCarneIndex 1
#define productorFrutasVerdurasIndex 2
#define consumidorTrigo1Index 3
#define consumidorTrigo2Index 4
#define consumidorCarne1Index 5
#define consumidorCarne2Index 6
#define consumidorFrutasVerduras1Index 7
#define consumidorFrutasVerduras2Index 8

char const *nombreHilo[] = {
    "Productor Trigo",
    "Productor Carne",
    "Productor Frutas y Verduras",
    "Consumidor Trigo 1", "Consumidor Trigo 2",
    "Consumidor Carne 1", "Consumidor Carne 2",
    "Consumidor Frutas y Verduras 1",
    "Consumidor Frutas y Verduras 2"
};

/////////////////////////////////////////
/// Variables de Control
/////////////////////////////////////////

char const *nombreMaterial[] = {
    "Trigo",
    "Carne",
    "Frutas y Verduras"
};

char const *nombreRegistro[] = {
    "Stock en boveda",
    "Registro de almacenamiento lleno",
    "Registro de stock vacío",
    "Capacidad de almacenamiento",
    "Total producido",
    "Total consumido"
};

int const capacidadDeProducir = 60,
    capacidadDeConsumir = 40;

#define regTrigo 0
#define regCarne 1
#define regFrutasVerduras 2

#define stockActualBoveda 0
#define registroAlmacenamientoLleno 1
#define registroStockVacio 2
#define capacidadAlmacenamiento 3
#define totalProduccido 4
#define totalConsumido 5

int registrosDeControl[3][6] = {
    {0, 0, 0, 0, 0, 0}, // Trigo
    {0, 0, 0, 0, 0, 0}, // Carne
    {0, 0, 0, 0, 0, 0}  // Frutas y Verduras
};

void *calculoZonaCritica( void *arg) {
    int idHilo  = *((int *) arg);
    free(arg);

    pthread_mutex_lock(&mutex);
    if (idHilo < 3) {

        // Calculo Hilo Productor
        int tmpResultado = capacidadDeProducir + registrosDeControl[idHilo][stockActualBoveda];

        if ( tmpResultado > registrosDeControl[idHilo][capacidadAlmacenamiento]) {

            printf("\nLa boveda de %s está llena\n", nombreMaterial[idHilo]);
            registrosDeControl[idHilo][registroAlmacenamientoLleno]++;
        } else {

            registrosDeControl[idHilo][stockActualBoveda] = tmpResultado;
            registrosDeControl[idHilo][totalProduccido] += capacidadDeProducir;
        }

    } else {
        // Calculo Hilo Consumidor
        int idMaterial = -1;

        switch (idHilo) {
            case consumidorTrigo1Index:
            case consumidorTrigo2Index:
                idMaterial = regTrigo;
                break;
            case consumidorCarne1Index:
            case consumidorCarne2Index:
                idMaterial = regCarne;
                break;
            case consumidorFrutasVerduras1Index:
            case consumidorFrutasVerduras2Index:
                idMaterial = regFrutasVerduras;
                break;
        }

        int tmpResultado = registrosDeControl[idMaterial][stockActualBoveda] - capacidadDeConsumir;

        if (tmpResultado < 0) {

            printf("\nLa boveda de %s está vacía\n", nombreMaterial[idMaterial]);
            registrosDeControl[idMaterial][registroStockVacio]++;
        } else {

            registrosDeControl[idMaterial][stockActualBoveda] = tmpResultado;
            registrosDeControl[idMaterial][totalConsumido] += capacidadDeConsumir;
        }

    }
    pthread_mutex_unlock(&mutex);

    pthread_exit(NULL);
}

int main() {

    // Pedir al usuario la capacidad de almacenamiento para cada material
    for (int id = 0; id < 3; id++) {
        printf("Ingrese la capacidad de almacenamiento para %s: ", nombreHilo[id]);
        scanf("%d", &registrosDeControl[id][capacidadAlmacenamiento]);
    }

    pthread_t hilos[9];
    // creacion de hilos
    for (int i = 0; i < 9; i++) {
        int *idHilo = malloc(sizeof(int));
        *idHilo = i;

        if ( pthread_create( &hilos[i], NULL, calculoZonaCritica, idHilo) != 0 ) {
            printf("\nError al crear el hilo: %s\n", nombreHilo[i]);
            free(idHilo);
        }
    }

    // espera de hilos
    for (int i = 0; i < 9; i++) {

        if ( pthread_join(hilos[i], NULL) != 0 ) {
            printf("\nError al esperar el hilo: %s\n", nombreHilo[i]);
        }
    }

    // reporte final
    printf("----------------------------------------------\n");
    printf("               REPORTE FINAL\n");
    printf("----------------------------------------------\n");

    for (int material = 0; material < 3; material++) {
        printf("----------------------------------------------\n");
        printf("%d) Material: %s\n", material+1, nombreMaterial[material]);
        printf("----------------------------------------------\n");

        for (int tipoRg = 0; tipoRg < 6; tipoRg++) {
            printf("%s: %d\n", nombreRegistro[tipoRg], registrosDeControl[material][tipoRg]);
        }
        printf("-------------------------------------------\n\n");
    }

    pthread_mutex_destroy(&mutex);
    return 0;
}