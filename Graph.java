package ru.ObfuscatorC;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.tree.ParseTree;
import ru.ObfuscatorC.Tokens.CBaseVisitor;
import ru.ObfuscatorC.Tokens.CParser;

import java.util.*;

public class Graph extends CBaseVisitor<String> {
    private int stateCounter = 0;
    private final Map<String, List<String>> graph = new LinkedHashMap<>();
    private final Deque<String> stateStack = new ArrayDeque<>();
    private final Map<String, String> bogusBranchMetadata = new HashMap<>();
    private final List<ParserRuleContext> parseTreeNodes = new ArrayList<>();

    private int obfuscationDegree;
    private boolean includesInserted = false;
    private boolean decryptFunctionInserted = false;
    private static TokenStreamRewriter rewriter = null;


    public Graph(int obfuscationDegree, TokenStream tokens) {
        this.obfuscationDegree = obfuscationDegree;
        this.rewriter = new TokenStreamRewriter(tokens);
    }
    public Map<String, List<String>> getGraph() {
        return graph;
    }
    public String getModifiedCode() {
        return rewriter.getText();
    }

    public Map<String, String> getBogusBranchMetadata() {
        return bogusBranchMetadata;
    }

    public List<ParserRuleContext> getParseTreeNodes() {
        return parseTreeNodes;
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

        parseTreeNodes.add(ctx);
        for (int i = 0; i < obfuscationDegree; i++) {
            String bogus = newState("function: bogus_function_" + i);
            bogusBranchMetadata.put(bogus, "function");
            popState();
        }
        for (var child : ctx.children) {
            visit(child);
        }

        popState();
        return null;
    }


    @Override
    public String visitFunction(CParser.FunctionContext ctx) {
        String oldName = ctx.ID().getText();
        if (oldName.equals("main")) {
            newState("function: " + oldName);
        } else {
            newState("function: " + oldName);
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
        newState("decl: " + ctx.type().getText() +" " + oldName);
        if (ctx.expression() != null) {
            visit(ctx.expression());
        }
        popState();
        return null;
    }
    @Override
    public String visitAssignment(CParser.AssignmentContext ctx) {

        if (ctx.ID() != null) {
            String oldName = ctx.ID().getText();
            newState("assign: " + oldName + " = " + ctx.expression().getText());
        }

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
//        parseTreeNodes.add(ctx);
        String value = "";
        if (ctx.expression() != null) {
            visit(ctx.expression());
            value = ctx.expression().getText();
        }
        newState("return " + value);
        popState();
        return null;
    }

    @Override
    public String visitBlock(CParser.BlockContext ctx) {
        newState("block");
        parseTreeNodes.add(ctx);
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
            newState("var: " + oldName);
            popState();
        } else if (ctx.NUMBER() != null) {
            String original = ctx.NUMBER().getText();
                newState("const: " + original);
                popState();
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
}