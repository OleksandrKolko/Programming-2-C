import java.util.Scanner;

/**
 * Головний клас для демонстрації роботи з тензорами.
 * Містить меню для взаємодії з користувачем.
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TensorIO<Double> tensorIO = new TensorIO<>();
        Tensor<Double> currentTensor = null;

        System.out.println("=== Система роботи з тензорами ===");

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Виберіть опцію: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        currentTensor = tensorIO.readFromConsole(Double.class);
                        tensorIO.printToConsole(currentTensor);
                        break;

                    case 2:
                        if (currentTensor == null) {
                            System.out.println("Спочатку створіть тензор!");
                            break;
                        }
                        System.out.print("Введіть ім'я JSON файлу: ");
                        String jsonFile = scanner.nextLine().trim();
                        tensorIO.writeToJson(currentTensor, jsonFile);
                        break;

                    case 3:
                        System.out.print("Введіть ім'я JSON файлу: ");
                        jsonFile = scanner.nextLine().trim();
                        currentTensor = tensorIO.readFromJson(jsonFile, Double.class);
                        tensorIO.printToConsole(currentTensor);
                        break;

                    case 4:
                        if (currentTensor == null) {
                            System.out.println("Спочатку створіть тензор!");
                            break;
                        }
                        System.out.print("Введіть ім'я XML файлу: ");
                        String xmlFile = scanner.nextLine().trim();
                        tensorIO.writeToXml(currentTensor, xmlFile);
                        break;

                    case 5:
                        System.out.print("Введіть ім'я XML файлу: ");
                        xmlFile = scanner.nextLine().trim();
                        currentTensor = tensorIO.readFromXml(xmlFile, Double.class);
                        tensorIO.printToConsole(currentTensor);
                        break;

                    case 6:
                        if (currentTensor == null) {
                            System.out.println("Спочатку створіть тензор!");
                            break;
                        }
                        tensorIO.printToConsole(currentTensor);
                        break;

                    case 7:
                        if (currentTensor == null) {
                            System.out.println("Спочатку створіть тензор!");
                            break;
                        }
                        performOperations(currentTensor, scanner);
                        break;

                    case 8:
                        currentTensor = createSampleTensor();
                        tensorIO.printToConsole(currentTensor);
                        break;

                    case 0:
                        running = false;
                        System.out.println("Дякуємо за використання програми!");
                        break;

                    default:
                        System.out.println("Невірний вибір. Спробуйте ще раз.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Будь ласка, введіть число!");
            } catch (Exception e) {
                System.out.println("Помилка: " + e.getMessage());
                e.printStackTrace();
            }
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n=== Головне меню ===");
        System.out.println("1. Створити тензор з консолі");
        System.out.println("2. Зберегти тензор у JSON файл");
        System.out.println("3. Завантажити тензор з JSON файлу");
        System.out.println("4. Зберегти тензор у XML файл");
        System.out.println("5. Завантажити тензор з XML файлу");
        System.out.println("6. Показати поточний тензор");
        System.out.println("7. Операції з тензором");
        System.out.println("8. Створити приклад тензора");
        System.out.println("0. Вийти");
    }

    private static void performOperations(Tensor<Double> tensor, Scanner scanner) throws TensorException {
        System.out.println("\n=== Операції з тензором ===");
        System.out.println("1. Додати тензор");
        System.out.println("2. Помножити на скаляр");
        System.out.println("3. Транспонувати (для 2D)");
        System.out.println("4. Обчислити статистику");

        int choice = Integer.parseInt(scanner.nextLine().trim());
        TensorIO<Double> io = new TensorIO<>();
        Tensor<Double> result = null;

        switch (choice) {
            case 1:
                System.out.println("Створіть другий тензор для додавання:");
                Tensor<Double> other = io.readFromConsole(Double.class);
                result = tensor.add(other);
                System.out.println("\nРезультат додавання:");
                io.printToConsole(result);
                break;

            case 2:
                System.out.print("Введіть скаляр: ");
                double scalar = Double.parseDouble(scanner.nextLine().trim());
                result = tensor.multiply(scalar);
                System.out.println("\nРезультат множення:");
                io.printToConsole(result);
                break;

            case 3:
                result = tensor.transpose();
                System.out.println("\nРезультат транспонування:");
                io.printToConsole(result);
                break;

            case 4:
                System.out.println("\nСтатистика тензора:");
                System.out.println("Сума: " + tensor.sum());
                System.out.println("Мінімум: " + tensor.min());
                System.out.println("Максимум: " + tensor.max());
                System.out.printf("Середнє: %.2f\n", tensor.sum() / tensor.getTotalElements());
                break;

            default:
                System.out.println("Невірний вибір операції");
        }
    }

    private static Tensor<Double> createSampleTensor() throws TensorException {
        // Створення прикладу тензора 2x3x2
        Double[] data = {
                1.0, 2.0, 3.0, 4.0, 5.0, 6.0,
                7.0, 8.0, 9.0, 10.0, 11.0, 12.0
        };
        int[] dimensions = {2, 3, 2};
        return new Tensor<>(data, dimensions, Double.class);
    }
}