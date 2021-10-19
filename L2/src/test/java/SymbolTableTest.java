import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.SymbolTable;

public class SymbolTableTest {
    private Integer numberSlots;
    private SymbolTable symbolTable;

    //ascii - 294;  hash 4
    private static final String symbol1 = "abc";
    //ascii - 48;  hash 8
    private static final String symbol2 = "0";
    //ascii - 318;  hash 8
    private static final String symbol3 = "deu";
    //ascii - 294;  hash 4
    private static final String symbol4 = "abc";
    private static final String symbol5 = "aaa";
    private static final String symbol6 = "bbb";
    private static final String symbol7 = "ccc";
    private static final String symbol8 = "eee";
    private static final String symbol9 = "fff";

    @Before
    public void setUp() {
        numberSlots = 10;
        symbolTable = new SymbolTable(numberSlots);
    }

    @Test
    public void getTest() {

        Pair<Integer, Integer> posSymbol1 = symbolTable.get(symbol1);
        Pair<Integer, Integer> posSymbol2 = symbolTable.get(symbol2);
        Pair<Integer, Integer> posSymbol3 = symbolTable.get(symbol3);
        Pair<Integer, Integer> posSymbol4 = symbolTable.get(symbol4);

        Assert.assertEquals(hash(symbol1, numberSlots), posSymbol1.getLeft());
        Assert.assertEquals(0, (int) posSymbol1.getRight());

        Assert.assertEquals(hash(symbol2, numberSlots), posSymbol2.getLeft());
        Assert.assertEquals(0, (int) posSymbol2.getRight());

        Assert.assertEquals(hash(symbol3, numberSlots), posSymbol3.getLeft());
        Assert.assertEquals(posSymbol2.getLeft(), posSymbol3.getLeft());
        Assert.assertEquals(1, (int) posSymbol3.getRight());

        Assert.assertEquals(hash(symbol4, numberSlots), posSymbol4.getLeft());
        Assert.assertEquals(posSymbol1.getLeft(), posSymbol4.getLeft());
        Assert.assertEquals(posSymbol1.getLeft(), posSymbol4.getLeft());
    }

    @Test
    public void resizeAndRehashTest() {
        symbolTable.get(symbol1);
        symbolTable.get(symbol2);
        symbolTable.get(symbol3);
        symbolTable.get(symbol5);
        symbolTable.get(symbol6);
        symbolTable.get(symbol7);
        symbolTable.get(symbol8);
        symbolTable.get(symbol9);

        Assert.assertEquals(numberSlots * 2, (int) symbolTable.getNumberSlots());
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
}
