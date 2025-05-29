package ru.ObfuscatorC;


import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import ru.ObfuscatorC.Tokens.CBaseVisitor;
import ru.ObfuscatorC.Tokens.CParser;

import java.util.*;


public class Generation extends CBaseVisitor<String> {
    private static TokenStreamRewriter rewriter = null;
    private static Map<String, List<String>> graph = new LinkedHashMap<>();
    private static List<ParserRuleContext> parseTreeNodes = new ArrayList<>();
    private static Map<String, String> bogusBranchMetadata = new HashMap<>();
    public Generation(Map<String, String> bogusBranchMetadata, Map<String, List<String>> graph, List<ParserRuleContext> parseTreeNodes, TokenStream tokens) {
        this.bogusBranchMetadata = bogusBranchMetadata;
        this.graph = graph;
        this.parseTreeNodes = parseTreeNodes;
        this.rewriter = new TokenStreamRewriter(tokens);
    }
    public static List<String> getBogusFunctions() {
        List<String> functions = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
            String node = entry.getKey();
            if (node.contains("bogus_function")) {
                functions.add(node);
            }
        }
        return functions;
    }

    public static List<String> getBogusBlocks() {
        List<String> blocks = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
            String node = entry.getKey();
            if (node.contains("bogus_block")) {
                blocks.add(node);
            }
        }
        return blocks;
    }
    public void injectBogusCode() {
        // 1. Вставляем все bogus-функции
        for (String func : getBogusFunctions()) {
            injectBogusFunction(func);
        }

        // 2. Вставляем все bogus-блоки
        for (String block : getBogusBlocks()) {
            injectBogusBlock(block);
        }
    }
    private static void injectBogusFunction(String funcNode) {
        String code = CRandomCodeGenerator.generateRandomFunction();

        CommonTokenStream tokens = (CommonTokenStream) rewriter.getTokenStream();
        List<Token> tokenList = tokens.getTokens();

        int insertIndex = -1;

        for (int i = 0; i < tokenList.size(); i++) {
            Token token = tokenList.get(i);
            // Находим начало функции: тип + имя + (
            if (token.getText().matches("int|void|char|float|double")) {
                for (int j = i + 1; j < i + 4 && j < tokenList.size(); j++) {
                    if (tokenList.get(j).getText().equals("(")) {
                        insertIndex = token.getTokenIndex();
                        break;
                    }
                }
            }
            if (insertIndex != -1) break;
        }

        if (insertIndex != -1) {
            Token insertBeforeToken = tokens.get(insertIndex);
            rewriter.insertBefore(insertBeforeToken, code + "\n");
        } else {
            // fallback — в конец
            Token eof = tokens.get(tokens.size() - 1);
            rewriter.insertBefore(eof, code + "\n");
        }
    }

    private static void injectBogusBlock(String blockNode) {
        String code = CRandomCodeGenerator.generateRandomStatement();

        // Ищем начало блока main (первая '{' после 'int main()')
        for (ParseTree node : parseTreeNodes) {
            if (node instanceof CParser.BlockContext) {
                Token startToken = ((ParserRuleContext) node).getStart();
                rewriter.insertAfter(startToken, code); // Вставляем сразу после '{'
                break;
            }
        }
    }
    public String getModifiedCode() {
        return rewriter.getText();
    }
    public String formatCCode(String code) {
        code = code.replaceAll("#include\\s*<[^>]+>", "$0\n");
        code = code.replaceAll("\\b(int|float|double|char|long|short|void)(\\s*)([a-zA-Z_][a-zA-Z0-9_]*)", "$1 $3");
        code = code.replaceAll("\\breturn(?=\\S)", "return ");

        return code;
    }



}
