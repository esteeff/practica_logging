NOMBRE: [Tu Nombre]

EXPEDIENTE: [Tu Expediente]

# Práctica guiada: Logging en Java con `java.util.logging`

## 1. Objetivo de aprendizaje

Al finalizar la práctica, el estudiante será capaz de **incorporar mecanismos de logging en una aplicación Java utilizando `java.util.logging`, configurar niveles de importancia, filtrar mensajes y almacenar registros en consola y archivos**.

El estudiante deberá poder:

- Crear y utilizar un objeto `Logger`.
- Generar mensajes con distintos niveles.
- Diferenciar `SEVERE`, `WARNING`, `INFO`, `CONFIG`, `FINE`, `FINER` y `FINEST`.
- Configurar niveles de filtrado.
- Utilizar `ConsoleHandler` y `FileHandler`.
- Generar bitácoras en XML y texto.
- Utilizar `SimpleFormatter`.
- Sustituir mensajes de diagnóstico con `System.out.println()` por logging estructurado.
- Relacionar logging con el manejo de excepciones.

## 2. Contextp

Se retomará una aplicación denominada:

**ProcesadorCalificaciones**

El programa lee un archivo con calificaciones, valida los datos y calcula su promedio.

Durante la ejecución interesa registrar eventos como:

```text
Inicio de aplicación
Archivo recibido
Archivo abierto correctamente
Número de registros procesados
Datos inválidos encontrados
Promedio calculado
Errores de lectura
Fin del procesamiento
```

En lugar de utilizar únicamente:

```java
System.out.println(...)
```

se incorporará la API:

```java
java.util.logging
```

Esto permitirá distinguir entre mensajes informativos, advertencias, errores y trazas detalladas.

## 3. Flujo general de logging

La aplicación envía mensajes a un objeto `Logger`, el cual puede aplicar filtros y dirigir los registros a uno o varios `Handler`; estos, a su vez, pueden formatear la salida antes de enviarla a consola, archivo u otro destino.

```text
Aplicación
    │
    ▼
 Logger
    │
    ├────────► ConsoleHandler
    │
    ├────────► FileHandler
    │
    └────────► SocketHandler
                  │
                  ▼
              Formatter
                  │
                  ▼
                Salida
```

## Parte I. Crear un Logger

### 4. Programa inicial

Crear:

```java
import java.util.logging.Logger;

public class ProcesadorCalificaciones {

    private static final Logger LOG =
        Logger.getLogger(
            ProcesadorCalificaciones.class.getName()
        );

    public static void main(String[] args) {

        LOG.info(
            "Iniciando ProcesadorCalificaciones"
        );

        System.out.println(
            "Procesando archivo..."
        );
    }
}
```

`Logger` se obtiene mediante el método estático `Logger.getLogger()`.

### 5. Actividad guiada 1: comparar `System.out` y `Logger`

Ejecutar el programa y observar la salida.

Comparar:

```java
System.out.println(
    "Iniciando aplicación"
);
```

con:

```java
LOG.info(
    "Iniciando aplicación"
);
```

Responder:

1. ¿Qué información adicional muestra el mensaje del Logger?
Muestra la fecha, la hora exacta, el nombre de la clase, el nombre del método (main) y el nivel de importancia (INFORMACIÓN).

2. ¿Aparece la fecha/hora?
Sí, aparece al principio de la línea (oct 26, 2026 4:48:10 P.M.).

3. ¿Aparece el nombre de la clase?
Sí, aparece como ProcesadorCalificaciones

4. ¿Aparece el nivel del mensaje?
Sí, en este caso aparece como INFORMACIÓN (que corresponde al nivel INFO).

5. ¿Cuál de los dos mecanismos proporciona más información para diagnosticar un problema?
El Logger. Porque System.out.println solo muestra el texto, mientras que el Logger nos dice cuándo ocurrió, en qué clase y con qué nivel de gravedad, lo cual es vital para encontrar errores en sistemas grandes.


## Parte II. Niveles de logging

### 6. Niveles

Los niveles, de mayor a menor importancia, son:

| Nivel | Uso |
|--|--|
| `SEVERE` | Error serio |
| `WARNING` | Problema o error potencial |
| `INFO` | Información general |
| `CONFIG` | Información de configuración |
| `FINE` | Traza de ejecución |
| `FINER` | Traza más detallada |
| `FINEST` | Traza muy detallada |



### 7. Generar mensajes con `log()`

Agregar:

```java
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
```


### 8. Métodos abreviados

También pueden utilizarse:

```java
LOG.severe("Error grave");
LOG.warning("Advertencia");
LOG.info("Mensaje informativo");
LOG.config("Configuración cargada");
LOG.fine("Detalle de ejecución");
LOG.finer("Detalle adicional");
LOG.finest("Traza extremadamente detallada");
```

### 9. Actividad de aprendizaje: clasificar mensajes

En la siguiente tabla, signar un nivel apropiado a cada situación.

| Situación | Nivel propuesto |
|---|---|
| La aplicación inicia correctamente | INFO|
| No existe el archivo solicitado |SEVERE (o WARNING, dependiendo de si el programa puede continuar sin él) |
| Se procesaron 250 registros | INFO |
| Se encontró una calificación inválida | WARNING|
| Se cargó `application.properties` | CONFIG |
| Se desea conocer el valor de una variable durante depuración | FINE (o FINER/FINEST)|
| La aplicación no puede continuar | SEVERE |



## Parte III. Incorporar logging al procesamiento de archivos

### 10. Aplicación base

```java
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
```

### 11. Ejecutar

Compilar:

```bash
javac ProcesadorCalificaciones.java
```

Ejecutar:

```bash
java ProcesadorCalificaciones calificaciones.txt
```

Posteriormente probar con:

```bash
java ProcesadorCalificaciones inexistente.txt
```

Identificar:

- mensajes `INFO`;
INFORMACIÓN: Archivo recibido: calificaciones.txt

INFORMACIÓN: Registros procesados: 7
- mensajes `SEVERE`;
GRAVE: Error al procesar archivo: inexistente.txt (El sistema no puede encontrar el archivo especificado)
- mensajes `FINE`.
Aunque el código tiene la instrucción LOG.fine("Procesando línea..."), no se imprimió nada en la pantalla, porque el nivel por defecto de Java es INFO. Todo lo que sea menor a INFO (como FINE, FINER, FINEST) se ignora automáticamente a menos que lo configuremos.


## Parte IV. Filtrado por niveles

### 12. Nivel predeterminado

De forma predeterminada, los mensajes con nivel inferior a `INFO` no se muestran, a menos que se modifique la configuración.

Agregar:

```java
LOG.setLevel(Level.FINE);
```

Sin embargo, algunos mensajes pueden todavía no aparecer porque los `Handler` también aplican niveles de filtrado.

```text
Logger
  │
  ▼
Handler
```


### 13. Configurar `ConsoleHandler`

```java
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
```

Agregar:

```java
LOG.setLevel(Level.FINE);

ConsoleHandler consola =
    new ConsoleHandler();

consola.setLevel(Level.FINE);

LOG.addHandler(consola);
```


### 14. Evitar mensajes duplicados

Para evitar duplicación por handlers heredados:

```java
LOG.setUseParentHandlers(false);
```

Configuración:

```java
LOG.setUseParentHandlers(false);
LOG.setLevel(Level.FINE);

ConsoleHandler consola =
    new ConsoleHandler();

consola.setLevel(Level.FINE);

LOG.addHandler(consola);
```

### 15. Actividad guiada 2: experimentar con niveles

Ejecutar el programa utilizando sucesivamente:

```java
LOG.setLevel(Level.SEVERE);
```

```java
LOG.setLevel(Level.WARNING);
```

```java
LOG.setLevel(Level.INFO);
```

```java
LOG.setLevel(Level.FINE);
```

```java
LOG.setLevel(Level.ALL);
```

Completar la siguiente tabla indicando que mensajes se muestran en cada configuración:

| Configuración | SEVERE | WARNING | INFO | FINE |
|---|---|---|---|---|
| `SEVERE` | SI | NO | NO | NO |
| `WARNING` | SI |SI | NO |NO |
| `INFO` | SI|SI |SI |NO |
| `FINE` |SI |SI | SI| SI|
| `ALL` |SI | SI| SI| SI|



## Parte V. Registrar datos válidos e inválidos

### 16. Modificar procesamiento

```java
try {

    int calificacion =
        Integer.parseInt(
            linea.trim()
        );

    if (
        calificacion < 0
        || calificacion > 100
    ) {

        LOG.warning(
            "Calificación fuera de rango: "
            + calificacion
        );

        continue;
    }

    LOG.fine(
        "Calificación válida: "
        + calificacion
    );

} catch (NumberFormatException e) {

    LOG.warning(
        "Dato no numérico: "
        + linea
    );
}
```

La bitácora diferencia:

```text
INFO
→ eventos generales

WARNING
→ datos problemáticos recuperables

FINE
→ información detallada de procesamiento
```


## Parte VI. Logging y excepciones

### 17. Registrar una excepción

En lugar de:

```java
catch (IOException e) {

    System.out.println(
        e.getMessage()
    );
}
```

utilizar:

```java
catch (IOException e) {

    LOG.log(
        Level.SEVERE,
        "Error durante la lectura del archivo",
        e
    );
}
```

Comparar la diferencia entre:

```java
LOG.severe(
    e.getMessage()
);
```

y:

```java
LOG.log(
    Level.SEVERE,
    "Error al leer archivo",
    e
);
```
La diferencia es que el ```java
LOG.log(
    Level.SEVERE,
    "Error al leer archivo",
    e
);
```
 te dice qué pasó y dónde pas, y el otro solo te dice que paso, pero no donde paso, solo sabes que hay un error

## Parte VII. Registrar mensajes en un archivo

### 18. Uso de `Handler`

Los tipos de `Handler` presentados incluyen:

- `ConsoleHandler`
- `FileHandler`
- `SocketHandler`


### 19. Crear un `FileHandler`

```java
import java.io.IOException;
import java.util.logging.FileHandler;
```

Configurar:

```java
try {

    FileHandler archivoLog =
        new FileHandler(
            "Logging.xml"
        );

    LOG.addHandler(
        archivoLog
    );

} catch (IOException e) {

    System.err.println(
        "No fue posible crear el log"
    );
}
```

Comprobar que se genera:

```text
Logging.xml
```

### 20. Examinar el XML generado

El archivo tendrá una estructura similar a:

```xml
<log>

    <record>

        <date>...</date>

        <millis>...</millis>

        <sequence>...</sequence>

        <logger>...</logger>

        <level>INFO</level>

        <class>
            ProcesadorCalificaciones
        </class>

        <method>main</method>

        <thread>...</thread>

        <message>
            Aplicación iniciada
        </message>

    </record>

</log>
```

Identificar:

Fecha: <date>2026-10-27T17:40:28.476854400Z</date>

Logger: <logger>ProcesadorCalificaciones</logger>

Nivel: <level>INFO</level> (también aparece FINE y WARNING)

Clase: <class>ProcesadorCalificaciones</class>

Método: <method>main</method>

Hilo: <thread>3</thread>

Mensaje: <message>Archivo recibido: calificaciones.txt</message>


## Parte VIII. Log en texto plano

### 21. Utilizar `SimpleFormatter`

Agregar:

```java
import java.util.logging.SimpleFormatter;
```

Configurar:

```java
FileHandler archivoLog =
    new FileHandler(
        "aplicacion.log"
    );

SimpleFormatter formato =
    new SimpleFormatter();

archivoLog.setFormatter(
    formato
);

LOG.addHandler(
    archivoLog
);
```

Abrir:

```text
aplicacion.log
```


### 22. Comparación XML vs texto

| Característica | XML | Texto |
|---|----|---|
| Fácil de leer por una persona | NO|SI |
| Fácil de procesar automáticamente | SI| NO|
| Estructura explícita |SI |NO |
| Tamaño más compacto |NO |SI |
| Adecuado para inspección rápida | NO| SI|



## Parte IX. Configuración completa

### 23. Crear un método de configuración

```java
private static void configurarLogger() {

    try {

        LOG.setUseParentHandlers(false);

        LOG.setLevel(Level.FINE);

        ConsoleHandler consola =
            new ConsoleHandler();

        consola.setLevel(Level.INFO);

        LOG.addHandler(
            consola
        );

        FileHandler archivo =
            new FileHandler(
                "procesador.log"
            );

        archivo.setLevel(
            Level.FINE
        );

        archivo.setFormatter(
            new SimpleFormatter()
        );

        LOG.addHandler(
            archivo
        );

    } catch (IOException e) {

        System.err.println(
            "No fue posible configurar logging: "
            + e.getMessage()
        );
    }
}
```

Invocar:

```java
configurarLogger();
```



### 24. Resultado esperado

```text
                     ┌──► Consola
                     │     INFO o superior
Aplicación ─► Logger ┤
                     │
                     └──► procesador.log
                           FINE o superior
```



## Parte X. Actividad integradora

### 25. Reto: instrumentar completamente la aplicación

Modificar `ProcesadorCalificaciones` para registrar al menos los siguientes eventos:


| Evento | Nivel sugerido |
|-|-|
| Inicio de la aplicación | INFO |
| Nombre del archivo recibido | CONFIG o INFO |
| Apertura correcta del archivo | FINE |
| Cada línea procesada | FINER |
| Calificación válida | FINE |
| Valor no numérico | WARNING |
| Valor fuera de rango | WARNING |
| Archivo inexistente | SEVERE |
| Número total de registros | INFO |
| Número de registros inválidos | INFO |
| Promedio calculado | INFO |
| Fin del procesamiento | INFO |

Requisitos:

1. Utilizar una constante `Logger`.
2. Utilizar al menos cuatro niveles diferentes.
3. Registrar mensajes en consola.
4. Registrar mensajes en un archivo.
5. Utilizar `FileHandler`.
6. Utilizar `SimpleFormatter`.
7. Configurar diferentes niveles para consola y archivo.
8. Registrar al menos una excepción con `LOG.log(...)`.
9. Evitar utilizar `System.out.println()` como mecanismo de diagnóstico.
10. Mantener los mensajes suficientemente descriptivos.



## 26. Código base para el reto

```java
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

    public static void main(String[] args) {

        configurarLogger();

        LOG.info(
            "Inicio de la aplicación"
        );

        if (args.length == 0) {

            LOG.severe(
                "No se especificó archivo"
            );

            return;
        }

        String nombreArchivo =
            args[0];

        LOG.config(
            "Archivo configurado: "
            + nombreArchivo
        );

        procesarArchivo(
            nombreArchivo
        );

        LOG.info(
            "Fin de la aplicación"
        );
    }

    private static void procesarArchivo(
            String nombreArchivo) {

        int registros = 0;
        int validos = 0;
        double suma = 0;

        try (
            BufferedReader lector =
                new BufferedReader(
                    new FileReader(
                        nombreArchivo
                    )
                )
        ) {

            LOG.fine(
                "Archivo abierto correctamente"
            );

            String linea;

            while (
                (linea = lector.readLine())
                != null
            ) {

                registros++;

                LOG.finer(
                    "Procesando línea "
                    + registros
                    + ": "
                    + linea
                );

                try {

                    int calificacion =
                        Integer.parseInt(
                            linea.trim()
                        );

                    if (
                        calificacion < 0
                        || calificacion > 100
                    ) {

                        LOG.warning(
                            "Valor fuera de rango: "
                            + calificacion
                        );

                        continue;
                    }

                    validos++;
                    suma += calificacion;

                    LOG.fine(
                        "Calificación válida: "
                        + calificacion
                    );

                } catch (
                    NumberFormatException e
                ) {

                    LOG.warning(
                        "Dato no numérico: "
                        + linea
                    );
                }
            }

            LOG.info(
                "Registros procesados: "
                + registros
            );

            LOG.info(
                "Registros válidos: "
                + validos
            );

            if (validos > 0) {

                double promedio =
                    suma / validos;

                LOG.info(
                    "Promedio: "
                    + promedio
                );
            }

        } catch (IOException e) {

            LOG.log(
                Level.SEVERE,
                "Error al procesar archivo "
                + nombreArchivo,
                e
            );
        }
    }

    private static void configurarLogger() {

        try {

            LOG.setUseParentHandlers(false);
            LOG.setLevel(Level.ALL);

            ConsoleHandler consola =
                new ConsoleHandler();

            consola.setLevel(
                Level.INFO
            );

            LOG.addHandler(
                consola
            );

            FileHandler archivo =
                new FileHandler(
                    "procesador.log"
                );

            archivo.setLevel(
                Level.FINE
            );

            archivo.setFormatter(
                new SimpleFormatter()
            );

            LOG.addHandler(
                archivo
            );

        } catch (IOException e) {

            System.err.println(
                "Error configurando Logger: "
                + e.getMessage()
            );
        }
    }
}
```


## 28. Entregables

El estudiante deberá entregar:
- Repositorio en GitHub
- `ProcesadorCalificaciones.java`.
- Archivo de datos utilizado.
- `procesador.log`.
- Tabla de niveles completada.
- Respuestas a la actividad de diagnóstico.
- Breve explicación de la diferencia entre `Logger`, `Handler`, `Level` y `Formatter`.

Logger (El Registrador): Es el punto de entrada. Es el objeto que usamos en el código para enviar los mensajes. Su trabajo es capturar el evento y decidir si el mensaje es lo suficientemente importante para ser procesado (según el nivel que tenga configurado).

Handler (El Manejador): Es el que decide a dónde va el mensaje. El Logger no sabe si el mensaje va a la pantalla o a un archivo; simplemente se lo pasa a sus Handler. Tenemos el ConsoleHandler (que lo imprime en pantalla) y el FileHandler (que lo guarda en un archivo).

Level (El Nivel): Es la etiqueta de gravedad del mensaje (SEVERE, WARNING, INFO, FINE, etc.). Se usa tanto en el Logger como en los Handler para filtrar. Por ejemplo, si configuras la consola en INFO, los mensajes de nivel FINE (más detallados) serán ignorados en la pantalla, aunque se sigan guardando en el archivo.

Formatter (El Formateador): Es el que decide cómo se ve el mensaje. El Handler le pasa el mensaje al Formatter, y este lo convierte en texto. Podemos usar SimpleFormatter (que da un formato de texto plano legible) o el XMLFormatter (que da un formato estructurado con etiquetas).

En resumen: El Logger atrapa el mensaje, el Level dice qué tan grave es, el Handler decide a dónde va, y el Formatter decide cómo se ve.


