import javax.swing.*;
import java.awt.*;

public class LaboratoriesTestUI extends JFrame {

    private LaboratoriesTestUI(String[] args) {
        initComponents(args);
    }

    private JTextField labCountText;
    private JTextField parameterNameText;
    private JTextField parameterAccuracyText;

    private JPanel createTestInfoPanel() {
        JPanel testInfoPanel = new JPanel();
        testInfoPanel.setLayout(new BoxLayout(testInfoPanel, BoxLayout.Y_AXIS));

        JPanel labCountPanel = new JPanel();
        labCountPanel.setLayout(new BoxLayout(labCountPanel, BoxLayout.X_AXIS));

        labCountPanel.add(new JLabel("Число лабораторий"));

        labCountText = new JTextField("3");
        labCountText.setMaximumSize(new Dimension(200, 50));
        labCountPanel.add(labCountText);

        JPanel parameterNamePanel = new JPanel();
        parameterNamePanel.setLayout(new BoxLayout(parameterNamePanel, BoxLayout.X_AXIS));

        parameterNamePanel.add(new JLabel("Измеряемый параметр, единицы измерения"));

        parameterNameText = new JTextField("Электромагнитное излучение, мкВт/см²");
        parameterNameText.setMaximumSize(new Dimension(200, 50));
        parameterNamePanel.add(parameterNameText);

        JPanel parameterAccuracyPanel = new JPanel();
        parameterAccuracyPanel.setLayout(new BoxLayout(parameterAccuracyPanel, BoxLayout.X_AXIS));

        parameterAccuracyPanel.add(new JLabel("Число знаков после запятой"));

        parameterAccuracyText = new JTextField("2");
        parameterAccuracyText.setMaximumSize(new Dimension(200, 50));
        parameterAccuracyPanel.add(parameterAccuracyText);

        testInfoPanel.add(Box.createVerticalStrut(10));
        testInfoPanel.add(labCountPanel);
        testInfoPanel.add(Box.createVerticalStrut(10));
        testInfoPanel.add(parameterNamePanel);
        testInfoPanel.add(Box.createVerticalStrut(10));
        testInfoPanel.add(parameterAccuracyPanel);
        testInfoPanel.add(Box.createVerticalStrut(10));
        testInfoPanel.add(Box.createGlue());
        return testInfoPanel;
    }

    private void initComponents(String[] args) {

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        }
        catch (UnsupportedLookAndFeelException | ClassNotFoundException |
                InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        UIManager.put("swing.boldMetal", Boolean.FALSE);

        Font font = new Font("ARIAL", Font.BOLD, 20);

        setMinimumSize(new Dimension(500, 400));


        JButton nextButton = new JButton("Далее");
        nextButton.setFont(font);
        nextButton.setFocusable(false);


        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Тест лабораторий");

        JPanel basePanel = new JPanel();
        basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.Y_AXIS));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));

        bottomPanel.add(nextButton);
        nextButton.setMaximumSize(new Dimension(150, 50));

        JPanel mainPanel = new JPanel();

        mainPanel.setLayout(new CardLayout());
        mainPanel.add(createTestInfoPanel());

        basePanel.add(mainPanel);
        basePanel.add(Box.createVerticalStrut(20));
        basePanel.add(bottomPanel);
        basePanel.add(Box.createVerticalStrut(20));

        add(basePanel);


        pack();
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new LaboratoriesTestUI(args).setVisible(true));
    }

}
