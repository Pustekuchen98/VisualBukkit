package us.donut.visualbukkit.blocks.statements;

import org.bukkit.entity.Player;
import us.donut.visualbukkit.blocks.StatementBlock;
import us.donut.visualbukkit.blocks.annotations.Category;
import us.donut.visualbukkit.blocks.annotations.Description;
import us.donut.visualbukkit.blocks.syntax.SyntaxNode;

@Category("Player")
@Description("Requests a player to download and switch to a resource pack from a url")
public class StatSetResourcePack extends StatementBlock {

    @Override
    protected SyntaxNode init() {
        return new SyntaxNode("set resource pack of", Player.class, "to", String.class);
    }

    @Override
    public String toJava() {
        return arg(0) + ".setResourcePack(" + arg(1) + ");";
    }
}
