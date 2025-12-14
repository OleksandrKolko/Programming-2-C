import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Клас для введення/виведення тензорів з різних джерел.
 * Підтримує консоль, JSON файли та XML файли.
 */
class TensorData<T extends Number> {
    private List<T> data;
    private List<Integer> dimensions;
    private String type;

    // Конструктори, гетери та сетери
    public TensorData() {}

    public TensorData(Tensor<T> tensor) {
        this.data = new ArrayList<>();
        for (T element : tensor.getData()) {
            data.add(element);
        }

        this.dimensions = new ArrayList<>();
        for (int dim : tensor.getDimensions()) {
            dimensions.add(dim);
        }

        this.type = tensor.getType().getSimpleName();
    }

    public Tensor<T> toTensor() throws TensorException {
        int[] dimsArray = new int[dimensions.size()];
        for (int i = 0; i < dimensions.size(); i++) {
            dimsArray[i] = dimensions.get(i);
        }

        @SuppressWarnings("unchecked")
        Class<T> tensorType = (Class<T>) getTypeClass(type);

        // Безпечне створення масиву
        T[] dataArray = createTypedArray(tensorType, data.size());
        for (int i = 0; i < data.size(); i++) {
            dataArray[i] = data.get(i);
        }

        return new Tensor<>(dataArray, dimsArray, tensorType);
    }

    @SuppressWarnings("unchecked")
    private T[] createTypedArray(Class<T> type, int size) {
        return (T[]) java.lang.reflect.Array.newInstance(type, size);
    }

    private Class<?> getTypeClass(String typeName) {
        switch (typeName) {
            case "Integer": return Integer.class;
            case "Double": return Double.class;
            case "Float": return Float.class;
            case "Long": return Long.class;
            default: return Double.class;
        }
    }

    // Гетери та сетери
    public List<T> getData() { return data; }
    public void setData(List<T> data) { this.data = data; }
    public List<Integer> getDimensions() { return dimensions; }
    public void setDimensions(List<Integer> dimensions) { this.dimensions = dimensions; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}

/**
 * Головний клас для операцій введення/виведення.
 */
public class TensorIO<T extends Number> {
    private ObjectMapper jsonMapper;
    private XmlMapper xmlMapper;
    private Scanner scanner;

    public TensorIO() {
        this.jsonMapper = new ObjectMapper();
        this.xmlMapper = new XmlMapper();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Читає тензор з консолі.
     */
    public Tensor<T> readFromConsole(Class<T> type) throws TensorException {
        System.out.println("=== Введення тензора з консолі ===");

        System.out.print("Введіть кількість розмірностей: ");
        int rank = Integer.parseInt(scanner.nextLine().trim());

        int[] dimensions = new int[rank];
        System.out.println("Введіть розмірності тензора:");
        for (int i = 0; i < rank; i++) {
            System.out.printf("Розмірність %d: ", i + 1);
            dimensions[i] = Integer.parseInt(scanner.nextLine().trim());
        }

        Tensor<T> tensor = new Tensor<>(dimensions, type);

        System.out.println("Введіть елементи тензора:");
        readTensorRecursive(tensor, new int[rank], 0, type);

        return tensor;
    }

    /**
     * Рекурсивне введення даних тензора.
     */
    private void readTensorRecursive(Tensor<T> tensor, int[] indices, int depth, Class<T> type) throws TensorException {
        if (depth == tensor.getDimensions().length - 1) {
            for (int i = 0; i < tensor.getDimensions()[depth]; i++) {
                indices[depth] = i;
                System.out.printf("Елемент %s: ", Arrays.toString(indices));
                tensor.set(readNumber(scanner, type), indices);
            }
        } else {
            for (int i = 0; i < tensor.getDimensions()[depth]; i++) {
                indices[depth] = i;
                readTensorRecursive(tensor, indices, depth + 1, type);
            }
        }
    }

    /**
     * Читає число потрібного типу зі сканера.
     */
    @SuppressWarnings("unchecked")
    private T readNumber(Scanner scanner, Class<T> type) {
        String input = scanner.nextLine().trim();
        if (type == Integer.class) {
            return (T) Integer.valueOf(Integer.parseInt(input));
        } else if (type == Double.class) {
            return (T) Double.valueOf(Double.parseDouble(input));
        } else if (type == Float.class) {
            return (T) Float.valueOf(Float.parseFloat(input));
        } else if (type == Long.class) {
            return (T) Long.valueOf(Long.parseLong(input));
        }
        return (T) Double.valueOf(Double.parseDouble(input));
    }

    /**
     * Зчитує тензор з JSON файлу.
     */
    public Tensor<T> readFromJson(String filename, Class<T> type) throws TensorException {
        try {
            TensorData<T> tensorData = jsonMapper.readValue(
                    new File(filename),
                    jsonMapper.getTypeFactory().constructParametricType(TensorData.class, type)
            );
            return tensorData.toTensor();
        } catch (IOException e) {
            throw new TensorException("Помилка читання JSON файлу: " + e.getMessage(), e);
        }
    }

    /**
     * Зчитує тензор з XML файлу.
     */
    public Tensor<T> readFromXml(String filename, Class<T> type) throws TensorException {
        try {
            TensorData<T> tensorData = xmlMapper.readValue(
                    new File(filename),
                    xmlMapper.getTypeFactory().constructParametricType(TensorData.class, type)
            );
            return tensorData.toTensor();
        } catch (IOException e) {
            throw new TensorException("Помилка читання XML файлу: " + e.getMessage(), e);
        }
    }

    /**
     * Записує тензор у JSON файл.
     */
    public void writeToJson(Tensor<T> tensor, String filename) throws TensorException {
        try {
            TensorData<T> tensorData = new TensorData<>(tensor);
            jsonMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename), tensorData);
            System.out.println("Тензор успішно записано у JSON файл: " + filename);
        } catch (IOException e) {
            throw new TensorException("Помилка запису JSON файлу: " + e.getMessage(), e);
        }
    }

    /**
     * Записує тензор у XML файл.
     */
    public void writeToXml(Tensor<T> tensor, String filename) throws TensorException {
        try {
            TensorData<T> tensorData = new TensorData<>(tensor);
            xmlMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename), tensorData);
            System.out.println("Тензор успішно записано у XML файл: " + filename);
        } catch (IOException e) {
            throw new TensorException("Помилка запису XML файлу: " + e.getMessage(), e);
        }
    }

    /**
     * Виводить тензор у консоль у зрозумілому форматі.
     */
    public void printToConsole(Tensor<T> tensor) {
        System.out.println("\n=== Поточний тензор ===");
        System.out.println("Тип: " + tensor.getType().getSimpleName());
        System.out.println("Розмірності: " + Arrays.toString(tensor.getDimensions()));
        System.out.println("Загальна кількість елементів: " + tensor.getTotalElements());
        System.out.println("Мінімальний елемент: " + tensor.min());
        System.out.println("Максимальний елемент: " + tensor.max());
        System.out.println("Сума всіх елементів: " + tensor.sum());
        System.out.println("\nДані тензора:");
        System.out.println(tensor);
    }
}