package us.donut.visualbukkit.blocks.expressions;

import org.bukkit.entity.Entity;
import us.donut.visualbukkit.blocks.ChangeType;
import us.donut.visualbukkit.blocks.ChangeableExpressionBlock;
import us.donut.visualbukkit.blocks.annotations.Category;
import us.donut.visualbukkit.blocks.annotations.Description;
import us.donut.visualbukkit.blocks.syntax.SyntaxNode;

@Category("Entity")
@Description({"The custom name visibility state of an entity", "Changers: set", "Returns: boolean"})
public class ExprCustomNameVisibility extends ChangeableExpressionBlock<Boolean> {

    @Override
    protected SyntaxNode init() {
        return new SyntaxNode("custom name visibility of", Entity.class);
    }

    @Override
    public String toJava() {
        return arg(0) + ".isCustomNameVisible()";
    }

    @Override
    public String change(ChangeType changeType, String delta) {
        return changeType == ChangeType.SET ? arg(0) + ".setCustomNameVisible(" + delta + ");" : null;
    }
}
