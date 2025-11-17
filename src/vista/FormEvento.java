package vista;

import controlador.GestorEventos;
import modelo.Evento;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//Formulario para crear y editar eventos
public class FormEvento extends JFrame {
    private GestorEventos gestor;
    private Evento eventoEditando;
    
    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private JTextField txtFecha;
    private JTextField txtHora;
    private JTextField txtUbicacion;
    private JTextField txtCapacidad;
    
    private JButton btnGuardar, btnCancelar;

    public FormEvento(GestorEventos gestor, Evento evento) {
        super(evento == null ? "Nuevo Evento" : "Editar Evento");
        this.gestor = gestor;
        this.eventoEditando = evento;
        
        inicializarComponentes();
        if (evento != null) {
            cargarDatosEvento();
        }
        
        pack();
        setVisible(true);
    }

    private void inicializarComponentes() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        //Crear panel
        JPanel panel = new JPanel();

        // Etiquetas y campos
        JLabel lblNombre = new JLabel("Nombre:");
        panel.add(lblNombre);
        txtNombre = new JTextField(30);
        panel.add(txtNombre);

        JLabel lblDescripcion = new JLabel("Descripción:");
        panel.add(lblDescripcion);
        txtDescripcion = new JTextField(30);
        panel.add(txtDescripcion);

        JLabel lblFecha = new JLabel("Fecha (dd/MM/yyyy):");
        panel.add(lblFecha);
        txtFecha = new JTextField(30);
        panel.add(txtFecha);

        JLabel lblHora = new JLabel("Hora (HH:mm):");
        panel.add(lblHora);
        txtHora = new JTextField(30);
        panel.add(txtHora);

        JLabel lblUbicacion = new JLabel("Ubicación:");
        panel.add(lblUbicacion);
        txtUbicacion = new JTextField(30);
        panel.add(txtUbicacion);

        JLabel lblCapacidad = new JLabel("Capacidad:");
        panel.add(lblCapacidad);
        txtCapacidad = new JTextField(30);
        panel.add(txtCapacidad);

        //Botones
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardarEvento();
            }
        });
        
        btnCancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        panel.add(btnGuardar);
        panel.add(btnCancelar);
        
        // Agregar panel al JFrame
        add(panel);
    }

    private void cargarDatosEvento() {
        txtNombre.setText(eventoEditando.getNombre());
        txtDescripcion.setText(eventoEditando.getDescripcion());
        
        DateTimeFormatter formatterFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatterHora = DateTimeFormatter.ofPattern("HH:mm");
        
        txtFecha.setText(eventoEditando.getFechaHora().format(formatterFecha));
        txtHora.setText(eventoEditando.getFechaHora().format(formatterHora));
        txtUbicacion.setText(eventoEditando.getUbicacion());
        txtCapacidad.setText(String.valueOf(eventoEditando.getCapacidadMaxima()));
    }

    private void guardarEvento() {
        // Validaciones básicas
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio");
            return;
        }

        if (txtFecha.getText().trim().isEmpty() || txtHora.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La fecha y hora son obligatorias");
            return;
        }

        try {
            // Parsear fecha y hora
            String fechaStr = txtFecha.getText().trim();
            String horaStr = txtHora.getText().trim();
            String fechaHoraStr = fechaStr + " " + horaStr;
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime fechaHora = LocalDateTime.parse(fechaHoraStr, formatter);

            // Validar que la fecha no sea pasada (solo para eventos nuevos)
            if (eventoEditando == null && fechaHora.isBefore(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this,
                    "No se puede crear un evento con fecha pasada");
                return;
            }

            int capacidad = Integer.parseInt(txtCapacidad.getText().trim());

            // Validar capacidad positiva
            if (capacidad <= 0) {
                JOptionPane.showMessageDialog(this,
                    "La capacidad debe ser mayor a cero");
                return;
            }

            if (eventoEditando != null) {
                int inscriptosActuales = eventoEditando.getAsistentesIds().size();
                if (capacidad < inscriptosActuales) {
                    JOptionPane.showMessageDialog(this,
                        "La capacidad no puede ser menor a los asistentes ya inscriptos (" + inscriptosActuales + ")");
                    return;
                }
            }

            // Crear o actualizar evento
            Evento eventoGuardado = null;
            if (eventoEditando == null) {
                // Nuevo evento
                eventoGuardado = gestor.crearEvento(
                    txtNombre.getText().trim(),
                    txtDescripcion.getText().trim(),
                    fechaHora,
                    txtUbicacion.getText().trim(),
                    capacidad
                );
                JOptionPane.showMessageDialog(this, "Evento creado");
                
                // Preguntar si quiere agregar asistentes/recursos ahora
                int opcion = JOptionPane.showConfirmDialog(this,
                    "¿Desea agregar asistentes o recursos al evento ahora?",
                    "Agregar Detalles",
                    JOptionPane.YES_NO_OPTION);
                
                if (opcion == JOptionPane.YES_OPTION && eventoGuardado != null) {
                    dispose();
                    // Abrir vista detalle del evento recién creado
                    new VistaDetalleEvento(gestor, eventoGuardado);
                    return;
                }
            } else {
                // Editar evento existente
                eventoEditando.setNombre(txtNombre.getText().trim());
                eventoEditando.setDescripcion(txtDescripcion.getText().trim());
                eventoEditando.setFechaHora(fechaHora);
                eventoEditando.setUbicacion(txtUbicacion.getText().trim());
                eventoEditando.setCapacidadMaxima(capacidad);
                
                JOptionPane.showMessageDialog(this, "Evento actualizado");
            }

            dispose();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error en formato de fecha/hora o capacidad\nEjemplo: 25/12/2025 y 18:30");
        }
    }
}
