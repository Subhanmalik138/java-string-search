package com.subhanmalik.stringsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Knuth-Morris-Pratt search, O(n + m) time and O(m) extra space.
 *
 * <p>The prefix function {@code pi[i]} is the length of the longest proper
 * prefix of {@code pattern[0..i]} that is also a suffix of it. When a
 * mismatch happens at text position {@code i} and pattern position
 * {@code j}, the algorithm falls back to {@code pi[j - 1]} instead of
 * restarting the pattern from scratch — the part of the pattern that
 * already matched is never re-scanned, which is what makes the scan
 * linear. Overlapping matches fall out naturally: after a full match
 * the fallback is {@code pi[m - 1]}, not 0.
 */
public final class KnuthMorrisPratt implements SearchAlgorithm {

    @Override
    public String name() {
        return "kmp";
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
        int[] pi = prefixFunction(pattern);
        int j = 0; // length of the current partial match
        for (int i = 0; i < n; i++) {
            while (j > 0 && text.charAt(i) != pattern.charAt(j)) {
                j = pi[j - 1];
            }
            if (text.charAt(i) == pattern.charAt(j)) {
                j++;
            }
            if (j == m) {
                matches.add(i - m + 1);
                j = pi[j - 1]; // keep scanning for overlapping matches
            }
        }
        return matches;
    }

    /** Classic prefix-function computation, O(m). */
    static int[] prefixFunction(String pattern) {
        int m = pattern.length();
        int[] pi = new int[m];
        int len = 0;
        for (int i = 1; i < m; i++) {
            while (len > 0 && pattern.charAt(i) != pattern.charAt(len)) {
                len = pi[len - 1];
            }
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
            }
            pi[i] = len;
        }
        return pi;
    }
}
