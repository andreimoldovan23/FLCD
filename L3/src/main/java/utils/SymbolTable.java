package utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import loaders.PropertiesLoader;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

@Slf4j
public class SymbolTable {
    private List<List<String>> hashMap = new ArrayList<>();
    @Getter private Integer numberSlots = PropertiesLoader.getInitialSlots();
    private Integer occupiedSlots = 0;

    private static final Double hashThreshold = PropertiesLoader.getThreshold();

    public SymbolTable() {
        log.trace("Symbol Table - initial slots: {} - threshold: {}", numberSlots, hashThreshold);

        IntStream.range(0, this.numberSlots)
                .forEach(nr -> this.hashMap.add(new ArrayList<>()));
    }

    private static Integer asciiSum(String string) {
        int sum = 0;
        for (var c : string.toCharArray())
            sum += c;
        return sum;
    }

    private static Integer hash(String symbol, Integer slots) {
        return asciiSum(symbol) % slots;
    }

    private void resizeAndRehash() {
        log.trace("Resize and rehash. Load factor currently: {}", Double.valueOf(occupiedSlots) / numberSlots);

        List<List<String>> newMap = new ArrayList<>();
        IntStream.range(0, 2 * numberSlots)
                .forEach(nr -> newMap.add(new ArrayList<>()));

        hashMap.stream()
                .filter(list -> list.size() > 0)
                .flatMap(Collection::stream).collect(Collectors.toList())
                .forEach(symbol -> newMap.get(hash(symbol, 2 * numberSlots)).add(symbol));

        hashMap = newMap;
        numberSlots *= 2;
    }

    private Pair<Integer, Integer> find(String symbol) {
        int firstPosition = hash(symbol, numberSlots);
        List<String> atPosition = hashMap.get(firstPosition);

        int secondPosition = -1;
        for (int i = 0; i < atPosition.size(); i++) {
            if (atPosition.get(i).equals(symbol)) {
                secondPosition = i;
                break;
            }
        }

        if (secondPosition == -1) {
            firstPosition = -1;
            log.trace("Symbol {} does not exist yet", symbol);
        }
        else {
            log.trace("Symbol {} already exists at: {}, {}", symbol, firstPosition, secondPosition);
        }

        return Pair.of(firstPosition, secondPosition);
    }

    private Pair<Integer, Integer> add(String symbol) {
        if (((double) occupiedSlots) / numberSlots >= hashThreshold) resizeAndRehash();

        int firstPos = hash(symbol, numberSlots);
        List<String> atPos = hashMap.get(firstPos);
        atPos.add(symbol);
        occupiedSlots += 1;

        log.trace("Added symbol {} at: {}, {}", symbol, firstPos, atPos.size() - 1);

        return Pair.of(firstPos, atPos.size() - 1);
    }

    public Pair<Integer, Integer> get(String symbol) {
        log.trace("Trying to get position of symbol: {}", symbol);

        Pair<Integer, Integer> pos = find(symbol);
        return pos.getLeft() == -1 ? add(symbol) : pos;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numberSlots; i++) {
            List<String> linkedList = hashMap.get(i);
            for (int j = 0; j < linkedList.size(); j++) {
                sb.append(linkedList.get(j)).append(" (").append(i).append(", ").append(j).append(")\n");
            }
        }
        return sb.toString();
    }
}
