package us.donut.visualbukkit.blocks.expressions;

import org.bukkit.configuration.file.YamlConfiguration;
import us.donut.visualbukkit.blocks.ExpressionBlock;
import us.donut.visualbukkit.blocks.annotations.Description;
import us.donut.visualbukkit.blocks.syntax.SyntaxNode;

import java.io.File;

@Description({"Loads a config from a file", "Returns: config"})
public class ExprConfigFromFile extends ExpressionBlock<YamlConfiguration> {

    @Override
    protected SyntaxNode init() {
        return new SyntaxNode("config from", File.class);
    }

    @Override
    public String toJava() {
        return "org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(" + arg(0) + ")";
    }
}
