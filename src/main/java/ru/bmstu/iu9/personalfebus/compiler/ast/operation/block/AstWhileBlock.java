package ru.bmstu.iu9.personalfebus.compiler.ast.operation.block;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.AstOperation;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.condition.AstCondition;
import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstVariable;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.AlreadyDeclaredException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.BadArithmeticExpressionException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.TypeIncompatibilityException;

import java.util.List;
import java.util.Set;

public class AstWhileBlock implements AstOperation {
    private final AstCondition condition;
    private final List<AstOperation> operations;

    public final static String TYPE = "WHILE_BLOCK";

    public AstWhileBlock(AstCondition condition, List<AstOperation> operations) {
        this.condition = condition;
        this.operations = operations;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, StringBuilder locals, AstFunction currentFunction) throws MissingException, TypeIncompatibilityException, AlreadyDeclaredException, BadArithmeticExpressionException {
        StringBuilder generatedCode = new StringBuilder();
        int num = labelGenerationHelper.getNum();
        generatedCode.append("br while_")
                .append(num)
                .append("_conditions\n");

        generatedCode.append("while_")
                .append(num)
                .append("_operations:\n");

        for (AstOperation operation : operations) {
            generatedCode.append(operation.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, locals, currentFunction));
        }

        generatedCode.append("while_")
                .append(num)
                .append("_conditions:\n");
        generatedCode.append(condition.generatedIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, locals, currentFunction));

        generatedCode.append("brtrue while_")
                .append(num)
                .append("_operations\n");

        return generatedCode.toString();
    }
}
