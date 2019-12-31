import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private UUID uuid = UUID.randomUUID();;
    private ArrayList<LabMeasure> labMeasures;
    private final String cssStyle = "<p style=\"font-size:20px; text-align:center\">";

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

    private JTextPane createUUIDTextPane() {
        JTextPane UUIDTextPanel = new JTextPane();
        UUIDTextPanel.setContentType("text/html"); // let the text pane know this is what you want
        UUIDTextPanel.setText("<html>" + cssStyle + "Уникальный идентификатор теста:<br>" + uuid.toString() + "</p></html>"); // showing off
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
        uuidPanel.add(createNextButton(), c);
        return uuidPanel;
    }

    private static class LabMeasure {
        int labNumber;
        JTextField labMeasureText;
        JTextField labAccuracyText;
        LabMeasure(int labNumber) {
            this.labNumber = labNumber;
        }
    }

    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    private JPanel createTestResultsPanel() {
        JPanel testResultsPanel = new JPanel();
        testResultsPanel.setLayout(new BoxLayout(testResultsPanel, BoxLayout.Y_AXIS));
        testResultsPanel.add(Box.createVerticalStrut(10));
//        testResultsPanel.add(new JLabel("Уникальный идентификатор теста: " + uuid.toString()));
        testResultsPanel.add(createUUIDTextPane());
        testResultsPanel.add(Box.createVerticalStrut(10));
        testResultsPanel.add(new JLabel("Результаты"));

        testResultsPanel.add(Box.createVerticalStrut(10));
        JLabel meanValueLabel = new JLabel("Среднее значение измерений:");
        JLabel meanErrorLabel = new JLabel("Среднеквадратичная погрешность:");


        JTextPane stringForMD5TextPane = new JTextPane();
        stringForMD5TextPane.setContentType("text/html"); // let the text pane know this is what you want
        stringForMD5TextPane.setText("<html>" + cssStyle + "Строка для контрольной суммы MD5: </p></html>"); // showing off
        stringForMD5TextPane.setEditable(false); // as before
        stringForMD5TextPane.setBackground(null); // this is the same as a JLabel
        stringForMD5TextPane.setBorder(null);

        JTextPane MD5TextPane = new JTextPane();
        MD5TextPane.setContentType("text/html"); // let the text pane know this is what you want
        MD5TextPane.setText("<html>" + cssStyle + "MD5 контрольная сумма: </p></html>"); // showing off
        MD5TextPane.setEditable(false); // as before
        MD5TextPane.setBackground(null); // this is the same as a JLabel
        MD5TextPane.setBorder(null);

        testResultsPanel.add(meanValueLabel);
        testResultsPanel.add(meanErrorLabel);

        testResultsPanel.add(Box.createVerticalStrut(10));

        testResultsPanel.add(stringForMD5TextPane);
        testResultsPanel.add(MD5TextPane);

        testResultsPanel.add(Box.createVerticalStrut(10));
        ArrayList<JLabel> labResultLabels = new ArrayList<JLabel>();
        for (LabMeasure measure : labMeasures) {
            JLabel labResultLabel = new JLabel("Лаборатория номер " + Integer.toString(measure.labNumber) + ".");
            testResultsPanel.add(labResultLabel);
            labResultLabels.add(labResultLabel);
        }
        testResultsPanel.add(Box.createVerticalStrut(10));


        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        JButton calculateButton = new JButton("Рассчитать результаты");
        ArrayList<Double> values = new ArrayList<>();
        ArrayList<Double> errors = new ArrayList<>();
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                double meanValue = 0;
                double meanSquareError = 0;
                for (LabMeasure labMeasure : labMeasures) {
                    double value = Double.parseDouble(labMeasure.labMeasureText.getText());
                    double error = Double.parseDouble(labMeasure.labAccuracyText.getText());
                    meanValue += value;
                    meanSquareError += error * error;
                    values.add(value);
                    errors.add(error);
                }
                meanValue /= labMeasures.size();
                meanSquareError = Math.sqrt(meanSquareError);
                meanValueLabel.setText(meanValueLabel.getText() + " " + String.format("%.4f", meanValue));
                meanErrorLabel.setText(meanErrorLabel.getText() + " " + String.format("%.4f", meanSquareError));

                values.sort(Double::compareTo);
                errors.sort(Double::compareTo);
                String md5String = "";
                for (Double value : values) {
                    md5String += String.format("%.4f;", value);
                }
                for (Double error : errors) {
                    md5String += String.format("%.4f;", error);
                }
                md5String += String.format(successCriteriaText.getText());

                stringForMD5TextPane.setText("<html>" + cssStyle + "Строка для контрольной суммы MD5:<br>" + md5String + "</p></html");
                try {
                    String md5Result = bytesToHex(MessageDigest.getInstance("MD5").digest(md5String.getBytes()));
                    MD5TextPane.setText("<html>" + cssStyle + "MD5 контрольная сумма:<br>" + md5Result + "</p></html>");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < labMeasures.size(); ++i) {
                    double diff = Math.abs(Double.parseDouble(labMeasures.get(i).labMeasureText.getText()) -
                            meanValue) / meanSquareError;
                    labResultLabels.get(i).setText(labResultLabels.get(i).getText() + " Отклонение " +
                            String.format("%.4f", diff) + ". Оценка результата: " +
                             (diff < Double.parseDouble(successCriteriaText.getText()) ? " Уд." : " Неуд."));
                }
            }
        });

        buttonsPanel.add(calculateButton);
        buttonsPanel.add(Box.createHorizontalStrut(30));
        buttonsPanel.add(new JButton("Экспорт в pdf"));

        testResultsPanel.add(buttonsPanel);
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


        JLabel labAccuracyLabel = new JLabel("Погрешность измерения лаборатории");
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

        JLabel labsCountLabel = new JLabel("Число лабораторий");
        labsCountLabel.setFont(font);
        c.gridx = 0;
        c.gridy = 0;
        testInfoPanel.add(labsCountLabel, c);

        labsCountText = new JTextField("3");
        labsCountText.setFont(font);
        c.gridx = 1;
        c.gridy = 0;
        testInfoPanel.add(labsCountText, c);

        JLabel measureParamLabel = new JLabel("Измеряемый параметр");
        measureParamLabel.setFont(font);
        c.gridx = 0;
        c.gridy = 1;
        testInfoPanel.add(measureParamLabel, c);

        parameterNameText = new JTextField("Электромагнитное излучение");
        parameterNameText.setFont(font);
        c.gridx = 1;
        c.gridy = 1;
        testInfoPanel.add(parameterNameText, c);

        JLabel measureUnitLabel = new JLabel("Единицы измерения");
        measureUnitLabel.setFont(font);
        c.gridx = 0;
        c.gridy = 2;
        testInfoPanel.add(measureUnitLabel, c);

        parameterUnitsText = new JTextField("мкВт/см²");
        parameterUnitsText.setFont(font);
        c.gridx = 1;
        c.gridy = 2;
        testInfoPanel.add(parameterUnitsText, c);

        JLabel successCriteriaLabel = new JLabel("Критерий оценки");
        successCriteriaLabel.setFont(font);
        c.gridx = 0;
        c.gridy = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        testInfoPanel.add(successCriteriaLabel, c);

        successCriteriaText = new JTextField("1");
        successCriteriaText.setFont(font);
        c.gridx = 1;
        c.gridy = 3;
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
                mainPanel.add(createTestResultsPanel());
            }
        });
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        testInfoPanel.add(Box.createVerticalStrut(40), c);

        c.gridx = 1;
        c.gridy = 5;
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


        setMinimumSize(new Dimension(700, 400));


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
