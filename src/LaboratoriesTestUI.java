import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.prefs.Preferences;

public class LaboratoriesTestUI extends JFrame {

    private LaboratoriesTestUI(String[] args) {
        initComponents(args);
    }

    private JTextField labsCountText;
    private JTextField parameterNameText;
    private JTextField parameterUnitsText;
    private JTextField successCriteriaText;
    private JTextField pointNumberText;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private int pointNumber = 1;
    private ArrayList<LabMeasure> labMeasures = new ArrayList<>();
    private UUID uuid = UUID.randomUUID();;
    private final String cssStyle = "<p style=\"font-size:16px; text-align:center\">";
    private final String labPanelString = "labPanel";
    private Font font = new Font("ARIAL", Font.BOLD, 20);
    private static final String LAST_USED_FOLDER="LAST_USED_FOLDER";
    private static final String PARAMETER_NAME="Измеряемый параметр";
    private static final String PARAMETER_UNITS="Единицы измерения";
    private static final String MEASURE_ACCURACY="Неопределённость измерения";

    private interface IVerifier {
        boolean verify(String text);
    }

    private class IntVerifier implements IVerifier {

        public boolean verify(String text) {
            int num;
            try {
                num = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                return false;
            }
            if (num > 0) {
                return true;
            }
            return false;
        }
    }

    private class DoubleVerifier implements IVerifier {

        public boolean verify(String text) {
            double num;
            try {
                num = Double.parseDouble(text);
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }
    }

    private class VerifiedKeyListener implements KeyListener {

        private ArrayList<JTextField> textFields;
        private JButton button;
        private IVerifier verifier;

        public VerifiedKeyListener(IVerifier verifier, JTextField textField, JButton button) {
            this.textFields = new ArrayList<>();
            textFields.add(textField);
            this.verifier = verifier;
            this.button = button;
        }

        public VerifiedKeyListener(IVerifier verifier, JTextField textField1, JTextField textField2, JButton button) {
            this.textFields = new ArrayList<>();
            textFields.add(textField1);
            textFields.add(textField2);
            this.verifier = verifier;
            this.button = button;
        }

        private void verify() {
            boolean enabled = true;
            for (JTextField textField : textFields) {
                if (!verifier.verify(textField.getText())) {
                    enabled = false;
                }
            }
            button.setEnabled(enabled);
        }

        @Override
        public void keyTyped(KeyEvent e) {
            verify();
        }

        @Override
        public void keyPressed(KeyEvent e) {
            verify();
        }

        @Override
        public void keyReleased(KeyEvent e) {
            verify();
        }
    }

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

    private void calculateResults() {
        testCase = new LabTestCase(Double.parseDouble(successCriteriaText.getText()));
        testCase.setPointNumber(pointNumber);
        for (LabMeasure measure : labMeasures) {
            testCase.setLabMeasure(new LabTestCase.ValueError(Double.parseDouble(measure.labMeasureText.getText()),
                    Double.parseDouble(measure.labAccuracyText.getText()), measure.labNumber));
        }
        testCase.calculateResults();
    }

    private String createHTMLReport() {

        String report = "<html>" + "<head>\n" +
                "<style>\n" +
                "table, th, td {\n" +
                "  border: 1px solid black;\n" +
                "  border-collapse: collapse;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>" + cssStyle + createUUIDText() + "<br><br>";

        report += createMeasurePointNumText() + "<br>";
        report += PARAMETER_NAME + ": " + parameterNameText.getText() + "<br>";
        report += PARAMETER_UNITS + ": " + parameterUnitsText.getText() + "<br><br>";

        report += "Среднее значение измерений: " + String.format("%.4f", testCase.getMeanValue()) + "<br>";
        report += "Среднеквадратичная погрешность: " + String.format("%.4f", testCase.getMeanSquareError()) + "<br><br>";

        report += "<table style=\"font-size:16px; text-align:center; width:100%\">" +
                "  <tr>\n" +
                "    <th>Испытательная<br>лаборатория №</th>\n" +
                "<th>Измерение</th><th>Неопределённость<br>измерения</th>" +
                "    <th>Отклонение</th>\n" +
                "    <th>Оценка<br>результата</th>\n" +
                "  </tr>";
        for (LabTestCase.ValueError measure : testCase.getLabMeasures()) {
            report += "<tr style=\"font-size:16px; text-align:center\"><td>" + Integer.toString(measure.labNumber) + "</td>";
            if (testCase.isCalculated()) {
                report += "<td>" + String.format("%.4f", measure.value) + "</td>" +
                        "<td>" + String.format("%.4f", measure.error) + "</td>" +
                        "<td>" + String.format("%.4f", testCase.getDiff(measure)) + "</td><td>" +
                        (testCase.getDiff(measure) < testCase.getSuccessCriteria() ? " Уд." : " Неуд.") + "</td>";
            }
            report += "</tr>";
        }
        report += "</table><br>";

        report += cssStyle + "Строка для контрольной суммы MD5:<br>" + testCase.getMd5String() + "<br>";
        report += "MD5 контрольная сумма:<br>" + testCase.getMd5Result() + "<br><br></p></html>";
        return report;
    }

    private JPanel createHTMLTestResultsPanel() {
        JPanel testResultsPanel = new JPanel();

        testResultsPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

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

        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;
        JButton nextPointButton = new JButton("Следующая точка");
        nextPointButton.setEnabled(false);
        nextPointButton.setFont(font);
        testResultsPanel.add(nextPointButton, c);

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (exportToPDF()) {
                    nextPointButton.setEnabled(true);
                }
            }
        });

        nextPointButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextPointButton.setEnabled(false);
                nextPointNumber();
                mainPanel.removeAll();
                initMainPanelLayout();
                cardLayout.show(mainPanel, labPanelString + "1");
            }
        });


        return testResultsPanel;
    }

    private boolean exportToPDF() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateFormat.format(date);
        String fileName = "Точка " + Integer.toString(pointNumber) + " " + strDate + ".pdf";
        Preferences prefs = Preferences.userRoot().node(getClass().getName());
        String folder = "", folder1 = "";
        folder = prefs.get(LAST_USED_FOLDER, folder1);
        folder += File.separator;
        JFileChooser fileChooser = new JFileChooser(new File(folder + fileName));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF document", "pdf");
        fileChooser.setFileFilter(filter);

        fileChooser.setSelectedFile(new File(folder + fileName));
        if (fileChooser.showSaveDialog(mainPanel) == JFileChooser.APPROVE_OPTION) {
            prefs.put(LAST_USED_FOLDER, fileChooser.getSelectedFile().getParent());
            return PDFExport.exportPDF(createHTMLReport(), fileChooser.getSelectedFile());
        }
        return false;
    }

    private String createMeasurePointNumText() {
        return "Точка измерений №" + Integer.toString(pointNumber);
    }

    private JPanel createLabResultsPanel(LabMeasure labMeasure) {
        JPanel labResultsPanel = new JPanel();
        labResultsPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.insets = new Insets(10, 10, 5, 5);

        JButton nextButton = createNextButton();
        nextButton.setEnabled(false);

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 2;
        labResultsPanel.add(createUUIDTextPane(), c);

        int row = 2;
        JLabel pointNumLabel = new JLabel(createMeasurePointNumText());
        pointNumLabel.setHorizontalAlignment(JLabel.CENTER);
        pointNumLabel.setFont(font);
        c.gridx = 0;
        c.gridy = row++;
        c.gridwidth = 2;
        c.gridheight = 1;
        labResultsPanel.add(pointNumLabel, c);

        JLabel labNumLabel = new JLabel("Измерение лаборатории " + Integer.toString(labMeasure.labNumber));
        labNumLabel.setHorizontalAlignment(JLabel.CENTER);
        labNumLabel.setFont(font);
        c.gridx = 0;
        c.gridy = row++;
        c.gridwidth = 2;
        c.gridheight = 1;
        labResultsPanel.add(labNumLabel, c);

        JLabel labMeasureLabel = new JLabel(parameterNameText.getText() + ", " + parameterUnitsText.getText());
        labMeasureLabel.setFont(font);
        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 1;
        c.gridheight = 1;
        labResultsPanel.add(labMeasureLabel, c);

        labMeasure.labMeasureText = new JTextField("");
        labMeasure.labAccuracyText = new JTextField("");

        KeyListener verifiedKeyListener = new VerifiedKeyListener(new DoubleVerifier(),
                labMeasure.labMeasureText, labMeasure.labAccuracyText, nextButton);


        labMeasure.labMeasureText.addKeyListener(verifiedKeyListener);
        labMeasure.labMeasureText.setFont(font);
        c.gridx = 1;
        c.gridy = row++;
        labResultsPanel.add(labMeasure.labMeasureText, c);


        JLabel labAccuracyLabel = new JLabel(MEASURE_ACCURACY);
        labAccuracyLabel.setFont(font);
        c.gridx = 0;
        c.gridy = row;
        labResultsPanel.add(labAccuracyLabel, c);

        labMeasure.labAccuracyText.addKeyListener(verifiedKeyListener);
        labMeasure.labAccuracyText.setFont(font);
        c.gridx = 1;
        c.gridy = row++;
        labResultsPanel.add(labMeasure.labAccuracyText, c);


        c.gridx = 1;
        c.gridy = row++;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.PAGE_END;
        labResultsPanel.add(nextButton, c);

        return labResultsPanel;
    }

    private JPanel createTestInfoPanel() {
        JPanel testInfoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.insets = new Insets(10, 10, 5, 5);

        JButton startPageNextButton = createNextButton();

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        testInfoPanel.add(createUUIDTextPane(), c);


        JLabel labsCountLabel = new JLabel("Число испытательных лабораторий");
        labsCountLabel.setFont(font);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        testInfoPanel.add(labsCountLabel, c);

        labsCountText = new JTextField("3");
        labsCountText.addKeyListener(new VerifiedKeyListener(new IntVerifier(), labsCountText, startPageNextButton));
        labsCountText.setFont(font);
        c.gridx = 1;
        c.gridy = 1;
        testInfoPanel.add(labsCountText, c);

        JLabel measureParamLabel = new JLabel(PARAMETER_NAME);
        measureParamLabel.setFont(font);
        c.gridx = 0;
        c.gridy = 2;
        testInfoPanel.add(measureParamLabel, c);

        parameterNameText = new JTextField("Электромагнитное излучение");
        parameterNameText.setFont(font);
        c.gridx = 1;
        c.gridy = 2;
        testInfoPanel.add(parameterNameText, c);

        JLabel measureUnitLabel = new JLabel(PARAMETER_UNITS);
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

        successCriteriaText = new JTextField("1.0");
        successCriteriaText.addKeyListener(
                new VerifiedKeyListener(new DoubleVerifier(), successCriteriaText, startPageNextButton));
        successCriteriaText.setFont(font);
        c.gridx = 1;
        c.gridy = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        testInfoPanel.add(successCriteriaText, c);

        JLabel pointNumberLabel = new JLabel("Номер начальной точки измерений");
        pointNumberLabel.setFont(font);
        c.gridx = 0;
        c.gridy = 5;
        c.fill = GridBagConstraints.HORIZONTAL;
        testInfoPanel.add(pointNumberLabel, c);

        pointNumberText = new JTextField("1");
        pointNumberText.addKeyListener(new VerifiedKeyListener(new IntVerifier(), pointNumberText, startPageNextButton));
        pointNumberText.setFont(font);
        c.gridx = 1;
        c.gridy = 5;
        c.fill = GridBagConstraints.HORIZONTAL;
        testInfoPanel.add(pointNumberText, c);

        ActionListener[] listeners = startPageNextButton.getActionListeners();
        for (ActionListener listener : listeners) {
            startPageNextButton.removeActionListener(listener);
        }
        startPageNextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPointNumber(Integer.parseInt(pointNumberText.getText()));
                initMainPanelLayout();
                cardLayout.show(mainPanel, labPanelString + "1");
            }
        });
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 2;
        testInfoPanel.add(Box.createVerticalStrut(40), c);

        c.gridx = 1;
        c.gridy = 7;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.PAGE_END;
        testInfoPanel.add(startPageNextButton, c);
        return testInfoPanel;
    }

    private void initMainPanelLayout() {
        testCase = new LabTestCase(0.0);
        labMeasures = new ArrayList<>();
        for (int i = 0; i < Integer.parseInt(labsCountText.getText()); ++i) {
            LabMeasure measure = new LabMeasure(i + 1);
            labMeasures.add(measure);
            mainPanel.add(labPanelString + Integer.toString(i + 1), createLabResultsPanel(measure));
        }
        mainPanel.add(createHTMLTestResultsPanel());
    }

    private void setPointNumber(int number) {
        pointNumber = number;
    }

    private void nextPointNumber() {
        pointNumber++;
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


        setMinimumSize(new Dimension(900, 900));


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
