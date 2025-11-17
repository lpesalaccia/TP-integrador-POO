package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Evento {
    private String id;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaHora;
    private String ubicacion;
    private int capacidadMaxima;
    private List<String> asistentesIds;
    private List<String> recursosIds;
    private String estado; // "PROGRAMADO", "EN_CURSO", "FINALIZADO", "CANCELADO"

    // Constructor por defecto
    public Evento() {
        this.asistentesIds = new ArrayList<>();
        this.recursosIds = new ArrayList<>();
        this.estado = "PROGRAMADO";
    }

    // Constructor con parametros basicos
    public Evento(String id, String nombre, String descripcion, LocalDateTime fechaHora, String ubicacion, int capacidadMaxima) {
        this();
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaHora = fechaHora;
        this.ubicacion = ubicacion;
        this.capacidadMaxima = capacidadMaxima;
    }

    // Metodos

    /* Verifica si el evento tiene capacidad disponible */
    public boolean tieneCapacidadDisponible() {
        return asistentesIds.size() < capacidadMaxima;
    }

    /* Agrega un asistente al evento si hay capacidad */
    public boolean agregarAsistente(String asistenteId) {
        if (tieneCapacidadDisponible() && !asistentesIds.contains(asistenteId)) {
            asistentesIds.add(asistenteId);
            return true;
        }
        return false;
    }

    /* Remueve un asistente del evento */
    public boolean removerAsistente(String asistenteId) {
        return asistentesIds.remove(asistenteId);
    }

    /* Agrega un recurso al evento */
    public void agregarRecurso(String recursoId) {
        if (!recursosIds.contains(recursoId)) {
            recursosIds.add(recursoId);
        }
    }

    /* elimina un recurso del evento */
    public boolean removerRecurso(String recursoId) {
        return recursosIds.remove(recursoId);
    }

    /* calcula el porcentaje de ocupacion del evento */
    public double getPorcentajeOcupacion() {
        if (capacidadMaxima == 0) return 0;
        return (asistentesIds.size() * 100.0) / capacidadMaxima;
    }

    //Verifica si el evento ya paso
    public boolean yaOcurrio() {
        return fechaHora.isBefore(LocalDateTime.now());
    }

    //convierte el evento a formato CSV para persistencia
    public String toCSV() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.join("|",
                id,
                nombre,
                descripcion,
                fechaHora.format(formatter),
                ubicacion,
                String.valueOf(capacidadMaxima),
                String.join(",", asistentesIds),
                String.join(",", recursosIds),
                estado
        );
    }

    //Crea un evento desde formato CSV
    public static Evento fromCSV(String csv) {
        String[] partes = csv.split("\\|");
        if (partes.length < 9) return null;

        Evento evento = new Evento();
        evento.id = partes[0];
        evento.nombre = partes[1];
        evento.descripcion = partes[2];
        evento.fechaHora = LocalDateTime.parse(partes[3], DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        evento.ubicacion = partes[4];
        evento.capacidadMaxima = Integer.parseInt(partes[5]);

        // Cargar asistentes
        if (!partes[6].isEmpty()) {
            String[] asistentes = partes[6].split(",");
            for (String asistente : asistentes) {
                evento.asistentesIds.add(asistente.trim());
            }
        }

        // Cargar recursos
        if (!partes[7].isEmpty()) {
            String[] recursos = partes[7].split(",");
            for (String recurso : recursos) {
                evento.recursosIds.add(recurso.trim());
            }
        }

        evento.estado = partes[8];

        return evento;
    }

    // Getters y Setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public int getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(int capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }

    public List<String> getAsistentesIds() { return new ArrayList<>(asistentesIds); }
    public void setAsistentesIds(List<String> asistentesIds) { this.asistentesIds = new ArrayList<>(asistentesIds); }

    public List<String> getRecursosIds() { return new ArrayList<>(recursosIds); }
    public void setRecursosIds(List<String> recursosIds) { this.recursosIds = new ArrayList<>(recursosIds); }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evento evento = (Evento) o;
        return Objects.equals(id, evento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format("Evento: %s - %s (%s) - Capacidad: %d/%d",
                nombre, fechaHora.format(formatter), ubicacion, asistentesIds.size(), capacidadMaxima);
    }
}