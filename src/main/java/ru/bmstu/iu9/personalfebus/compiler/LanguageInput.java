package ru.bmstu.iu9.personalfebus.compiler;

import ru.bmstu.iu9.personalfebus.compiler.lexer.Lexer;
import ru.bmstu.iu9.personalfebus.compiler.lexer.token.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LanguageInput {
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line  = in.readLine();

        StringBuilder input = new StringBuilder();
        while (line != null) {
            input.append(line);
            input.append('\n');

            // next iteration
            line = in.readLine();
        }

        Lexer lexer = new Lexer(input.toString());

        while (lexer.hasTokens()) {
            Token token = lexer.nextToken();
            System.out.println("------");
            System.out.println(token.getType());
            System.out.println(token.getBody());
            //System.out.println("------");
        }
    }
}