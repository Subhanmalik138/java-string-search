package com.subhanmalik.stringsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Rabin-Karp search with a polynomial rolling hash.
 *
 * <p>The hash of a window is updated in O(1) when the window slides:
 * subtract the outgoing character's contribution, multiply by the base,
 * add the incoming character. Only windows whose hash equals the
 * pattern's hash get a full character-by-character comparison, so hash
 * collisions can never produce a false positive — they only cost one
 * extra comparison. Expected time is O(n + m); the pathological case
 * (every window collides) degrades to O(n * m), which is why the modulus
 * is a large prime rather than a small one.
 *
 * <p>Arithmetic stays in {@code long}: with base 257 and modulus
 * 1,000,000,007, every intermediate value is below
 * {@code 257 * modulus}, far from overflow, so no modular reduction
 * tricks are needed to stay correct.
 */
public final class RabinKarp implements SearchAlgorithm {

    private static final long BASE = 257;
    private static final long MODULUS = 1_000_000_007L;

    @Override
    public String name() {
        return "rabin-karp";
    }

    @Override
    public List<Integer> findAll(String text, String pattern) {
        Objects.requireNonNull(text);
        Objects.requireNonNull(pattern);
        List<Integer> matches = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        if (m == 0 || m > n) {
            return matches;
        }
        long patternHash = hash(pattern, 0, m);
        long windowHash = hash(text, 0, m);
        // BASE^(m-1) mod MODULUS: the weight of the outgoing character.
        long highestPower = 1;
        for (int i = 1; i < m; i++) {
            highestPower = (highestPower * BASE) % MODULUS;
        }
        for (int i = 0; i <= n - m; i++) {
            if (windowHash == patternHash && text.regionMatches(i, pattern, 0, m)) {
                matches.add(i);
            }
            if (i < n - m) {
                long outgoing = (text.charAt(i) * highestPower) % MODULUS;
                windowHash = (windowHash - outgoing + MODULUS) % MODULUS;
                windowHash = (windowHash * BASE + text.charAt(i + m)) % MODULUS;
            }
        }
        return matches;
    }

    private static long hash(String s, int from, int to) {
        long h = 0;
        for (int i = from; i < to; i++) {
            h = (h * BASE + s.charAt(i)) % MODULUS;
        }
        return h;
    }
}
