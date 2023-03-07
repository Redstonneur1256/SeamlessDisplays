package fr.redstonneur1256.sd.mixins.logic;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.gl.FrameBuffer;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Tmp;
import fr.redstonneur1256.sd.accessors.Display;
import fr.redstonneur1256.sd.DrawCommand;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import mindustry.world.Edges;
import mindustry.world.blocks.logic.LogicDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LogicDisplay.LogicDisplayBuild.class)
public class LogicDisplayBuildMixin extends Building implements Display {

    @Shadow
    public FrameBuffer buffer;
    @Shadow
    public float color;
    @Shadow
    public float stroke;
    private Seq<Display> connected;

    @Override
    public void begin(int xOffset, int yOffset) {
        int size = ((LogicDisplay) block).displaySize;

        if(buffer == null) {
            buffer = new FrameBuffer(size, size);
            //clear the buffer - some OSs leave garbage in it
            buffer.begin(Pal.darkerMetal);
            buffer.end();
        }

        Tmp.m1.set(Draw.proj());
        Draw.proj(0, 0, size, size);
        Draw.proj().translate(xOffset, yOffset);

        buffer.begin();
        Draw.color(color);
        Lines.stroke(stroke);
    }

    @Override
    public void flush(Seq<DrawCommand> commands) {
        for(DrawCommand command : commands) {
            int p0 = command.p0;
            int p1 = command.p1;
            int p2 = command.p2;
            int p3 = command.p3;
            int p4 = command.p4;
            int p5 = command.p5;

            switch(command.type) {
                case LogicDisplay.commandClear:
                    Core.graphics.clear(p0 / 255f, p1 / 255f, p2 / 255f, 1f);
                    break;
                case LogicDisplay.commandLine:
                    Lines.line(p0, p1, p2, p3);
                    break;
                case LogicDisplay.commandRect:
                    Fill.crect(p0, p1, p2, p3);
                    break;
                case LogicDisplay.commandLineRect:
                    Lines.rect(p0, p1, p2, p3);
                    break;
                case LogicDisplay.commandPoly:
                    Fill.poly(p0, p1, Math.min(p2, 25), p3, p4);
                    break;
                case LogicDisplay.commandLinePoly:
                    Lines.poly(p0, p1, Math.min(p2, 25), p3, p4);
                    break;
                case LogicDisplay.commandTriangle:
                    Fill.tri(p0, p1, p2, p3, p4, p5);
                    break;
                case LogicDisplay.commandColor:
                    Draw.color(color = Color.toFloatBits(p0, p1, p2, p3));
                    break;
                case LogicDisplay.commandStroke:
                    Lines.stroke(stroke = p0);
                    break;
                case LogicDisplay.commandImage:
                    Draw.rect(Fonts.logicIcon(p2), p0, p1, p3, p3, p4);
                    break;
            }
        }
    }

    @Override
    public void end() {
        buffer.end();
        Draw.proj(Tmp.m1);
        Draw.reset();
    }

    @Override
    public void placed() {
        super.placed();

        updateChain();
        connected().each(Display::updateChain);
    }

    @Inject(method = "remove", at = @At("HEAD"))
    public void remove(CallbackInfo ci) {
        updateChain();
        connected().each(Display::updateChain);
    }

    @Override
    public void draw() {
        Draw.blend(Blending.disabled);
        Draw.draw(Draw.z(), () -> {
            if(buffer == null) {
                begin(0, 0);
                end();
            }
            float size = Vars.tilesize * block.size;
            Draw.rect(Draw.wrap(buffer.getTexture()), x, y, size, -size);
        });
        Draw.blend();
    }

    @Override
    public void updateChain() {
        connected = null;
    }

    @Override
    public Seq<Display> connected() {
        if(connected == null) {
            connected = findConnected();
        }

        return connected;
    }

    private Seq<Display> findConnected() {
        Seq<Building> queue = new Seq<>(false);
        Seq<Display> all = new Seq<>(false);

        queue.add(this);
        all.add(this);

        while(queue.any()) {
            Building building = queue.pop();

            for(Point2 direction : Edges.getEdges(building.block.size)) {
                Building nearby = building.nearby(direction.x, direction.y);
                if(!(nearby instanceof Display)) {
                    continue;
                }
                if(!all.addUnique((Display) nearby)) {
                    continue;
                }
                queue.add(nearby);
            }
        }

        return all;
    }

}
