package editor;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TextEditor extends JFrame {


    private JTextArea textArea;
    private String filePath = null;
    private Boolean isRegex = false;
    private JTextField searchField;
    private HashMap<Integer, Integer> searchResult;
    private int currentIndex;
    private ArrayList<Integer> searchResStart;
    private ArrayList<Integer> searchResEnd;
    private JFileChooser fileChooser;
    private boolean isSaved;

    public TextEditor() {
        super("The first app");

        fileChooser = new JFileChooser();
        fileChooser.setName("FileChooser");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);

        // MenuFile
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menu.setName("MenuFile");
        menu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);

        //MenuFile Buttons
        JMenuItem menuOpen = new JMenuItem("Open");
        JMenuItem menuSave = new JMenuItem("Save");
        JMenuItem menuExit = new JMenuItem("Exit");

        menuOpen.setName("MenuOpen");
        menuSave.setName("MenuSave");
        menuExit.setName("MenuExit");

        menuOpen.addActionListener(new OpenButton());
        menuSave.addActionListener(new SaveButton());
        menuExit.addActionListener(e -> {
            System.exit(0);
        });
        menu.add(menuOpen);
        menu.add(menuSave);
        menu.addSeparator();
        menu.add(menuExit);

        //MenuSearch
        JMenu searchMenu = new JMenu("Search");
        searchMenu.setName("MenuSearch");
        menu.setMnemonic(KeyEvent.VK_S);
        menuBar.add(searchMenu);

        //MenuFileButtons
        JMenuItem menuSearch = new JMenuItem("Start search");
        JMenuItem menuPrevious = new JMenuItem("Previous search");
        JMenuItem menuNext = new JMenuItem("Next match");
        JMenuItem menuRegex = new JMenuItem("Use regex");

        menuSearch.setName("MenuStartSearch");
        menuPrevious.setName("MenuPreviousMatch");
        menuNext.setName("MenuNextMatch");
        menuRegex.setName("MenuUseRegExp");

        menuSearch.addActionListener(new SearchButton());
        menuPrevious.addActionListener(new PreviousMatchButton());
        menuNext.addActionListener(new NextMatchButton());
        menuRegex.addItemListener(new UseRegExCheckbox());

        searchMenu.add(menuSearch);
        searchMenu.add(menuPrevious);
        searchMenu.add(menuNext);
        searchMenu.add(menuRegex);

        menuBar.add(searchMenu);
        setJMenuBar(menuBar);


        // Main textEdit panel
        textArea = new JTextArea();
        textArea.setBounds(17, 17, 500, 400);
        textArea.setName("TextArea");
        textArea.setRows(16);
        textArea.setColumns(60);

        // Search panel
        searchField = new JTextField();
        searchField.setName("SearchField");
        searchField.setColumns(35);

        // Button Save
        JButton saveButton = new JButton((new ImageIcon("Text Editor/task/res/SaveIcon.png")));
        saveButton.setName("SaveButton");
        saveButton.setContentAreaFilled(false);
        saveButton.setFocusPainted(false);
        saveButton.setOpaque(false);
        saveButton.setMargin(new Insets(1, 1, 1, 1));
        saveButton.addActionListener(new SaveButton());

        // Button Open
        JButton openButton = new JButton(new ImageIcon("Text Editor/task/res/OpenIcon.png"));
        openButton.setName("OpenButton");
        openButton.setContentAreaFilled(false);
        openButton.setFocusPainted(false);
        openButton.setOpaque(false);
        openButton.setMargin(new Insets(1, 1, 1, 1));
        openButton.addActionListener(new OpenButton());

        // Button Search
        JButton searchButton = new JButton(new ImageIcon("Text Editor/task/res/FindIcon.png"));
        searchButton.setName("StartSearchButton");
        searchButton.setContentAreaFilled(false);
        searchButton.setFocusPainted(false);
        searchButton.setOpaque(false);
        searchButton.setMargin(new Insets(1, 1, 1, 1));
        searchButton.addActionListener(new SearchButton());

        // Button previousMatch
        JButton previousMatchButton = new JButton(new ImageIcon("Text Editor/task/res/BackIcon.png"));
        previousMatchButton.setName("PreviousMatchButton");
        previousMatchButton.setContentAreaFilled(false);
        previousMatchButton.setFocusPainted(false);
        previousMatchButton.setOpaque(false);
        previousMatchButton.setMargin(new Insets(1, 1, 1, 1));
        previousMatchButton.addActionListener(new PreviousMatchButton());

        //Button nextMatch

        JButton nextMatchButton = new JButton(new ImageIcon("Text Editor/task/res/ForwardIcon.png"));
        nextMatchButton.setName("NextMatchButton");
        nextMatchButton.setContentAreaFilled(false);
        nextMatchButton.setFocusPainted(false);
        nextMatchButton.setOpaque(false);
        nextMatchButton.setMargin(new Insets(1, 1, 1, 1));
        nextMatchButton.addActionListener(new NextMatchButton());

        //CheckBox

        JCheckBox useRegExCheckbox = new JCheckBox("Use regex");
        useRegExCheckbox.setName("UseRegExCheckbox");
        useRegExCheckbox.addItemListener(new UseRegExCheckbox());


        JPanel topPanel = new JPanel();
        JScrollPane bottomPanel = new JScrollPane(textArea);
        bottomPanel.setName("ScrollPane");

        topPanel.setLayout(new FlowLayout());
        bottomPanel.setLayout(new ScrollPaneLayout());

        topPanel.add(openButton);
        topPanel.add(saveButton);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(previousMatchButton);
        topPanel.add(nextMatchButton);
        topPanel.add(useRegExCheckbox);

        setLayout(new FlowLayout());
        add(topPanel);
        add(bottomPanel);
        add(fileChooser);
        setVisible(true);

    }

    private class OpenButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            fileChooser.setVisible(true);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(TextEditor.this);
            if (result == JFileChooser.APPROVE_OPTION)
                try {
                    filePath = fileChooser.getSelectedFile().getPath();
                    byte[] bytes = Files.readAllBytes(Paths.get(filePath));
                    textArea.setText(new String(bytes));
                    isSaved = false;
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            fileChooser.setVisible(false);
        }
    }

    private class SaveButton implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            fileChooser.setVisible(true);
            if (!isSaved) {
                try (FileWriter fileWriter = new FileWriter(new File(filePath))) {
                    String string = textArea.getText();
                    fileWriter.write(string);
                    isSaved = true;
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            } else {
                fileChooser.setDialogTitle("Saving file");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int res = fileChooser.showSaveDialog(TextEditor.this);
                if (res == JFileChooser.APPROVE_OPTION){
                    try (FileWriter fw = new FileWriter(fileChooser.getSelectedFile())){
                        fw.write(textArea.toString());
                    } catch (IOException exception){
                        exception.printStackTrace();
                    }
                }

            }

            fileChooser.setVisible(false);
        }

    }

    private class SearchButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isRegex) {
                String searchText = searchField.getText();
                Pattern pattern = Pattern.compile(searchText, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(textArea.getText());
                searchResStart = new ArrayList<>();
                searchResEnd = new ArrayList<>();
                currentIndex = -1;
                while (matcher.find()) {
                    searchResStart.add(matcher.start());
                    searchResEnd.add(matcher.end());
                }
            } else {
                String searchText = searchField.getText();
                Pattern pattern = Pattern.compile(searchText, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(textArea.getText());
                searchResStart = new ArrayList<>();
                searchResEnd = new ArrayList<>();
                currentIndex = -1;
                while (matcher.find()) {
                    searchResStart.add(matcher.start());
                    searchResEnd.add(matcher.end());
                }
            }
        }
    }

    private class PreviousMatchButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            currentIndex--;
            if (currentIndex < 0){
                currentIndex = searchResStart.size() - 1;
            }
            textArea.setCaretPosition(searchResStart.get(currentIndex));
            textArea.select(searchResStart.get(currentIndex), searchResEnd.get(currentIndex));
            textArea.grabFocus();
        }
    }

    private class NextMatchButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            currentIndex++;
            if (currentIndex >= searchResEnd.size()){
                currentIndex = 0;
            }
            textArea.setCaretPosition(searchResStart.get(currentIndex));
            textArea.select(searchResStart.get(currentIndex), searchResEnd.get(currentIndex));
            textArea.grabFocus();
        }
    }

    private class UseRegExCheckbox implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            isRegex = !isRegex;
        }
    }
}
