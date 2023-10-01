import java.io.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
//      dirInTerminal();  //1
//      dirOnTerminal();  //2
//      verDirectorioEspecifico("S:\\My Drive\\2ºDAM");  //OK
//      ejecutarComando("dir");
//        mostrarProcesosUsuario("Usuario");
//        ordenarTareasPor("MemUsage"); // No estoy seguro de que funcione correctamente
//        porcentajeMemoriaUsuario("Usuario"); // Pinta bien pero no funciona, con ayuda de psinfo.exe descargado y alojado en C:/
//        hacerPing("8.8.8.8"); //OK
//        buscarArchivoEnDirectorio("C:\\PSTools","PsInfo64.exe"); //OK
    }


    static void dirInTerminal(){
        try{
            ProcessBuilder iniciarPrograma = new ProcessBuilder("cmd.exe", "/c", "dir"); //La opción "/c" se utiliza para indicarle a cmd.exe que ejecute el comando que se proporciona a continuación y luego se cierre. En otras palabras, "/c" significa "ejecutar y cerrar"
            Process proceso = iniciarPrograma.start();

            // Obtener la salida del proceso
            InputStream inputStream = proceso.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String linea;
            StringBuilder salida = new StringBuilder();
            while ((linea = reader.readLine()) != null) {
                salida.append(linea).append("\n");
            }

            // Imprimir la salida o hacer lo que desees con ella
            System.out.println(salida.toString());
        }catch (IOException io){
            System.out.println("Error");
        }
    }

    static void dirOnTerminal(){
        try{
            ProcessBuilder iniciarPrograma = new ProcessBuilder("cmd.exe","/c","start brave.exe"); //La opción "/c" se utiliza para indicarle a cmd.exe que ejecute el comando que se proporciona a continuación y luego se cierre. En otras palabras, "/c" significa "ejecutar y cerrar"
            Process proceso = iniciarPrograma.start();
        }catch (IOException io){
            System.out.println("Error");
        }
    }

    public static void verDirectorioEspecifico(String directorio) {
        try {
            // Verificar que el directorio existe
            File directorioFile = new File(directorio);

            if (!directorioFile.isDirectory()) {
                System.out.println("El directorio no existe");
                return;
            }

            // Crear el comando con la ruta del directorio
            String comando = "cmd.exe /c dir \"" + directorio + "\"";

            // Ejecutar el comando
            Process proceso = Runtime.getRuntime().exec(comando);

            // Obtener la salida del proceso
            InputStream inputStream = proceso.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String linea;
            StringBuilder salida = new StringBuilder();
            while ((linea = reader.readLine()) != null) {
                salida.append(linea).append("\n");
            }

            // Imprimir la salida o hacer lo que desees con ella
            System.out.println(salida.toString());
        } catch (IOException io) {
            System.out.println("Error: " + io.getMessage());
        }
    }

    static boolean verificarComandoInterno(String comando) {
        boolean existe = true;
        try {

            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", comando);
            pb.redirectErrorStream(true);

            Process proceso = pb.start();
            proceso.waitFor();

            // Si el proceso devuelve un código de salida igual a 0, el comando interno existe
            if (proceso.exitValue() == 0) {
                System.out.println("El comando interno '" + comando + "' existe.");
            } else {
                System.out.println("El comando interno '" + comando + "' no existe.");
                existe = false;
            }
        } catch (Exception e) {
            System.out.println("Error al verificar el comando interno '" + comando + "': " + e.getMessage());
        }
        return existe;
    }

    static void ejecutarComando(String comando){
        if (verificarComandoInterno(comando)){
            try {


                ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe","/c",comando);
                Process process = processBuilder.start();
                process.waitFor();
                System.out.printf("Comando ejecutado");
            }catch (IOException io){
                System.out.printf("Error" + io.getMessage());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }else {
            System.out.printf("Comando no ejecutado al no ser correcto");
        }
    }

    public static void mostrarProcesosUsuario(String nombreUsuario) {

        String usuario = nombreUsuario;
//        String usuario = System.getenv("USERNAME"); De esta forma el sistema coge el nombre de usario actual
//        Para conocer el nombre de usuario desde cmd escribe 'echo %USERNAME%' sin las comillas
//        Recuerda que para esta consulta necesitas privilegios de administrador

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("tasklist", "/FI", "USERNAME eq " + usuario);
            Process proceso = processBuilder.start();

            BufferedReader lector = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
            String linea;
            while ((linea = lector.readLine()) != null) {
                System.out.println(linea);
            }

            proceso.waitFor();
            lector.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void ordenarTareasPor(String campoOrdenacion) {
        /** ALGUNOS POSIBLES CAMPOS DE ORDENACION
         * ImageName: Ordenar por el nombre del proceso.
         * PID: Ordenar por el ID del proceso.
         * SessionName: Ordenar por el nombre de la sesión.
         * `Session#: Ordenar por el número de sesión.
         * MemUsage: Ordenar por el uso de memoria.
         */
            String campoOrden = campoOrdenacion;

        try {
            // Ejecutar el comando PowerShell para obtener la lista de procesos ordenada
            String comandoPowerShell = "Get-Process | Sort-Object -Property " + campoOrden + " | Format-Table -AutoSize";
            ProcessBuilder processBuilder = new ProcessBuilder("powershell", comandoPowerShell);
            processBuilder.redirectErrorStream(true);
            Process proceso = processBuilder.start();

            BufferedReader lector = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
            String linea;
            while ((linea = lector.readLine()) != null) {
                System.out.println(linea);
            }

            proceso.waitFor();
            lector.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void porcentajeMemoriaUsuario(String nombreUsuario) {
        String usuario = nombreUsuario;

        try {
            // Ejecutar el comando PsInfo para obtener el porcentaje de memoria utilizado por el usuario
            ProcessBuilder processBuilder = new ProcessBuilder("C:/PSTools/psinfo", "-s", "-c", usuario);
            Process proceso = processBuilder.start();

            BufferedReader lector = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.contains("Commit:")) {
                    String[] partes = linea.split(":");
                    String porcentajeMemoria = partes[1].trim();
                    System.out.println("Porcentaje de memoria utilizado por " + usuario + ": " + porcentajeMemoria);
                    break;
                }
            }

            proceso.waitFor();
            lector.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void hacerPing(String direccionIP) {
        try{
            ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/C","ping "+direccionIP);
            Process process = processBuilder.start();

            BufferedReader lector = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String linea;
            while ((linea = lector.readLine()) != null) {
                System.out.println(linea);
            }
            lector.close();
            process.waitFor();
        }catch (IOException io){
            System.out.println(io.getMessage());
        }catch (InterruptedException in){
            System.out.println(in.getMessage());
        }
    }

    static void buscarArchivoEnDirectorio(String nombreDirectorio, String nombreArchivo){
        try{
            ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/C","dir /s /b "+nombreDirectorio + "\\"+nombreArchivo);
            Process process = processBuilder.start();

            BufferedReader lector = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String linea;
            while ((linea = lector.readLine()) != null) {
                System.out.println(linea);
            }
            Thread.sleep(7000); // Simulamos 7 segundos aquí
            System.out.println("Proceso completado");
            lector.close();
            process.waitFor();
        }catch (IOException io){
            System.out.println(io.getMessage());
        }catch (InterruptedException in){
            System.out.println("El proceso fue interrumpido");
            System.out.println(in.getMessage());
        }




    }
}




