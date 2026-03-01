
/// Esta mal planteado

// //
// // Created by brigido on 1/3/26.
// //
//
//
// #include <stdio.h>
// #include <stdlib.h>
// #include <pthread.h>
//
//
//
// // Arreglos de strings - FORMA CORRECTA con char*
// char *nombreProductores[] = {
//     "Trigo",
//     "Carne",
//     "Frutas y Verduras"
// };
// char *nombreConsumidores[] = {
//     "Trigo 1" , "Trigo 2",
//     "Carne 1", "Carne 2",
//     "Frutas y Verduras 1", "Frutas y Verduras 2"
// };
//
// // Limites de Almacenamiento
// int limiteAlmacenamiento  = 100;
// int capacidadDeProducir = 15;
// int capacidadDeConsumir = 5;
//
// // Índice Arreglos i
// int trigo = 0;
// int carne = 1;
// int fruteYVerdura = 2;
//
// // Índide de Arreglos j
// int materialDisponible = 0;
// int regisrosAlmacenamientoLleno = 1;
// int regisroStockVacio = 2;
//
// // Registros de Control
// int registrosDeControl[3][3] = {
//     {0, 0, 0}, // Trigo
//     {0, 0, 0}, // Carne
//     {0, 0, 0}  // Frutas y Verduras
// };
//
// pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;
//
// void *consumir(void *arg) {
//     // Obtener el ID del material
//     int idMaterial = *((int *) arg);
//     free(arg);
//     // Liberar el argumento después de usarlo
//
//     printf("\nIniciando Hilo Consumidor %s\n", nombreConsumidores[idMaterial]);
//
//     int *resultado = malloc(sizeof(int));
//     if (resultado == NULL) {
//         printf("Hilo Consumidor %d: Error al asignar memoria\n", idMaterial);
//         pthread_exit(NULL);
//     }
//
//     *resultado = registrosDeControl[idMaterial][materialDisponible] - capacidadDeConsumir;
//     return resultado;
// }
//
// void *producir(void *arg) {
//
//     // Obtener el ID del material (CORRECCIÓN: usar malloc para pasar por referencia segura)
//     int idMaterial = *((int *) arg);
//     free(arg);
//     // Liberar el argumento después de usarlo
//
//     printf("\nIniciando Hilo Productor %s\n", nombreProductores[idMaterial]);
//
//     int *resultado = malloc(sizeof(int));
//     if (resultado == NULL) {
//         printf("Hilo Productor %d: Error al asignar memoria\n", idMaterial);
//         pthread_exit(NULL);
//     }
//
//     *resultado = registrosDeControl[idMaterial][materialDisponible] + capacidadDeProducir;
//     return resultado;
// }
//
// void *calculoProductor(int resultado, int idMaterial) {
//     pthread_mutex_lock(&mutex);
//
//     if (resultado > limiteAlmacenamiento) {
//         registrosDeControl[idMaterial][regisrosAlmacenamientoLleno]++;
//         printf("\nHilo Productor %s: Almacenamiento lleno. No se puede producir más.\n", nombreProductores[idMaterial]);
//     } else {
//         registrosDeControl[idMaterial][materialDisponible] = resultado;
//         printf("\nHilo Productor %s: Material Disponible = %d\n", nombreProductores[idMaterial], resultado);
//     }
//
//     pthread_mutex_unlock(&mutex);
//     pthread_exit(NULL);
// }
//
// void *calculoConsumidor(int resultado, int idMaterial) {
//     pthread_mutex_lock(&mutex);
//
//     if (resultado < 0) {
//         registrosDeControl[idMaterial][regisroStockVacio]++;
//         printf("\nHilo Consumidor %s: Stock vacío. No se puede consumir más.\n", nombreConsumidores[idMaterial]);
//     } else {
//         registrosDeControl[idMaterial][materialDisponible] = resultado;
//         printf("\nHilo Consumidor %s: Material Disponible = %d\n", nombreConsumidores[idMaterial], resultado);
//     }
//
//     pthread_mutex_unlock(&mutex);
//     pthread_exit(NULL);
// }
//
// int main() {
//
//     void *retorno;
//
//     // Hilos
//     pthread_t hilosProductores[3];
//     pthread_t hilosConsumidores[6];
//
//     ///////////////////////////////////////
//     /// Crear Hilos Productores
//     ///////////////////////////////////////
//     for (int i = 0; i < 3; i++) {
//         // Pasar copia de i, no dirección de i variable
//         int *idMaterial = malloc(sizeof(int));
//         *idMaterial = i;
//
//         if (pthread_create(&hilosProductores[i], NULL, producir, idMaterial) != 0) {
//             printf("\nError al crear el hilo: %s\n", nombreProductores[i]);
//             free(idMaterial);
//         }
//     }
//
//     ///////////////////////////////////////
//     /// Esperar a que los hilos productores terminen
//     ///////////////////////////////////////
//     for (int i = 0; i < 3; i++) {
//         if (pthread_join(hilosProductores[i], &retorno) != 0) {
//             printf("\nError al esperar el hilo: %s\n", nombreProductores[i]);
//         } else {
//             if (retorno != NULL) {
//                 // CORRECCIÓN: retorno es int*, desreferenciarlo
//                 int resultado = *((int *)retorno);
//                 calculoProductor(resultado, i);
//                 free(retorno);
//             } else {
//                 printf("\nHilo Productor %s: Error al obtener el resultado\n", nombreProductores[i]);
//             }
//         }
//     }
//
//     ///////////////////////////////////////
//     /// Crear Hilos Consumidores
//     ///////////////////////////////////////
//     for (int i = 0; i < 6; i++) {
//         int *idMaterial = malloc(sizeof(int));
//         *idMaterial = i / 2;
//
//         if (pthread_create(&hilosConsumidores[i], NULL, consumir, idMaterial) != 0) {
//             printf("\nError al crear el hilo: %s\n", nombreConsumidores[i]);
//             free(idMaterial);
//         }
//     }
//
//     ///////////////////////////////////////
//     /// Esperar a que los hilos consumidores terminen
//     ///////////////////////////////////////
//     for (int i = 0; i < 6; i++) {
//         if (pthread_join(hilosConsumidores[i], &retorno) != 0) {
//             printf("\nError al esperar el hilo: %s\n", nombreConsumidores[i]);
//         } else {
//             if (retorno != NULL) {
//                 int resultado = *((int *)retorno);
//                 calculoConsumidor(resultado, i / 2);
//                 free(retorno);
//             } else {
//                 printf("\nHilo Consumidor %s: Error al obtener el resultado\n", nombreConsumidores[i]);
//             }
//         }
//     }
//
//     printf("\nTodos los hilos han terminado.\n");
//
//     for (int i = 0; i < 3; i++) {
//         printf("\nMaterial: %s\n", nombreProductores[i]);
//         for (int j = 0; j < 3; j++) {
//             if (j == materialDisponible) {
//                 printf("Material Disponible: %d\n", registrosDeControl[i][j]);
//             } else if (j == regisrosAlmacenamientoLleno) {
//                 printf("Almacenamiento Lleno: %d\n", registrosDeControl[i][j]);
//             } else if (j == regisroStockVacio) {
//                 printf("Stock Vacío: %d\n", registrosDeControl[i][j]);
//             }
//         }
//     }
//
//     pthread_mutex_destroy(&mutex);
//     return 0;
// }