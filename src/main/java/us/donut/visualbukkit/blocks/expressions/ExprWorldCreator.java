package us.donut.visualbukkit.blocks.expressions;

import org.bukkit.WorldCreator;
import us.donut.visualbukkit.blocks.ExpressionBlock;
import us.donut.visualbukkit.blocks.annotations.Description;
import us.donut.visualbukkit.blocks.syntax.SyntaxNode;

@Description({"A new world creator", "Returns: world creator"})
public class ExprWorldCreator extends ExpressionBlock<WorldCreator> {

    @Override
    protected SyntaxNode init() {
        return new SyntaxNode("world creator for new world named", String.class);
    }

    @Override
    public String toJava() {
        return "new WorldCreator(" + arg(0) + ")";
    }
}
