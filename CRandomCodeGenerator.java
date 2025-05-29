package ru.ObfuscatorC;
import java.util.*;

public class CRandomCodeGenerator {
    private static final Random random = new Random();
    private static final String[] TYPES = {"int"};
    private static final String[] OPERATORS = {"+", "-", "*", "/", "%"};
    private static final String[] COMPARATORS = {"==", "!=", "<", ">", "<=", ">="};

    // Safety limits
    private static final int MAX_FUNCTIONS = 2;
    private static final int MAX_FUNC_STATEMENTS = 2;
    private static final int MAX_LOOP_DEPTH = 2;
    private static final int MAX_VARS_IN_SCOPE = 2;
    private static final int MAX_EXPRESSION_COMPLEXITY = 2;

    private static HashMap<String, String> varMap = new HashMap<>();
    private static Set<String> functionNames = new HashSet<>();
    private static int currentLoopDepth = 0;
    private static int counter = 0;
    private static Stack<HashMap<String, String>> scopeStack = new Stack<>();
    private static HashMap<String, Boolean> varInitStatus = new HashMap<>();

    private static class FunctionSignature {
        String returnType;
        List<String> paramTypes;

        FunctionSignature(String returnType, List<String> paramTypes) {
            this.returnType = returnType;
            this.paramTypes = paramTypes;
        }
    }

    private static Map<String, FunctionSignature> functionSignatures = new HashMap<>();
    public String generateRandomCode(int complexity) {
        enterScope();
        complexity = Math.min(complexity, 5);

        StringBuilder code = new StringBuilder();
        code.append("#include <stdio.h>\n\n");

        int funcCount = 1 + random.nextInt(Math.min(complexity, MAX_FUNCTIONS));
        for (int i = 0; i < funcCount; i++) {
            code.append(generateRandomFunction()).append("\n\n");
        }

        code.append("int main() {\n");
        for (int i = 0; i < Math.min(complexity * 2, MAX_FUNC_STATEMENTS); i++) {
            code.append("    ").append(generateRandomStatement()).append("\n");
        }
        code.append("    return 0;\n");
        code.append("}\n");
        exitScope();
        return code.toString();
    }
    private static void enterScope() {
        scopeStack.push(new HashMap<>());
    }

    private static void exitScope() {
        scopeStack.pop();
    }

    private static void addVariable(String name, String type) {
        scopeStack.peek().put(name, type);
    }

    public static String generateRandomFunction() {
        enterScope();
        String returnType = TYPES[random.nextInt(TYPES.length)];
        String funcName = generateUniqueFunctionName("func");
        functionNames.add(funcName);

        HashMap<String, String> oldVarMap = new HashMap<>(varMap);
        varMap.clear();

        StringBuilder func = new StringBuilder();

        int paramCount = random.nextInt(3);
        List<String> paramTypes = new ArrayList<>();
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < paramCount; i++) {
            if (i > 0) params.append(", ");
            String type = TYPES[random.nextInt(TYPES.length)];
            String name = generateRandomName("param");
            addVariable(name, type);
            params.append(type).append(" ").append(name);
            varMap.put(name, type);
            paramTypes.add(type);
        }

        functionSignatures.put(funcName, new FunctionSignature(returnType, paramTypes));

        func.append(returnType).append(" ").append(funcName)
                .append("(").append(params).append(") {\n");

        // Generate function body
        int stmtCount = 2;
//                + random.nextInt(Math.min(5, MAX_FUNC_STATEMENTS - 2));
        for (int i = 0; i < stmtCount; i++) {
            func.append("    ").append(generateRandomStatement()).append("\n");

//            if (varMap.size() > MAX_VARS_IN_SCOPE) {
//                func.append("    // Variables cleared to prevent overflow\n");
//                removeSomeVariables();
//            }
        }

        if (!"void".equals(returnType)) {
            func.append("    return ").append(generateSafeExpression()).append(";\n");
        }

        func.append("}");

        // Restore previous variable scope
        varMap = oldVarMap;
        exitScope();

        return func.toString();
    }

    public static String generateRandomStatement() {
        enterScope();
        int choice = random.nextInt(100);

        if (choice < 30 && varMap.size() < MAX_VARS_IN_SCOPE) {
            exitScope();
            return generateVariableDeclaration();

//        } else if (choice < 60) {
//            return generateAssignment();
        } else if (choice < 70 && currentLoopDepth < MAX_LOOP_DEPTH) {
            return generateIfStatement();
//        } else if (choice < 85 && currentLoopDepth < MAX_LOOP_DEPTH) {
//            return generateForLoop();
        } else if (choice < 95 && currentLoopDepth < MAX_LOOP_DEPTH) {
            return generateWhileLoop();
//        } else if (!functionSignatures.isEmpty()) {
//            return generateVariableDeclarationF() + generateFunctionCall();
        } else {
            return generateVariableDeclaration();
//        return generateWhileLoop();
        }
    }

    private static String generateVariableDeclaration() {
        String type = TYPES[random.nextInt(TYPES.length)];
        String name = generateRandomName("v");

        // Временно исключаем переменную из списка
        varMap.remove(name);
        String expr = generateSafeExpression();
        addVariable(name, type);
        varInitStatus.put(name, true);
        return String.format("%s %s = %s;", type, name, expr);
    }
    private static String generateVariableDeclarationF() {
        String type = TYPES[random.nextInt(TYPES.length)];
        String name = generateRandomName("v");

        varMap.remove(name);
        String expr = generateSafeExpression();
        addVariable(name, type);
        varInitStatus.put(name, true);
        return String.format("%s %s = ", type, name);
    }


    private static String generateVariableDeclaration(String type, String name) {
        addVariable(name, type);
        varInitStatus.put(name, true);
        return String.format("%s %s;", type, name);
    }
    private static String generateAssignment() {
        String var = getRandomVar();

        if (!isVariableDeclared(var)) {
            String type = TYPES[random.nextInt(TYPES.length)];
            return generateVariableDeclaration(type, var) + "\n" + var + " = " + generateSafeExpression() + ";";
        }

        return var + " = " + generateSafeExpression() + ";";
    }

    private static boolean isVariableDeclared(String var) {
        return scopeStack.stream().anyMatch(scope -> scope.containsKey(var));
    }

    private static String generateIfStatement() {
        enterScope();
        currentLoopDepth++;
        try {
            StringBuilder sb = new StringBuilder();

            String type = "int";
            String name = generateRandomName("v");
            String initExpr = Integer.toString(random.nextInt(10));
            addVariable(name, type);
            varInitStatus.put(name, true);
            sb.append(String.format("%s %s = %s;\n", type, name, initExpr));

            String logicOp = random.nextBoolean() ? "&&" : "||";
            int left, right;

            if (logicOp.equals("&&")) {
                left = 50 + random.nextInt(50);
                right = random.nextInt(50);
            } else {
                left = 100;
                right = 0;
            }

            sb.append("if (").append(name)
                    .append(" > ").append(left)
                    .append(" ").append(logicOp).append(" ")
                    .append(name).append(" < ").append(right)
                    .append(") {\n");


            String message = generateRandomString();
            sb.append("    printf(\" ").append(message).append(": %d\\n\", ").append(name).append(");\n");

            int stmtCount = 1 + random.nextInt(2);
            for (int i = 0; i < stmtCount; i++) {
                sb.append("    ").append(generateRandomStatement()).append("\n");
            }

            sb.append("}");

            if (random.nextBoolean()) {
                sb.append(" else {\n");

                enterScope();

                for (int i = 0; i < stmtCount; i++) {
                    sb.append("    ").append(generateRandomStatement()).append("\n");
                }

                exitScope();

                sb.append("}");
            }

            exitScope();
            return sb.toString();
        } finally {
            currentLoopDepth--;
        }
    }
    private static String generateRandomString() {
        int len = 5 + random.nextInt(5);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            char c = (char) ('a' + random.nextInt(26));
            sb.append(c);
        }
        return sb.toString();
    }



    private static String generateForLoop() {
        enterScope();
        currentLoopDepth++;
        try {
            if (currentLoopDepth > MAX_LOOP_DEPTH) {
                return generateAssignment();
            }

            String var = generateRandomName("loopvar");
            varMap.put(var, "int");

            StringBuilder sb = new StringBuilder();
            sb.append("for (int ").append(var).append(" = ")
                    .append(random.nextInt(5)).append("; ")
                    .append(var).append(" < ").append(5 + random.nextInt(10)).append("; ")
                    .append(var).append("++")
                    .append(") {\n");

            int stmtCount = 1 + random.nextInt(3);
            for (int i = 0; i < stmtCount; i++) {
                sb.append("    ").append(generateRandomStatement()).append("\n");
            }

            sb.append("}");
            exitScope();
            return sb.toString();
        } finally {
            currentLoopDepth--;
        }
    }
    private static String generateVariableDeclaration1(String type, String name) {
        addVariable(name, type);
        varInitStatus.put(name, true);
        return String.format("%s %s = %s;", type, name, generateSafeExpression());
    }


    private static String generateWhileLoop() {
        enterScope();
        currentLoopDepth++;
        try {
            if (currentLoopDepth > MAX_LOOP_DEPTH) {
                return generateAssignment();
            }

            StringBuilder sb = new StringBuilder();


            String type = "int";
            String name = generateRandomName("v");
            String initExpr = Integer.toString(random.nextInt(5));
            addVariable(name, type);
            varInitStatus.put(name, true);
            sb.append(String.format("%s %s = %s;\n", type, name, initExpr));

            int target = 5 + random.nextInt(10);
            String comparator = "<";

            if (random.nextBoolean()) {
                comparator = "<";
            } else {
                comparator = ">";
                target = random.nextInt(10) + 5;
                sb.setLength(0);
                initExpr = Integer.toString(target + random.nextInt(5) + 1);
                sb.append(String.format("%s %s = %s;\n", type, name, initExpr));
            }

            sb.append("while (").append(name).append(" ").append(comparator).append(" ").append(target).append(") {\n");

            int stmtCount = 1 + random.nextInt(2);
            for (int i = 0; i < stmtCount; i++) {
                sb.append("    ").append(generateRandomStatement()).append("\n");
            }
            String update = comparator.equals("<") ? name + " = " + name + " + 1;" : name + " = " + name + " - 1;";
            sb.append("    ").append(update).append("\n");

            sb.append("}");

            exitScope();
            return sb.toString();
        } finally {
            currentLoopDepth--;
        }
    }

    private static String generateCondition(String varName) {
        return varName + " " + COMPARATORS[random.nextInt(COMPARATORS.length)] + " " + generateSafeExpression();
    }


    private static String generateFunctionCall() {
        String[] functions = functionSignatures.keySet().toArray(new String[0]);
        String funcName = functions[random.nextInt(functions.length)];
        FunctionSignature signature = functionSignatures.get(funcName);

        StringBuilder args = new StringBuilder();
        for (int i = 0; i < signature.paramTypes.size(); i++) {
            if (i > 0) args.append(", ");
            String type = signature.paramTypes.get(i);
            args.append(generateTypedExpression(type));
        }

        return funcName + "(" + args.toString() + ");";
    }
    private static String generateTypedExpression(String type) {
        if ("int".equals(type)) {
            return generateSafeExpression();
        }
        // Для других типов
        return "0"; // fallback
    }

    private static String generateRandomArgs() {
        int argCount = random.nextInt(3);
        StringBuilder args = new StringBuilder();
        for (int i = 0; i < argCount; i++) {
            if (i > 0) args.append(", ");
            args.append(generateSafeExpression());
        }
        return args.toString();
    }

    private static String generateSafeExpression() {
        StringBuilder expr = new StringBuilder();
        int parts = 1 + random.nextInt(Math.min(3, MAX_EXPRESSION_COMPLEXITY));

        for (int i = 0; i < parts; i++) {
            if (i > 0) {
                expr.append(" ").append(OPERATORS[random.nextInt(OPERATORS.length)]).append(" ");
            }

//            if (random.nextInt(100) < 70 || varMap.size() == 1) {
                expr.append(generateRandomValue());
//            } else {
//                expr.append(getRandomVar());
//            }

        }

        return expr.toString();
    }

    private static String generateRandomCondition() {

//        if (hasVariablesInScope()) {
            return getRandomVar() + " " +
                    COMPARATORS[random.nextInt(COMPARATORS.length)] + " " +
                    generateSafeExpression();
//        }
//        else {

//            String newVar = generateVariableDeclaration();
//            return newVar.split(" ")[1].replace(";", "") + " < 100"; // Пример: v0 < 100
//        }
    }

    private static boolean hasVariablesInScope() {
        return scopeStack.stream().anyMatch(scope -> !scope.isEmpty());
    }

    private static String generateRandomValue() {
        switch (random.nextInt(3)) {
            case 0: return Integer.toString(random.nextInt(100));
//            case 1: return Float.toString(random.nextFloat() * 100);
//            case 2: return "'" + (char)('a' + random.nextInt(26)) + "'";
            default: return Integer.toString(random.nextInt(100));
        }
    }

    private static String generateRandomName(String prefix) {
        return prefix + (counter++);
    }

    private static String getRandomVar() {
        for (int i = scopeStack.size() - 1; i >= 0; i--) {
            HashMap<String, String> scope = scopeStack.get(i);
            if (!scope.isEmpty()) {
                String[] vars = scope.keySet().toArray(new String[0]);
                return vars[random.nextInt(vars.length)];
            }
        }
        // Если переменных нет, создаём новую
        return generateVariableDeclaration().split(" ")[1].replace(";", "");
    }

    private static String generateUniqueFunctionName(String prefix) {
        String name;
        do {
            name = prefix + (counter++);
        } while (functionNames.contains(name));
        return name;
    }

    private static void removeSomeVariables() {
        varMap.keySet().removeIf(key -> random.nextBoolean());
    }

    public static void main(String[] args) {
        CRandomCodeGenerator generator = new CRandomCodeGenerator();
        String randomCode = generator.generateRandomCode(1);
        System.out.println(randomCode);
    }
}