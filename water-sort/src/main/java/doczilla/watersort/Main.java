package doczilla.watersort;

import doczilla.watersort.entity.Move;
import doczilla.watersort.entity.Tube;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

    private static Move[] solve(int[][] initial, int capacity) {
        Tube[] tubes = new Tube[initial.length];
        for (int i = 0; i < initial.length; i++) {
            Tube tube = new Tube(capacity);
            for (int j = 0; j < initial[i].length; j++) {
                tube.pourIn(initial[i][j], 1);
            }
            tubes[i] = tube;
        }

        GameState initialState = new GameState(tubes, new Move[0]);
        if (initialState.isSolved()) {
            return new Move[0];
        }

        List<GameState> queue = new ArrayList<>();
        queue.add(initialState);

        Set<String> visited = new HashSet<>();
        visited.add(initialState.getHash());

        int iterations = 0;
        int maxIterations = 10000;
        while (!queue.isEmpty() && iterations < maxIterations) {
            iterations++;

            for (int i = 0; i < queue.size() - 1; i++) {
                for (int j = i + 1; j < queue.size(); j++) {
                    GameState gs1 = queue.get(j);
                    GameState gs2 = queue.get(i);
                    if (evaluateState(gs1.getTubes()) > evaluateState(gs2.getTubes())) {
                        queue.set(i, gs1);
                        queue.set(j, gs2);
                    }
                }
            }

            GameState currentState = queue.get(0);
            queue.remove(0);

            if (currentState.isSolved()) {
                return currentState.getMoves();
            }

            GameState[] nextMoves = generateMoves(currentState);
            for (GameState next : nextMoves) {
                String hash = next.getHash();

                boolean seen = false;
                for (String v : visited) {
                    if (v.equals(hash)) {
                        seen = true;
                        break;
                    }
                }
                if (seen) {
                    continue;
                }

                visited.add(hash);
                queue.add(next);
            }
        }

        return null;
    }

    private static GameState[] generateMoves(GameState state) {
        GameState[] temp = new GameState[5000];
        int k = 0;

        for (int from = 0; from < state.getTubes().length; from++) {
            Tube fromTube = state.getTubes()[from];
            if (fromTube.isEmpty()) {
                continue;
            }
            for (int to = 0; to < state.getTubes().length; to++) {
                if (from == to) {
                    continue;
                }
                Tube toTube = state.getTubes()[to];
                int maxCount = Math.min(fromTube.topCount(), toTube.freeCount());
                for (int count = 1; count <= maxCount; count++) {
                    if (canPour(fromTube, toTube, count)) {
                        GameState newState = state.copy();
                        pour(newState, from, to, count);
                        temp[k++] = newState;
                    }
                }
            }
        }

        GameState[] nextStates = new GameState[k];
        System.arraycopy(temp, 0, nextStates, 0, k);
        return nextStates;
    }

    private static boolean canPour(Tube from, Tube to, int count) {
        if (from.isEmpty() || count <= 0 || to.freeCount() < count) {
            return false;
        }
        int colorA = from.topColor();
        int colorB = to.topColor();
        return colorB == -1 || colorA == colorB;
    }

    private static void pour(GameState state, int from, int to, int count) {
        Tube fromTube = state.getTubes()[from];
        Tube toTube = state.getTubes()[to];

        int color = fromTube.topColor();
        fromTube.pourOut(count);
        toTube.pourIn(color, count);

        Move[] newMoves = new Move[state.getMoves().length + 1];
        System.arraycopy(state.getMoves(), 0, newMoves, 0, state.getMoves().length);
        newMoves[state.getMoves().length] = new Move(from, to);
        state.setMoves(newMoves);
    }

    private static int evaluateState(Tube[] tubes) {
        int score = 0;
        for (Tube tube : tubes) {
            if (tube.isEmpty()) {
                score += 10;
                continue;
            }
            if (tube.isCorrect()) {
                score += tube.size() * 5;
                if (tube.size() == tube.getCapacity()) {
                    score += 20;
                }
            } else {
                Set<Integer> unique = new HashSet<>();
                for (Integer i : tube) {
                    unique.add(i);
                }
                score -= unique.size() * 2;
            }
        }
        return score;
    }

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Enter N and V:");
        String[] parts = br.readLine().trim().split(" ");
        int N = Integer.parseInt(parts[0]);
        int V = Integer.parseInt(parts[1]);

        int[][] initial = new int[N][];
        for (int i = 0; i < N; i++) {
            System.out.println("Enter tube " + i + ":");
            String line = br.readLine().trim();
            if (line.isEmpty()) {
                initial[i] = new int[0];
                continue;
            }
            String[] nums = line.split(" ");
            int length = Math.min(nums.length, V);
            int[] arr = new int[length];
            for (int j = 0; j < length; j++) {
                arr[j] = Integer.parseInt(nums[j]);
            }
            initial[i] = arr;
        }

        Move[] solution = solve(initial, V);
        if (solution != null) {
            for (Move move : solution) {
                System.out.print(move);
                System.out.print(" ");
            }
        }
    }
}
