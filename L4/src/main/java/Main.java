import java.util.Scanner;

import model.FAProcessor;
import ui.UI;

public class Main {
    public static void main(String[] args) {
        FAProcessor processor = new FAProcessor();
        Scanner scanner = new Scanner(System.in);
        UI ui = new UI(processor, scanner);

        ui.run();
    }
}
