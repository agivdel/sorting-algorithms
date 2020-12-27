package agivdel.sortingAlgorithms;

import java.util.Random;

public final class Algorithms {

    private static final Random random = new Random();

    private Algorithms() {
    }

    public static Double[] prepareArray(long limit, double numberOrigin, double numberBound) {
        return random.doubles(limit, numberOrigin, numberBound).boxed().toArray(Double[]::new);
    }

    /**
     * в дальнейшем при добавлении новых алгоритмов
     * соблюдать одинаковый порядок их расположения в этом классе и в comboBox в sample.fxml
     */
    public static void select(Double[] array, int index, int i) {
        switch (index) {
            case 0:
                selectionSort(array, i);
                break;
            case 1:
                insertionSort(array, i);
                break;
                default:

        }
    }


    /**
     * Сортировка выбором
     */
    public static void selectionSort(Double[] array, int i) {
            double min = array[i];
            int minId = i;
            for (int j = i + 1; j < array.length; j++) {
                if (array[j] < min) {
                    min = array[j];
                    minId = j;
                }
            }
            double temp = array[i];
            array[i] = min;
            array[minId] = temp;
    }


    /**
     * Сортировка вставками
     */
    public static void insertionSort(Double[] array, int i) {
            double current = array[i];
            int j = i - 1;
            for (; j >= 0; j--) {
                if (current < array[j]) {
                    array[j + 1] = array[j];
                } else {
                    break;
                }
            }
            array[j + 1] = current;
    }
}
