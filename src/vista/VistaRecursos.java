package vista;

import controlador.GestorEventos;
import modelo.Recurso;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//Vista para gestionar recursos (salones, equipos, catering)
public class VistaRecursos extends JFrame {
    private GestorEventos gestor;
    private JTextArea txtAreaRecursos;
    private JButton btnNuevo, btnEditar, btnEliminar, btnCerrar;

    public VistaRecursos(GestorEventos gestor) {
        super("Gestión de Recursos");
        this.gestor = gestor;
        
        inicializarComponentes();
        cargarRecursos();
        
        pack();
        setVisible(true);
    }

    private void inicializarComponentes() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Crear panel
        JPanel panel = new JPanel();

        // Título
        JLabel lblTitulo = new JLabel(" GESTIÓN DE RECURSOS ");
        panel.add(lblTitulo);

        // Área de texto para mostrar recursos
        txtAreaRecursos = new JTextArea(20, 50);
        txtAreaRecursos.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtAreaRecursos);
        panel.add(scrollPane);

        // Botones
        btnNuevo = new JButton("Nuevo Recurso");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnCerrar = new JButton("Cerrar");

        btnNuevo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                crearNuevoRecurso();
            }
        });

        btnEditar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editarRecurso();
            }
        });

        btnEliminar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                eliminarRecurso();
            }
        });

        btnCerrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gestor.guardarTodo();
                dispose();
            }
        });

        panel.add(btnNuevo);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnCerrar);

        add(panel);
    }

    private void cargarRecursos() {
        txtAreaRecursos.setText("");
        
        txtAreaRecursos.append(" LISTA DE RECURSOS \n\n");
        txtAreaRecursos.append("(Use el NUMERO para editar/eliminar)\n\n");
        
        if (gestor.getRecursos().isEmpty()) {
            txtAreaRecursos.append("No hay recursos registrados.\n");
            return;
        }
        
        int numero = 1;
        for (Recurso recurso : gestor.getRecursos()) {
            txtAreaRecursos.append("Recurso #" + numero + "\n");
            txtAreaRecursos.append("  Nombre: " + recurso.getNombre() + "\n");
            txtAreaRecursos.append("  Tipo: " + recurso.getTipo() + "\n");
            txtAreaRecursos.append("  Descripción: " + recurso.getDescripcion() + "\n");
            
            if (recurso.esSalon()) {
                txtAreaRecursos.append("  Capacidad: " + recurso.getCapacidad() + " personas\n");
            }
            
            txtAreaRecursos.append("  Ubicación: " + (recurso.getUbicacion() != null ? recurso.getUbicacion() : "No especificada") + "\n");
            txtAreaRecursos.append("  Costo por hora: $" + recurso.getCostoPorHora() + "\n");
            txtAreaRecursos.append("  Estado: " + (recurso.isDisponible() ? "Disponible" : "No disponible") + "\n\n");
            numero++;
        }
    }

    private void crearNuevoRecurso() {
        // Solicitar tipo
        String[] tipos = {"SALON", "AUDIOVISUAL", "CATERING", "MOBILIARIO", "OTRO"};
        String tipo = (String) JOptionPane.showInputDialog(this,
            "Seleccione el tipo de recurso:",
            "Tipo de Recurso",
            JOptionPane.QUESTION_MESSAGE,
            null,
            tipos,
            tipos[0]);
        
        if (tipo == null) return;
        
        // Solicitar nombre
        String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre del recurso:");
        if (nombre == null || nombre.trim().isEmpty()) return;
        
        // Solicitar descripción
        String descripcion = JOptionPane.showInputDialog(this, "Ingrese la descripción:");
        if (descripcion == null) descripcion = "";
        
        // Solicitar ubicación
        String ubicacion = JOptionPane.showInputDialog(this, "Ingrese la ubicación:");
        if (ubicacion == null) ubicacion = "";
        
        // Solicitar capacidad (solo para salones)
        int capacidad = 0;
        if (tipo.equals("SALON")) {
            String capacidadStr = JOptionPane.showInputDialog(this, "Ingrese la capacidad (número de personas):");
            if (capacidadStr != null && !capacidadStr.trim().isEmpty()) {
                try {
                    capacidad = Integer.parseInt(capacidadStr.trim());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Capacidad inválida, se usará 0");
                }
            }
        }
        
        // Solicitar costo
        double costo = 0.0;
        String costoStr = JOptionPane.showInputDialog(this, "Ingrese el costo por hora:");
        if (costoStr != null && !costoStr.trim().isEmpty()) {
            try {
                costo = Double.parseDouble(costoStr.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Costo inválido, se usará 0");
            }
        }
        
        // Crear recurso
        Recurso recurso = gestor.crearRecurso(nombre.trim(), tipo, descripcion.trim());
        recurso.setUbicacion(ubicacion.trim());
        recurso.setCapacidad(capacidad);
        recurso.setCostoPorHora(costo);
        
        JOptionPane.showMessageDialog(this, "Recurso creado exitosamente");
        cargarRecursos();
        gestor.guardarTodo();
    }

    private void editarRecurso() {
        if (gestor.getRecursos().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay recursos para editar");
            return;
        }
        
        String numeroStr = JOptionPane.showInputDialog(this, "Ingrese el NUMERO del recurso a editar:");
        
        if (numeroStr != null && !numeroStr.trim().isEmpty()) {
            try {
                int numero = Integer.parseInt(numeroStr.trim());
                
                if (numero >= 1 && numero <= gestor.getRecursos().size()) {
                    Recurso recurso = gestor.getRecursos().get(numero - 1);
                    
                    // Editar nombre
                    String nombre = JOptionPane.showInputDialog(this, "Nombre:", recurso.getNombre());
                    if (nombre != null && !nombre.trim().isEmpty()) {
                        recurso.setNombre(nombre.trim());
                    }
                    
                    // Editar descripción
                    String descripcion = JOptionPane.showInputDialog(this, "Descripción:", recurso.getDescripcion());
                    if (descripcion != null) {
                        recurso.setDescripcion(descripcion.trim());
                    }
                    
                    // Editar ubicación
                    String ubicacion = JOptionPane.showInputDialog(this, "Ubicación:", recurso.getUbicacion());
                    if (ubicacion != null) {
                        recurso.setUbicacion(ubicacion.trim());
                    }
                    
                    // Editar capacidad (solo para salones)
                    if (recurso.esSalon()) {
                        String capacidadStr = JOptionPane.showInputDialog(this, "Capacidad:", recurso.getCapacidad());
                        if (capacidadStr != null && !capacidadStr.trim().isEmpty()) {
                            try {
                                int capacidad = Integer.parseInt(capacidadStr.trim());
                                recurso.setCapacidad(capacidad);
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(this, "Capacidad inválida");
                            }
                        }
                    }
                    
                    // Editar costo
                    String costoStr = JOptionPane.showInputDialog(this, "Costo por hora:", recurso.getCostoPorHora());
                    if (costoStr != null && !costoStr.trim().isEmpty()) {
                        try {
                            double costo = Double.parseDouble(costoStr.trim());
                            recurso.setCostoPorHora(costo);
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(this, "Costo inválido");
                        }
                    }
                    
                    // Editar disponibilidad
                    int disponible = JOptionPane.showConfirmDialog(this,
                        "¿El recurso está disponible?",
                        "Disponibilidad",
                        JOptionPane.YES_NO_OPTION);
                    recurso.setDisponible(disponible == JOptionPane.YES_OPTION);
                    
                    JOptionPane.showMessageDialog(this, "Recurso actualizado");
                    cargarRecursos();
                    gestor.guardarTodo();
                } else {
                    JOptionPane.showMessageDialog(this, "Numero invalido");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Debe ingresar un numero valido");
            }
        }
    }

    private void eliminarRecurso() {
        if (gestor.getRecursos().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay recursos para eliminar");
            return;
        }
        
        String numeroStr = JOptionPane.showInputDialog(this, "Ingrese el NUMERO del recurso a eliminar:");
        
        if (numeroStr != null && !numeroStr.trim().isEmpty()) {
            try {
                int numero = Integer.parseInt(numeroStr.trim());
                
                if (numero >= 1 && numero <= gestor.getRecursos().size()) {
                    Recurso recurso = gestor.getRecursos().get(numero - 1);
                    
                    int confirmacion = JOptionPane.showConfirmDialog(this,
                        "¿Está seguro que desea eliminar: " + recurso.getNombre() + "?",
                        "Confirmar",
                        JOptionPane.YES_NO_OPTION);

                    if (confirmacion == JOptionPane.YES_OPTION) {
                        if (gestor.eliminarRecurso(recurso.getId())) {
                            JOptionPane.showMessageDialog(this, "Recurso eliminado");
                            cargarRecursos();
                            gestor.guardarTodo();
                        } else {
                            JOptionPane.showMessageDialog(this, "No se pudo eliminar el recurso");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Numero invalido");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Debe ingresar un numero valido");
            }
        }
    }
}
