package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class UIStyles {

    public static final Color BACKGROUND_COLOR = new Color(240, 242, 245);
    public static final Color SIDEBAR_COLOR = new Color(255, 255, 255);
    public static final Color CARD_BACKGROUND = new Color(255, 255, 255);
    
    public static final Color PRIMARY_COLOR = new Color(79, 70, 229);
    public static final Color PRIMARY_HOVER = new Color(67, 56, 202);
    public static final Color PRIMARY_LIGHT = new Color(238, 242, 255);
    public static final Color SECONDARY_COLOR = new Color(255, 255, 255);
    
    public static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    public static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    public static final Color BORDER_COLOR = new Color(229, 231, 235);
    
    public static final Color SUCCESS_COLOR = new Color(16, 185, 129);
    public static final Color WARNING_COLOR = new Color(245, 158, 11);
    public static final Color DANGER_COLOR = new Color(220, 38, 38);
    
    public static final Color PURPLE_COLOR = PRIMARY_COLOR;
    public static final Color ORANGE_COLOR = PRIMARY_COLOR;
    public static final Color TEAL_COLOR = PRIMARY_COLOR;
    public static final Color BLUE_COLOR = PRIMARY_COLOR;
    
    public static final Font FONT_HERO = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_H2 = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_MENU = new Font("Segoe UI", Font.PLAIN, 14);

    public static final Color PRIMARY = PRIMARY_COLOR;
    public static final Color SECONDARY = SECONDARY_COLOR;
    public static final Color TEXT_DARK = TEXT_PRIMARY;
    public static final Color TEXT_GRAY = TEXT_SECONDARY;
    
    public static ImageIcon createIcon(String type, Color bgColor, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(bgColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, size, size, size/3f, size/3f));
        
        Color iconColor = Color.WHITE;
        if (bgColor.equals(SECONDARY_COLOR) || bgColor.getAlpha() < 255) iconColor = TEXT_PRIMARY;
        g2.setColor(iconColor);
        
        drawSymbol(g2, type, size);
        
        g2.dispose();
        return new ImageIcon(image);
    }
    
    public static ImageIcon createCircleIcon(String type, Color bgColor, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        g2.setColor(bgColor);
        g2.fill(new Ellipse2D.Float(0, 0, size, size));
        
        if (bgColor.equals(Color.WHITE) || bgColor.equals(SECONDARY_COLOR)) {
            g2.setColor(PRIMARY_COLOR);
        } else {
            g2.setColor(Color.WHITE);
        }
        
        if (type.length() <= 2) {
             Font font = new Font("Segoe UI", Font.BOLD, (int)(size/2.2));
             g2.setFont(font);
             FontMetrics fm = g2.getFontMetrics();
             int x = (size - fm.stringWidth(type)) / 2;
             int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
             g2.drawString(type, x, y);
        } else {
             drawSymbol(g2, type, size);
        }
        
        g2.dispose();
        return new ImageIcon(image);
    }
    
    private static void drawSymbol(Graphics2D g2, String type, int size) {
        float cx = size / 2f;
        float cy = size / 2f;
        float s = size * 0.5f;
        
        Stroke originalStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(size * 0.05f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        switch (type) {
            case "ORDERS": case "📄":
                Path2D doc = new Path2D.Float();
                float dx = cx - s/2 + 2;
                float dy = cy - s/2;
                doc.moveTo(dx, dy);
                doc.lineTo(dx + s - 4, dy);
                doc.lineTo(dx + s, dy + 4);
                doc.lineTo(dx + s, dy + s);
                doc.lineTo(dx, dy + s);
                doc.closePath();
                g2.draw(doc);
                g2.drawLine((int)(dx+3), (int)(dy+4), (int)(dx+s-3), (int)(dy+4));
                g2.drawLine((int)(dx+3), (int)(dy+8), (int)(dx+s-3), (int)(dy+8));
                break;
                
            case "NEW_ORDER": case "ADD": case "➕": case "✨":
                g2.drawLine((int)cx, (int)(cy-s/2), (int)cx, (int)(cy+s/2));
                g2.drawLine((int)(cx-s/2), (int)cy, (int)(cx+s/2), (int)cy);
                break;
                
            case "LOW_STOCK": case "⚠️":
                Path2D tri = new Path2D.Float();
                tri.moveTo(cx, cy - s/2);
                tri.lineTo(cx + s/1.8, cy + s/2);
                tri.lineTo(cx - s/1.8, cy + s/2);
                tri.closePath();
                g2.draw(tri);
                g2.drawLine((int)cx, (int)(cy-2), (int)cx, (int)(cy+4));
                g2.drawLine((int)cx, (int)(cy+7), (int)cx, (int)(cy+7));
                break;
                
            case "ENQUIRIES": case "❓":
                Font qFont = new Font("Segoe UI", Font.BOLD, (int)(size * 0.7));
                g2.setFont(qFont);
                FontMetrics qFm = g2.getFontMetrics();
                String qText = "?";
                int qX = (size - qFm.stringWidth(qText)) / 2;
                int qY = ((size - qFm.getHeight()) / 2) + qFm.getAscent();
                g2.drawString(qText, qX, qY);
                break;
                
            case "PAYMENTS": case "💳":
                g2.drawRoundRect((int)(cx-s/1.8), (int)(cy-s/2.5), (int)(s*1.2), (int)(s*0.8), 4, 4);
                g2.drawLine((int)(cx-s/1.8), (int)(cy-s/6), (int)(cx+s/1.8-1), (int)(cy-s/6));
                g2.fillRect((int)(cx-s/3), (int)(cy+s/8), (int)(s/4), (int)(s/8));
                break;
                
            case "CUSTOMERS": case "👥": case "USER":
                g2.drawOval((int)(cx-s/4), (int)(cy-s/2), (int)(s/2), (int)(s/2));
                g2.drawArc((int)(cx-s/1.5), (int)(cy+s/6), (int)(s*1.3), (int)s, 0, 180);
                break;
                
            case "PENDING": case "⏳":
                Path2D hg = new Path2D.Float();
                hg.moveTo(cx-s/2, cy-s/2);
                hg.lineTo(cx+s/2, cy-s/2);
                hg.lineTo(cx, cy);
                hg.lineTo(cx+s/2, cy+s/2);
                hg.lineTo(cx-s/2, cy+s/2);
                hg.lineTo(cx, cy);
                hg.closePath();
                g2.draw(hg);
                break;
                
            case "INVENTORY": case "📋":
                g2.drawRoundRect((int)(cx-s/2.5), (int)(cy-s/2), (int)(s*0.8), (int)s, 3, 3);
                g2.fillRect((int)(cx-s/4), (int)(cy-s/2), (int)(s/2), (int)(s/6));
                g2.drawLine((int)(cx-s/4), (int)(cy), (int)(cx+s/4), (int)(cy));
                g2.drawLine((int)(cx-s/4), (int)(cy+s/4), (int)(cx+s/4), (int)(cy+s/4));
                break;
                
            case "DELIVERY": case "TRUCK": case "🚚":
                g2.drawRect((int)(cx-s/1.5), (int)(cy-s/3), (int)(s), (int)(s/1.8));
                g2.drawRect((int)(cx+s/3), (int)(cy-s/6), (int)(s/3), (int)(s/2.5));
                g2.fillOval((int)(cx-s/3), (int)(cy+s/5), (int)(s/4), (int)(s/4));
                g2.fillOval((int)(cx+s/3), (int)(cy+s/5), (int)(s/4), (int)(s/4));
                break;
                
            case "HISTORY": case "📜":
                g2.drawRect((int)(cx-s/2.5), (int)(cy-s/2), (int)(s*0.8), (int)s);
                g2.drawLine((int)(cx-s/4), (int)(cy-s/4), (int)(cx+s/4), (int)(cy-s/4));
                g2.drawLine((int)(cx-s/4), (int)(cy), (int)(cx+s/4), (int)(cy));
                g2.drawLine((int)(cx-s/4), (int)(cy+s/4), (int)(cx+s/4), (int)(cy+s/4));
                break;
                
            case "REFRESH": case "🔄":
                Font rFont = new Font("Segoe UI Symbol", Font.BOLD, (int)(size * 0.7));
                g2.setFont(rFont);
                FontMetrics rFm = g2.getFontMetrics();
                String rText = "\u21BB";
                int rX = (size - rFm.stringWidth(rText)) / 2;
                int rY = ((size - rFm.getHeight()) / 2) + rFm.getAscent();
                g2.drawString(rText, rX, rY);
                break;
                
            default:
                g2.drawOval((int)(cx-s/4), (int)(cy-s/4), (int)(s/2), (int)(s/2));
                break;
        }
        g2.setStroke(originalStroke);
    }

    public static class ModernButton extends JButton {
        private Color bgColor;
        private Color hoverColor;
        private boolean isHovered = false;

        public ModernButton(String text, Color bg) {
            super(text);
            this.bgColor = bg;
            this.hoverColor = bg.darker();
            setFont(FONT_BUTTON);
            setForeground(bg == SECONDARY_COLOR ? TEXT_PRIMARY : Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            if (bg.equals(SECONDARY_COLOR)) {
                setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
                this.hoverColor = new Color(249, 250, 251); 
            }

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (isHovered) {
                g2.setColor(hoverColor);
            } else {
                g2.setColor(bgColor);
            }
            
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static JButton createButton(String text, Color bg) {
        ModernButton btn = new ModernButton(text, bg);
        btn.setPreferredSize(new Dimension(140, 42));
        return btn;
    }

    public static JTextField createTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setFont(FONT_BODY);
        field.setBorder(new EmptyBorder(6, 12, 6, 12));
        field.setPreferredSize(new Dimension(250, 42));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_PRIMARY);
        return field;
    }
    
    public static JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setFont(FONT_BODY);
        field.setBorder(new EmptyBorder(6, 12, 6, 12));
        field.setPreferredSize(new Dimension(250, 42));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_PRIMARY);
        return field;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(48);
        table.setFont(FONT_BODY);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(new Color(79, 70, 229, 30));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setBackground(Color.WHITE);
        
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setBackground(Color.WHITE);
                l.setForeground(TEXT_SECONDARY);
                l.setFont(FONT_SMALL);
                l.setBorder(new EmptyBorder(0, 10, 0, 10));
                l.setPreferredSize(new Dimension(0, 40));
                return l;
            }
        });
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground(Color.WHITE);
                c.setForeground(TEXT_PRIMARY);
                ((JLabel) c).setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        });
    }

    public static JScrollPane createScrollPane(JComponent content) {
        JScrollPane sp = new JScrollPane(content);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Color.WHITE);
        sp.setBackground(Color.WHITE);
        
        sp.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(209, 213, 219);
                this.trackColor = Color.WHITE;
            }
            @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        });
        
        return sp;
    }
    
    private static JButton createZeroButton() {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(0, 0));
        return btn;
    }

    public static JPanel createCardPanel() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,10));
                g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 12, 12);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        return p;
    }
    
    public static JPanel createSimpleCardPanel() {
        return createCardPanel();
    }

    public static JLabel createLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }
    
    public static void showMessage(Component p, String msg, String title, int type) {
        JOptionPane.showMessageDialog(p, msg, title, type);
    }
    
    public static int showConfirm(Component p, String msg, String title) {
        return JOptionPane.showConfirmDialog(p, msg, title, JOptionPane.YES_NO_OPTION);
    }

    public static JPanel createSectionHeader(String title, String subtitle, Icon icon) {
        JPanel p = new JPanel(new BorderLayout(15, 0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        if (icon != null) {
            JLabel iconLbl = new JLabel(icon);
            p.add(iconLbl, BorderLayout.WEST);
        }
        
        JLabel t = new JLabel(title);
        t.setFont(FONT_HEADER);
        t.setForeground(TEXT_PRIMARY);
        
        JLabel s = new JLabel(subtitle);
        s.setFont(FONT_BODY);
        s.setForeground(TEXT_SECONDARY);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        textPanel.add(t);
        textPanel.add(s);
        
        p.add(textPanel, BorderLayout.CENTER);
        return p;
    }
    
    public static JPanel createSectionHeader(String title, String subtitle) {
        return createSectionHeader(title, subtitle, null);
    }
    
    public static JComboBox<String> createComboBox() {
        JComboBox<String> cb = new JComboBox<String>() { };
        cb.setBackground(Color.WHITE);
        cb.setFont(FONT_BODY);
        cb.setPreferredSize(new Dimension(200, 42));
        return cb;
    }
    
    public static JSpinner createSpinner(int min, int max, int val) {
        JSpinner s = new JSpinner(new SpinnerNumberModel(val, min, max, 1));
        s.setFont(FONT_BODY);
        s.setPreferredSize(new Dimension(100, 42));
        return s;
    }
    
    public static JTextArea createTextArea(int r, int c) {
        JTextArea t = new JTextArea(r, c);
        t.setFont(FONT_BODY);
        t.setBorder(new EmptyBorder(10, 10, 10, 10));
        t.setForeground(TEXT_PRIMARY);
        return t;
    }
}
