package ralph;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class RalphTest {

    @Test
    void run_emptyInput_doesNotThrowNoSuchElement() {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        try {
            // Empty input simulates EOF (e.g., user pressed Ctrl-D/closed stdin)
            System.setIn(new ByteArrayInputStream(new byte[0]));
            // Capture output to keep test output clean
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));

            Ralph app = new Ralph("data/duke_test.txt");
            assertDoesNotThrow(() -> app.run());
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}
