import parser.LexicalParser;
import utils.SymbolTable;

public class Main {
    public static void main(String[] args) {
        SymbolTable identifiersTable = new SymbolTable();
        SymbolTable constantsTable = new SymbolTable();
        LexicalParser parser = new LexicalParser(identifiersTable, constantsTable);
        parser.parse();
    }
}
