package com.subhanmalik.stringsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Boyer-Moore-Horspool search: the simplified Boyer-Moore variant that
 * keeps only the bad-character rule, applied to the last character of
 * the current window.
 *
 * <p>On each alignment the window's last character decides the shift:
 * if it occurs in the pattern (outside the last position), the window
 * slides so that occurrence lines up; otherwise the whole window is
 * skipped. In practice this is sublinear on natural text — most windows
 * are rejected after a single character comparison — which is why it
 * beats KMP on long, low-match-rate inputs despite a worst case of
 * O(n * m) (e.g. searching "aaa...ab" in "aaa...a").
 *
 * <p>The shift table is a map rather than a 65,536-entry array: it stays
 * small for short patterns and works for the full {@code char} range
 * without paying for an alphabet that never appears.
 */
public final class BoyerMooreHorspool implements SearchAlgorithm {

    @Override
    public String name() {
        return "bmh";
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
        Map<Character, Integer> shift = shiftTable(pattern);
        int i = 0;
        while (i <= n - m) {
            int j = m - 1;
            while (j >= 0 && pattern.charAt(j) == text.charAt(i + j)) {
                j--;
            }
            if (j < 0) {
                matches.add(i);
                // Advance by 1, not by a full shift, so overlapping
                // matches (e.g. "aa" in "aaa") are not skipped.
                i++;
            } else {
                int skip = shift.getOrDefault(text.charAt(i + m - 1), m);
                i += Math.max(skip, 1);
            }
        }
        return matches;
    }

    /**
     * For each character, how far the window may shift when that
     * character is the window's last character. Only pattern positions
     * before the last one are indexed — the last character's own entry
     * would always shift by 0, which is not a shift at all.
     */
    static Map<Character, Integer> shiftTable(String pattern) {
        Map<Character, Integer> table = new HashMap<>();
        int m = pattern.length();
        for (int i = 0; i < m - 1; i++) {
            table.put(pattern.charAt(i), m - 1 - i);
        }
        return table;
    }
}
