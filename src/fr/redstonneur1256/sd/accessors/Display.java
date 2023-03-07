package fr.redstonneur1256.sd.accessors;

import arc.struct.Seq;
import fr.redstonneur1256.sd.DrawCommand;

public interface Display {

    void updateChain();

    void begin(int xOffset, int yOffset);

    void flush(Seq<DrawCommand> commands);

    void end();
    
    Seq<Display> connected();

}
