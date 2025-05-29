package ru.ObfuscatorC;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.tree.ParseTree;
import ru.ObfuscatorC.Tokens.CBaseVisitor;
import ru.ObfuscatorC.Tokens.CParser;

import java.util.List;

public class AddDecr extends CBaseVisitor<String> {
    private static TokenStreamRewriter rewriter = null;
    private boolean includesInserted = false;
    private boolean decryptFunctionInserted = false;

    public AddDecr(TokenStream tokens) {
        this.rewriter = new TokenStreamRewriter(tokens);
    }

    @Override
    public String visitProgram(CParser.ProgramContext ctx) {
        insertIncludesAndDecrypt(ctx);
        for (var child : ctx.children) {
            visit(child);
        }
        return null;
    }
    private void insertIncludesAndDecrypt(CParser.ProgramContext ctx) {
        if (decryptFunctionInserted) return;
        decryptFunctionInserted = true;

        StringBuilder builder = new StringBuilder();

        if (!includesInserted) {
            includesInserted = true;
            builder.append("#include <stdlib.h>\n");
            builder.append("#include <string.h>\n\n");
        }

        builder.append("""
    char* decrypt_string(const char* enc, int key) {
        size_t len = strlen(enc);
        char* dec = malloc(len + 1);
        for (size_t i = 0; i < len; i++) {
            dec[i] = enc[i] ^ key;
        }
        dec[len] = '\\0';
        return dec;
    }

    """);
        List<ParseTree> children = ctx.children;
        int insertIndex = 0;
        for (ParseTree child : children) {
            if (child instanceof CParser.PreprocessorContext) {
                insertIndex++;
            } else {
                break;
            }
        }

        if (insertIndex == 0) {
            rewriter.insertBefore(ctx.start, builder.toString());
        } else {
            ParseTree lastPreprocessor = children.get(insertIndex - 1);
            rewriter.insertAfter(((ParserRuleContext) lastPreprocessor).stop, "\n" + builder.toString());
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
