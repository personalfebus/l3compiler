package ru.bmstu.iu9.personalfebus.compiler.ast.value;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.BadArithmeticExpressionException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.TypeIncompatibilityException;

import java.util.*;

//возможно стоит добавить проверку типов прямо здесь (не надо)
public class AstArithExpr implements RValue {
    /**
     * выражение из операторов и значений в обратной польской нотации
     */
    private List<AstArithExprPart> parts;
    private Deque<AstArithExprPart> stack;

    private static String TYPE = "ARITHMETIC_EXPRESSION";

    public AstArithExpr() {
        this.parts = new ArrayList<>();
        this.stack = new ArrayDeque<>();
    }

    public List<AstArithExprPart> getParts() {
        return parts;
    }

    public void addPart(AstArithExprPart part) throws BadArithmeticExpressionException {
        if (part.getType().equals("OPERATOR")) {
            AstArithOperator operator = (AstArithOperator)part;

            if (operator.getSubType().equals("UNARY_OPERATOR")) {
                stack.push(part);
            } else if (operator.getSubType().equals("BINARY_OPERATOR")) {
                while (stack.peekLast() != null && stack.peekLast().getType().equals("OPERATOR")) {
                    AstArithOperator operator2 = (AstArithOperator)stack.peekLast();
                    if (operator2.getPriority() < operator.getPriority()
                        || (operator2.getPriority() == operator.getPriority() && !operator.isRightAssociative())) {
                        parts.add(stack.pop());
                    } else break;
                }
                stack.push(part);
            } else {
                //WTF
                throw new BadArithmeticExpressionException();
            }
        } else if (part.getType().equals("ARITHMETIC_EXPRESSION")) {
            System.out.println("WTF");
            throw new BadArithmeticExpressionException();
        } else if (part.getType().equals("SEPARATOR_OPEN")) {
            stack.push(part);
        } else if (part.getType().equals("SEPARATOR_CLOSE")) {
            while (stack.peekLast() != null && !stack.peekLast().getType().equals("SEPARATOR_OPEN")) {
                parts.add(stack.pop());
            }

            if (stack.peekLast() == null) {
                System.out.println("NOT CLOSED SEPARATORS");
                throw new BadArithmeticExpressionException();
            }

            stack.pop();
        } else {
            parts.add(part);
        }
    }

    public void emptyStack() throws BadArithmeticExpressionException {
        while (!stack.isEmpty()) {
            if (!stack.peekLast().getType().equals("OPERATOR")) throw new BadArithmeticExpressionException();
            else parts.add(stack.pop());
        }
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, AstFunction currentFunction) throws MissingException, TypeIncompatibilityException {
        StringBuilder generatedCode = new StringBuilder();
        for (AstArithExprPart part : parts) {
            generatedCode.append(part.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, currentFunction));
        }

        return generatedCode.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
