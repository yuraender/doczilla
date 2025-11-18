package doczilla.watersort.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Move {

    private final int from, to;

    @Override
    public String toString() {
        return "( " + from + ", " + to + " )";
    }
}
