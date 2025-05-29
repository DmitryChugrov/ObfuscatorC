// Generated from D:/ObfuscatorC/src/main/java/ru/ObfuscatorC/C.g4 by ANTLR 4.13.2
package ru.ObfuscatorC.Tokens;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link CParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(CParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#preprocessor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPreprocessor(CParser.PreprocessorContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#macro_function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMacro_function(CParser.Macro_functionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(CParser.ValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#filename}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFilename(CParser.FilenameContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(CParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParams(CParser.ParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(CParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#struct}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStruct(CParser.StructContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#struct_field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStruct_field(CParser.Struct_fieldContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#global_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGlobal_declaration(CParser.Global_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(CParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(CParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration(CParser.DeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(CParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#if_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf_statement(CParser.If_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#for_loop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFor_loop(CParser.For_loopContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#while_loop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhile_loop(CParser.While_loopContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#return_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturn_statement(CParser.Return_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(CParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#logical_or}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogical_or(CParser.Logical_orContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#logical_and}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogical_and(CParser.Logical_andContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#equality}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEquality(CParser.EqualityContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#relational}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelational(CParser.RelationalContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#additive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditive(CParser.AdditiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#multiplicative}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicative(CParser.MultiplicativeContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#shift}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitShift(CParser.ShiftContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#bitwise_and}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBitwise_and(CParser.Bitwise_andContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#bitwise_xor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBitwise_xor(CParser.Bitwise_xorContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#bitwise_or}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBitwise_or(CParser.Bitwise_orContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimary(CParser.PrimaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#struct_member_access}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStruct_member_access(CParser.Struct_member_accessContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#function_call}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_call(CParser.Function_callContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#args}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgs(CParser.ArgsContext ctx);
	/**
	 * Visit a parse tree produced by {@link CParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(CParser.TypeContext ctx);
}