package com.subhanmalik.stringsearch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class StringSearchTest {

    static Stream<SearchAlgorithm> algorithms() {
        return Stream.of(new KnuthMorrisPratt(), new BoyerMooreHorspool(), new RabinKarp());
    }

    @ParameterizedTest
    @MethodSource("algorithms")
    void findsSingleOccurrence(SearchAlgorithm algorithm) {
        assertEquals(List.of(6), algorithm.findAll("hello world", "world"));
    }

    @ParameterizedTest
    @MethodSource("algorithms")
    void findsOverlappingOccurrences(SearchAlgorithm algorithm) {
        assertEquals(List.of(0, 1, 2), algorithm.findAll("aaaaa", "aaa"));
        assertEquals(List.of(0, 1), algorithm.findAll("aaa", "aa"));
    }

    @ParameterizedTest
    @MethodSource("algorithms")
    void findsMatchesAtBothEnds(SearchAlgorithm algorithm) {
        assertEquals(List.of(0, 2), algorithm.findAll("ababa", "aba"));
    }

    @ParameterizedTest
    @MethodSource("algorithms")
    void returnsEmptyWhenAbsent(SearchAlgorithm algorithm) {
        assertTrue(algorithm.findAll("hello world", "xyz").isEmpty());
    }

    @ParameterizedTest
    @MethodSource("algorithms")
    void returnsEmptyForDegenerateInputs(SearchAlgorithm algorithm) {
        assertTrue(algorithm.findAll("", "a").isEmpty());
        assertTrue(algorithm.findAll("abc", "").isEmpty());
        assertTrue(algorithm.findAll("ab", "abc").isEmpty());
    }

    @ParameterizedTest
    @MethodSource("algorithms")
    void matchesWholeText(SearchAlgorithm algorithm) {
        assertEquals(List.of(0), algorithm.findAll("exact", "exact"));
    }

    @ParameterizedTest
    @MethodSource("algorithms")
    void isCaseSensitive(SearchAlgorithm algorithm) {
        assertTrue(algorithm.findAll("Hello", "hello").isEmpty());
        assertEquals(List.of(0), algorithm.findAll("Hello", "Hello"));
    }

    @ParameterizedTest
    @MethodSource("algorithms")
    void rejectsNullInputs(SearchAlgorithm algorithm) {
        assertThrows(NullPointerException.class, () -> algorithm.findAll(null, "a"));
        assertThrows(NullPointerException.class, () -> algorithm.findAll("abc", null));
    }

    @Test
    void allAlgorithmsAgreeOnAFixedCorpus() {
        // The three algorithms must never disagree: any divergence is a
        // bug in at least one of them, and this corpus covers the shapes
        // that usually expose one (repetition, overlap, near-misses).
        String text = "ababa babab aabaaab abababab mississippi banana aaaaa";
        List<String> patterns = List.of(
                "aba", "bab", "aa", "aaa", "ababa", "issi", "ana", "a", "z", "ababa bab");
        List<SearchAlgorithm> algorithms = algorithms().toList();
        for (String pattern : patterns) {
            List<Integer> expected = algorithms.get(0).findAll(text, pattern);
            for (SearchAlgorithm algorithm : algorithms) {
                assertEquals(expected, algorithm.findAll(text, pattern),
                        algorithm.name() + " disagrees on pattern '" + pattern + "'");
            }
        }
    }

    @Test
    void kmpPrefixFunctionMatchesKnownValues() {
        assertEquals(List.of(0, 1, 0, 1, 2, 2, 0),
                toList(KnuthMorrisPratt.prefixFunction("aabaaac")));
        assertEquals(List.of(0, 1, 2, 3, 4, 5),
                toList(KnuthMorrisPratt.prefixFunction("aaaaaa")));
    }

    private static List<Integer> toList(int[] array) {
        return java.util.Arrays.stream(array).boxed().toList();
    }
}
