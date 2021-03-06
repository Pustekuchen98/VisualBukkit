package us.donut.visualbukkit.blocks.expressions;

import org.bukkit.event.entity.EntityDamageEvent;
import us.donut.visualbukkit.blocks.ChangeType;
import us.donut.visualbukkit.blocks.ChangeableExpressionBlock;
import us.donut.visualbukkit.blocks.annotations.Description;
import us.donut.visualbukkit.blocks.annotations.Event;
import us.donut.visualbukkit.blocks.syntax.SyntaxNode;

@Description({"The damage amount in an EntityDamageEvent", "Changers: set, add, remove", "Returns: number"})
@Event(EntityDamageEvent.class)
public class ExprDamage extends ChangeableExpressionBlock<Double> {

    @Override
    protected SyntaxNode init() {
        return new SyntaxNode("damage");
    }

    @Override
    public String toJava() {
        return "event.getDamage()";
    }

    @Override
    public String change(ChangeType changeType, String delta) {
        switch (changeType) {
            case SET: return "event.setDamage(" + delta + ");";
            case ADD: return change(ChangeType.SET, toJava() + "-" + delta);
            case REMOVE: return change(ChangeType.SET, toJava() + "+" + delta);
            default: return null;
        }
    }
}
