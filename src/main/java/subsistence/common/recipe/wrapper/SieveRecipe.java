package subsistence.common.recipe.wrapper;

import net.minecraft.item.ItemStack;
import subsistence.common.lib.RandomStack;
import subsistence.common.util.StackHelper;

import java.util.ArrayList;
import java.util.List;

public class SieveRecipe {

    private final ItemStack input;
    private final RandomStack[] outputBlock;
    private final RandomStack[] outputHand;
    private final int durationBlock;
    private final int durationHand;

    private final boolean ignoreNBT;

    public SieveRecipe(ItemStack input, RandomStack[] outputBlock, RandomStack[] outputHand, int durationBlock, int durationHand, boolean ignoreNBT) {
        this.input = input;
        this.outputBlock = outputBlock;
        this.outputHand = outputHand;
        this.durationBlock = durationBlock;
        this.durationHand = durationHand;
        this.ignoreNBT = ignoreNBT;
    }

    public int getDurationBlock() {
        return durationBlock;
    }

    public int getDurationHand() {
        return durationHand;
    }

    public boolean valid(ItemStack stack) {
        return StackHelper.areStacksSimilar(stack, input, ignoreNBT);
    }

    public ItemStack[] get(ItemStack input, boolean block) {
        List<ItemStack> out = new ArrayList<ItemStack>();

        if (block)
            for (RandomStack stack : outputBlock) {
                out.add(stack.get());
            }
        else
            for (RandomStack stack : outputHand) {
                out.add(stack.get());
            }

        return out.toArray(new ItemStack[out.size()]);
    }
}