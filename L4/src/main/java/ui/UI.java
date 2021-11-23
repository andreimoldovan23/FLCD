package ui;

import java.util.Scanner;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import model.FAProcessor;

@RequiredArgsConstructor
public class UI {
    private final FAProcessor faProcessor;
    private final Scanner scanner;

    private static final String[] commands = {
            "1. Print all states",
            "2. Print alphabet",
            "3. Print initial state",
            "4. Print all final states",
            "5. Print all transitions",
            "6. Check if sequence is accepted by FA",
            "7. Exit"
    };

    private void printAllStates() {
        System.out.println("States: " + String.join(", ", faProcessor.getStates()));
    }

    private void printAlphabet() {
        System.out.println("Alphabet: " + String.join(", ", faProcessor.getEvents()));
    }

    private void printInitialState() {
        System.out.println("Initial state: " + faProcessor.getInitialState());
    }

    private void printFinalStates() {
        System.out.println("Final states: " + String.join(", ", faProcessor.getFinalStates()));
    }

    private void printTransitions() {
        System.out.println(
                "\nTransitions:\n" +
                faProcessor.getTransitions().stream()
                        .map(elem -> "(" + elem.getLeft().getLeft() + ", " + elem.getLeft().getRight() + ") -> " + elem.getRight())
                        .collect(Collectors.joining("\n")));
    }

    private void checkSeq() {
        System.out.println("Type sequence: ");
        String seq = scanner.nextLine();

        try {
            String result = faProcessor.matchSeq(seq);
            System.out.println("Reached final state: " + result);
        } catch (RuntimeException re) {
            System.out.println(re.getMessage());
        }
    }

    private void invokeOptions(Integer menuOption) {
        switch (menuOption) {
            case 1 -> printAllStates();
            case 2 -> printAlphabet();
            case 3 -> printInitialState();
            case 4 -> printFinalStates();
            case 5 -> printTransitions();
            case 6 -> checkSeq();
            case 7 -> System.exit(0);
        }
    }

    public void run() {
        while (true) {
            System.out.println("\nMenu\n");
            System.out.println(String.join("\n", commands));
            System.out.println();
            System.out.println("Type input:");

            String option = scanner.nextLine();
            int menuOption;
            try {
                menuOption = Integer.parseInt(option.trim());
                if (menuOption < 1 || menuOption > 7) throw new RuntimeException();
            } catch (NumberFormatException ne) {
                System.out.println("Wrong input");
                continue;
            } catch (RuntimeException re) {
                System.out.println("Invalid menu option");
                continue;
            }

            invokeOptions(menuOption);
        }
    }
}
