import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcesadorCalificaciones {

    private static final Logger LOG =
        Logger.getLogger(
            ProcesadorCalificaciones.class.getName()
        );

    public static void main(String[] args) {

        LOG.log(
            Level.INFO,
            "Aplicación iniciada"
        );

        LOG.log(
            Level.WARNING,
            "No se proporcionó archivo"
        );

        LOG.log(
            Level.SEVERE,
            "No fue posible continuar"
        );
    }
}
