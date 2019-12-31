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

    private JTextField labCountText;
    private JTextField parameterNameText;
    private JTextField successCriteriaText;
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
        testResultsPanel.add(new JLabel("Уникальный идентификатор теста: " + uuid.toString()));
        testResultsPanel.add(Box.createVerticalStrut(10));
        testResultsPanel.add(new JLabel("Результаты"));

        testResultsPanel.add(Box.createVerticalStrut(10));
        JLabel meanValueLabel = new JLabel("Среднее значение измерений:");
        JLabel meanErrorLabel = new JLabel("Среднеквадратичная погрешность:");


        JTextPane stringForMD5TextPane = new JTextPane();
        stringForMD5TextPane.setContentType("text/html"); // let the text pane know this is what you want
        stringForMD5TextPane.setText("<html>Строка для контрольной суммы MD5: </html>"); // showing off
        stringForMD5TextPane.setEditable(false); // as before
        stringForMD5TextPane.setBackground(null); // this is the same as a JLabel
        stringForMD5TextPane.setBorder(null);

        JTextPane MD5TextPane = new JTextPane();
        MD5TextPane.setContentType("text/html"); // let the text pane know this is what you want
        MD5TextPane.setText("<html>MD5 контрольная сумма: </html>"); // showing off
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

                stringForMD5TextPane.setText("<html>Строка для контрольной суммы MD5: " + md5String + "</html");
                try {
                    String md5Result = bytesToHex(MessageDigest.getInstance("MD5").digest(md5String.getBytes()));
                    MD5TextPane.setText("<html>MD5 контрольная сумма: " + md5Result + "</html>");
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

        labResultsPanel.add(Box.createVerticalStrut(10));
        labResultsPanel.add(new JLabel("Уникальный идентификатор теста: " + uuid.toString()));
        labResultsPanel.add(Box.createVerticalStrut(10));
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
        parameterNameText.setMaximumSize(new Dimension(300, 50));
        parameterNamePanel.add(parameterNameText);

        JPanel successCriteriaPanel = new JPanel();
        successCriteriaPanel.setLayout(new BoxLayout(successCriteriaPanel, BoxLayout.X_AXIS));

        successCriteriaPanel.add(new JLabel("Критерий оценки"));
        successCriteriaText = new JTextField("1");
        successCriteriaText.setMaximumSize(new Dimension(200, 50));
        successCriteriaPanel.add(successCriteriaText);

        testInfoPanel.add(Box.createVerticalStrut(10));
        testInfoPanel.add(labCountPanel);
        testInfoPanel.add(Box.createVerticalStrut(10));
        testInfoPanel.add(parameterNamePanel);
        testInfoPanel.add(Box.createVerticalStrut(10));
        testInfoPanel.add(successCriteriaPanel);
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
