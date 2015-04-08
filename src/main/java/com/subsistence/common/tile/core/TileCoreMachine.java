package com.subsistence.common.tile.core;

import com.subsistence.common.network.nbt.NBTHandler;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author dmillerw
 */
public abstract class TileCoreMachine extends TileCore {

    @NBTHandler.NBTData
    @NBTHandler.DescriptionData
    public ForgeDirection orientation = ForgeDirection.UNKNOWN;

    public TileCoreMachine() {
        super();

        this.handler.addField(TileCoreMachine.class, "orientation");
    }
}