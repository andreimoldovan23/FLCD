package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import loaders.PropertiesLoader;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

@Slf4j
@Getter
public class FAProcessor {
    private final List<String> states;
    private final List<String> events;
    private final List<String> finalStates;
    private final String initialState;
    private final List<Pair<Pair<String, String>, String>> transitions;

    private static List<String> getElementsFromLine(Integer index, List<String> lines) {
        return Arrays.stream(lines.get(index).split(" ")).collect(Collectors.toList());
    }

    private static List<Pair<Pair<String, String>, String>> getTransitionsFromFile(Integer startIndex, List<String> lines) {
        List<Pair<Pair<String, String>, String>> transitions = new ArrayList<>();
        for (int i = startIndex; i < lines.size(); i++) {
            List<String> elements = Arrays.stream(lines.get(i).split(" ")).collect(Collectors.toList());

            if (elements.size() != 3) {
                log.error("Invalid FA file format");
                throw new RuntimeException();
            }

            transitions.add(Pair.of(Pair.of(elements.get(0), elements.get(1)), elements.get(2)));
        }

        return transitions;
    }

    public FAProcessor() {
        List<String> fileLines = PropertiesLoader.loadFile();
        if (fileLines.size() <= 4) {
            log.error("Invalid FA file format");
            throw new RuntimeException();
        }

        states = getElementsFromLine(0, fileLines);
        events = getElementsFromLine(1, fileLines);
        initialState = getElementsFromLine(2, fileLines).get(0);
        finalStates = getElementsFromLine(3, fileLines);
        transitions = getTransitionsFromFile(4, fileLines);
    }

    public String matchSeq(String seq) {
        char[] characters = seq.toCharArray();
        String current = initialState;

        for (char c : characters) {
            String symbol = String.valueOf(c);

            if (!events.contains(symbol))
                throw new RuntimeException(String.format("Invalid symbol. The symbol %s is not part of the alphabet", symbol));

            String temp = current;
            Optional<String> maybeNext = transitions.stream()
                    .filter(elem -> elem.getLeft().getLeft().equals(temp) && elem.getLeft().getRight().equals(symbol))
                    .map(Pair::getRight)
                    .findFirst();

            if (maybeNext.isEmpty()) {
                throw new RuntimeException(String.format("Sequence doesn't match. There is no transition from %s with %s", current, symbol));
            }

            current = maybeNext.get();
        }

        if (!finalStates.contains(current))
            throw new RuntimeException(String.format("Invalid sequence. The state %s was reached but it isn't final", current));
        return current;
    }

    @Override
    public String toString() {
        return "States: " +
                String.join(", ", states) +
                "\nEvents: " +
                String.join(", ", events) +
                "\nInitial state: " +
                initialState +
                "\nFinal States: " +
                String.join(", ", finalStates) +
                "\nTransitions:\n" +
                transitions.stream()
                        .map(elem -> "(" + elem.getLeft().getLeft() + ", " + elem.getLeft().getRight() + ") -> " + elem.getRight())
                        .collect(Collectors.joining("\n"));
    }
}
