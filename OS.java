import java.util.Scanner;
import java.util.Random;

public class OS {
    private static String language = "ua"; // Мова за замовчуванням (ua або en)

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        String input;

        // Вибір мови
        System.out.println("Виберіть мову / Choose a language: (ua/en)");
        String langChoice = scanner.nextLine();
        if (langChoice.equalsIgnoreCase("en")) {
            language = "en";
            System.out.println("Welcome! Type 'commands' to see all commands or 'shutdown' to exit.");
        } else {
            System.out.println("Привіт! Введіть команду 'команди' для списку всіх команд або 'вимкнути' для завершення.");
        }

        do {
            System.out.print("> ");
            input = scanner.nextLine();

            // Перевірка введених команд
            if (input.equalsIgnoreCase(getText("calculator"))) {
                runCalculator();
            } else if (input.equalsIgnoreCase(getText("notepad"))) {
                runNotepad();
            } else if (input.equalsIgnoreCase(getText("phoneBook"))) {
                runPhoneBook();
            } else if (input.equalsIgnoreCase("...")) {
                OS1();
            } else if (input.equalsIgnoreCase(getText("commands"))) {
                help();
            } else if (input.equalsIgnoreCase("i love you")) {
                loe();
            } else if (input.equalsIgnoreCase("program")) {
                rit();
            } else if (!input.equalsIgnoreCase(getText("shutdown"))) {
                System.out.println(getText("unknownCommand"));
            }
        } while (!input.equalsIgnoreCase(getText("shutdown")));

        System.out.println(getText("goodbye"));
        scanner.close();
    }

    // Метод для запуску калькулятора
    private static void runCalculator() {
        Scanner scanner = new Scanner(System.in);
        double num1, num2, result;
        char operator;

        System.out.println(getText("enterFirstNumber"));
        num1 = scanner.nextDouble();

        System.out.println(getText("enterSecondNumber"));
        num2 = scanner.nextDouble();

        System.out.println(getText("enterOperator"));
        operator = scanner.next().charAt(0);

        switch (operator) {
            case '+':
                result = num1 + num2;
                break;
            case '-':
                result = num1 - num2;
                break;
            case '*':
                result = num1 * num2;
                break;
            case '/':
                if (num2 != 0) {
                    result = num1 / num2;
                } else {
                    System.out.println(getText("divideByZero"));
                    return;
                }
                break;
            default:
                System.out.println(getText("invalidOperation"));
                return;
        }

        System.out.println(getText("result") + result);
    }

    // Метод для запуску блокнота
    private static void runNotepad() {
        System.out.println(getText("notepadLaunch"));
        // TODO: Додати реалізацію
    }

    // Метод для запуску телефонної книги
    private static void runPhoneBook() {
        System.out.println(getText("phoneBookLaunch"));
        // TODO: Додати реалізацію
    }

    // Підсвічка
    private static void OS1() {
        System.out.println(getText("developer") + " Тимофій Андрушко");
    }

    // Список команд
    private static void help() {
        if (language.equals("ua")) {
            System.out.println("Доступні команди: калькулятор, блокнот, телефонна книга, вимкнути");
        } else {
            System.out.println("Available commands: calculator, notepad, phone book, shutdown");
        }
    }

    // Секретне повідомлення
    private static void loe() {
        System.out.println("♡");
    }

    // Ефект "хакера"
    private static void rit() throws InterruptedException {
        Random random = new Random();
        String[] characters = {"А", "Б", "В", "Г", "Ґ", "Д", "!", "?", "+", "-"};

        for (int i = 0; i < 10; i++) { // Виводимо 10 рядків
            StringBuilder line = new StringBuilder();
            for (int j = 0; j < 50; j++) {
                line.append(characters[random.nextInt(characters.length)]);
            }
            System.out.println(line);
            Thread.sleep(200); // Затримка для ефекту миготіння
        }
    }

    // Локалізація тексту
    private static String getText(String key) {
        switch (key) {
            case "calculator":
                return language.equals("ua") ? "калькулятор" : "calculator";
            case "notepad":
                return language.equals("ua") ? "блокнот" : "notepad";
            case "phoneBook":
                return language.equals("ua") ? "телефонна книга" : "phone book";
            case "commands":
                return language.equals("ua") ? "команди" : "commands";
            case "shutdown":
                return language.equals("ua") ? "вимкнути" : "shutdown";
            case "unknownCommand":
                return language.equals("ua") ? "Невідома команда. Спробуйте ще раз." : "Unknown command. Please try again.";
            case "goodbye":
                return language.equals("ua") ? "До побачення!" : "Goodbye!";
            case "enterFirstNumber":
                return language.equals("ua") ? "Введіть перше число:" : "Enter the first number:";
            case "enterSecondNumber":
                return language.equals("ua") ? "Введіть друге число:" : "Enter the second number:";
            case "enterOperator":
                return language.equals("ua") ? "Введіть операцію (+, -, *, /):" : "Enter the operation (+, -, *, /):";
            case "divideByZero":
                return language.equals("ua") ? "Ділити на нуль не можна" : "Division by zero is not allowed";
            case "invalidOperation":
                return language.equals("ua") ? "Неправильна операція." : "Invalid operation.";
            case "result":
                return language.equals("ua") ? "Результат: " : "Result: ";
            case "notepadLaunch":
                return language.equals("ua") ? "Запускаю блокнот..." : "Launching notepad...";
            case "phoneBookLaunch":
                return language.equals("ua") ? "Запускаю телефонну книгу..." : "Launching phone book...";
            case "developer":
                return language.equals("ua") ? "Розробник OS:" : "OS Developer:";
            default:
                return "";
        }
    }
}
