package ru.ObfuscatorC.Semantic;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.tree.ParseTree;
import ru.ObfuscatorC.CRandomCodeGenerator;
import ru.ObfuscatorC.Tokens.CBaseVisitor;
import ru.ObfuscatorC.Tokens.CParser;

import java.util.*;

public class SemanticAnalyzer extends CBaseVisitor<String> {
    private int stateCounter = 0;
    private final Map<String, List<String>> graph = new LinkedHashMap<>();
    private final Deque<String> stateStack = new ArrayDeque<>();
    private static TokenStreamRewriter rewriter = null;
    private static final Map<String, String> varMap = new HashMap<>();
    private static final Map<String, String> functionMap = new HashMap<>();
    private static final Map<String, String> bogusBranchMetadata = new HashMap<>();
    private static final List<ParserRuleContext> parseTreeNodes = new ArrayList<>();
    private static int funcCounter = 0;
    private static int varCounter = 0;
    private final int obfuscationDegree;

    public SemanticAnalyzer(TokenStream tokens, int obfuscationDegree) {
        this.rewriter = new TokenStreamRewriter(tokens);
        this.obfuscationDegree = obfuscationDegree;
    }
    public String getModifiedCode() {
        return rewriter.getText();
    }

    private static String rename(String original) {
        return varMap.computeIfAbsent(original, k -> "v" + varCounter++);
    }
    private static String renameFunction(String original) {
        return functionMap.computeIfAbsent(original, k -> "func" + funcCounter++);
    }

    private String newState(String label) {
        String state = "S" + stateCounter++ + " [" + label + "]";
        graph.put(state, new ArrayList<>());
        if (!stateStack.isEmpty()) {
            graph.get(stateStack.peek()).add(state);
        }
        stateStack.push(state);
        return state;
    }

    private void popState() {
        stateStack.pop();
    }

    public void printGraph() {
        System.out.println("Graph:");
        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
            for (String target : entry.getValue()) {
                System.out.println(entry.getKey() + " -> " + target);
            }
        }
    }

    @Override
    public String visitProgram(CParser.ProgramContext ctx) {
        newState("program");
        for (var child : ctx.children) {
            visit(child);
        }
        popState();
        return null;
    }

    @Override
    public String visitFunction(CParser.FunctionContext ctx) {
        String oldName = ctx.ID().getText();
        if (oldName.equals("main")){
            newState("function: " + oldName);
        } else if (functionMap.containsKey(oldName)){
            rewriter.replace(ctx.ID().getSymbol(), functionMap.get(oldName));
            newState("function: " + functionMap.get(oldName));
        }else if (!functionMap.containsKey(oldName)){
            String newName = renameFunction(oldName);
            rewriter.replace(ctx.ID().getSymbol(), newName);
            newState("function: " + newName);
        }
        for (int i = 0; i < obfuscationDegree; i++) {
            String bogus = newState("bogus_function_" + i);
            bogusBranchMetadata.put(bogus, "function");
            popState();
        }
        if (ctx.params() != null) {
            visit(ctx.params());
        }
        visit(ctx.block());
        popState();
        return null;
    }

    @Override
    public String visitParams(CParser.ParamsContext ctx) {
        newState("params");
        for (var p : ctx.param()) {
            visit(p);
        }
        popState();
        return null;
    }

    @Override
    public String visitParam(CParser.ParamContext ctx) {
        newState("param: " + ctx.type().getText() + " " + ctx.ID().getText());
        popState();
        return null;
    }

    @Override
    public String visitStruct(CParser.StructContext ctx) {
        newState("struct: " + ctx.ID().getText());
        for (var field : ctx.struct_field()) {
            visit(field);
        }
        popState();
        return null;
    }

    @Override
    public String visitStruct_field(CParser.Struct_fieldContext ctx) {
        newState("field: " + ctx.type().getText() + " " + ctx.ID().getText());
        popState();
        return null;
    }

    @Override
    public String visitGlobal_declaration(CParser.Global_declarationContext ctx) {
        newState("global: " + ctx.type().getText() + " " + ctx.ID().getText());
        if (ctx.value() != null) {
            visit(ctx.value());
        }
        popState();
        return null;
    }

    @Override
    public String visitDeclaration(CParser.DeclarationContext ctx) {
        String oldName = ctx.ID().getText();
        String newName = rename(oldName);
        rewriter.replace(ctx.ID().getSymbol(), newName);
        newState("decl: " + ctx.type().getText() +" " + newName);
        if (ctx.expression() != null) {
            visit(ctx.expression());
        }
        popState();
        return null;
    }

    @Override
    public String visitAssignment(CParser.AssignmentContext ctx) {
        // Переименование переменной слева от =
        if (ctx.ID() != null) {
            String oldName = ctx.ID().getText();
            String newName = varMap.getOrDefault(oldName, oldName);
            rewriter.replace(ctx.ID().getSymbol(), newName);
            newState("assign: " + newName + " = " + ctx.expression().getText());
        }

        // Обход выражения справа — чтобы тоже заменить переменные и числа
        visit(ctx.expression());

        popState();
        return null;
    }


    @Override
    public String visitIf_statement(CParser.If_statementContext ctx) {
        newState("if: " + ctx.expression().getText());
        visit(ctx.statement(0));
        if (ctx.statement().size() > 1) {
            newState("else");
            visit(ctx.statement(1));
            popState();
        }
        popState();
        return null;
    }

    @Override
    public String visitWhile_loop(CParser.While_loopContext ctx) {
        newState("while: " + ctx.expression().getText());
        visit(ctx.statement());
        popState();
        return null;
    }

    @Override
    public String visitFor_loop(CParser.For_loopContext ctx) {
        newState("for loop");
        if (ctx.declaration() != null) visit(ctx.declaration());
        if (ctx.expression() != null) visit(ctx.expression());
//        if (ctx.assignment() != null) visit(ctx.assignment());
        visit(ctx.statement());
        popState();
        return null;
    }

    @Override
    public String visitReturn_statement(CParser.Return_statementContext ctx) {
        parseTreeNodes.add(ctx);
        String value = "";
        if (ctx.expression() != null) {
            visit(ctx.expression()); // для замены переменных и чисел
            value = ctx.expression().getText(); // берём оригинальный (или переименованный) текст
        }
        String newName ="";
        if (varMap.containsKey(value)){
            newName = varMap.get(value);
        }
        newState("return " + newName);
        popState();
        return null;
    }


    @Override
    public String visitBlock(CParser.BlockContext ctx) {
        newState("block");
        for (int i = 0; i < obfuscationDegree; i++) {
            String bogus = newState("bogus_block_" + i);
            bogusBranchMetadata.put(bogus, "block");
            popState();
        }
        for (var stmt : ctx.statement()) {
            visit(stmt);
        }
        popState();
        return null;
    }

    @Override
    public String visitExpression(CParser.ExpressionContext ctx) {
        return visit(ctx.logical_or());
    }

    @Override
    public String visitLogical_or(CParser.Logical_orContext ctx) {
        newState("logical_or");
        for (var child : ctx.logical_and()) {
            visit(child);
        }
        popState();
        return null;
    }

    @Override
    public String visitLogical_and(CParser.Logical_andContext ctx) {
        newState("logical_and");
        for (var child : ctx.equality()) {
            visit(child);
        }
        popState();
        return null;
    }

    @Override
    public String visitEquality(CParser.EqualityContext ctx) {
        newState("equality: " + ctx.getText());
        for (var r : ctx.relational()) {
            visit(r);
        }
        popState();
        return null;
    }

    @Override
    public String visitRelational(CParser.RelationalContext ctx) {
        newState("relational: " + ctx.getText());
        for (var a : ctx.additive()) {
            visit(a);
        }
        popState();
        return null;
    }

    @Override
    public String visitAdditive(CParser.AdditiveContext ctx) {
        newState("add: " + ctx.getText());
        for (var m : ctx.multiplicative()) {
            visit(m);
        }
        popState();
        return null;
    }

    @Override
    public String visitMultiplicative(CParser.MultiplicativeContext ctx) {
        newState("mul: " + ctx.getText());
        for (var p : ctx.shift()) {
            visit(p);
        }
        popState();
        return null;
    }

    @Override
    public String visitShift(CParser.ShiftContext ctx) {
        newState("shift: " + ctx.getText());
        for (var p : ctx.bitwise_and()) {
            visit(p);
        }
        popState();
        return null;
    }

    @Override
    public String visitBitwise_and(CParser.Bitwise_andContext ctx) {
        newState("and_b: " + ctx.getText());
        for (var p : ctx.bitwise_xor()) {
            visit(p);
        }
        popState();
        return null;
    }

    @Override
    public String visitBitwise_xor(CParser.Bitwise_xorContext ctx) {
        newState("xor_b: " + ctx.getText());
        for (var p : ctx.bitwise_or()) {
            visit(p);
        }
        popState();
        return null;
    }

    @Override
    public String visitBitwise_or(CParser.Bitwise_orContext ctx) {
        newState("or_b: " + ctx.getText());
        for (var p : ctx.primary()) {
            visit(p);
        }
        popState();
        return null;
    }

    @Override
    public String visitPrimary(CParser.PrimaryContext ctx) {
        if (ctx.ID() != null) {
            String oldName = ctx.ID().getText();
            String newName = varMap.getOrDefault(oldName, oldName);
            rewriter.replace(ctx.ID().getSymbol(), newName);
            newState("var: " + newName);
            popState();
        } else if (ctx.NUMBER() != null) {
            String original = ctx.NUMBER().getText();
            try {
                int value = Integer.parseInt(original);
                String obfuscated = obfuscateNumber(value);
                rewriter.replace(ctx.NUMBER().getSymbol(), obfuscated);
                newState("const: " + original);
//                newState("const: " + original );
                popState();
            } catch (NumberFormatException e) {
                // fallback: skip obfuscation for invalid number
            }
        } else if (ctx.STRING() != null || ctx.CHAR() != null) {
            newState("const: " + ctx.getText());
            popState();
        } else if (ctx.expression() != null) {
            return visit(ctx.expression());
        } else if (ctx.function_call() != null) {
            return visit(ctx.function_call());
        } else if (ctx.struct_member_access() != null) {
            return visit(ctx.struct_member_access());
        }
        return null;
    }
    private String obfuscateNumber(int n) {
        Random rand = new Random(n * 31L + 17); // стабильный, но переменный

        if (n == 0) {
            switch (rand.nextInt(3)) {
                case 0: return "5 % 5";
                case 1: return "3 - 3";
                default: return "(1 << 2) - 4";
            }
        }

        if (n == 1) {
            switch (rand.nextInt(4)) {
                case 0: return "6 % 5";
                case 1: return "7 - 6";
                case 2: return "(1 << 0)";
                default: return "9 / 9";
            }
        }

        // для n > 1 — более сложная обфускация
        int k = rand.nextInt(2, 10);
        int quotient = n / k;
        int remainder = n % k;

        String expr = "(" + quotient + " * " + k + ")";
        if (remainder != 0) {
            expr += " + " + remainder;
        }

        if (rand.nextBoolean()) {
            int fakeAdd = rand.nextInt(1, 5);
            int modBase = n + fakeAdd;
            expr = "(" + expr + " + " + fakeAdd + ") % " + modBase + " + " + (n - ((n + fakeAdd) % modBase));
        }

        return expr;
    }


    @Override
    public String visitFunction_call(CParser.Function_callContext ctx) {
        String oldName = ctx.ID().getText();
        if (functionMap.containsKey(oldName)) {
            rewriter.replace(ctx.ID().getSymbol(), functionMap.get(oldName));
            newState("call: " + functionMap.get(oldName) );
        }else if (!functionMap.containsKey(oldName)){
            String newName = renameFunction(oldName);
            rewriter.replace(ctx.ID().getSymbol(), newName);
            newState("call: " + newName);
        }
        if (ctx.args() != null) {
            visit(ctx.args());
        }
        popState();
        return null;
    }

    @Override
    public String visitArgs(CParser.ArgsContext ctx) {
        for (var expr : ctx.expression()) {
            visit(expr);
        }
        return null;
    }

    @Override
    public String visitStruct_member_access(CParser.Struct_member_accessContext ctx) {
        newState("access: " + ctx.ID(0).getText() + "." + ctx.ID(1).getText());
        popState();
        return null;
    }

    @Override
    public String visitValue(CParser.ValueContext ctx) {
        if (ctx.expression() != null) {
            return visit(ctx.expression());
        } else if (ctx.ID() != null || ctx.NUMBER() != null || ctx.STRING() != null || ctx.CHAR() != null) {
            newState("value: " + ctx.getText());
            popState();
        }
        return null;
    }
//    @Override
//    public String visit(ParseTree parseTree) {
//        if (parseTree == null) {
//            System.out.println("Попытка посетить null-узел");
//            return null;
//        }
//        System.out.println("Посещение узла: " + parseTree.getClass().getSimpleName());
//        return parseTree.accept(this);
//    }
    public String toDot() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph StateGraph {\n");
        sb.append("  node [shape=box, style=filled, fillcolor=lightgray];\n");

        for (String from : graph.keySet()) {
            String fromLabel = from.replace("\"", "\\\"");
            sb.append("  \"" + fromLabel + "\";\n");
            for (String to : graph.get(from)) {
                String toLabel = to.replace("\"", "\\\"");
                sb.append("  \"" + fromLabel + "\" -> \"" + toLabel + "\";\n");
            }
        }

        sb.append("}\n");
        return sb.toString();
    }
    public String formatCCode(String code) {
        // Исправляем разорванные инкременты и декременты: i + + -> i++
        code = code.replaceAll("(\\w+)\\s*\\+\\s*\\+", "$1++");
        code = code.replaceAll("(\\w+)\\s*\\-\\s*\\-", "$1--");

        // Добавляем перенос строки после точки с запятой
        code = code.replaceAll(";", ";\n");

        // Добавляем переносы строк вокруг фигурных скобок
        code = code.replaceAll("\\{", " {\n");
        code = code.replaceAll("}", "\n}\n");

        // Расставляем пробелы между типами и переменными (например, intx -> int x)
        code = code.replaceAll("(int|float|double|char|long|short|void)(\\s*)(\\w)", "$1 $3");

        // Расставляем пробелы вокруг операторов (осторожно, без порчи ++, --, == и т.д.)
        code = code.replaceAll("(?<=[^+<>!=])=(?=[^=])", " = ");
        code = code.replaceAll("(?<!\\+|\\-)\\+(?!\\+)", " + ");
        code = code.replaceAll("(?<!\\+|\\-)\\-(?!\\-)", " - ");
        code = code.replaceAll("\\*", " * ");
        code = code.replaceAll("/", " / ");
        code = code.replaceAll("%", " % ");

        // Схлопываем повторяющиеся пробелы (но не убиваем пробелы между типами и именами!)
        code = code.replaceAll("[ \t]+", " ");

        // Отступы по уровню вложенности
        String[] lines = code.split("\n");
        StringBuilder result = new StringBuilder();
        int indentLevel = 0;
        for (String line : lines) {
            line = line.trim();
            if (line.endsWith("}")) indentLevel--;
            for (int i = 0; i < indentLevel; i++) result.append("    ");
            result.append(line).append("\n");
            if (line.endsWith("{")) indentLevel++;
        }

        return result.toString().trim();
    }

    public static void injectBogusCode() {
        for (Map.Entry<String, String> entry : bogusBranchMetadata.entrySet()) {
            String state = entry.getKey();
            String type = entry.getValue();

            switch (type) {
                case "function" -> injectBogusFunction(state);
                case "block" -> injectBogusBlock(state);
            }
        }
    }
    private static void injectBogusFunction(String state) {
//        String funcName = renameFunction("bogus_" + state);
//        String varName = rename("x");
//        String iVar = rename("i");
//
//        String code = "\nint " + funcName + "() {\n" +
//                "    int " + varName + " = 0;\n" +
//                "    for (int " + iVar + " = 0; " + iVar + " < 10; " + iVar + "++) {\n" +
//                "        " + varName + " += (" + iVar + " * " + iVar + ") % 3;\n" +
//                "    }\n" +
//                "    return " + varName + ";\n" +
//                "}\n";
        String code = CRandomCodeGenerator.generateRandomFunction();

        Token eof = rewriter.getTokenStream().get(rewriter.getTokenStream().size() - 1);
        rewriter.insertBefore(eof, code);
    }


    private static void injectBogusBlock(String state) {
//        String varName = rename("tmp" + state);
//        String code = "    int " + varName + " = 0;\n" +
//                "    while (" + varName + " < 2) {\n" +
//                "        " + varName + " +1;\n" +
//                "    }\n";
        String code = CRandomCodeGenerator.generateRandomStatement();

        // вставим перед return
        for (ParseTree node : parseTreeNodes) {
            if (node instanceof CParser.Return_statementContext ret) {
                Token token = ((ParserRuleContext) ret).getStart();
                rewriter.insertBefore(token, code);
                return;
            }
        }

    }





}
