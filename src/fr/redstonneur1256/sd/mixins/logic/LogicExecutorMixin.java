package fr.redstonneur1256.sd.mixins.logic;

import arc.struct.Seq;
import fr.redstonneur1256.sd.accessors.AdvancedExecutor;
import fr.redstonneur1256.sd.DrawCommand;
import mindustry.logic.LExecutor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LExecutor.class)
public class LogicExecutorMixin implements AdvancedExecutor {

    private Seq<DrawCommand> drawQueue = new Seq<>(LExecutor.maxDisplayBuffer);

    @Override
    public Seq<DrawCommand> getDrawQueue() {
        return drawQueue;
    }
}
