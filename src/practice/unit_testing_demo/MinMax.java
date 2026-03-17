package practice.unit_testing_demo;

public class MinMax {

    // Returns the minimum value in an array
    public static int findMin(int[] numbers) {
        if (numbers == null || numbers.length == 0) {
            throw new IllegalArgumentException("Array must not be empty");
        }
        int min = numbers[0];
        for (int n : numbers) {
            if (n < min) min = n;
        }
        return min;
    }

    // Returns the maximum value in an array
    public static int findMax(int[] numbers) {
        if (numbers == null || numbers.length == 0) {
            throw new IllegalArgumentException("Array must not be empty");
        }
        int max = numbers[0];
        for (int n : numbers) {
            if (n > max) max = n;
        }
        return max;
    }
}

