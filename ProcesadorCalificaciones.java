import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcesadorCalificaciones {

    private static final Logger LOG =
        Logger.getLogger(
            ProcesadorCalificaciones.class.getName()
        );

    
    static {
        LOG.setUseParentHandlers(false); 
        LOG.setLevel(Level.FINE);      
        ConsoleHandler consola = new ConsoleHandler();
        consola.setLevel(Level.FINE);  
        LOG.addHandler(consola);
    }

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
                LOG.finer("Procesando línea " + contador + ": " + linea);

                try {
                    int calificacion = Integer.parseInt(linea.trim());

                    if (calificacion < 0 || calificacion > 100) {
                        LOG.warning("Calificación fuera de rango: " + calificacion);
                        continue;
                    }

                    LOG.fine("Calificación válida: " + calificacion);

                } catch (NumberFormatException e) {
                    LOG.warning("Dato no numérico: " + linea);
                }
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
