package agivdel.sortingAlgorithms;

import java.util.Random;

public class Algorithms {
    public static Double[] array;
    private static final Random random = new Random();

    public Algorithms(long limit, double numberOrigin, double numberBound) {
        array = random.doubles(limit, numberOrigin, numberBound).boxed().toArray(Double[]::new);
    }

    public Double[] getArray() {
        return array;
    }

    /**
     * в дальнейшем при добавлении новых алгоритмов
     * соблюдать одинаковый порядок их расположения в этом классе и в comboBox в sample.fxml
     */
    public void select(int index, int i) {
        switch (index) {
            case 0:
                selectionSort(i);
                break;
            case 1:
                insertionSort(i);
                break;
        }
    }


    /**
     * Сортировка выбором
     */
    public void selectionSort(int i) {
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
    public void insertionSort(int i) {
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
