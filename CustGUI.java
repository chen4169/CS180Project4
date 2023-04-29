import javax.swing.*;
import java.awt.*;

public class CustGUI extends JFrame {

    private JLabel titleLabel;
    public JButton search;
    public JButton viewMarket;
    public JButton sort;
    public JButton export;
    public JButton viewCart;

    public CustGUI() {

        setTitle("Market");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);

        JPanel panel = new JPanel() {  // creating a panel with dark background for window
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(40, 40, 40));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // Set the panel layout to BorderLayout
        panel.setLayout(new BorderLayout(5, 5));  // sets layout with gaps between components

        // Title Stuff -> Creates and makes it look good
        titleLabel = new JLabel("Welcome to the Marketplace");
        titleLabel.setFont(new Font("Helvetica", Font.BOLD, 28)); // Increase title font size
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Helvetica", Font.BOLD, 28));

        panel.add(titleLabel, BorderLayout.NORTH); // adding title to panel at the top

        // the 5 buttons needed for customer client
        search = createStyledButton("Search");
        viewMarket = createStyledButton("View Market");
        sort = createStyledButton("Sort");
        export = createStyledButton("Export History");
        viewCart = createStyledButton("View Cart");

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10)); // 2x2 panel for buttons
        buttonPanel.setOpaque(false);  // makes it work idk

        // Adding buttons to the 2x2 buttonPanel
        buttonPanel.add(search);
        buttonPanel.add(viewMarket);
        buttonPanel.add(sort);
        buttonPanel.add(export);

        // Adding 2x2 to the center of the dark background panel
        panel.add(buttonPanel, BorderLayout.CENTER);

        // View cart button goes under the 2x2 buttonPanel to maintain symmetry
        panel.add(viewCart, BorderLayout.SOUTH);


        add(panel);  // Just makes the dark panel into the GUI window

        setLocationRelativeTo(null); // Puts in the middle
    }

    // Function to make the buttons look good (and sizes them the same way)
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Helvetica", Font.PLAIN, 16)); // Button Title font stuff
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(128, 128, 128)); // Makes the background gray to match dark background
        button.setPreferredSize(new Dimension(150, 150)); // Makes buttons same size for symmetry
        return button;
    }
}
