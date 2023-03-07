package fr.redstonneur1256.sd.mixins.logic.instructions;

import arc.struct.Seq;
import fr.redstonneur1256.sd.accessors.AdvancedExecutor;
import fr.redstonneur1256.sd.accessors.Display;
import fr.redstonneur1256.sd.DrawCommand;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.logic.LExecutor;
import mindustry.world.blocks.logic.LogicDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LExecutor.DrawFlushI.class)
public class DrawFlushInstructionMixin implements LExecutor.LInstruction {

    @Shadow
    public int target;

    @Override
    public void run(LExecutor exec) {
        Building building = exec.building(target);

        if(Vars.headless ||
                !Vars.renderer.drawDisplays ||
                !(building instanceof LogicDisplay.LogicDisplayBuild) ||
                (building.team != exec.team && !exec.privileged)) {
            return;
        }

        Seq<DrawCommand> commands = ((AdvancedExecutor) exec).getDrawQueue();

        for(Display display : ((Display) building).connected()) {
            LogicDisplay.LogicDisplayBuild build = (LogicDisplay.LogicDisplayBuild) display;

            int pixelsPerTile = ((LogicDisplay) build.block).displaySize / build.block.size;

            int xOffset = (building.tile.x - build.tile.x) * pixelsPerTile;
            int yOffset = (building.tile.y - build.tile.y) * pixelsPerTile;

            display.begin(xOffset, yOffset);
            display.flush(commands);
            display.end();
        }

        DrawCommand.POOL.freeAll(commands);
        commands.clear();
    }

}
