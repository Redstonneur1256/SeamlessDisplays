package fr.redstonneur1256.sd.accessors;

import arc.struct.Seq;
import fr.redstonneur1256.sd.DrawCommand;

public interface AdvancedExecutor {

    Seq<DrawCommand> getDrawQueue();

}
