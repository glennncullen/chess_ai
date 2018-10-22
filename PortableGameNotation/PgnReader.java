package PortableGameNotation;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Stack;

class PgnReader {

    private String path;

    PgnReader(String path){
        this.path = path;
    }

    Stack pgnFileToStack() {
        Stack<String> gameStack = new Stack<>();
        Path file = Paths.get(path);
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if(line.length() > 0) {
                    if (line.charAt(0) >= '0' && line.charAt(0) <= '9') {
                        for(int i = 0; i < line.length(); i++){
                            sb.append(line.charAt(i));
                        }
                        sb.append(' ');
                    }
                    if((line.contains("1/2") || line.contains("1-0") || line.contains("0-1")) && !(line.contains("esult"))){
                        gameStack.push(sb.toString());
                        sb = new StringBuilder();
                    }
                }
            }
        } catch (IOException e) {
            System.err.format("IOException: ", e);
            System.exit(1);
        }
        Collections.reverse(gameStack);
        return gameStack;
    }

}