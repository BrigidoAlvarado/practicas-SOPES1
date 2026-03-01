#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>

int main(int arg, char* argv[]) {

    int status = -1;

    if ( arg < 2) {
        printf("Uso: %s <Ruta Directorio> \n", argv[0]);
        return 1;
    }

    // RECIBIR EL NOMBRE DEL DIRECTORIOclear
    char *directorio = argv[1];

    // PROCESO HIJO
    pid_t pid = fork();

    // VALIDACION HIJO CREADO
    if (pid < 0) {
        printf("Error al crear el proceso hijo\n");
        return 1;
    }

    // LOGICA PROCESO HIJO
    if (pid == 0) {

        execlp("ls", "ls", "-l", directorio, NULL);

        printf("Error al ejecutar exec\n");
        exit(EXIT_FAILURE);
    }

    // PROCESO PADRE
    pid_t hijoTerminado = wait(&status);


    if (WIFEXITED(status)) {
        int codigoSalida = WEXITSTATUS(status);

        if (codigoSalida == EXIT_SUCCESS) {
            printf("\nListado mostrado existosamente\n");
        } else {
            printf("\n\nLa orden no pudo ser ejecutada\n");
        }
    }
    return EXIT_SUCCESS;
}