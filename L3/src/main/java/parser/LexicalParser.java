package parser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import loaders.FileLoader;
import loaders.PropertiesLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import utils.SymbolTable;

@Slf4j
public class LexicalParser {
    private static final String IDENTIFIER = "id";
    private static final String CONSTANT = "const";

    private final List<String> codeLines;
    private final List<String> tokens;

    private final String pifOutPath;
    private final String stIDOutPath;
    private final String stCTOutPath;

    private final SymbolTable identifiersTable;
    private final SymbolTable constantsTable;

    private final List<Pair<String, Pair<Integer, Integer>>> pif;

    public LexicalParser(SymbolTable identifiersTable, SymbolTable constantsTable) {
        codeLines = FileLoader.getLinesFromFile(PropertiesLoader.getSourceCodePath());
        tokens = FileLoader.getLinesFromFile(PropertiesLoader.getTokensPath());

        log.trace("Code lines are: {}", codeLines);
        log.trace("Tokens are: {}", tokens);

        this.pifOutPath = PropertiesLoader.getPIFOutPath();
        this.stIDOutPath = PropertiesLoader.getSTIDOutPath();
        this.stCTOutPath = PropertiesLoader.getSTCTOutPath();
        this.identifiersTable = identifiersTable;
        this.constantsTable = constantsTable;

        pif = new ArrayList<>();
    }

    private List<String> detect(String line) {
        List<String> elements = new ArrayList<>();
        int index;

        while (line.length() > 0) {
            if (line.startsWith("\"")) {
                index = line.substring(1).indexOf("\"");
                if (index != -1) index += 2;
            }
            else {
                Pair<String, Integer> firstToken = Pair.of("", -1);
                for (var token : tokens) {
                    int tokenPos = line.indexOf(token);
                    if ((tokenPos != -1 && firstToken.getRight() == -1) || (tokenPos != -1 && tokenPos < firstToken.getRight()))
                        firstToken = Pair.of(token, tokenPos);

                    if (firstToken.getRight() == 0) break;
                }

                index = firstToken.getRight() == 0 ? firstToken.getLeft().length() : firstToken.getRight();
            }

            if (index == -1) {
                elements.add(line);
                break;
            }

            elements.add(line.substring(0, index));
            line = line.substring(index);
        }

        return elements;
    }

    private boolean patternMatches(String input, String patternString) {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    private boolean isConstant(String token) {
        return patternMatches(token, "^(\"[a-zA-Z0-9]+[a-zA-Z0-9 .,:;?!_()#]*\")|(0)|([+-]?[1-9][0-9]*)|true|false|TRUE|FALSE$");
    }

    private boolean isIdentifier(String token) {
        return patternMatches(token, "^[a-zA-Z]+[a-zA-Z0-9]*$");
    }

    public void parse() {
        try {
            codeLines
               .stream().filter(line -> !(line.toCharArray()[0] == '#' && line.toCharArray()[1] == '#'))
               .forEach(line -> {
                List<String> lineTokens = detect(line);

                for (var token : lineTokens) {
                    if (tokens.contains(token)) {

                        log.trace("Adding '{}' to PIF", token);
                        pif.add(Pair.of(token, Pair.of(0, 0)));

                    } else if (isConstant(token)) {

                        log.trace("Adding constant to PIF");
                        pif.add(Pair.of(CONSTANT, constantsTable.get(token)));

                    } else if (isIdentifier(token)) {

                        log.trace("Adding identifier to PIF");
                        pif.add(Pair.of(IDENTIFIER, identifiersTable.get(token)));

                    } else {
                        throw new RuntimeException("Lexical error at line " + codeLines.indexOf(line) + " at token " + token);
                    }
                }
            });


            log.trace("LEXICAL CORRECT");

            FileLoader.writeToFile(stIDOutPath, identifiersTable.toString());
            FileLoader.writeToFile(stCTOutPath, constantsTable.toString());
            FileLoader.writeToFile(pifOutPath, toString());
        } catch (RuntimeException re) {
            log.trace(re.getMessage());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        pif.forEach(elem -> sb.append(elem.getLeft()).append(" (")
                .append(elem.getRight().getLeft())
                .append(", ").append(elem.getRight().getRight())
                .append(")\n")
        );

        return sb.toString();
    }
}
