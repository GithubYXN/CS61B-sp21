package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.*;

public class ArrayDequeTest {

    @Test
    public void test() {

        ArrayDeque<Integer> dq = new ArrayDeque<>();
        int N = 50000;

        for (int i = 0; i < N; i++) {
            int op = StdRandom.uniform(0, 5);
            int ranVal = StdRandom.uniform(0, 1000);
            if (op == 0) {
                dq.addFirst(ranVal);
            } else if (op == 1) {
                dq.addLast(ranVal);
            } else if (op == 2) {
                dq.removeFirst();
            } else if (op == 3) {
                dq.removeLast();
            } else if (op == 4) {
                int ranIdx = StdRandom.uniform(0, dq.getLength());
                dq.get(ranIdx);
            }
        }

    }

    @Test
    public void test2() {

        ArrayDeque<Integer> dq = new ArrayDeque<>();
        dq.addFirst(5);
        assertFalse(dq.isEmpty());

    }
}
