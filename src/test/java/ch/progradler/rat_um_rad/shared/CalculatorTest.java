package ch.progradler.rat_um_rad.shared;

import ch.progradler.rat_um_rad.shared.util.Calculator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorTest {
    @Test
    public void testAdd() {
        int a = 5;
        int b = 3;
        int result;
        result = Calculator.add(a, b);
        assertEquals(8, result);
    }
}
