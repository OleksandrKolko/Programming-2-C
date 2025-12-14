import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Узагальнений клас Tensor, що представляє тензор довільної розмірності.
 * @param <T> тип елементів тензора (повинен бути підкласом Number)
 *
 * Тензор - це багатовимірний масив, який узагальнює поняття:
 * - Скаляр: тензор 0-го рангу (розмірність [])
 * - Вектор: тензор 1-го рангу (розмірність [n])
 * - Матриця: тензор 2-го рангу (розмірність [n, m])
 * - Тензор 3-го рангу: (розмірність [n, m, k]) і т.д.
 */
public class Tensor<T extends Number> {
    private T[] data;
    private int[] dimensions;
    private int[] strides;
    private Class<T> type;
    private int totalElements;

    /**
     * Основний конструктор тензора.
     * @param dimensions розмірності тензора
     * @param type клас типу елементів
     * @throws TensorException якщо розмірності некоректні
     */
    public Tensor(int[] dimensions, Class<T> type) throws TensorException {
        if (dimensions == null || dimensions.length == 0) {
            throw new TensorException("Dimensions cannot be null or empty");
        }

        for (int dim : dimensions) {
            if (dim <= 0) {
                throw new TensorException("All dimensions must be positive");
            }
        }

        this.dimensions = dimensions.clone();
        this.type = type;
        calculateStrides();
        calculateTotalElements();

        // Створення масиву потрібного типу
        this.data = createArray(totalElements);
    }

    /**
     * Конструктор для створення тензора з існуючими даними.
     * @param data дані тензора
     * @param dimensions розмірності тензора
     * @param type клас типу елементів
     * @throws TensorException якщо розмірності не співпадають з кількістю даних
     */
    public Tensor(T[] data, int[] dimensions, Class<T> type) throws TensorException {
        this(dimensions, type);

        int expectedElements = 1;
        for (int dim : dimensions) {
            expectedElements *= dim;
        }

        if (data.length != expectedElements) {
            throw new TensorException(
                    String.format("Data length (%d) doesn't match dimensions product (%d)",
                            data.length, expectedElements)
            );
        }

        this.data = data.clone();
    }

    /**
     * Обчислює загальну кількість елементів тензора.
     */
    private void calculateTotalElements() {
        totalElements = 1;
        for (int dim : dimensions) {
            totalElements *= dim;
        }
    }

    /**
     * Обчислює strides (кроки) для швидкого доступу до елементів.
     * Stride[i] показує, скільки елементів потрібно пропустити при зміні i-го індексу.
     */
    private void calculateStrides() {
        strides = new int[dimensions.length];
        strides[strides.length - 1] = 1;

        for (int i = dimensions.length - 2; i >= 0; i--) {
            strides[i] = strides[i + 1] * dimensions[i + 1];
        }
    }

    /**
     * Створює масив потрібного типу за допомогою рефлексії.
     */
    @SuppressWarnings("unchecked")
    private T[] createArray(int size) {
        return (T[]) java.lang.reflect.Array.newInstance(type, size);
    }

    /**
     * Перетворює багатовимірний індекс у лінійний індекс.
     * @param indices багатовимірні індекси
     * @return лінійний індекс у масиві data
     * @throws TensorException якщо індекси некоректні
     */
    private int getLinearIndex(int... indices) throws TensorException {
        if (indices.length != dimensions.length) {
            throw new TensorException(
                    String.format("Expected %d indices, got %d",
                            dimensions.length, indices.length)
            );
        }

        int index = 0;
        for (int i = 0; i < indices.length; i++) {
            if (indices[i] < 0 || indices[i] >= dimensions[i]) {
                throw new TensorException(
                        String.format("Index %d out of bounds for dimension %d (size %d)",
                                indices[i], i, dimensions[i])
                );
            }
            index += indices[i] * strides[i];
        }
        return index;
    }

    /**
     * Отримує елемент тензора за багатовимірними індексами.
     */
    public T get(int... indices) throws TensorException {
        return data[getLinearIndex(indices)];
    }

    /**
     * Встановлює елемент тензора за багатовимірними індексами.
     */
    public void set(T value, int... indices) throws TensorException {
        data[getLinearIndex(indices)] = value;
    }

    /**
     * Додає два тензори (елемент-до-елемента).
     */
    public Tensor<T> add(Tensor<T> other) throws TensorException {
        if (!Arrays.equals(this.dimensions, other.dimensions)) {
            throw new TensorException("Tensors must have the same dimensions for addition");
        }

        Tensor<T> result = new Tensor<>(dimensions, type);
        for (int i = 0; i < totalElements; i++) {
            if (type == Integer.class) {
                result.data[i] = type.cast(
                        ((Integer)this.data[i]) + ((Integer)other.data[i])
                );
            } else if (type == Double.class) {
                result.data[i] = type.cast(
                        ((Double)this.data[i]) + ((Double)other.data[i])
                );
            } else if (type == Float.class) {
                result.data[i] = type.cast(
                        ((Float)this.data[i]) + ((Float)other.data[i])
                );
            } else if (type == Long.class) {
                result.data[i] = type.cast(
                        ((Long)this.data[i]) + ((Long)other.data[i])
                );
            }
        }
        return result;
    }

    /**
     * Множить тензор на скаляр.
     */
    public Tensor<T> multiply(T scalar) throws TensorException {
        Tensor<T> result = new Tensor<>(dimensions, type);
        for (int i = 0; i < totalElements; i++) {
            if (type == Integer.class) {
                result.data[i] = type.cast(
                        ((Integer)this.data[i]) * ((Integer)scalar)
                );
            } else if (type == Double.class) {
                result.data[i] = type.cast(
                        ((Double)this.data[i]) * ((Double)scalar)
                );
            } else if (type == Float.class) {
                result.data[i] = type.cast(
                        ((Float)this.data[i]) * ((Float)scalar)
                );
            } else if (type == Long.class) {
                result.data[i] = type.cast(
                        ((Long)this.data[i]) * ((Long)scalar)
                );
            }
        }
        return result;
    }

    /**
     * Транспонування тензора (для 2D тензорів).
     */
    public Tensor<T> transpose() throws TensorException {
        if (dimensions.length != 2) {
            throw new TensorException("Transpose is only supported for 2D tensors (matrices)");
        }

        int[] newDimensions = {dimensions[1], dimensions[0]};
        Tensor<T> result = new Tensor<>(newDimensions, type);

        for (int i = 0; i < dimensions[0]; i++) {
            for (int j = 0; j < dimensions[1]; j++) {
                result.set(this.get(i, j), j, i);
            }
        }

        return result;
    }

    /**
     * Обчислює суму всіх елементів тензора.
     */
    public double sum() {
        double total = 0.0;
        for (T element : data) {
            total += element.doubleValue();
        }
        return total;
    }

    /**
     * Знаходить максимальний елемент тензора.
     */
    public T max() {
        T max = data[0];
        for (T element : data) {
            if (element.doubleValue() > max.doubleValue()) {
                max = element;
            }
        }
        return max;
    }


    public T min() {
        T min = data[0];
        for (T element : data) {
            if (element.doubleValue() < min.doubleValue()) {
                min = element;
            }
        }
        return min;
    }

    /**
     * Перетворює тензор в рядок для виводу.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tensor");
        sb.append(Arrays.toString(dimensions));
        sb.append(" {\n");
        toStringRecursive(sb, new int[dimensions.length], 0);
        sb.append("}");
        return sb.toString();
    }

    /**
     * Рекурсивний метод для форматованого виводу тензора.
     */
    private void toStringRecursive(StringBuilder sb, int[] indices, int depth) {
        if (depth == dimensions.length - 1) {
            sb.append("    ".repeat(depth));
            sb.append("[");
            for (int i = 0; i < dimensions[depth]; i++) {
                indices[depth] = i;
                try {
                    sb.append(get(indices));
                } catch (TensorException e) {
                    sb.append("ERROR");
                }
                if (i < dimensions[depth] - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            if (depth > 0) {
                sb.append(",");
            }
            sb.append("\n");
        } else {
            sb.append("    ".repeat(depth));
            sb.append("[\n");
            for (int i = 0; i < dimensions[depth]; i++) {
                indices[depth] = i;
                toStringRecursive(sb, indices, depth + 1);
            }
            sb.append("    ".repeat(depth));
            sb.append("]");
            if (depth > 0) {
                sb.append(",");
            }
            sb.append("\n");
        }
    }

    public int[] getDimensions() {
        return dimensions.clone();
    }

    public int getTotalElements() {
        return totalElements;
    }

    public Class<T> getType() {
        return type;
    }

    public T[] getData() {
        return data.clone();
    }
}