package vista;

import controlador.GestorEventos;
import javax.swing.*;
import java.awt.*;

//Ventana para mostrar estadísticas del sistema
public class VistaEstadisticas extends JFrame {
    
    public VistaEstadisticas(GestorEventos gestor) {
        super("Estadísticas del Sistema");
        
        GestorEventos.EstadisticasEventos stats = gestor.generarEstadisticas();
        
        inicializarComponentes(stats);
        
        setSize(400, 350);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void inicializarComponentes(GestorEventos.EstadisticasEventos stats) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Título
        JLabel lblTitulo = new JLabel("ESTADÍSTICAS DEL SISTEMA");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        int fila = 1;

        // Total eventos
        agregarFila(panel, gbc, fila++, "Total de eventos:", String.valueOf(stats.totalEventos));
        agregarFila(panel, gbc, fila++, "Eventos futuros:", String.valueOf(stats.eventosFuturos));
        agregarFila(panel, gbc, fila++, "Eventos pasados:", String.valueOf(stats.eventosPasados));
        agregarFila(panel, gbc, fila++, "Total de asistentes:", String.valueOf(stats.totalAsistentes));
        agregarFila(panel, gbc, fila++, "Total de recursos:", String.valueOf(stats.totalRecursos));
        agregarFila(panel, gbc, fila++, "Promedio asistentes/evento:", String.format("%.1f", stats.promedioAsistentesPorEvento));
        agregarFila(panel, gbc, fila++, "Promedio ocupación:", String.format("%.1f%%", stats.promedioOcupacion));

        // Botón cerrar
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel.add(btnCerrar, gbc);

        add(panel);
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, String valor) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblValor, gbc);
    }
}

