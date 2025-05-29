// Generated from D:/ObfuscatorC/src/main/java/ru/ObfuscatorC/C.g4 by ANTLR 4.13.2
package ru.ObfuscatorC.Tokens;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CParser}.
 */
public interface CListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(CParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(CParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#preprocessor}.
	 * @param ctx the parse tree
	 */
	void enterPreprocessor(CParser.PreprocessorContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#preprocessor}.
	 * @param ctx the parse tree
	 */
	void exitPreprocessor(CParser.PreprocessorContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#macro_function}.
	 * @param ctx the parse tree
	 */
	void enterMacro_function(CParser.Macro_functionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#macro_function}.
	 * @param ctx the parse tree
	 */
	void exitMacro_function(CParser.Macro_functionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(CParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(CParser.ValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#filename}.
	 * @param ctx the parse tree
	 */
	void enterFilename(CParser.FilenameContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#filename}.
	 * @param ctx the parse tree
	 */
	void exitFilename(CParser.FilenameContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(CParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(CParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#params}.
	 * @param ctx the parse tree
	 */
	void enterParams(CParser.ParamsContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#params}.
	 * @param ctx the parse tree
	 */
	void exitParams(CParser.ParamsContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(CParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(CParser.ParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#struct}.
	 * @param ctx the parse tree
	 */
	void enterStruct(CParser.StructContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#struct}.
	 * @param ctx the parse tree
	 */
	void exitStruct(CParser.StructContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#struct_field}.
	 * @param ctx the parse tree
	 */
	void enterStruct_field(CParser.Struct_fieldContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#struct_field}.
	 * @param ctx the parse tree
	 */
	void exitStruct_field(CParser.Struct_fieldContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#global_declaration}.
	 * @param ctx the parse tree
	 */
	void enterGlobal_declaration(CParser.Global_declarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#global_declaration}.
	 * @param ctx the parse tree
	 */
	void exitGlobal_declaration(CParser.Global_declarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(CParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(CParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(CParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(CParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(CParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(CParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(CParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(CParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#if_statement}.
	 * @param ctx the parse tree
	 */
	void enterIf_statement(CParser.If_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#if_statement}.
	 * @param ctx the parse tree
	 */
	void exitIf_statement(CParser.If_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#for_loop}.
	 * @param ctx the parse tree
	 */
	void enterFor_loop(CParser.For_loopContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#for_loop}.
	 * @param ctx the parse tree
	 */
	void exitFor_loop(CParser.For_loopContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#while_loop}.
	 * @param ctx the parse tree
	 */
	void enterWhile_loop(CParser.While_loopContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#while_loop}.
	 * @param ctx the parse tree
	 */
	void exitWhile_loop(CParser.While_loopContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#return_statement}.
	 * @param ctx the parse tree
	 */
	void enterReturn_statement(CParser.Return_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#return_statement}.
	 * @param ctx the parse tree
	 */
	void exitReturn_statement(CParser.Return_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(CParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(CParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#logical_or}.
	 * @param ctx the parse tree
	 */
	void enterLogical_or(CParser.Logical_orContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#logical_or}.
	 * @param ctx the parse tree
	 */
	void exitLogical_or(CParser.Logical_orContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#logical_and}.
	 * @param ctx the parse tree
	 */
	void enterLogical_and(CParser.Logical_andContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#logical_and}.
	 * @param ctx the parse tree
	 */
	void exitLogical_and(CParser.Logical_andContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#equality}.
	 * @param ctx the parse tree
	 */
	void enterEquality(CParser.EqualityContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#equality}.
	 * @param ctx the parse tree
	 */
	void exitEquality(CParser.EqualityContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#relational}.
	 * @param ctx the parse tree
	 */
	void enterRelational(CParser.RelationalContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#relational}.
	 * @param ctx the parse tree
	 */
	void exitRelational(CParser.RelationalContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#additive}.
	 * @param ctx the parse tree
	 */
	void enterAdditive(CParser.AdditiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#additive}.
	 * @param ctx the parse tree
	 */
	void exitAdditive(CParser.AdditiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#multiplicative}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicative(CParser.MultiplicativeContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#multiplicative}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicative(CParser.MultiplicativeContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#shift}.
	 * @param ctx the parse tree
	 */
	void enterShift(CParser.ShiftContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#shift}.
	 * @param ctx the parse tree
	 */
	void exitShift(CParser.ShiftContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#bitwise_and}.
	 * @param ctx the parse tree
	 */
	void enterBitwise_and(CParser.Bitwise_andContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#bitwise_and}.
	 * @param ctx the parse tree
	 */
	void exitBitwise_and(CParser.Bitwise_andContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#bitwise_xor}.
	 * @param ctx the parse tree
	 */
	void enterBitwise_xor(CParser.Bitwise_xorContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#bitwise_xor}.
	 * @param ctx the parse tree
	 */
	void exitBitwise_xor(CParser.Bitwise_xorContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#bitwise_or}.
	 * @param ctx the parse tree
	 */
	void enterBitwise_or(CParser.Bitwise_orContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#bitwise_or}.
	 * @param ctx the parse tree
	 */
	void exitBitwise_or(CParser.Bitwise_orContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary(CParser.PrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary(CParser.PrimaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#struct_member_access}.
	 * @param ctx the parse tree
	 */
	void enterStruct_member_access(CParser.Struct_member_accessContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#struct_member_access}.
	 * @param ctx the parse tree
	 */
	void exitStruct_member_access(CParser.Struct_member_accessContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#function_call}.
	 * @param ctx the parse tree
	 */
	void enterFunction_call(CParser.Function_callContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#function_call}.
	 * @param ctx the parse tree
	 */
	void exitFunction_call(CParser.Function_callContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#args}.
	 * @param ctx the parse tree
	 */
	void enterArgs(CParser.ArgsContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#args}.
	 * @param ctx the parse tree
	 */
	void exitArgs(CParser.ArgsContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(CParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(CParser.TypeContext ctx);
}