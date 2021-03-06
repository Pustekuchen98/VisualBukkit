package us.donut.visualbukkit.blocks.statements;

import us.donut.visualbukkit.blocks.ParentBlock;
import us.donut.visualbukkit.blocks.annotations.Description;
import us.donut.visualbukkit.blocks.annotations.Name;
import us.donut.visualbukkit.blocks.syntax.SyntaxNode;

@Name("If Statement")
@Description("Runs code if a condition is true")
public class StatIf extends ParentBlock {

    @Override
    protected SyntaxNode init() {
        return new SyntaxNode("if", boolean.class);
    }

    @Override
    public String toJava() {
        return "if (" + arg(0) + ") {" + getChildJava() + "}";
    }
}
