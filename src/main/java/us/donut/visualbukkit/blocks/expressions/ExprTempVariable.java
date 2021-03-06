package us.donut.visualbukkit.blocks.expressions;

import us.donut.visualbukkit.blocks.ChangeType;
import us.donut.visualbukkit.blocks.ChangeableExpressionBlock;
import us.donut.visualbukkit.blocks.annotations.Description;
import us.donut.visualbukkit.blocks.syntax.InputParameter;
import us.donut.visualbukkit.blocks.syntax.SyntaxNode;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Description({"A temporary variable", "Changers: set, delete, clear, add, remove", "Returns: object"})
public class ExprTempVariable extends ChangeableExpressionBlock<Object> {

    @Override
    protected SyntaxNode init() {
        return new SyntaxNode("temp variable", new InputParameter());
    }

    @Override
    public String toJava() {
        return "tempVariables.get(" + getVariable() + ")";
    }

    @Override
    public String change(ChangeType changeType, String delta) {
        switch (changeType) {
            case SET: return "tempVariables.put(" + getVariable() + "," + delta + ");";
            case ADD: return "addToVariable(" + getVariable() + "," + delta + ", tempVariables);";
            case REMOVE: return "removeFromVariable(" + getVariable() + "," + delta + ", tempVariables);";
            case DELETE: case CLEAR: return "tempVariables.remove(" + getVariable() + ");" ;
            default: return null;
        }
    }

    private String getVariable() {
        String encodedString = Base64.getEncoder().encodeToString(arg(0).getBytes(StandardCharsets.UTF_8));
        return "decode(\"" + encodedString + "\")";
    }
}
