package org.cstr24.hyphengl.interop.source.tf;

import org.cstr24.hyphengl.interop.source.kv.KeyValueEntry;

import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TFItemLibrary {
    public  List<String> acceptablePrefabs = List.of(
            "base_hat", "base_misc", "beard",
            "cosmetic", "grenades", "hat",
            "hat_decoration", "mask", "misc",
            "promo", "pyrovision_googles", "score_reward_hat",
            "valve");
    HashMap<String, TFItemDef> itemDefMap;
    HashMap<String, TFItemDef> itemNameMap;

    ArrayList<String> itemKeys;

    public TFItemLibrary(){
        itemDefMap = new HashMap<>();
        itemNameMap = new HashMap<>();
        itemKeys = new ArrayList<>();
    }
    public int getItemCount(){
        return itemDefMap.size();
    }

    public void load(String handle){
        KeyValueEntry tfItems = null;
        try {
            tfItems = new KeyValueParserTF().parse(Paths.get(handle)).treeRoot;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        /*System.out.println("*** top level keys ***");
        tfItems.getKeySet().forEach(System.out::println);*/

        var prefabs = tfItems.getChild("prefabs");
        prefabs.getKeySet().forEach(e -> {
            var def = prefabs.getChild(e);
            TFItemDef prefab = new TFItemDef(e).prefab(true);
            prefab.definitionEntry = def;

            addDefinition(e, prefab);
        });

        var items = tfItems.getChild("items");
        items.getKeySet().forEach(e -> {
            KeyValueEntry def = items.getChild(e);
            String itemName = e;
            if (def.hasChild("item_name")){
                itemName = def.getChild("item_name").getValue();
            }
            TFItemDef itemDef = new TFItemDef(itemName).prefab(false);
            itemDef.definitionEntry = def;

            addDefinition(e, itemDef);
        });

        itemKeys.forEach(k -> {
            this.processDefinition(itemDefMap.get(k)); //process in order, to preserve inheritance
        });
    }

    public TFItemDef getDefinition(String key){
        return itemDefMap.get(key);
    }
    public void addDefinition(String key, TFItemDef value){
        itemDefMap.put(key, value);
        itemKeys.add(key);
        itemNameMap.put(value.protoName, value);
    }
    public TFItemDef getRandomDefinition(){
         boolean acceptable = false;
         TFItemDef result = null;
         int numKeys = itemKeys.size();
         var tlRandom = ThreadLocalRandom.current();

         while (!acceptable){
             result = itemDefMap.get(itemKeys.get(tlRandom.nextInt(numKeys)));
             acceptable = itemDefIsAcceptable(result);
             if (result.prefab){
                 acceptable = false;

             }
         }

         //System.out.println(result);

         return result;
    }
    public boolean itemDefIsAcceptable(TFItemDef test){
        return !test.prefab && !test.isSubClassOf("medal") &&
                test.isSubClassOf("hat") ||
                test.isSubClassOf("base_hat") ||
                test.isSubClassOf("cosmetic") ||
                test.isSubClassOf("beard") ||
                test.isSubClassOf("grenades") ||
                test.isSubClassOf("mask") ||
                test.isSubClassOf("misc");
    }

    public void processDefinition(TFItemDef itemDef){
        KeyValueEntry def = itemDef.definitionEntry;

        if (def.hasChild("prefab")){
            String[] superclasses = def.getChild("prefab").getValue().split(" ");

            itemDef.superClasses = new TFItemDef[superclasses.length];
            for (int i = 0; i < superclasses.length; i++){
                itemDef.superClasses[i] = itemNameMap.get(superclasses[i]);
                itemDef.modelPerClass.putAll(itemDef.superClasses[i].modelPerClass);
                itemDef.bodyGroups = itemDef.superClasses[i].bodyGroups; //will be overridden if present
                itemDef.usedByClasses = itemDef.superClasses[i].usedByClasses; //will be overridden if present
                itemDef.playerModel = itemDef.superClasses[i].playerModel; //this will be overridden if present
            }
        }

        if (def.hasChild("visuals")){
            //I want to know about:
            // player body groups (it tells me what things on the body it replaces)
            // styles (if any)
            var visNode = def.getChild("visuals");
            if (visNode.hasChild("player_bodygroups")){
                var pbgNode = visNode.getChild("player_bodygroups");
                var resultList = new String[pbgNode.children.size()];
                int j = 0;
                for (String s : pbgNode.getKeySet()) {
                    resultList[j++] = s;
                }
                itemDef.bodyGroups = resultList;
            }
            if (visNode.hasChild("styles")){
                var stlNode = visNode.getChild("styles");
                int styleCount = stlNode.children.size();

                itemDef.styles = new TFItemStyle[styleCount];
                for (int i = 0; i < styleCount; i++){
                    itemDef.styles[i] = new TFItemStyle();
                    var currNode = stlNode.getChild(Integer.toString(i));

                    if (currNode != null){
                        //System.out.println(currNode);
                        if (currNode.hasChild("skin")){
                            int styleInt = Integer.parseInt(currNode.getChild("skin").value);
                            itemDef.styles[i].skinRed = styleInt;
                            itemDef.styles[i].skinBlue = styleInt;
                        }else{
                            if (currNode.hasChild("skin_red")){
                                itemDef.styles[i].skinRed = Integer.parseInt(currNode.getChild("skin_red").value);
                            }
                            if (currNode.hasChild("skin_blu")){
                                itemDef.styles[i].skinBlue = Integer.parseInt(currNode.getChild("skin_blu").value);
                            }
                            if (currNode.hasChild("name")){
                                itemDef.styles[i].styleName = currNode.getChild("name").value;
                            }
                        }
                    }
                }
            }
        }

        //model_player_per_class, let's see how this looks
        if (def.hasChild("model_player_per_class")){
            var modelPerClass = def.getChild("model_player_per_class");
            modelPerClass.getKeySet().forEach(k -> {
                itemDef.modelPerClass.put(k, modelPerClass.getChild(k).getValue());
            });
        }
        if (def.hasChild("model_player")){
            itemDef.playerModel = def.getChild("model_player").getValue();
        }
        if (def.hasChild("equip_region")){
            itemDef.equipRegion = def.getChild("equip_region").getValue();
        }
        if (def.hasChild("used_by_classes")){
            var ubcNode = def.getChild("used_by_classes");
            itemDef.usedByClasses = new String[ubcNode.children.size()];
            int j = 0;
            for (String s : ubcNode.getKeySet()){
                itemDef.usedByClasses[j++] = s;
            }
        }

        if (def.hasChild("capabilities")){

        }
    }
}
