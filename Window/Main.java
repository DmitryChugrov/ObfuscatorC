package ru.ObfuscatorC.Window;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import ru.ObfuscatorC.Generation;
import ru.ObfuscatorC.Graph;
import ru.ObfuscatorC.Renaming;
import ru.ObfuscatorC.Tokens.CLexer;
import ru.ObfuscatorC.Tokens.CParser;

import javax.swing.*;
import java.io.*;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Выбор степени обфускации
                String levelInput = JOptionPane.showInputDialog(
                        null,
                        "Введите степень обфускации (1-3):",
                        "Степень обфускации",
                        JOptionPane.QUESTION_MESSAGE
                );

                if (levelInput == null) {
                    System.out.println("Отменено пользователем.");
                    return;
                }

                int level;
                try {
                    level = Integer.parseInt(levelInput.trim());
                    if (level < 1 || level > 3) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Введите число от 1 до 3.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 2. Выбор .c файла
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Выберите .c файл");
                int result = fileChooser.showOpenDialog(null);

                if (result != JFileChooser.APPROVE_OPTION) {
                    System.out.println("Файл не выбран.");
                    return;
                }

                File inputFile = fileChooser.getSelectedFile();
                String inputContent = Files.readString(inputFile.toPath());

                // 3. Лексер, парсер, граф
                CLexer lexer = new CLexer(CharStreams.fromString(inputContent));
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                CParser parser = new CParser(tokens);
                ParseTree tree = parser.program();

                Graph graph = new Graph(1, tokens); // степень передаём сюда
                graph.visit(tree);

                Generation generation = new Generation(
                        graph.getBogusBranchMetadata(),
                        graph.getGraph(),
                        graph.getParseTreeNodes(),
                        tokens
                );
                generation.injectBogusCode();

                String modifiedCode = generation.formatCCode(generation.getModifiedCode());

                // 4. Повторный парсинг и переименование
                CLexer lexer2 = new CLexer(CharStreams.fromString(modifiedCode));
                CommonTokenStream tokens2 = new CommonTokenStream(lexer2);
                CParser parser2 = new CParser(tokens2);
                ParseTree tree2 = parser2.program();
                Renaming renaming = new Renaming(tokens2);
                renaming.visit(tree2);

                String finalCode = generation.formatCCode(renaming.getModifiedCode());

                // 5. Сохранение
                JFileChooser saveChooser = new JFileChooser();
                saveChooser.setDialogTitle("Сохранить модифицированный файл");
                saveChooser.setSelectedFile(new File(
                        inputFile.getParent(),
                        removeExtension(inputFile.getName()) + "_mod.c"
                ));
                int saveResult = saveChooser.showSaveDialog(null);

                if (saveResult != JFileChooser.APPROVE_OPTION) {
                    System.out.println("Сохранение отменено.");
                    return;
                }

                File saveFile = saveChooser.getSelectedFile();
                Files.writeString(saveFile.toPath(), finalCode);
                JOptionPane.showMessageDialog(null, "Файл сохранен:\n" + saveFile.getAbsolutePath());

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static String removeExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? filename : filename.substring(0, dotIndex);
    }
}
