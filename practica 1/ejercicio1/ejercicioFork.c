#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

int main(int argc, char *argv[]) {

    //variables para tipo cambio
    float tipoCambioDollar = 7.75;
    float tipoCambioPeso = 0.39;
    float tipoCambioEuro = 8.4;
    float test = 12;

    // Validacion de parametros
    if (argc != 2) {
        printf("Uso: %s <Cant Quetzales> \n", argv[0]);
        return 1;
    }

    // Obtener la cantidad de quetzales ingresada
    float cantidadQuetzlales = atof(argv[1]);

    // validar que la cantidad de quetzales no sea negativa
    if (cantidadQuetzlales < 0) {
        printf("La cantidad de quetzales es invalida");
        return 1;
    }

    printf("\n%.2f GTQ:\n", cantidadQuetzlales);

    // declaracion fork
    pid_t pid = fork();

    /*
     * 1.
     */
    // Creacion del primer proceso hijo
    // validacion de errores
    if (pid < 0) {
        printf("Error en fork al crear proceso hijo para conversion en dolar");
        return 1;
    }

    // logica de conversion
    if (pid == 0) {
        printf("\nHIJO USD\n");
        printf("PID: %d  PPID: %d\n", getpid(), getppid());
        printf("USD: %.2f\n", cantidadQuetzlales / tipoCambioDollar);

       // TIPO DE CAMBIO A CERO
        tipoCambioDollar = 0;
        tipoCambioPeso = 0;
        tipoCambioEuro = 0;

        exit(0);

    }

    /*
     * 2.
     */
    // Creacion del segundo proceso hijo
    pid = fork();
    // validacion de errores
    if (pid < 0) {
        printf("Error en fork al crear proceso hijo para conversion en pesos mexicanos");
        return 1;
    }

    // logica de conversion
    if (pid == 0) {
        printf("\nHIJO MXN\n");
        printf("PID: %d  PPID: %d\n", getpid(), getppid());
        printf("MXN: %.2f\n", cantidadQuetzlales / tipoCambioPeso);
        exit(0);
    }

    /*
     * 3.
     */
    // Creacion del tercer proceso hijo
    pid = fork();
    // validacion de errores
    if (pid < 0) {
        printf("Error en fork al crear proceso hijo para conversion en euros");
        return 1;
    }

    // logica de conversion
    if (pid == 0) {
        printf("\nHIJO EUR\n");
        printf("PID: %d  PPID: %d\n", getpid(), getppid());
        printf("EUR: %.2f\n", cantidadQuetzlales / tipoCambioEuro);
        exit(0);
    }
}