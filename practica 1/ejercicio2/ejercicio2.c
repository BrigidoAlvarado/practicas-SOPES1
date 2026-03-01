#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>

int main (int arg, char* argv[]) {

    if ( arg < 2) {
        printf("Uso: %s <Cant Ganancia> \n", argv[0]);
        return 1;
    }

    // GANANCIA INGRESADA
    float gananciaIngresada = atof(argv[1]);

    // GASTOS OPERATIVOS
    /*carnet 202230251*/
    float sueldoEmpleado = 85 * 2;
    float electricidad = 40 * 1;
    float aguaEstimada = 15 * 2;
    float rentaLocal = 60 * 2;
    float costoTotal = sueldoEmpleado + electricidad + aguaEstimada + rentaLocal;

    int status;
    int codigoSalida = 1;

    printf("\nGanancia ingresada Q%2.f\n", gananciaIngresada);

    pid_t pidProceso1, pidProceso2;

    //PROCESO 1
    pidProceso1 = fork();
    if (pidProceso1 < 0) {
        printf("Error en fork al crear proceso hijo 1");
        return 1;
    } else if (pidProceso1 == 0) {
        printf("\nSueldo empleado: %2.f", sueldoEmpleado);
        printf("\nElectricidad: %2.f", electricidad);
        printf("\nAgua Estimada: %2.f", aguaEstimada);
        printf("\nRenta local: %2.f", rentaLocal);
        printf("\nCosto total: %2.f", costoTotal);
        return 0;
    }

    //PROCESO 2
    pidProceso2 = fork();

    if (pidProceso2 < 0) {
        printf("Error en fork al crear proceso hijo 2");
        return 1;
    } else if (pidProceso2 == 0) {
        float ganaciaNeta = gananciaIngresada - costoTotal;


        printf("\n\nganacia neta: %2.f\n", ganaciaNeta);

        //VALIDACION GANACIA O PERDIDAS
        if (gananciaIngresada > costoTotal) {
            return EXIT_SUCCESS;
        } else {
            return EXIT_FAILURE;
        }
    }

    // PROCESO PADRE
    for (int i = 0; i < 2; ++i) {
        pid_t hijoTerminado = wait(&status);
        if (WIFEXITED(status)) {
            if (hijoTerminado == pidProceso2) {
                codigoSalida = WEXITSTATUS(status);
            }
        }
    }

    if (codigoSalida == EXIT_SUCCESS) {
        printf("\n\nCierre Exitoso\n");
    } else if (codigoSalida == EXIT_FAILURE) {
        printf("\n\nSe registraron perdidas\n");
    }
}