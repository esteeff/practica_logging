import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

public class ProcesadorCalificaciones {

    private static final Logger LOG =
        Logger.getLogger(
            ProcesadorCalificaciones.class.getName()
        );

    public static void main(String[] args) {

        if (args.length == 0) {

            LOG.severe(
                "No se especificó archivo de entrada"
            );

            return;
        }

        String archivo = args[0];

        LOG.info(
            "Archivo recibido: " + archivo
        );

        try (
            BufferedReader lector =
                new BufferedReader(
                    new FileReader(archivo)
                )
        ) {

            String linea;
            int contador = 0;

            while (
                (linea = lector.readLine()) != null
            ) {

                contador++;

                LOG.fine(
                    "Procesando línea "
                    + contador
                );
            }

            LOG.info(
                "Registros procesados: "
                + contador
            );

        } catch (IOException e) {

            LOG.severe(
                "Error al procesar archivo: "
                + e.getMessage()
            );
        }
    }
}
