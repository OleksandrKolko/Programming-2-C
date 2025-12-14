/**
 * Клас власного винятку для обробки помилок роботи з тензорами.
 * Наслідує стандартний Exception, додає можливість передавати повідомлення.
 */
public class TensorException extends Exception {
    public TensorException(String message) {
        super(message);
    }

    public TensorException(String message, Throwable cause) {
        super(message, cause);
    }
}