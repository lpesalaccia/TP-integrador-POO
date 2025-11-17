package vista;

import controlador.GestorEventos;
import modelo.Asistente;
import modelo.Evento;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

//Vista detallada de un evento con gestión de asistentes
public class VistaDetalleEvento extends JFrame {
    private GestorEventos gestor;
    private Evento evento;
    
    private JTextArea txtDetalles;
    private JButton btnAgregarAsistente, btnQuitarAsistente, btnAgregarRecurso, btnQuitarRecurso, btnCerrar;

    public VistaDetalleEvento(GestorEventos gestor, Evento evento) {
        super("Detalle del Evento");
        this.gestor = gestor;
        this.evento = evento;
        
        inicializarComponentes();
        cargarDatos();
        
        pack();
        setVisible(true);
    }

    private void inicializarComponentes() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Crear panel
        JPanel panel = new JPanel();

        // area de texto para mostrar información
        txtDetalles = new JTextArea(20, 50);
        txtDetalles.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtDetalles);
        panel.add(scrollPane);

        // Botones
        btnAgregarAsistente = new JButton("Agregar Asistente");
        btnQuitarAsistente = new JButton("Quitar Asistente");
        btnAgregarRecurso = new JButton("Agregar Recurso");
        btnQuitarRecurso = new JButton("Quitar Recurso");
        btnCerrar = new JButton("Cerrar");
        
        btnAgregarAsistente.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                agregarAsistente();
            }
        });
        
        btnQuitarAsistente.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quitarAsistente();
            }
        });
        
        btnAgregarRecurso.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                agregarRecurso();
            }
        });
        
        btnQuitarRecurso.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quitarRecurso();
            }
        });
        
        btnCerrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        panel.add(btnAgregarAsistente);
        panel.add(btnQuitarAsistente);
        panel.add(btnAgregarRecurso);
        panel.add(btnQuitarRecurso);
        panel.add(btnCerrar);
        
        // Agregar panel al JFrame
        add(panel);
    }

    private void cargarDatos() {
        txtDetalles.setText("");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        txtDetalles.append(" DETALLE DEL EVENTO \n\n");
        txtDetalles.append("Nombre: " + evento.getNombre() + "\n");
        txtDetalles.append("Descripción: " + evento.getDescripcion() + "\n");
        txtDetalles.append("Fecha y Hora: " + evento.getFechaHora().format(formatter) + "\n");
        txtDetalles.append("Ubicación: " + evento.getUbicacion() + "\n");
        txtDetalles.append("Estado: " + evento.getEstado() + "\n");
        txtDetalles.append("Capacidad: " + evento.getAsistentesIds().size() + "/" + evento.getCapacidadMaxima() + "\n\n");
        
        txtDetalles.append(" ASISTENTES \n");
        txtDetalles.append("(Use el NUMERO para quitar asistentes)\n\n");
        
        if (evento.getAsistentesIds().isEmpty()) {
            txtDetalles.append("No hay asistentes registrados.\n\n");
        } else {
            int numero = 1;
            for (String asistenteId : evento.getAsistentesIds()) {
                Asistente asistente = gestor.buscarAsistente(asistenteId);
                if (asistente != null) {
                    txtDetalles.append("Asistente #" + numero + "\n");
                    txtDetalles.append("  Nombre: " + asistente.getNombreCompleto() + "\n");
                    txtDetalles.append("  Email: " + asistente.getEmail() + "\n\n");
                    numero++;
                }
            }
        }
        
        // Mostrar recursos
        txtDetalles.append(" RECURSOS ASIGNADOS \n");
        txtDetalles.append("(Use el NUMERO para quitar recursos)\n\n");
        
        if (evento.getRecursosIds().isEmpty()) {
            txtDetalles.append("No hay recursos asignados.\n\n");
        } else {
            int numero = 1;
            for (String recursoId : evento.getRecursosIds()) {
                modelo.Recurso recurso = gestor.buscarRecurso(recursoId);
                if (recurso != null) {
                    txtDetalles.append("Recurso #" + numero + "\n");
                    txtDetalles.append("  Nombre: " + recurso.getNombre() + "\n");
                    txtDetalles.append("  Tipo: " + recurso.getTipo() + "\n");
                    txtDetalles.append("  Ubicación: " + (recurso.getUbicacion() != null ? recurso.getUbicacion() : "N/A") + "\n\n");
                    numero++;
                }
            }
        }
    }

    private void agregarAsistente() {
        // Verificar capacidad
        if (!evento.tieneCapacidadDisponible()) {
            JOptionPane.showMessageDialog(this, "El evento está completo");
            return;
        }

        // Preguntar si es asistente nuevo o existente
        String[] opciones = {"Asistente Existente", "Nuevo Asistente", "Cancelar"};
        int opcion = JOptionPane.showOptionDialog(this,
            "¿Qué desea hacer?",
            "Agregar Asistente",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]);

        if (opcion == 0) {
            // Asistente existente
            agregarAsistenteExistente();
        } else if (opcion == 1) {
            // Nuevo asistente
            crearNuevoAsistente();
        }
    }

    private void agregarAsistenteExistente() {
        // Crear lista de asistentes disponibles
        DefaultListModel<Asistente> modelo = new DefaultListModel<>();
        for (Asistente asistente : gestor.getAsistentes()) {
            if (!evento.getAsistentesIds().contains(asistente.getId())) {
                modelo.addElement(asistente);
            }
        }
        
        if (modelo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay asistentes disponibles");
            return;
        }
        
        JList<Asistente> lista = new JList<>(modelo);
        lista.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Asistente) {
                    Asistente a = (Asistente) value;
                    setText(a.getNombreCompleto() + " - " + a.getEmail());
                }
                return this;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        
        int opcion = JOptionPane.showConfirmDialog(this, scrollPane, 
            "Seleccione un asistente", JOptionPane.OK_CANCEL_OPTION);
        
        if (opcion == JOptionPane.OK_OPTION && lista.getSelectedValue() != null) {
            Asistente asistente = lista.getSelectedValue();
            if (gestor.inscribirAsistente(evento.getId(), asistente.getId())) {
                JOptionPane.showMessageDialog(this, "Asistente agregado");
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo agregar el asistente");
            }
        }
    }

    private void crearNuevoAsistente() {
        String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre:");
        if (nombre == null || nombre.trim().isEmpty()) {
            return;
        }
        
        String apellido = JOptionPane.showInputDialog(this, "Ingrese el apellido:");
        if (apellido == null || apellido.trim().isEmpty()) {
            return;
        }
        
        String email = JOptionPane.showInputDialog(this, "Ingrese el email:");
        if (email == null || email.trim().isEmpty()) {
            return;
        }
        
        email = email.trim();
        
        // Validar formato de email
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Email inválido. Debe contener @ y un punto");
            return;
        }
        
        // Validar email duplicado
        Asistente asistenteExistente = gestor.buscarAsistentePorEmail(email);
        if (asistenteExistente != null) {
            JOptionPane.showMessageDialog(this, 
                "Ya existe un asistente con ese email: " + asistenteExistente.getNombreCompleto());
            return;
        }
        
        Asistente nuevoAsistente = gestor.crearAsistente(nombre.trim(), apellido.trim(), email);
        
        if (gestor.inscribirAsistente(evento.getId(), nuevoAsistente.getId())) {
            JOptionPane.showMessageDialog(this, "Asistente creado y agregado");
            cargarDatos();
        }
    }

    private void quitarAsistente() {
        List<Asistente> asistentesEvento = gestor.getAsistentesDeEvento(evento.getId());
        if (asistentesEvento.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay asistentes en este evento");
            return;
        }
        
        DefaultListModel<Asistente> modelo = new DefaultListModel<>();
        for (Asistente asistente : asistentesEvento) {
            modelo.addElement(asistente);
        }
        
        JList<Asistente> lista = new JList<>(modelo);
        lista.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Asistente) {
                    Asistente a = (Asistente) value;
                    setText(a.getNombreCompleto() + " - " + a.getEmail());
                }
                return this;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        
        int opcion = JOptionPane.showConfirmDialog(this, scrollPane, 
            "Seleccione el asistente a quitar", JOptionPane.OK_CANCEL_OPTION);
        
        if (opcion == JOptionPane.OK_OPTION && lista.getSelectedValue() != null) {
            Asistente asistente = lista.getSelectedValue();
            int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Desea quitar a " + asistente.getNombreCompleto() + " del evento?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                if (gestor.desinscribirAsistente(evento.getId(), asistente.getId())) {
                    JOptionPane.showMessageDialog(this, "Asistente quitado");
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo quitar al asistente");
                }
            }
        }
    }

    private void agregarRecurso() {
        // Crear lista de recursos disponibles
        DefaultListModel<modelo.Recurso> modelo = new DefaultListModel<>();
        for (modelo.Recurso recurso : gestor.getRecursos()) {
            if (!evento.getRecursosIds().contains(recurso.getId()) && recurso.isDisponible()) {
                modelo.addElement(recurso);
            }
        }
        
        if (modelo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay recursos disponibles o todos ya están asignados");
            return;
        }
        
        JList<modelo.Recurso> lista = new JList<>(modelo);
        lista.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof modelo.Recurso) {
                    modelo.Recurso r = (modelo.Recurso) value;
                    String texto = r.getNombre() + " (" + r.getTipo() + ")";
                    if (r.getUbicacion() != null && !r.getUbicacion().isEmpty()) {
                        texto += " - " + r.getUbicacion();
                    }
                    setText(texto);
                }
                return this;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        
        int opcion = JOptionPane.showConfirmDialog(this, scrollPane, 
            "Seleccione un recurso", JOptionPane.OK_CANCEL_OPTION);
        
        if (opcion == JOptionPane.OK_OPTION && lista.getSelectedValue() != null) {
            modelo.Recurso recurso = lista.getSelectedValue();
            if (gestor.asignarRecursoAEvento(evento.getId(), recurso.getId())) {
                JOptionPane.showMessageDialog(this, "Recurso asignado al evento");
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "El recurso no está disponible");
            }
        }
    }

    private void quitarRecurso() {
        List<modelo.Recurso> recursosEvento = gestor.getRecursosDeEvento(evento.getId());
        if (recursosEvento.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay recursos asignados a este evento");
            return;
        }
        
        DefaultListModel<modelo.Recurso> modelo = new DefaultListModel<>();
        for (modelo.Recurso recurso : recursosEvento) {
            modelo.addElement(recurso);
        }
        
        JList<modelo.Recurso> lista = new JList<>(modelo);
        lista.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof modelo.Recurso) {
                    modelo.Recurso r = (modelo.Recurso) value;
                    String texto = r.getNombre() + " (" + r.getTipo() + ")";
                    if (r.getUbicacion() != null && !r.getUbicacion().isEmpty()) {
                        texto += " - " + r.getUbicacion();
                    }
                    setText(texto);
                }
                return this;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        
        int opcion = JOptionPane.showConfirmDialog(this, scrollPane, 
            "Seleccione el recurso a quitar", JOptionPane.OK_CANCEL_OPTION);
        
        if (opcion == JOptionPane.OK_OPTION && lista.getSelectedValue() != null) {
            modelo.Recurso recurso = lista.getSelectedValue();
            int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Desea quitar el recurso " + recurso.getNombre() + " del evento?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                if (gestor.liberarRecursoDeEvento(evento.getId(), recurso.getId())) {
                    JOptionPane.showMessageDialog(this, "Recurso quitado del evento");
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo quitar el recurso");
                }
            }
        }
    }
}
