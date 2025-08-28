import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import com.xyz.Ui.Menu;
import com.xyz.Utility.MongoDBConnection;

public class Main {
    private static JFrame splashFrame;
    private static JFrame mainFrame;
    
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }
        
        SwingUtilities.invokeLater(() -> {
            showSplashScreen();
            initializeApplication();
        });
    }
    
    private static void showSplashScreen() {
        splashFrame = new JFrame();
        splashFrame.setUndecorated(true);
        splashFrame.setSize(500, 300);
        splashFrame.setLocationRelativeTo(null);
        
        JPanel splashPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(70, 130, 180),
                    0, getHeight(), new Color(25, 25, 112)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw title
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 28));
                String title = "Doctor Channelling System";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(title)) / 2;
                int y = getHeight() / 2 - 20;
                g2d.drawString(title, x, y);
                
                // Draw subtitle
                g2d.setFont(new Font("Arial", Font.PLAIN, 14));
                String subtitle = "Loading...";
                fm = g2d.getFontMetrics();
                x = (getWidth() - fm.stringWidth(subtitle)) / 2;
                y = getHeight() / 2 + 20;
                g2d.drawString(subtitle, x, y);
                
                g2d.dispose();
            }
        };
        
        splashFrame.add(splashPanel);
        splashFrame.setVisible(true);
    }
    
    private static void initializeApplication() {
        // Simulate loading time
        Timer timer = new Timer(2000, e -> {
            try {
                // Test database connection
                testDatabaseConnection();
                
                // Close splash screen
                splashFrame.dispose();
                
                // Show main application
                showMainApplication();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, 
                    "Error initializing application: " + ex.getMessage(),
                    "Initialization Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private static void testDatabaseConnection() {
        try {
            MongoDBConnection.getInstance();
        } catch (Exception e) {
            System.out.println("⚠️ Database connection failed, will use demo mode: " + e.getMessage());
        }
    }
    
    private static void showMainApplication() {
        mainFrame = new Menu();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Add window listener for cleanup
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanup();
            }
        });
        
        mainFrame.setVisible(true);
        
        // Show welcome message
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(mainFrame,
                "Welcome to Doctor Channelling System!\n\n" +
                "This application helps you manage:\n" +
                "• Doctor registrations and schedules\n" +
                "• Patient records and information\n" +
                "• Appointment booking and management\n\n" +
                "The system is ready to use!",
                "Welcome", JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    private static void cleanup() {
        try {
            // Close database connection if exists
            MongoDBConnection connection = MongoDBConnection.getInstance();
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }
}
