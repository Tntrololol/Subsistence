package com.subsistence.common.recipe.core;

import com.google.gson.Gson;
import com.subsistence.common.recipe.manager.GeneralManager;
import cpw.mods.fml.common.FMLLog;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author lclc98
 */
public class GeneralParser {

    public static class ParsedGeneral {

        public int barrelRain;
        public int handCrank;
        public int waterMill;
        public int wormwoodDry;
    }

    public static void parseFile(File file) {
        try {
            FMLLog.info("[Subsistence] Parsing " + file.getName());
            ParsedGeneral recipe = new Gson().fromJson(new FileReader(file), ParsedGeneral.class);
            verifyParse(recipe);
        } catch (IOException ex) {
            FMLLog.warning("[Subsistence] Failed to parse " + file.getName());
        }
    }

    private static void verifyParse(ParsedGeneral recipe) {

        GeneralManager.rain = recipe.barrelRain;
        GeneralManager.handCrank = recipe.handCrank;
        GeneralManager.waterMill = recipe.waterMill;
        GeneralManager.wormwoodDry = recipe.wormwoodDry;
    }
}
