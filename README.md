# ¡Bienvenido a la aplicación CraftTech!
## Esta guía le ayudará a comprender cómo utilizar nuestra aplicación, que está diseñada para gestionar la producción de muebles de madera personalizados.
- Índice
- Introducción
- Requisitos previos
- Instalación
- Ejecución de la aplicación
- Dependencias
- Uso de la aplicación
- Página de inicio de sesión
- Página del cliente
- Página de administración
- Soporte

### La aplicación CraftTech es una aplicación de escritorio diseñada para optimizar la producción y la gestión de muebles de madera personalizados.
### La aplicación cuenta con un sistema de inicio de sesión para clientes y administradores, que permite diferentes funcionalidades según el rol del usuario.

### Prerrequisitos

Antes de comenzar, asegúrese de tener instalado lo siguiente en su máquina:

- Java Development Kit (JDK) 11 o superior
- Apache Maven
- Un navegador web
- Instalación

### Clonar el repositorio:

- sh
- Copiar código
- git clone https://github.com/JordyChamba/PROYECTOFINAL_CraftTech.git
  
### Ejecute la aplicación de escritorio:

- Abra el proyecto en su IDE (por ejemplo, IntelliJ IDEA, Eclipse).
- Primero ejecute la clase Main ubicada en el paquete src/main/java/uce/edu/ec/PROYECTOFINAL_PAII_CraftTech_SprinBoot.
- Luego ejecute la clase Main ubicada en el paquete src/main/java/uce/edu/ec/PROYECTOFINAL_PAII_CraftTech_Swing.

### Dependencias

A continuación, se incluye una descripción general rápida de las dependencias utilizadas en este proyecto y sus propósitos:

## - Spring Boot Starter Web (spring-boot-starter-web):

### Propósito:

proporciona las funcionalidades web básicas para el backend de Spring Boot, incluida la creación de servicios web RESTful.

### Uso:

se utiliza para gestionar solicitudes y respuestas HTTP para la aplicación.

## - Gson (com.google.code.gson:gson):

### Propósito: 
una biblioteca para convertir objetos Java en su representación JSON y viceversa.

### Uso: 

se utiliza para el análisis y serialización de JSON en la aplicación.

## - Spring Boot Starter Test (spring-boot-starter-test):

### Propósito: 

proporciona un conjunto completo de herramientas para probar aplicaciones Spring Boot.

### Uso: 

se utiliza para escribir y ejecutar pruebas para garantizar que la aplicación funcione correctamente.

## - FlatLaf (com.formdev:flatlaf):

### Propósito:

una biblioteca de apariencia para aplicaciones Java Swing, que proporciona un diseño plano y moderno.

### Uso: 

se utiliza para mejorar la apariencia visual de los componentes de la interfaz de usuario Swing en la aplicación.

## - Conector MySQL/J (com.mysql:mysql-connector-j):

### Propósito:

Un controlador JDBC para conectarse a bases de datos MySQL.

### Uso:

Se utiliza para permitir que el backend de Spring Boot interactúe con una base de datos MySQL para almacenar datos de usuario y pedidos.

## Uso de la aplicación

 ## Página de inicio de sesión
 
- Cuando inicie la aplicación, aparecerá la página de inicio de sesión.
- Nombre de usuario: Ingrese su nombre de usuario.
- Contraseña: Ingrese su contraseña.
- Botón de inicio de sesión: Haga clic para iniciar sesión.
- Botón de registro: Haga clic para abrir el formulario de registro si es un usuario nuevo.

 ![image](https://github.com/user-attachments/assets/ce6ca41a-8eb7-4aba-8ebd-91bdd2cdb912)


#### Nota: La aplicación diferencia entre usuarios administradores y clientes. Use @admin como nombre de usuario y contraseña para iniciar sesión como administrador.

## Página del cliente

Una vez que haya iniciado sesión correctamente como cliente, se lo dirigirá a la página del cliente. Aquí, puede:

- Ver y seleccionar materiales: Elija materiales para sus muebles de madera personalizados.
- Ver y seleccionar productos: Elija entre varios productos como sillas, mesas y camas.
- Realizar pedidos: Realice un pedido del producto y material seleccionados.
- Carrito de compras: vea los detalles de los productos que ha seleccionado.
- Enviar pedido: envíe su pedido para procesarlo.
- Cambiar cuenta: cierre la sesión y regrese a la página de inicio de sesión.

  ![image](https://github.com/user-attachments/assets/8f3fc10d-0481-4179-b887-355011b94800)


## Página de administración

Los administradores tienen acceso a la página de administración después de iniciar sesión. Esta página les permite:

- Ver pedidos: vea todos los pedidos realizados por los clientes.
- Administrar materiales: cree los materiales que serán utilizados en los productos.
- Administrar productos: cree los productos ofrecidos por la empresa.
- Detalles del pedido: vea información detallada sobre cada pedido.
- Actualizar datos: actualice los datos que se muestran en la página.
- Cambiar cuenta: cierre la sesión y regrese a la página de inicio de sesión.

 ![image](https://github.com/user-attachments/assets/0ff3261c-706b-4127-b8ca-df58a0ae3fd4)

 ![image](https://github.com/user-attachments/assets/da094f81-1b80-4b93-8971-ae37111f0afb)


### Soporte

Si tiene algún problema o tiene preguntas, comuníquese con nuestro equipo de soporte a 
support@crafttech.com.

## ¡Gracias por usar la aplicación CraftTech!

# ***Licencia***

Distribuido bajo la licencia MIT. Ver LICENSE para más información.
