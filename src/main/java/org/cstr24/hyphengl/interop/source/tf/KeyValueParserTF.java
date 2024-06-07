package org.cstr24.hyphengl.interop.source.tf;

import org.cstr24.hyphengl.filesystem.HyFile;
import org.cstr24.hyphengl.interop.source.kv.*;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Stack;

public class KeyValueParserTF {
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

    public KeyValueTree parse(String fileContents) throws Exception {
        LinkedList<KeyValueToken> tokens = new LinkedList<>();
        int charCursor = 0;
        int textLength = fileContents.length();

        while (charCursor < textLength){
            boolean foundMatch = false;

            for (KeyValueTokenType tokenType : KeyValueTokenType.values()){
                var matcher = tokenType.regexPattern.matcher(fileContents);
                matcher.region(charCursor, textLength);

                if (matcher.lookingAt()){
                    String foundValue = matcher.group();
                    //System.out.println(tokenType.name() + ": " + foundValue);

                    charCursor += foundValue.length();
                    foundMatch = true;

                    if (tokenType == KeyValueTokenType.Value || tokenType == KeyValueTokenType.AlternativeValue || tokenType == KeyValueTokenType.LeftBrace || tokenType == KeyValueTokenType.RightBrace){
                        if (tokenType == KeyValueTokenType.Value || tokenType == KeyValueTokenType.AlternativeValue){
                            tokens.add(new KeyValueToken(foundValue.substring(1, foundValue.length() - 1), KeyValueTokenType.Value));
                        }else{
                            tokens.add(new KeyValueToken(foundValue, tokenType));
                        }
                    }else{
                        if (tokenType != KeyValueTokenType.Whitespace && tokenType != KeyValueTokenType.NewLine){
                            //System.out.println("found a different type of non-whitespace token: " + foundValue + " - " + tokenType);
                        }
                    }
                }
            }
            if (!foundMatch){
                throw new Exception("failed at " + charCursor + " - surrounding text: " + fileContents.substring(charCursor, Math.min(charCursor + 64, fileContents.length())));
            }
        }
        return constructTree(tokens);
    }

    public KeyValueTree constructTree(LinkedList<KeyValueToken> tokens) throws Exception {
        KeyValueEntry rootEntry = null;
        KeyValueEntry currentEntry = null;

        Stack<KeyValueEntry> processingTree = new Stack<>();

        int tokensProcessed = 0;

        while (!tokens.isEmpty()){
            KeyValueToken currToken = tokens.peek();
            if (currToken != null){
                tokens.pop();
                tokensProcessed++;

                switch (currToken.type){
                    case Value -> {
                        KeyValueToken nextToken = tokens.peek();
                        if (nextToken != null){
                            tokens.pop();
                            tokensProcessed++;

                            if (nextToken.type == KeyValueTokenType.Value){
                                KeyValueEntry toAdd = new KeyValueEntry(currToken.value, nextToken.value);
                                currentEntry.addChild(currToken.value, toAdd);

                                //System.out.println("adding: " + currToken.value + " / " + nextToken.value + " to " + currentEntry);

                            }else if (nextToken.type == KeyValueTokenType.LeftBrace){
                                KeyValueEntry newLevel = new KeyValueEntry(currToken.value, "");

                                //System.out.println(currToken.value + " - a new parent");

                                if (rootEntry == null){
                                    rootEntry = newLevel;
                                }else{
                                    currentEntry.addChild(currToken.value, newLevel);
                                }

                                currentEntry = newLevel;

                                processingTree.push(newLevel);
                            }
                        }else{
                            throw new Exception("value with no next element -> tokens processed: " + tokensProcessed);
                        }
                    }
                    case RightBrace -> {
                        if (!processingTree.isEmpty()){
                            //System.out.println(processingTree.peek().name + " - popping parent");
                            processingTree.pop();

                            if (!processingTree.isEmpty()){
                                currentEntry = processingTree.peek();
                            }
                        }
                    }
                }
            }
        }

        return new KeyValueTree(rootEntry);
    }
}
