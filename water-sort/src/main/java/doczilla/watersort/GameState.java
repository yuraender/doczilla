package doczilla.watersort;

import doczilla.watersort.entity.Move;
import doczilla.watersort.entity.Tube;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class GameState {

    private final Tube[] tubes;
    @Setter
    private Move[] moves;

    public String getHash() {
        StringBuilder sb = new StringBuilder();
        for (Tube t : tubes) {
            int a = 0;
            for (Integer i : t) {
                sb.append(i);
                if (a < t.size() - 1) {
                    sb.append(",");
                }
                a++;
            }
            sb.append(";");
        }
        return sb.toString();
    }

    public boolean isSolved() {
        for (Tube tube : tubes) {
            if (tube.isEmpty()) {
                continue;
            }
            if (!tube.isCorrect()) {
                return false;
            }
            if (tube.size() != tube.getCapacity()) {
                return false;
            }
        }
        return true;
    }

    public GameState copy() {
        Tube[] newTubes = new Tube[tubes.length];
        for (int i = 0; i < tubes.length; i++) {
            newTubes[i] = tubes[i].copy();
        }
        Move[] newMoves = new Move[moves.length];
        System.arraycopy(moves, 0, newMoves, 0, moves.length);
        return new GameState(newTubes, newMoves);
    }
}
