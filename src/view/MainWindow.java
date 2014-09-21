package view;

import controller.Main;
import model.imageProcessing.OtsuBinarize;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class MainWindow {
    private JButton recognizeButton;
    private JPanel panel1;
    private JButton train1Button;
    private JButton train2Button;
    private JButton train3Button;
    private JButton train4Button;

    public MainWindow() {
        recognizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main main = new Main();
                main.in = chooseFile();
                try {
                    OtsuBinarize.c = 7;
                    main.processImage();
                    setButtonsEnable(false);
                    boolean success = main.recognize();
                    if(success) {
                        main.saveFile(getSaveFile());
                    }
                    setButtonsEnable(true);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        train1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main main = new Main();
                main.in = chooseFile();
                try {
                    OtsuBinarize.c = 6;
                    main.processImage();
                    setButtonsEnable(false);
                    main.train(0);
                    setButtonsEnable(true);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        train2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main main = new Main();
                main.in = chooseFile();
                try {
                    OtsuBinarize.c = 6;
                    main.processImage();
                    setButtonsEnable(false);
                    main.train(1);
                    setButtonsEnable(true);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        train3Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main main = new Main();
                main.in = chooseFile();
                try {
                    OtsuBinarize.c = 6;
                    main.processImage();
                    setButtonsEnable(false);
                    main.train(2);
                    setButtonsEnable(true);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        train4Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main main = new Main();
                main.in = chooseFile();
                try {
                    OtsuBinarize.c = 6;
                    main.processImage();
                    setButtonsEnable(false);
                    main.train(3);
                    setButtonsEnable(true);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public static void start(){

        JFrame frame = new JFrame("MainWindow");
        frame.setContentPane(new MainWindow().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public String chooseFile(){
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "JPG Images", "jpg");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String path = file.getAbsolutePath();
            return path.substring(0, path.length() - 4);
        }
        return null;
    }

    public File getSaveFile(){
        File fw = null;
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showSaveDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                fw = new File(chooser.getSelectedFile()+".txt");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return fw;
    }

    public void setButtonsEnable(boolean enabled){
        recognizeButton.setEnabled(enabled);
        train1Button.setEnabled(enabled);
        train2Button.setEnabled(enabled);
        train3Button.setEnabled(enabled);
        train4Button.setEnabled(enabled);

    }
}
