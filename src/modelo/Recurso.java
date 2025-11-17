package modelo;

import java.util.Objects;

//Clase que representa un recurso disponible para eventos
public class Recurso {
    private String id;
    private String nombre;
    private String tipo; // "SALON", "AUDIOVISUAL", "CATERING", "MOBILIARIO", "OTRO"
    private String descripcion;
    private int capacidad; // Para salones, numero de personas; para equipos, cantidad disponible
    private boolean disponible;
    private double costoPorHora;
    private String ubicacion;
    private String observaciones;

    // Constructor por defecto
    public Recurso() {
        this.disponible = true;
        this.costoPorHora = 0.0;
    }

    // Constructor con parametros basicos
    public Recurso(String id, String nombre, String tipo, String descripcion) {
        this();
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    // Constructor con todos los parametros
    public Recurso(String id, String nombre, String tipo, String descripcion,
                   int capacidad, double costoPorHora, String ubicacion) {
        this(id, nombre, tipo, descripcion);
        this.capacidad = capacidad;
        this.costoPorHora = costoPorHora;
        this.ubicacion = ubicacion;
    }

    // Metodos

    //Verifica si el recurso está disponible para reserva
    public boolean estaDisponible() {
        return disponible;
    }

    //Reserva el recurso (marca como no disponible)
    public void reservar() {
        this.disponible = false;
    }

    //Libera el recurso (marca como disponible)
    public void liberar() {
        this.disponible = true;
    }

    //Calcula el costo total por un numero de horas
    public double calcularCosto(int horas) {
        return costoPorHora * horas;
    }

    //Verifica si el recurso es de tipo salon
    public boolean esSalon() {
        return "SALON".equals(tipo);
    }

    //verifica si el recurso tiene la capacidad suficiente
    public boolean tieneCapacidadSuficiente(int capacidadRequerida) {
        return capacidad >= capacidadRequerida;
    }

    //Obtiene informacion resumida del recurso
    public String getInfoResumida() {
        StringBuilder info = new StringBuilder();
        info.append(nombre).append(" (").append(tipo).append(")");
        if (esSalon() && capacidad > 0) {
            info.append(" - Capacidad: ").append(capacidad).append(" personas");
        }
        if (ubicacion != null && !ubicacion.isEmpty()) {
            info.append(" - ").append(ubicacion);
        }
        return info.toString();
    }

    //Convierte el recurso a formato CSV para persistencia
    public String toCSV() {
        return String.join("|",
                id,
                nombre != null ? nombre : "",
                tipo != null ? tipo : "",
                descripcion != null ? descripcion : "",
                String.valueOf(capacidad),
                String.valueOf(disponible),
                String.valueOf(costoPorHora),
                ubicacion != null ? ubicacion : "",
                observaciones != null ? observaciones : ""
        );
    }

    //Crea un recurso desde formato CSV
    public static Recurso fromCSV(String csv) {
        String[] partes = csv.split("\\|");
        if (partes.length < 9) return null;

        Recurso recurso = new Recurso();
        recurso.id = partes[0];
        recurso.nombre = partes[1].isEmpty() ? null : partes[1];
        recurso.tipo = partes[2].isEmpty() ? null : partes[2];
        recurso.descripcion = partes[3].isEmpty() ? null : partes[3];
        recurso.capacidad = Integer.parseInt(partes[4]);
        recurso.disponible = Boolean.parseBoolean(partes[5]);
        recurso.costoPorHora = Double.parseDouble(partes[6]);
        recurso.ubicacion = partes[7].isEmpty() ? null : partes[7];
        recurso.observaciones = partes[8].isEmpty() ? null : partes[8];

        return recurso;
    }

    // Getters y Setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public double getCostoPorHora() { return costoPorHora; }
    public void setCostoPorHora(double costoPorHora) { this.costoPorHora = costoPorHora; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recurso recurso = (Recurso) o;
        return Objects.equals(id, recurso.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s - %s",
                nombre, tipo, disponible ? "Disponible" : "No disponible",
                ubicacion != null ? ubicacion : "Sin ubicación");
    }
}