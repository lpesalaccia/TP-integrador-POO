package modelo;

import java.util.Objects;

/* Clase que representa un asistente de eventos */
public class Asistente {
    private String id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String organizacion;
    private String cargo;

    // Constructor por defecto
    public Asistente() {}

    // Constructor con parámetros básicos
    public Asistente(String id, String nombre, String apellido, String email) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

    // Constructor completo
    public Asistente(String id, String nombre, String apellido, String email,
                     String telefono, String organizacion, String cargo) {
        this(id, nombre, apellido, email);
        this.telefono = telefono;
        this.organizacion = organizacion;
        this.cargo = cargo;
    }

    /* Obtiene el nombre completo del asistente */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    /* Verifica si el email es valido */
    public boolean esEmailValido() {
        return email != null && email.contains("@") && email.contains(".");
    }

    /* Genera informacion de contacto formateada */
    public String getInfoContacto() {
        StringBuilder info = new StringBuilder();
        info.append("Email: ").append(email);
        if (telefono != null && !telefono.isEmpty()) {
            info.append(" | Tel: ").append(telefono);
        }
        return info.toString();
    }

    /* Convierte el asistente a formato CSV para persistencia */
    public String toCSV() {
        return String.join("|",
                id,
                nombre != null ? nombre : "",
                apellido != null ? apellido : "",
                email != null ? email : "",
                telefono != null ? telefono : "",
                organizacion != null ? organizacion : "",
                cargo != null ? cargo : ""
        );
    }

    /* Crea un asistente desde formato CSV */
    public static Asistente fromCSV(String csv) {
        String[] partes = csv.split("\\|");
        if (partes.length < 7) return null;

        Asistente asistente = new Asistente();
        asistente.id = partes[0];
        asistente.nombre = partes[1].isEmpty() ? null : partes[1];
        asistente.apellido = partes[2].isEmpty() ? null : partes[2];
        asistente.email = partes[3].isEmpty() ? null : partes[3];
        asistente.telefono = partes[4].isEmpty() ? null : partes[4];
        asistente.organizacion = partes[5].isEmpty() ? null : partes[5];
        asistente.cargo = partes[6].isEmpty() ? null : partes[6];

        return asistente;
    }

    // Getters y Setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getOrganizacion() { return organizacion; }
    public void setOrganizacion(String organizacion) { this.organizacion = organizacion; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asistente asistente = (Asistente) o;
        return Objects.equals(id, asistente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getNombreCompleto());
        if (organizacion != null && !organizacion.isEmpty()) {
            sb.append(" (").append(organizacion).append(")");
        }
        sb.append(" - ").append(email);
        return sb.toString();
    }
}