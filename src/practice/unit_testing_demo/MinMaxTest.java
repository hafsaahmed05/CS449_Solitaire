package practice.unit_testing_demo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class MinMaxTest {

    @Test
    public void testFindMin() {
        int[] numbers = {8, 3, 9, 1, 7};;
        assertEquals(1, MinMax.findMin(numbers));
    }

    @Test
    public void testFindMax() {
        int[] numbers = {8, 3, 9, 1, 7};;
        assertEquals(9, MinMax.findMax(numbers));
    }
}
