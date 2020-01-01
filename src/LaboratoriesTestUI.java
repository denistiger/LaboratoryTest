import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;

public class LaboratoriesTestUI extends JFrame {

    private LaboratoriesTestUI(String[] args) {
        initComponents(args);
    }

    private JTextField labsCountText;
    private JTextField parameterNameText;
    private JTextField parameterUnitsText;
    private JTextField successCriteriaText;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private ArrayList<LabMeasure> labMeasures = new ArrayList<>();
    private UUID uuid = UUID.randomUUID();;
    private final String cssStyle = "<p style=\"font-size:20px; text-align:center\">";

    private LabTestCase testCase = new LabTestCase(0.0);

    private static class LabMeasure {
        int labNumber;
        JTextField labMeasureText;
        JTextField labAccuracyText;
        LabMeasure(int labNumber) {
            this.labNumber = labNumber;
        }
    }

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

    private String createUUIDText() {
        return "Уникальный идентификатор теста:<br>" + uuid.toString();
    }

    private JTextPane createUUIDTextPane() {
        JTextPane UUIDTextPanel = new JTextPane();
        UUIDTextPanel.setContentType("text/html"); // let the text pane know this is what you want
        UUIDTextPanel.setText("<html>" + cssStyle + createUUIDText() + "</p></html>"); // showing off
        UUIDTextPanel.setEditable(false); // as before
        UUIDTextPanel.setBackground(null); // this is the same as a JLabel
        UUIDTextPanel.setBorder(null);
        UUIDTextPanel.setMaximumSize(new Dimension(700, 100));
        return UUIDTextPanel;
    }

    private JPanel createUIDPanel() {
        JPanel uuidPanel = new JPanel();
        uuidPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.insets = new Insets(10, 10, 5, 5);

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        uuidPanel.add(createUUIDTextPane(), c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        uuidPanel.add(Box.createVerticalStrut(40), c);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        uuidPanel.add(Box.createVerticalStrut(40), c);

        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 1;
        JButton nextButton = createNextButton();
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                setMinimumSize(new Dimension(700, 600));
            }
        });
        uuidPanel.add(nextButton, c);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        uuidPanel.add(Box.createVerticalStrut(400), c);

        return uuidPanel;
    }

    private void calculateResults() {
        testCase = new LabTestCase(Double.parseDouble(successCriteriaText.getText()));
        testCase.setPointNumber(0); // TODO implement point numbers;
        for (LabMeasure measure : labMeasures) {
            testCase.setLabMeasure(new LabTestCase.ValueError(Double.parseDouble(measure.labMeasureText.getText()),
                    Double.parseDouble(measure.labAccuracyText.getText()), measure.labNumber));
        }
        testCase.calculateResults();
    }



    private String createHTMLReport() {

        String report = "<html>" + cssStyle + createUUIDText() + "<br><br>";

        report += "Среднее значение измерений: " + String.format("%.4f", testCase.getMeanValue()) + "<br>";
        report += "Среднеквадратичная погрешность: " + String.format("%.4f", testCase.getMeanSquareError()) + "<br><br>";

        for (LabTestCase.ValueError measure : testCase.getLabMeasures()) {
            report += "Лаборатория номер " + Integer.toString(measure.labNumber) + ". ";
            if (testCase.isCalculated()) {
                report += "Отклонение " + String.format("%.4f", testCase.getDiff(measure)) + ". Оценка результата: " +
                        (testCase.getDiff(measure) < testCase.getSuccessCriteria() ? " Уд." : " Неуд.");
            }
            report += "<br>";
        }
        report += "<br>";

        report += "Строка для контрольной суммы MD5:<br>" + testCase.getMd5String() + "<br>";
        report += "MD5 контрольная сумма:<br>" + testCase.getMd5Result() + "<br><br></p></html>";
        return report;
    }

    private JPanel createHTMLTestResultsPanel() {
        JPanel testResultsPanel = new JPanel();

        testResultsPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        Font font = new Font("ARIAL", Font.BOLD, 20);

        JTextPane reportTextPane = new JTextPane();
        reportTextPane.setContentType("text/html"); // let the text pane know this is what you want
        reportTextPane.setText(createHTMLReport()); // showing off
        reportTextPane.setEditable(false); // as before
        reportTextPane.setBackground(null); // this is the same as a JLabel
        reportTextPane.setBorder(null);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.insets = new Insets(10, 10, 5, 5);

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        testResultsPanel.add(reportTextPane, c);


        JButton calculateButton = new JButton("Рассчитать результаты");
        calculateButton.setFont(font);
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                calculateResults();
                reportTextPane.setText(createHTMLReport());
            }
        });


        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.anchor = GridBagConstraints.PAGE_END;
        testResultsPanel.add(calculateButton, c);

        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        JButton exportButton = new JButton("Экспорт в pdf");
        exportButton.setFont(font);
        testResultsPanel.add(exportButton, c);

        return testResultsPanel;
    }

    private JPanel createLabResultsPanel(LabMeasure labMeasure) {
        JPanel labResultsPanel = new JPanel();
        labResultsPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.insets = new Insets(10, 10, 5, 5);

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 2;
        labResultsPanel.add(createUUIDTextPane(), c);

        Font font = new Font("ARIAL", Font.BOLD, 20);

        JLabel labNumLabel = new JLabel("Измерение лаборатории " + Integer.toString(labMeasure.labNumber));
        labNumLabel.setHorizontalAlignment(JLabel.CENTER);
        labNumLabel.setFont(font);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.gridheight = 1;
        labResultsPanel.add(labNumLabel, c);

        JLabel labMeasureLabel = new JLabel(parameterNameText.getText() + ", " + parameterUnitsText.getText());
        labMeasureLabel.setFont(font);
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        c.gridheight = 1;
        labResultsPanel.add(labMeasureLabel, c);

        labMeasure.labMeasureText = new JTextField("");
        labMeasure.labMeasureText.setFont(font);
        c.gridx = 1;
        c.gridy = 3;
        labResultsPanel.add(labMeasure.labMeasureText, c);


        JLabel labAccuracyLabel = new JLabel("Неопределённость измерений");
        labAccuracyLabel.setFont(font);
        c.gridx = 0;
        c.gridy = 4;
        labResultsPanel.add(labAccuracyLabel, c);

        labMeasure.labAccuracyText = new JTextField("");
        labMeasure.labAccuracyText.setFont(font);
        c.gridx = 1;
        c.gridy = 4;
        labResultsPanel.add(labMeasure.labAccuracyText, c);


        c.gridx = 1;
        c.gridy = 6;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.PAGE_END;
        labResultsPanel.add(createNextButton(), c);

        return labResultsPanel;
    }

    private JPanel createTestInfoPanel() {
        JPanel testInfoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.insets = new Insets(10, 10, 5, 5);

        Font font = new Font("ARIAL", Font.BOLD, 20);

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        testInfoPanel.add(createUUIDTextPane(), c);


        JLabel labsCountLabel = new JLabel("Число лабораторий");
        labsCountLabel.setFont(font);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        testInfoPanel.add(labsCountLabel, c);

        labsCountText = new JTextField("3");
        labsCountText.setFont(font);
        c.gridx = 1;
        c.gridy = 1;
        testInfoPanel.add(labsCountText, c);

        JLabel measureParamLabel = new JLabel("Измеряемый параметр");
        measureParamLabel.setFont(font);
        c.gridx = 0;
        c.gridy = 2;
        testInfoPanel.add(measureParamLabel, c);

        parameterNameText = new JTextField("Электромагнитное излучение");
        parameterNameText.setFont(font);
        c.gridx = 1;
        c.gridy = 2;
        testInfoPanel.add(parameterNameText, c);

        JLabel measureUnitLabel = new JLabel("Единицы измерения");
        measureUnitLabel.setFont(font);
        c.gridx = 0;
        c.gridy = 3;
        testInfoPanel.add(measureUnitLabel, c);

        parameterUnitsText = new JTextField("мкВт/см²");
        parameterUnitsText.setFont(font);
        c.gridx = 1;
        c.gridy = 3;
        testInfoPanel.add(parameterUnitsText, c);

        JLabel successCriteriaLabel = new JLabel("Критерий оценки");
        successCriteriaLabel.setFont(font);
        c.gridx = 0;
        c.gridy = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        testInfoPanel.add(successCriteriaLabel, c);

        successCriteriaText = new JTextField("1");
        successCriteriaText.setFont(font);
        c.gridx = 1;
        c.gridy = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        testInfoPanel.add(successCriteriaText, c);

        JButton nextButton = createNextButton();
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                labMeasures = new ArrayList<>();
                for (int i = 0; i < Integer.parseInt(labsCountText.getText()); ++i) {
                    LabMeasure measure = new LabMeasure(i + 1);
                    labMeasures.add(measure);
                    mainPanel.add(createLabResultsPanel(measure));
                }
                mainPanel.add(createHTMLTestResultsPanel());
            }
        });
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 2;
        testInfoPanel.add(Box.createVerticalStrut(40), c);

        c.gridx = 1;
        c.gridy = 6;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.PAGE_END;
        testInfoPanel.add(nextButton, c);
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


        setMinimumSize(new Dimension(700, 800));


        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Тест лабораторий");

        JPanel basePanel = new JPanel();
        basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.Y_AXIS));

        mainPanel = new JPanel();

        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        mainPanel.add(createTestInfoPanel());

        basePanel.add(mainPanel);
        basePanel.add(Box.createVerticalStrut(20));

        add(basePanel);
        pack();
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new LaboratoriesTestUI(args).setVisible(true));
    }

}
