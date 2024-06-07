package org.cstr24.hyphengl.interop.source.tf;

import org.cstr24.hyphengl.interop.source.kv.KeyValueEntry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class TFItemDef {
    public String protoName;
    public TFItemDef[] superClasses; //inherits from these
    public KeyValueEntry definitionEntry;
    public boolean prefab;
    public HashMap<String, String> modelPerClass; //class, model
    public String equipRegion;
    public String playerModel;
    public String[] bodyGroups;
    public String[] usedByClasses;
    public TFItemStyle[] styles;

    public TFItemDef(String pName){
        this.protoName = pName;
        modelPerClass = new HashMap<>();
        bodyGroups = new String[0];
        usedByClasses = new String[0];
        styles = new TFItemStyle[0];
    }

    public TFItemDef extend(TFItemDef... definitions){
        superClasses = definitions;

        return this;
    }

    public TFItemDef prefab(boolean pStat){
        prefab = pStat;
        return this;
    }

    public boolean isPrefab() {
        return prefab;
    }

    public String getRandomSuitableClass(){
        var mpc = modelPerClass.keySet().toArray(String[]::new);
        String classResult = "";

        //System.out.println("finding random suitable class for item def " + this.protoName);
        while (classResult.isBlank() || classResult.equals("basename")){
            classResult = mpc[ThreadLocalRandom.current().nextInt(modelPerClass.size())];
        }

        return classResult;
    }

    public boolean isSubClassOf(String className){
        boolean result = false;
        if (superClasses != null && superClasses.length > 0){
            for (int i = 0; i < superClasses.length; i++){
                if (superClasses[i].protoName.equalsIgnoreCase(className)){
                    result = true;
                    break;
                }else{
                    result = superClasses[i].isSubClassOf(className);
                }
            }
        }
        return result;
    }

    public TFItemDef setProperty(String name, String val){
        if (!definitionEntry.hasChild(name)){
            //System.out.println("The definition " + protoName + " is probably not supposed to have " + name + " assigned."); //let's warn the user that the definition doesn't contain this property
            definitionEntry.addChild(name, new KeyValueEntry(name, val));
        }

        return this;
    }

    public boolean hasProperty(String name){
        if (definitionEntry.hasChild(name)){
            return true;
        }else{
            return Arrays.stream(superClasses).anyMatch(baseClass -> baseClass.hasProperty(name));
        }
    }

    public String getProperty(String name){
        String retVal = "";
        if (definitionEntry.hasChild(name)){
            retVal = definitionEntry.getChild(name).getValue(); //we're the newest override in the chain
        }else{
            if (superClasses != null){
                //need to check with all these guys
                for (int i = 0; i < superClasses.length; i++){
                    if (superClasses[i].hasProperty(name)){
                        retVal = superClasses[i].getProperty(name);
                    }
                }
            }
        }
        System.out.println(protoName + " | property " + name + " value: " + retVal);
        return retVal;
    }

    public String getModelForClass(String mercClass, int mercTeam) {
        //System.out.println("begin get of " + protoName + " for " + c);

        String result;
        if (modelPerClass.isEmpty()){
            result = playerModel;
        }else{
            if (modelPerClass.containsKey(mercClass)){
                result = modelPerClass.get(mercClass);
            }else if (modelPerClass.containsKey("basename")){ //we don't have 'c' in there, but there is basename
                if (modelPerClass.size() == 1){
                    //this means we only have basename in there (such as item 30069) and need to get a random usable class.
                    //does usedByClasses contain 'c'? if so, use that, otherwise, get a random one.

                    if (usedByClasses.length == 0){
                        //System.out.println("we don't have used by classes either");
                        result = modelPerClass.get("basename");
                    }else{
                        String finalC = mercClass;
                        String usableClass = Arrays.stream(usedByClasses) //does usedByClasses contain 'c'? if so, return it. if not, get a random one.
                                .filter(s -> s.equals(finalC)).findAny()
                                .orElse(usedByClasses[ThreadLocalRandom.current().nextInt(usedByClasses.length)]);
                        mercClass = usableClass.toLowerCase();
                        result = modelPerClass.getOrDefault(mercClass, modelPerClass.get("basename"));
                    }

                }else{
                    //this means we have a few others and need to get a random suitable one that is NOT basename, because it won't work
                    result = modelPerClass.get(getRandomSuitableClass());
                }
            }else{
                //we don't have a basename either, so just get a random one
                result = modelPerClass.get(getRandomSuitableClass());
            }
        }
        if (mercClass.equals("demoman")){
            mercClass = "demo";
        }
        //System.out.println(this.protoName + " getting for class " + c + " - interim result " + result);

        if (result == null){
            return "";
        }
        return result.replace("%s", mercClass);
    }
}
