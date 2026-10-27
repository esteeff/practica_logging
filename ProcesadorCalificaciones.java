import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ProcesadorCalificaciones {

    private static final Logger LOG =
        Logger.getLogger(
            ProcesadorCalificaciones.class.getName()
        );

    
    static {
        try {
            LOG.setUseParentHandlers(false);
            LOG.setLevel(Level.FINE); 

          
            ConsoleHandler consola = new ConsoleHandler();
            consola.setLevel(Level.INFO);
            LOG.addHandler(consola);

            
            FileHandler archivoLog = new FileHandler("procesador.log");
            archivoLog.setLevel(Level.FINE);
            archivoLog.setFormatter(new SimpleFormatter());
            LOG.addHandler(archivoLog);

        } catch (IOException e) {
            System.err.println("No fue posible configurar el logger: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            LOG.severe("No se especificó archivo de entrada");
            return;
        }

        String archivo = args[0];
        LOG.info("Archivo recibido: " + archivo);

        try (
            BufferedReader lector =
                new BufferedReader(
                    new FileReader(archivo)
                )
        ) {
                        String linea;
            int contador = 0;
            int validos = 0;
            double suma = 0;

            while ((linea = lector.readLine()) != null) {
                contador++;
                LOG.finer("Procesando línea " + contador + ": " + linea);

                try {
                    int calificacion = Integer.parseInt(linea.trim());

                    if (calificacion < 0 || calificacion > 100) {
                        LOG.warning("Calificación fuera de rango: " + calificacion);
                        continue;
                    }

                    LOG.fine("Calificación válida: " + calificacion);
                                        validos++;
                    suma += calificacion;

                } catch (NumberFormatException e) {
                    LOG.warning("Dato no numérico: " + linea);
                }
            }

            LOG.info("Registros procesados: " + contador);
                        LOG.info("Registros válidos: " + validos);
            
            if (validos > 0) {
                double promedio = suma / validos;
                LOG.info("Promedio: " + promedio);
            }

        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error durante la lectura del archivo", e);
        }
    }
}