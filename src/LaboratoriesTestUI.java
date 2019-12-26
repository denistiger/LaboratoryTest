import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.UUID;

public class LaboratoriesTestUI extends JFrame {

    private LaboratoriesTestUI(String[] args) {
        initComponents(args);
    }

    private JTextField labCountText;
    private JTextField parameterNameText;
    private JTextField parameterAccuracyText;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private UUID uuid;
    private ArrayList<LabMeasure> labMeasures;

    private JButton createNextButton() {
        Font font = new Font("ARIAL", Font.BOLD, 20);
        JButton nextButton = new JButton("Далее");
        nextButton.setFont(font);
        nextButton.setFocusable(false);
        nextButton.setMaximumSize(new Dimension(150, 50));

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextView();
            }
        });
        return nextButton;
    }

    private JPanel createUIDPanel() {
        JPanel uuidPanel = new JPanel();
        uuidPanel.setLayout(new BoxLayout(uuidPanel, BoxLayout.Y_AXIS));
        uuid = UUID.randomUUID();
        uuidPanel.add(new JLabel("Уникальный идентификатор теста: " + uuid.toString()));
        uuidPanel.add(createNextButton());
        return uuidPanel;
    }

    private class LabMeasure {
        int labNumber;
        JTextField labMeasureText;
        JTextField labAccuracyText;
        LabMeasure(int labNumber) {
            this.labNumber = labNumber;
        }
    }

    private JPanel createTestResultsPanel() {
        JPanel testResultsPanel = new JPanel();
        testResultsPanel.setLayout(new BoxLayout(testResultsPanel, BoxLayout.Y_AXIS));
        testResultsPanel.add(new JLabel("Результаты"));
        return testResultsPanel;
    }

    private JPanel createLabResultsPanel(LabMeasure labMeasure) {
        JPanel labResultsPanel = new JPanel();
        labResultsPanel.setLayout(new BoxLayout(labResultsPanel, BoxLayout.Y_AXIS));

        Font font = new Font("ARIAL", Font.BOLD, 20);

        JPanel labMeasurePanel = new JPanel();
        labMeasurePanel.setLayout(new BoxLayout(labMeasurePanel, BoxLayout.X_AXIS));

        labMeasurePanel.add(new JLabel(parameterNameText.getText()));

        labMeasure.labMeasureText = new JTextField("");
        labMeasure.labMeasureText.setMaximumSize(new Dimension(200, 50));
        labMeasurePanel.add(labMeasure.labMeasureText);

        JPanel labAccuracyPanel = new JPanel();
        labAccuracyPanel.setLayout(new BoxLayout(labAccuracyPanel, BoxLayout.X_AXIS));

        labAccuracyPanel.add(new JLabel("Погрешность измерения лаборатории"));

        labMeasure.labAccuracyText = new JTextField("");
        labMeasure.labAccuracyText.setMaximumSize(new Dimension(200, 50));
        labAccuracyPanel.add(labMeasure.labAccuracyText);

        labResultsPanel.add(new JLabel("Измерение лаборатории " + Integer.toString(labMeasure.labNumber)));
        labResultsPanel.add(Box.createVerticalStrut(10));
        labResultsPanel.add(labMeasurePanel);
        labResultsPanel.add(Box.createVerticalStrut(10));
        labResultsPanel.add(labAccuracyPanel);
        labResultsPanel.add(Box.createVerticalStrut(10));
        labResultsPanel.add(createNextButton());
        labResultsPanel.add(Box.createVerticalStrut(10));
        labResultsPanel.add(Box.createGlue());
        return labResultsPanel;
    }

    private JPanel createTestInfoPanel() {
        JPanel testInfoPanel = new JPanel();
        testInfoPanel.setLayout(new BoxLayout(testInfoPanel, BoxLayout.Y_AXIS));

        Font font = new Font("ARIAL", Font.BOLD, 20);

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
        JButton nextButton = createNextButton();
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                labMeasures = new ArrayList<>();
                for (int i = 0; i < Integer.parseInt(labCountText.getText()); ++i) {
                    LabMeasure measure = new LabMeasure(i + 1);
                    labMeasures.add(measure);
                    mainPanel.add(createLabResultsPanel(measure));
                }
                mainPanel.add(createTestResultsPanel());
            }
        });
        testInfoPanel.add(nextButton);
        testInfoPanel.add(Box.createVerticalStrut(10));
        testInfoPanel.add(Box.createGlue());
        return testInfoPanel;
    }

    private void nextView() {
        cardLayout.next(mainPanel);
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


        setMinimumSize(new Dimension(500, 400));


        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Тест лабораторий");

        JPanel basePanel = new JPanel();
        basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.Y_AXIS));

        mainPanel = new JPanel();

        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        mainPanel.add(createTestInfoPanel());
        mainPanel.add(createUIDPanel());


        basePanel.add(mainPanel);
        basePanel.add(Box.createVerticalStrut(20));

        add(basePanel);


        pack();
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new LaboratoriesTestUI(args).setVisible(true));
    }

}
