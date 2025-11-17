
package controlador;

import modelo.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//Clase que gestiona la logica de negocio para eventos
public class GestorEventos {
    private List<Evento> eventos;
    private List<Asistente> asistentes;
    private List<Recurso> recursos;
    private int contadorEventos;
    private int contadorAsistentes;
    private int contadorRecursos;
    private RepositorioEventos repositorio;

    public GestorEventos() {
        this.eventos = new ArrayList<>();
        this.asistentes = new ArrayList<>();
        this.recursos = new ArrayList<>();
        this.contadorEventos = 1;
        this.contadorAsistentes = 1;
        this.contadorRecursos = 1;
        this.repositorio = new RepositorioEventos();
    }

   
   
    // Gestion de Eventos

    //Crea un nuevo evento y lo agrega al sistema
    public Evento crearEvento(String nombre, String descripcion, LocalDateTime fechaHora,
                              String ubicacion, int capacidadMaxima) {
        String id = "EVT-" + String.format("%03d", contadorEventos++);
        Evento evento = new Evento(id, nombre, descripcion, fechaHora, ubicacion, capacidadMaxima);
        eventos.add(evento);
        return evento;
    }

    //Busca un evento por ID
    public Evento buscarEvento(String id) {
        return eventos.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    //Obtiene todos los eventos futuros
    public List<Evento> getEventosFuturos() {
        LocalDateTime ahora = LocalDateTime.now();
        return eventos.stream()
                .filter(e -> e.getFechaHora().isAfter(ahora))
                .sorted((e1, e2) -> e1.getFechaHora().compareTo(e2.getFechaHora()))
                .collect(Collectors.toList());
    }

    //Obtiene todos los eventos pasados
    public List<Evento> getEventosPasados() {
        LocalDateTime ahora = LocalDateTime.now();
        return eventos.stream()
                .filter(e -> e.getFechaHora().isBefore(ahora))
                .sorted((e1, e2) -> e2.getFechaHora().compareTo(e1.getFechaHora()))
                .collect(Collectors.toList());
    }

    //Elimina un evento del sistema
    public boolean eliminarEvento(String id) {
        Evento evento = buscarEvento(id);
        if (evento == null) {
            return false;
        }

        // Liberar todos los recursos asociados antes de eliminar el evento
        for (String recursoId : evento.getRecursosIds()) {
            liberarRecursoDeEvento(evento.getId(), recursoId);
        }

        return eventos.remove(evento);
    }



    // Gestion de Asistentes

    //Crea un nuevo asistente y lo agrega al sistema
    public Asistente crearAsistente(String nombre, String apellido, String email) {
        String id = "AST-" + String.format("%03d", contadorAsistentes++);
        Asistente asistente = new Asistente(id, nombre, apellido, email);
        asistentes.add(asistente);
        return asistente;
    }

    //Busca un asistente por ID
    public Asistente buscarAsistente(String id) {
        return asistentes.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    //Busca asistentes por email
    public Asistente buscarAsistentePorEmail(String email) {
        return asistentes.stream()
                .filter(a -> a.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    //Inscribe un asistente a un evento
    public boolean inscribirAsistente(String eventoId, String asistenteId) {
        Evento evento = buscarEvento(eventoId);
        Asistente asistente = buscarAsistente(asistenteId);

        if (evento != null && asistente != null) {
            return evento.agregarAsistente(asistenteId);
        }
        return false;
    }

    //Desinscribe un asistente de un evento
    public boolean desinscribirAsistente(String eventoId, String asistenteId) {
        Evento evento = buscarEvento(eventoId);
        if (evento != null) {
            return evento.removerAsistente(asistenteId);
        }
        return false;
    }



    // Gestion de Recursos

    //Crea un nuevo recurso y lo agrega al sistema
    public Recurso crearRecurso(String nombre, String tipo, String descripcion) {
        String id = "REC-" + String.format("%03d", contadorRecursos++);
        Recurso recurso = new Recurso(id, nombre, tipo, descripcion);
        recursos.add(recurso);
        return recurso;
    }

    //Busca un recurso por ID
    public Recurso buscarRecurso(String id) {
        return recursos.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    //Obtiene recursos disponibles por tipo
    public List<Recurso> getRecursosDisponiblesPorTipo(String tipo) {
        return recursos.stream()
                .filter(r -> r.getTipo().equals(tipo) && r.isDisponible())
                .collect(Collectors.toList());
    }

    //Asigna un recurso a un evento
    public boolean asignarRecursoAEvento(String eventoId, String recursoId) {
        Evento evento = buscarEvento(eventoId);
        Recurso recurso = buscarRecurso(recursoId);

        if (evento != null && recurso != null && recurso.isDisponible()) {
            evento.agregarRecurso(recursoId);
            recurso.reservar();
            return true;
        }
        return false;
    }

    //Libera un recurso de un evento
    public boolean liberarRecursoDeEvento(String eventoId, String recursoId) {
        Evento evento = buscarEvento(eventoId);
        Recurso recurso = buscarRecurso(recursoId);

        if (evento != null && recurso != null) {
            if (evento.removerRecurso(recursoId)) {
                recurso.liberar();
                return true;
            }
        }
        return false;
    }

    //Elimina un recurso del sistema y lo desasocia de los eventos
    public boolean eliminarRecurso(String id) {
        Recurso recurso = buscarRecurso(id);
        if (recurso == null) {
            return false;
        }

        for (Evento evento : eventos) {
            evento.removerRecurso(id);
        }

        return recursos.remove(recurso);
    }




        // Analisis y reportes

    //Genera estadisticas basicas de eventos
    public EstadisticasEventos generarEstadisticas() {
        EstadisticasEventos stats = new EstadisticasEventos();
        LocalDateTime ahora = LocalDateTime.now();

        stats.totalEventos = eventos.size();
        stats.eventosFuturos = (int) eventos.stream().filter(e -> e.getFechaHora().isAfter(ahora)).count();
        stats.eventosPasados = (int) eventos.stream().filter(e -> e.getFechaHora().isBefore(ahora)).count();
        stats.totalAsistentes = asistentes.size();
        stats.totalRecursos = recursos.size();

        if (!eventos.isEmpty()) {
            stats.promedioAsistentesPorEvento = eventos.stream()
                    .mapToInt(e -> e.getAsistentesIds().size())
                    .average()
                    .orElse(0.0);

            stats.promedioOcupacion = eventos.stream()
                    .mapToDouble(Evento::getPorcentajeOcupacion)
                    .average()
                    .orElse(0.0);
        }

        return stats;
    }

    //Obtiene los asistentes de un evento especifico
    public List<Asistente> getAsistentesDeEvento(String eventoId) {
        Evento evento = buscarEvento(eventoId);
        if (evento == null) return new ArrayList<>();

        return evento.getAsistentesIds().stream()
                .map(this::buscarAsistente)
                .filter(asistente -> asistente != null)
                .collect(Collectors.toList());
    }

    //Obtiene los recursos de un evento especifico
    public List<Recurso> getRecursosDeEvento(String eventoId) {
        Evento evento = buscarEvento(eventoId);
        if (evento == null) return new ArrayList<>();

        return evento.getRecursosIds().stream()
                .map(this::buscarRecurso)
                .filter(recurso -> recurso != null)
                .collect(Collectors.toList());
    }


    // Getters para acceder a las colecciones

    public List<Evento> getEventos() { return new ArrayList<>(eventos); }
    public List<Asistente> getAsistentes() { return new ArrayList<>(asistentes); }
    public List<Recurso> getRecursos() { return new ArrayList<>(recursos); }

    // Setters para cargar desde persistencia

    public void setEventos(List<Evento> eventos) {
        this.eventos = new ArrayList<>(eventos);
        actualizarContadorEventos();
    }

    public void setAsistentes(List<Asistente> asistentes) {
        this.asistentes = new ArrayList<>(asistentes);
        actualizarContadorAsistentes();
    }

    public void setRecursos(List<Recurso> recursos) {
        this.recursos = new ArrayList<>(recursos);
        actualizarContadorRecursos();
    }



    // Metodos auxiliares para mantener contadores

    private void actualizarContadorEventos() {
        contadorEventos = eventos.stream()
                .map(Evento::getId)
                .filter(id -> id.startsWith("EVT-"))
                .mapToInt(id -> Integer.parseInt(id.substring(4)))
                .max()
                .orElse(0) + 1;
    }

    private void actualizarContadorAsistentes() {
        contadorAsistentes = asistentes.stream()
                .map(Asistente::getId)
                .filter(id -> id.startsWith("AST-"))
                .mapToInt(id -> Integer.parseInt(id.substring(4)))
                .max()
                .orElse(0) + 1;
    }

    private void actualizarContadorRecursos() {
        contadorRecursos = recursos.stream()
                .map(Recurso::getId)
                .filter(id -> id.startsWith("REC-"))
                .mapToInt(id -> Integer.parseInt(id.substring(4)))
                .max()
                .orElse(0) + 1;
    }

    // Metodos de Persistencia (delegados al repositorio)

    //Guarda todos los datos (eventos, asistentes y recursos)
    public void guardarTodo() {
        repositorio.guardarTodo(eventos, asistentes, recursos);
    }

    //Carga todos los datos (eventos, asistentes y recursos)
    public void cargarTodo() {
        repositorio.cargarTodo(eventos, asistentes, recursos);
        actualizarContadorEventos();
        actualizarContadorAsistentes();
        actualizarContadorRecursos();
    }

    //Clase interna para estadisticas de eventos
    public static class EstadisticasEventos {
        public int totalEventos;
        public int eventosFuturos;
        public int eventosPasados;
        public int totalAsistentes;
        public int totalRecursos;
        public double promedioAsistentesPorEvento;
        public double promedioOcupacion;

        @Override
        public String toString() {
            return String.format(
                    "Estadísticas de Eventos:\n" +
                            "Total de eventos: %d\n" +
                            "Eventos futuros: %d\n" +
                            "Eventos pasados: %d\n" +
                            "Total de asistentes: %d\n" +
                            "Total de recursos: %d\n" +
                            "Promedio de asistentes por evento: %.1f\n" +
                            "Promedio de ocupación: %.1f%%",
                    totalEventos, eventosFuturos, eventosPasados,
                    totalAsistentes, totalRecursos,
                    promedioAsistentesPorEvento, promedioOcupacion
            );
        }
    }

}