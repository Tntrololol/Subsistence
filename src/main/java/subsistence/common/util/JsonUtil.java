package subsistence.common.util;

import com.google.gson.*;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import subsistence.common.recipe.wrapper.stack.GenericStack;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * @author dmillerw
 */
public class JsonUtil {

    private static Gson gson;

    public static Gson gson() {
        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();

            gsonBuilder.registerTypeAdapter(GenericStack.class, new GenericStackDeserializer());
            gsonBuilder.registerTypeAdapter(ItemStack.class, new ItemStackDeserializer());
            gsonBuilder.registerTypeAdapter(FluidStack.class, new FluidStackDeserializer());

            gson = gsonBuilder.setPrettyPrinting().create();
        }
        return gson;
    }

    public static class GenericStackDeserializer implements JsonDeserializer<GenericStack> {

        @Override
        public GenericStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            ItemStack[] contents = new ItemStack[0];
            if (json.isJsonPrimitive()) {
                boolean resolved = false;
                String string = json.getAsString();

                if (string.contains(":")) { // Item? or ore tag
                    if (string.startsWith("ore:")) {
                        string = string.substring(4);
                        // will fall through to the ore tag detection
                    } else {
                        contents = new ItemStack[1];
                        contents[0] = new ItemStack(GameData.getItemRegistry().getObject(string));
                        resolved = true;
                    }
                } else { // If not, try appending 'minecraft:' and see if it's a valid item
                    Item item = GameData.getItemRegistry().getObject("minecraft:" + string);
                    if (item != null) {
                        contents = new ItemStack[1];
                        contents[0] = new ItemStack(item);
                        resolved = true;
                    }
                }

                if (!resolved) {
                    // If we've gotten here, it's just an ore dictionary tag
                    ArrayList<ItemStack> ores = OreDictionary.getOres(string);
                    contents = ores.toArray(new ItemStack[ores.size()]);
                }
            } else if (json.isJsonObject()) {// It's a full ItemStack object
                contents = new ItemStack[1];
                contents[0] = context.deserialize(json, ItemStack.class);
            }

            GenericStack genericStack = new GenericStack();
            genericStack.contents = contents;
            return genericStack;
        }
    }

    public static class ItemStackDeserializer implements JsonDeserializer<ItemStack> {

        @Override
        public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject()) {
                if (json.isJsonPrimitive()) {
                    String string = json.getAsString();

                    if (!string.contains(":")) {
                        string = "minecraft:" + string;
                    }

                    Item item = GameData.getItemRegistry().getObject(string);
                    if (item == null) {
                        throw new JsonParseException(string + " is not a valid item!");
                    }

                    return new ItemStack(item);
                } else {
                    throw new JsonParseException("Cannot deserialize ItemStack from " + json.getClass().getSimpleName());
                }
            } else {
                JsonObject object = json.getAsJsonObject();

                if (!object.has("item")) {
                    throw new JsonParseException("ItemStack json object is missing 'item' key");
                }

                String itemString = object.get("item").getAsString();
                int damage = object.has("damage") ? object.get("damage").getAsInt() : 0;
                int amount = object.has("amount") ? object.get("amount").getAsInt() : 1;

                if (!itemString.contains(":")) {
                    itemString = "minecraft:" + itemString;
                }

                Item item = GameData.getItemRegistry().getObject(itemString);
                if (item == null) {
                    throw new JsonParseException(itemString + " is not a valid item!");
                }

                return new ItemStack(GameData.getItemRegistry().getObject(itemString), amount, damage);
            }
        }
    }

    public static class FluidStackDeserializer implements JsonDeserializer<FluidStack> {

        @Override
        public FluidStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject()) {
                if (json.isJsonPrimitive()) {
                    String string = json.getAsString();

                    Fluid fluid = FluidRegistry.getFluid(string);
                    if (fluid == null) {
                        throw new JsonParseException(string + " is not a valid fluid!");
                    }

                    return new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME);
                } else {
                    throw new JsonParseException("Cannot deserialize FluidStack from " + json.getClass().getSimpleName());
                }
            } else {
                JsonObject object = json.getAsJsonObject();

                if (!object.has("fluid")) {
                    throw new JsonParseException("ItemStack json object is missing 'fluid' key");
                }

                String fluidString = object.get("item").getAsString();
                int amount = object.has("amount") ? object.get("amount").getAsInt() : FluidContainerRegistry.BUCKET_VOLUME;

                Fluid fluid = FluidRegistry.getFluid(fluidString);
                if (fluid == null) {
                    throw new JsonParseException(fluidString + " is not a valid fluid!");
                }

                return new FluidStack(FluidRegistry.getFluid(fluidString), amount);
            }
        }
    }
}
