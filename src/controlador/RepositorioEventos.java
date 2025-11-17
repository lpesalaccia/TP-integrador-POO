package controlador;

import modelo.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

//Clase que maneja la persistencia de datos en archivos de texto
public class RepositorioEventos {
    private static final String ARCHIVO_EVENTOS = "eventos.txt";
    private static final String ARCHIVO_ASISTENTES = "asistentes.txt";
    private static final String ARCHIVO_RECURSOS = "recursos.txt";

    //Guarda todos los eventos en un archivo de texto
    public void guardarEventos(List<Evento> eventos) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_EVENTOS))) {
            for (Evento evento : eventos) {
                writer.println(evento.toCSV());
            }
        }
    }

    //Carga todos los eventos desde un archivo de texto
    public List<Evento> cargarEventos() throws IOException {
        List<Evento> eventos = new ArrayList<>();
        File archivo = new File(ARCHIVO_EVENTOS);
        if (!archivo.exists()) return eventos;

        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_EVENTOS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                Evento evento = Evento.fromCSV(linea);
                if (evento != null) {
                    eventos.add(evento);
                }
            }
        }
        return eventos;
    }

    //Guarda todos los asistentes en un archivo de texto
    public void guardarAsistentes(List<Asistente> asistentes) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_ASISTENTES))) {
            for (Asistente asistente : asistentes) {
                writer.println(asistente.toCSV());
            }
        }
    }

    //Carga todos los asistentes desde un archivo de texto
    public List<Asistente> cargarAsistentes() throws IOException {
        List<Asistente> asistentes = new ArrayList<>();
        File archivo = new File(ARCHIVO_ASISTENTES);
        if (!archivo.exists()) return asistentes;

        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_ASISTENTES))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                Asistente asistente = Asistente.fromCSV(linea);
                if (asistente != null) {
                    asistentes.add(asistente);
                }
            }
        }
        return asistentes;
    }

    //Guarda todos los recursos en un archivo de texto
    public void guardarRecursos(List<Recurso> recursos) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_RECURSOS))) {
            for (Recurso recurso : recursos) {
                writer.println(recurso.toCSV());
            }
        }
    }

    //Carga todos los recursos desde un archivo de texto
    public List<Recurso> cargarRecursos() throws IOException {
        List<Recurso> recursos = new ArrayList<>();
        File archivo = new File(ARCHIVO_RECURSOS);
        if (!archivo.exists()) return recursos;

        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_RECURSOS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                Recurso recurso = Recurso.fromCSV(linea);
                if (recurso != null) {
                    recursos.add(recurso);
                }
            }
        }
        return recursos;
    }

    //Guarda todos los datos (eventos, asistentes y recursos)
    public void guardarTodo(List<Evento> eventos, List<Asistente> asistentes, List<Recurso> recursos) {
        try {
            guardarEventos(eventos);
            guardarAsistentes(asistentes);
            guardarRecursos(recursos);
        } catch (IOException e) {
            System.err.println("Error al guardar datos: " + e.getMessage());
        }
    }

    //Carga todos los datos (eventos, asistentes y recursos)
    public void cargarTodo(List<Evento> eventos, List<Asistente> asistentes, List<Recurso> recursos) {
        try {
            eventos.clear();
            eventos.addAll(cargarEventos());
            
            asistentes.clear();
            asistentes.addAll(cargarAsistentes());
            
            recursos.clear();
            recursos.addAll(cargarRecursos());
        } catch (IOException e) {
            System.err.println("Error al cargar datos: " + e.getMessage());
        }
    }
}

