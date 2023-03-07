package fr.redstonneur1256.sd;

import arc.util.pooling.Pool;
import arc.util.pooling.Pools;

public class DrawCommand {

    public static final Pool<DrawCommand> POOL = Pools.get(DrawCommand.class, DrawCommand::new);

    public byte type;
    public int p0;
    public int p1;
    public int p2;
    public int p3;
    public int p4;
    public int p5;
    public int p6;

    public static DrawCommand get(byte type, int p0, int p1, int p2, int p3, int p4, int p5) {
        DrawCommand command = POOL.obtain();
        command.type = type;
        command.p0 = p0;
        command.p1 = p1;
        command.p2 = p2;
        command.p3 = p3;
        command.p4 = p4;
        command.p5 = p5;
        return command;
    }

}
