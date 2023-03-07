package fr.redstonneur1256.sd.mixins.logic.instructions;

import arc.struct.Seq;
import fr.redstonneur1256.sd.accessors.AdvancedExecutor;
import fr.redstonneur1256.sd.DrawCommand;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.logic.LExecutor;
import mindustry.world.blocks.logic.LogicDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LExecutor.DrawI.class)
public class DrawInstructionMixin implements LExecutor.LInstruction {

    @Shadow
    public byte type;
    @Shadow
    public int x;
    @Shadow
    public int y;
    @Shadow
    public int p1;
    @Shadow
    public int p2;
    @Shadow
    public int p4;
    @Shadow
    public int p3;

    @Override
    public void run(LExecutor exec) {
        Seq<DrawCommand> drawQueue = ((AdvancedExecutor) exec).getDrawQueue();
        if(Vars.headless || drawQueue.size >= LExecutor.maxDisplayBuffer) {
            return;
        }

        //explicitly unpack colorPack, it's pre-processed here
        if(type == LogicDisplay.commandColorPack) {
            double packed = exec.num(x);
            int value = (int) Double.doubleToRawLongBits(packed);
            int r = (value & 0xff000000) >>> 24;
            int g = (value & 0x00ff0000) >>> 16;
            int b = (value & 0x0000ff00) >>> 8;
            int a = (value & 0x000000ff);
            drawQueue.add(DrawCommand.get(LogicDisplay.commandColor, r, g, b, a, 0, 0));
        } else {
            int num1 = exec.numi(p1);
            if(type == LogicDisplay.commandImage) {
                Object obj = exec.obj(p1);
                num1 = obj instanceof UnlockableContent ? ((UnlockableContent) obj).iconId : 0;
            }

            drawQueue.add(DrawCommand.get(type, exec.numi(x), exec.numi(y), num1, exec.numi(p2), exec.numi(p3), exec.numi(p4)));
        }
    }

}
