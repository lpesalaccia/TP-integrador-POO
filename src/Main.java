import controlador.GestorEventos;
import vista.PantallaPrincipal;

//Clase principal que inicia la aplicación
public class Main {
    public static void main(String[] args) {
        // Crear el gestor de eventos
        GestorEventos gestor = new GestorEventos();
        
        // Cargar datos desde archivos
        gestor.cargarTodo();
        
        // Crear y mostrar la ventana principal
        PantallaPrincipal ventana = new PantallaPrincipal(gestor);
    }
}