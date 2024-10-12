package deque;

import org.junit.Test;

public class ArrayDequeTest {

    @Test
    public void test() {
        ArrayDeque<Integer> dq = new ArrayDeque<>();
        dq.addFirst(1);
        dq.addLast(2);
        dq.addLast(3);
        dq.addLast(4);
        dq.addLast(5);

        dq.removeFirst();
        System.out.println(dq.get(9));

        dq.printDeque();
    }
}
