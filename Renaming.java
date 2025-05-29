package ru.ObfuscatorC;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;
import ru.ObfuscatorC.Tokens.CBaseVisitor;
import ru.ObfuscatorC.Tokens.CParser;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Renaming extends CBaseVisitor<String> {
    private static TokenStreamRewriter rewriter = null;
    private static final Map<String, String> varMap = new HashMap<>();
    private static final Map<String, String> functionMap = new HashMap<>();
    private static int funcCounter = 0;
    private static int varCounter = 0;
    private static final int FIXED_STRING_LENGTH = 16;

    public Renaming(TokenStream tokens) {
        this.rewriter = new TokenStreamRewriter(tokens);
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

    @Override
    public String visitProgram(CParser.ProgramContext ctx) {
        
        for (var child : ctx.children) {
            visit(child);
        }
        
        return null;
    }

    @Override
    public String visitFunction(CParser.FunctionContext ctx) {
        String oldName = ctx.ID().getText();
        if (oldName.equals("main")){
            
        }else if (oldName.equals("printf")){

        }else if (functionMap.containsKey(oldName)){
            rewriter.replace(ctx.ID().getSymbol(), functionMap.get(oldName));
            
        }else if (!functionMap.containsKey(oldName)){
            String newName = renameFunction(oldName);
            rewriter.replace(ctx.ID().getSymbol(), newName);
            
        }
        if (ctx.params() != null) {
            visit(ctx.params());
        }
        visit(ctx.block());
        
        return null;
    }

    @Override
    public String visitParams(CParser.ParamsContext ctx) {
        
        for (var p : ctx.param()) {
            visit(p);
        }
        
        return null;
    }

    @Override
    public String visitParam(CParser.ParamContext ctx) {
        
        
        return null;
    }

    @Override
    public String visitStruct(CParser.StructContext ctx) {
        
        for (var field : ctx.struct_field()) {
            visit(field);
        }
        
        return null;
    }

    @Override
    public String visitStruct_field(CParser.Struct_fieldContext ctx) {
        
        
        return null;
    }

    @Override
    public String visitGlobal_declaration(CParser.Global_declarationContext ctx) {
        
        if (ctx.value() != null) {
            visit(ctx.value());
        }
        
        return null;
    }

    @Override
    public String visitDeclaration(CParser.DeclarationContext ctx) {
        String oldName = ctx.ID().getText();
        String newName = rename(oldName);
        rewriter.replace(ctx.ID().getSymbol(), newName);
        
        if (ctx.expression() != null) {
            visit(ctx.expression());
        }
        
        return null;
    }

    @Override
    public String visitAssignment(CParser.AssignmentContext ctx) {
        if (ctx.ID() != null) {
            String oldName = ctx.ID().getText();
            String newName = varMap.getOrDefault(oldName, oldName);
            rewriter.replace(ctx.ID().getSymbol(), newName);
            
        }
        visit(ctx.expression());

        
        return null;
    }


    @Override
    public String visitIf_statement(CParser.If_statementContext ctx) {
        visit(ctx.expression());
        visit(ctx.statement(0));
        if (ctx.statement().size() > 1) {
            
            visit(ctx.statement(1));
            
        }
        
        return null;
    }

    @Override
    public String visitWhile_loop(CParser.While_loopContext ctx) {
        visit(ctx.expression());
        visit(ctx.statement());
        
        return null;
    }

    @Override
    public String visitFor_loop(CParser.For_loopContext ctx) {
        
        if (ctx.declaration() != null) visit(ctx.declaration());
        if (ctx.expression() != null) visit(ctx.expression());
//        if (ctx.assignment() != null) visit(ctx.assignment());
        visit(ctx.statement());
        
        return null;
    }

    @Override
    public String visitReturn_statement(CParser.Return_statementContext ctx) {
        String value = "";
        if (ctx.expression() != null) {
            visit(ctx.expression());
            value = ctx.expression().getText();
        }
        String newName ="";
        if (varMap.containsKey(value)){
            newName = varMap.get(value);
        }
        
        return null;
    }


    @Override
    public String visitBlock(CParser.BlockContext ctx) {

        for (var stmt : ctx.statement()) {
            visit(stmt);
        }
        
        return null;
    }

    @Override
    public String visitExpression(CParser.ExpressionContext ctx) {
        return visit(ctx.logical_or());
    }

    @Override
    public String visitLogical_or(CParser.Logical_orContext ctx) {
        
        for (var child : ctx.logical_and()) {
            visit(child);
        }
        
        return null;
    }

    @Override
    public String visitLogical_and(CParser.Logical_andContext ctx) {
        
        for (var child : ctx.equality()) {
            visit(child);
        }
        
        return null;
    }

    @Override
    public String visitEquality(CParser.EqualityContext ctx) {
        
        for (var r : ctx.relational()) {
            visit(r);
        }
        
        return null;
    }

    @Override
    public String visitRelational(CParser.RelationalContext ctx) {
        
        for (var a : ctx.additive()) {
            visit(a);
        }
        
        return null;
    }

    @Override
    public String visitAdditive(CParser.AdditiveContext ctx) {
        
        for (var m : ctx.multiplicative()) {
            visit(m);
        }
        
        return null;
    }

    @Override
    public String visitMultiplicative(CParser.MultiplicativeContext ctx) {
        
        for (var p : ctx.shift()) {
            visit(p);
        }
        
        return null;
    }

    @Override
    public String visitShift(CParser.ShiftContext ctx) {
        
        for (var p : ctx.bitwise_and()) {
            visit(p);
        }
        
        return null;
    }

    @Override
    public String visitBitwise_and(CParser.Bitwise_andContext ctx) {
        
        for (var p : ctx.bitwise_xor()) {
            visit(p);
        }
        
        return null;
    }

    @Override
    public String visitBitwise_xor(CParser.Bitwise_xorContext ctx) {
        
        for (var p : ctx.bitwise_or()) {
            visit(p);
        }
        
        return null;
    }

    @Override
    public String visitBitwise_or(CParser.Bitwise_orContext ctx) {
        
        for (var p : ctx.primary()) {
            visit(p);
        }
        
        return null;
    }

    @Override
    public String visitPrimary(CParser.PrimaryContext ctx) {
        if (ctx.ID() != null) {
            String oldName = ctx.ID().getText();
            String newName = varMap.getOrDefault(oldName, oldName);
            rewriter.replace(ctx.ID().getSymbol(), newName);
            
            
        } else if (ctx.NUMBER() != null) {
            String original = ctx.NUMBER().getText();
            try {
                int value = Integer.parseInt(original);
                String obfuscated = obfuscateNumber(value);
                rewriter.replace(ctx.NUMBER().getSymbol(), obfuscated);
                
//                
                
            } catch (NumberFormatException e) {

            }
        } else if (ctx.STRING() != null) {
            String raw = ctx.STRING().getText();
            String clean = raw.substring(1, raw.length() - 1);
            String obfuscated = obfuscateString(clean);
            rewriter.replace(ctx.STRING().getSymbol(), obfuscated);
        }
        else if (ctx.CHAR() != null) {
                return ctx.CHAR().getText();
        } else if (ctx.expression() != null) {
            return visit(ctx.expression());
        } else if (ctx.function_call() != null) {
            return visit(ctx.function_call());
        } else if (ctx.struct_member_access() != null) {
            return visit(ctx.struct_member_access());
        }
        return null;
    }


    private static String obfuscateString(String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);

        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < FIXED_STRING_LENGTH; i++) {
            byte b = (i < bytes.length) ? bytes[i] : (byte) 'A';
            sb.append(String.format("\\x%02x", b));
        }
        sb.append("\"");
        return sb.toString();
    }

    private String obfuscateNumber(int n) {
        Random rand = new Random(n * 31L + 17);

        if (n == 0) {
            switch (rand.nextInt(3)) {
                case 0: return "5%5";
                case 1: return "3-3";
                default: return "(1<<2)-4";
            }
        }

        if (n == 1) {
            switch (rand.nextInt(4)) {
                case 0: return "6%5";
                case 1: return "7-6";
                case 2: return "(1<<0)";
                default: return "9/9";
            }
        }

        // для n > 1 — более сложная обфускация
        int k = rand.nextInt(2, 10);
        int quotient = n / k;
        int remainder = n % k;
//        System.out.println(n);

        String expr = "(" + quotient + "*" + k + ")";
        if (remainder != 0) {
            expr += "+" + remainder;
//            System.out.println(expr);
        }

        if (rand.nextBoolean()) {
            int fakeAdd = rand.nextInt(1, 5);
            int modBase = n + fakeAdd;
            expr = "(" + expr + "+" + fakeAdd + ")%" + modBase + "+" + (n - ((n + fakeAdd) % modBase));
        }

        return "(" + expr + ")";
    }


    @Override
    public String visitFunction_call(CParser.Function_callContext ctx) {
        String oldName = ctx.ID().getText();
        if (oldName.equals("printf")){
            visit(ctx.args());

        }else if (functionMap.containsKey(oldName)) {
            rewriter.replace(ctx.ID().getSymbol(), functionMap.get(oldName));
            
        }else if (!functionMap.containsKey(oldName)){
            String newName = renameFunction(oldName);
            rewriter.replace(ctx.ID().getSymbol(), newName);
            
        }
        if (ctx.args() != null) {
            visit(ctx.args());
        }
        
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
        
        
        return null;
    }

    @Override
    public String visitValue(CParser.ValueContext ctx) {
        if (ctx.expression() != null) {
            return visit(ctx.expression());
        } else if (ctx.ID() != null || ctx.NUMBER() != null || ctx.STRING() != null || ctx.CHAR() != null) {


        }
        return null;
    }
}
