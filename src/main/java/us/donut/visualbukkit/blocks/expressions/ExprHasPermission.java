package us.donut.visualbukkit.blocks.expressions;

import org.bukkit.permissions.Permissible;
import us.donut.visualbukkit.blocks.ConditionBlock;
import us.donut.visualbukkit.blocks.annotations.Description;
import us.donut.visualbukkit.blocks.syntax.ChoiceParameter;
import us.donut.visualbukkit.blocks.syntax.SyntaxNode;

@Description({"Checks if a permissible object has a permission", "Returns: boolean"})
public class ExprHasPermission extends ConditionBlock {

    @Override
    protected SyntaxNode init() {
        return new SyntaxNode(Permissible.class, new ChoiceParameter("has", "does not have"), "permission", String.class);
    }

    @Override
    protected String toNonNegatedJava() {
        return arg(0) + ".hasPermission(" + arg(2) + ")";
    }
}
