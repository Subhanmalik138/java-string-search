package com.subhanmalik.stringsearch;

import java.util.List;

/**
 * A single-pattern string search algorithm.
 *
 * <p>All implementations share the same contract so they can be swapped
 * without touching callers, and so the CLI can cross-check them against
 * each other:
 *
 * <ul>
 *   <li>{@code findAll} returns the start index of every occurrence of
 *       {@code pattern} in {@code text}, in ascending order, including
 *       overlapping occurrences (e.g. "aaa" occurs at 0, 1 and 2 in
 *       "aaaaa").</li>
 *   <li>An empty pattern, an empty text, or a pattern longer than the
 *       text matches nothing and returns an empty list. An empty pattern
 *       matching at every position is a defensible convention, but it is
 *       useless for a search tool and surprising in a CLI, so this API
 *       deliberately returns no matches for it.</li>
 *   <li>Matching is exact and case-sensitive. Case-insensitive search is
 *       the caller's job (fold both strings first); baking a locale into
 *       the algorithm would make its complexity and behaviour harder to
 *       reason about.</li>
 * </ul>
 */
public interface SearchAlgorithm {

    /** Human-readable name, used by the CLI (e.g. "kmp"). */
    String name();

    /**
     * Returns every start index of {@code pattern} in {@code text}.
     *
     * @throws NullPointerException if either argument is null
     */
    List<Integer> findAll(String text, String pattern);
}
