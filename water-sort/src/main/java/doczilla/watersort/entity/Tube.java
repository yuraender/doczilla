package doczilla.watersort.entity;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

@Getter
@RequiredArgsConstructor
public class Tube implements Iterable<Integer> {

    private final Deque<Integer> tube = new ArrayDeque<>();
    private final int capacity;

    public void pourIn(int color, int count) {
        for (int i = 0; i < count; i++) {
            tube.addLast(color);
        }
    }

    public void pourOut(int count) {
        for (int i = 0; i < count; i++) {
            tube.pollLast();
        }
    }

    public int topColor() {
        return !tube.isEmpty() ? tube.peekLast() : -1;
    }

    public int topCount() {
        if (tube.isEmpty()) {
            return 0;
        }
        int color = tube.peekLast();
        int count = 0;
        Iterator<Integer> it = tube.descendingIterator();
        while (it.hasNext()) {
            if (it.next() != color) {
                break;
            }
            count++;
        }
        return count;
    }

    public int freeCount() {
        return capacity - tube.size();
    }

    public boolean isCorrect() {
        if (tube.isEmpty()) {
            return false;
        }
        for (int c : tube) {
            if (c != tube.peekFirst()) {
                return false;
            }
        }
        return true;
    }

    public int size() {
        return tube.size();
    }

    public boolean isEmpty() {
        return tube.isEmpty();
    }

    public Tube copy() {
        Tube copy = new Tube(capacity);
        copy.tube.addAll(this.tube);
        return copy;
    }

    @NonNull
    @Override
    public Iterator<Integer> iterator() {
        return tube.iterator();
    }
}
