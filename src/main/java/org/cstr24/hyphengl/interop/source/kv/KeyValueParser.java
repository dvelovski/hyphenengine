package org.cstr24.hyphengl.interop.source.kv;

import org.cstr24.hyphengl.filesystem.HyFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import java.util.function.Function;

public class KeyValueParser {
    public static void main(String[] args) throws Exception {
        var tfItems = new KeyValueParser().parse(Paths.get("H:\\Games\\Windows\\SteamLibrary\\SteamApps\\common\\Team Fortress 2\\tf\\scripts\\items\\items_game.txt")).treeRoot;
        System.out.println("*** top level keys ***");
        tfItems.getKeySet().forEach(System.out::println);

        System.out.println("*** prefabs ***");
        var prefabs = tfItems.getChild("prefabs");
        prefabs.getKeySet().forEach(System.out::println);

        System.out.println("*** acceptable items ***");
        var items = tfItems.getChild("items");
        /*items.getKeySet().forEach(System.out::println);*/
        items.children.values().stream().filter(e -> {
            var item = e.get(0);
            return item.hasChild("prefab");
        }).forEach(e -> {});

        var equipConflicts = tfItems.getChild("equip_conflicts");
        var equipRegions = tfItems.getChild("equip_regions_list");
        var gameInfo = tfItems.getChild("game_info");
        var strLookups = tfItems.getChild("string_lookups");

        System.out.println();
    }

    public KeyValueTree parse(HyFile in) throws Exception {
        return parse(in.readString());
    }

    public KeyValueTree parse(Path in) throws Exception {
        if (Files.exists(in)){
            String fileContents = Files.readString(in);

            return parse(fileContents);
        }else{
            throw new FileNotFoundException();
        }
    }

    public KeyValueTree parse(String contents) throws Exception{
        int contentIndex = 0;

        LinkedList<KeyValueToken> tokens = new LinkedList<>();
        while (contentIndex < contents.length()){
            boolean foundMatch = false;

            for (KeyValueTokenType kvType : KeyValueTokenType.values()){
                var matcher = kvType.regexPattern.matcher(contents);
                matcher.region(contentIndex, contents.length());

                if (matcher.lookingAt()){
                    foundMatch = true;

                    String str = matcher.group();
                    contentIndex += str.length();

                    /*if (kvType == KeyValueTokenType.Comment){
                        System.out.println("comment detected: " + str);
                    }*/

                    //System.out.println("Now looking at: " + fileContents.substring(contentIndex));

                    if (kvType != KeyValueTokenType.NewLine && kvType != KeyValueTokenType.Whitespace && kvType != KeyValueTokenType.Comment){
                        var finalTokenType = kvType;
                        String finalTokenVal = str;

                        if (kvType == KeyValueTokenType.Number || kvType == KeyValueTokenType.DXDirective || finalTokenType == KeyValueTokenType.ValueNoQuote){
                            finalTokenType = KeyValueTokenType.Value;
                        }else {
                            if (kvType == KeyValueTokenType.AlternativeValue){
                                kvType = finalTokenType = KeyValueTokenType.Value;
                            }
                            if (kvType == KeyValueTokenType.Value){
                                finalTokenVal = str.substring(1, str.length() - 1); //strip the leading and ending quotes
                            }
                        }

                        tokens.add(new KeyValueToken(finalTokenVal, finalTokenType));
                        //System.out.println("Matched " + kvType + ": " + str);
                    }

                    break;
                }
            }
            if (!foundMatch){
                System.out.println("Did not find a match! \n\"" + contents.substring(contentIndex) + "\"");
                throw new Exception();
            }
        }

        //tokens.forEach(System.out::println);
        return constructTree(tokens);
    }

    public KeyValueTree constructTree(LinkedList<KeyValueToken> tokens){
        KeyValueTree kvTree = new KeyValueTree();
        Stack<KeyValueEntry> treeHierarchy = new Stack<>();

        KeyValueEntry currentLevel = null;
        //treeHierarchy.push(currentLevel);

        while (!tokens.isEmpty()){
            KeyValueToken currToken = tokens.pop();
            switch (currToken.type){
                case Value -> {
                    KeyValueToken nextEntry = tokens.peek();

                    if (nextEntry.type == KeyValueTokenType.Value){
                        //construct an entry
                        String k = currToken.value;
                        String v = tokens.pop().value;
                        //System.out.println("k / v pair: " + k + ": " + v);

                        treeHierarchy.peek().addChild(k, new KeyValueEntry(k, v));
                    }else if (nextEntry.type == KeyValueTokenType.LeftBrace){
                        String k = currToken.value;
                        KeyValueEntry newLevel = new KeyValueEntry(k, "");

                        if (currentLevel == null){
                            kvTree.treeRoot = newLevel;
                        }else{
                            currentLevel.addChild(k, newLevel);
                        }

                        treeHierarchy.push(newLevel);

                        currentLevel = newLevel;

                        //System.out.println("entered new level: " + k);
                        tokens.pop();
                    }//System.out.println("next token is: " + tokens.peek());
                    //TODO handle other types
                }
                case RightBrace -> {
                    //System.out.println("end of level: " + hierarchyPop.name);
                    if (!treeHierarchy.isEmpty()){
                        currentLevel = treeHierarchy.peek();
                    }
                }
            }
        }

        return kvTree;
    }
}
