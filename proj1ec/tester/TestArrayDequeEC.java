package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {

    @Test
    public void TestArrayDeque() {
        StudentArrayDeque<Integer> L = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> A = new ArrayDequeSolution<>();
        String log = "";
        int N = 50000;

        for (int i = 0; i < N; i++) {
            int op = StdRandom.uniform(0, 4);
            int ranVal = StdRandom.uniform(0, 1000);
            if (op == 0) {
                L.addFirst(ranVal);
                A.addFirst(ranVal);
                log = log + "addFirst(" + ranVal + ")\n";
            } else if (op == 1) {
                L.addLast(ranVal);
                A.addLast(ranVal);
                log = log + "addLast(" + ranVal + ")\n";
            } else if (op == 2) {
                if (!L.isEmpty() && !A.isEmpty()) {
                    Integer aVal = A.removeFirst();
                    Integer lVal = L.removeFirst();
                    log = log + "removeFirst()\n";
                    assertEquals(log, aVal, lVal);
                }
            } else if (op == 3) {
                if (!L.isEmpty() && !A.isEmpty()) {
                    Integer aVal = A.removeLast();
                    Integer lVal = L.removeLast();
                    log = log + "removeLast()\n";
                    assertEquals(log, aVal, lVal);
                }
            }
        }
    }
}
