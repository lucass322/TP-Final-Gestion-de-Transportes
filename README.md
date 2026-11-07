# RutaGo

Sistema de gestión y consulta de rutas que permite encontrar opciones de transporte entre dos destinos, considerando diferentes medios de transporte y sus tarifas.

## Descripción

RutaGo es un proyecto desarrollado en Java que permite gestionar información relacionada con rutas, transportes y pasajeros, ofreciendo distintas alternativas para realizar un viaje desde un punto de origen hasta un destino.

## Tecnologías utilizadas

- **Lenguaje:** Java 26.
- **Interfaz gráfica:** JavaFX 27.
- **Iconografía:** Ikonli 12.3.1.
- **IDE:** IntelliJ IDEA.
- **Control de versiones:** Git y GitHub.

## Dependencias

El proyecto utiliza las siguientes bibliotecas externas:

- **JavaFX 27:** desarrollo de la interfaz gráfica.
- **Ikonli 12.3.1:** integración de iconos en JavaFX.
- **FontAwesome 5:** conjunto de iconos utilizado mediante Ikonli.

## Requisitos previos

Para ejecutar el proyecto se necesita:

- JDK 26 o superior.
- IntelliJ IDEA.
- JavaFX SDK 27.
- Git para clonar el repositorio.

## Configuración del entorno

### 1. Descargar JavaFX

1. Ingresar al sitio oficial de [Gluon](https://gluonhq.com/products/javafx/).
2. Descargar JavaFX SDK 27 correspondiente al sistema operativo.
3. Descomprimir el archivo descargado en una ubicación local.

### 2. Configurar JavaFX en IntelliJ IDEA

1. Abrir el proyecto en IntelliJ IDEA.
2. Ingresar en `File → Project Structure → Libraries`.
3. Seleccionar `+ → Java`.
4. Buscar la carpeta `lib` dentro del JavaFX SDK descargado.
5. Seleccionar todos los archivos `.jar` y agregarlos al proyecto.

### 3. Configurar la ejecución

1. Ingresar en `Run → Edit Configurations`.
2. Seleccionar la configuración de ejecución de `Main`.
3. En el campo `VM options`, agregar: --module-path="RUTA_JAVAFX/lib" --add-modules=javafx.controls,javafx.fxml

### 4. Configurar Ikonli

El proyecto utiliza Ikonli para incorporar iconos en la interfaz gráfica.

Las bibliotecas necesarias se encuentran en la carpeta `lib/` del repositorio:

- `ikonli-core-12.3.1.jar`
- `ikonli-javafx-12.3.1.jar`
- `ikonli-fontawesome5-pack-12.3.1.jar`

Para configurarlas en IntelliJ IDEA:

1. Ingresar en `File → Project Structure → Libraries`.
2. Seleccionar `+ → Java`.
3. Buscar la carpeta `lib/` del proyecto.
4. Seleccionar los tres archivos `.jar`.
5. Agregarlos como bibliotecas del proyecto.

1. Clonar el repositorio: git clone URL_DEL_REPOSITORIO
2. Abrir la carpeta del proyecto desde IntelliJ IDEA.
3. Configurar el JDK 26.
4. Agregar las bibliotecas de JavaFX e Ikonli siguiendo los pasos anteriores.
5. Configurar las opciones de ejecución de JavaFX.
6. Ejecutar la clase Main.

## Estado del proyecto

Actualmente, el proyecto se encuentra en desarrollo.

### Funcionalidades implementadas

- [x] Configuración inicial del proyecto JavaFX.
- [x] Interfaz gráfica de inicio de sesión.
- [x] Visualización y ocultación de contraseña.
- [x] Iconografía mediante Ikonli.
- [x] Indicadores visuales de foco en los campos de entrada.
- [ ] Validación de credenciales.
- [ ] Integración con la base de datos.


## Autoría

Proyecto desarrollado como práctica de programación orientada a objetos utilizando Java.
