package vista;

import controlador.GestorEventos;
import modelo.Evento;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;

//Ventana principal de la aplicación
public class PantallaPrincipal extends JFrame {
    private GestorEventos gestor;
    private JList<Evento> listaEventos;
    private DefaultListModel<Evento> modeloLista;
    private JComboBox<String> filtroEventos;
    private JButton btnNuevo, btnEditar, btnEliminar, btnVerDetalle, btnRecursos, btnEstadisticas, btnSalir;

    public PantallaPrincipal(GestorEventos gestor) {
        super("Gestor de Eventos");
        this.gestor = gestor;
        inicializarComponentes();
        cargarEventos();
        
        setSize(700, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void inicializarComponentes() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crear el panel principal con BorderLayout
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior con título y filtros
        JPanel panelSuperior = new JPanel(new BorderLayout(5, 5));
        JLabel lblTitulo = new JLabel(" GESTOR DE EVENTOS ", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelSuperior.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelFiltros.add(new JLabel("Mostrar:"));
        filtroEventos = new JComboBox<>(new String[]{"Todos", "Futuros", "Pasados"});
        filtroEventos.addActionListener(e -> cargarEventos());
        panelFiltros.add(filtroEventos);
        panelSuperior.add(panelFiltros, BorderLayout.SOUTH);

        panel.add(panelSuperior, BorderLayout.NORTH);

        // Lista de eventos con modelo
        modeloLista = new DefaultListModel<>();
        listaEventos = new JList<>(modeloLista);
        listaEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaEventos.setCellRenderer(new EventoListCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(listaEventos);
        scrollPane.setPreferredSize(new Dimension(650, 300));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        btnNuevo = new JButton("Nuevo");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnVerDetalle = new JButton("Ver Detalle");
        btnRecursos = new JButton("Gestionar Recursos");
        btnEstadisticas = new JButton("Ver Estadísticas");
        btnSalir = new JButton("Salir");

        // Habilitar/deshabilitar botones según selección
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnVerDetalle.setEnabled(false);

        listaEventos.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                boolean haySeleccion = listaEventos.getSelectedValue() != null;
                btnEditar.setEnabled(haySeleccion);
                btnEliminar.setEnabled(haySeleccion);
                btnVerDetalle.setEnabled(haySeleccion);
            }
        });

        // Listeners para cada botón
        btnNuevo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                abrirFormularioNuevo();
            }
        });

        btnEditar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editarEventoSeleccionado();
            }
        });

        btnEliminar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                eliminarEventoSeleccionado();
            }
        });

        btnVerDetalle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                verDetalleEvento();
            }
        });

        btnRecursos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                abrirGestionRecursos();
            }
        });

        btnEstadisticas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarEstadisticas();
            }
        });

        btnSalir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                salir();
            }
        });

        // Agregar botones al panel
        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnVerDetalle);
        panelBotones.add(btnRecursos);
        panelBotones.add(btnEstadisticas);
        panelBotones.add(btnSalir);

        panel.add(panelBotones, BorderLayout.SOUTH);

        // Agregar el panel al JFrame
        add(panel);
    }

    private void cargarEventos() {
        modeloLista.clear();
        for (Evento evento : obtenerEventosFiltrados()) {
            modeloLista.addElement(evento);
        }
    }

    private java.util.List<Evento> obtenerEventosFiltrados() {
        String opcion = (String) filtroEventos.getSelectedItem();
        if ("Futuros".equals(opcion)) {
            return gestor.getEventosFuturos();
        } else if ("Pasados".equals(opcion)) {
            return gestor.getEventosPasados();
        }
        return gestor.getEventos();
    }

    private void abrirFormularioNuevo() {
        FormEvento form = new FormEvento(gestor, null);
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                cargarEventos();
                gestor.guardarTodo();
            }
        });
    }

    private void editarEventoSeleccionado() {
        Evento evento = listaEventos.getSelectedValue();
        if (evento != null) {
            FormEvento form = new FormEvento(gestor, evento);
            form.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    cargarEventos();
                    gestor.guardarTodo();
                }
            });
        }
    }

    private void eliminarEventoSeleccionado() {
        Evento evento = listaEventos.getSelectedValue();
        if (evento != null) {
            int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar: " + evento.getNombre() + "?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                gestor.eliminarEvento(evento.getId());
                JOptionPane.showMessageDialog(this, "Evento eliminado");
                cargarEventos();
                gestor.guardarTodo();
            }
        }
    }

    private void verDetalleEvento() {
        Evento evento = listaEventos.getSelectedValue();
        if (evento != null) {
            VistaDetalleEvento detalle = new VistaDetalleEvento(gestor, evento);
            detalle.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    cargarEventos();
                    gestor.guardarTodo();
                }
            });
        }
    }

    private void abrirGestionRecursos() {
        VistaRecursos vistaRecursos = new VistaRecursos(gestor);
        vistaRecursos.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                cargarEventos();
            }
        });
    }

    private void mostrarEstadisticas() {
        new VistaEstadisticas(gestor);
    }

    private void salir() {
        gestor.guardarTodo();
        System.exit(0);
    }

    public void actualizarLista() {
        cargarEventos();
    }

    // Renderer personalizado para mostrar eventos en la lista
    private class EventoListCellRenderer extends DefaultListCellRenderer {
        private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Evento) {
                Evento evento = (Evento) value;
                String texto = String.format("%s - %s (%s) - %d/%d asistentes",
                    evento.getNombre(),
                    evento.getFechaHora().format(formatter),
                    evento.getUbicacion(),
                    evento.getAsistentesIds().size(),
                    evento.getCapacidadMaxima());
                setText(texto);
            }
            
            return this;
        }
    }
}
