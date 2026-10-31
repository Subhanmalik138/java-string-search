package com.subhanmalik.stringsearch;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/**
 * CLI: search a file (or stdin) for a pattern with one algorithm, or
 * with all three at once as a cross-check.
 *
 * <pre>
 *   java -jar string-search.jar [--ignore-case] &lt;kmp|bmh|rabin-karp|all&gt; &lt;pattern&gt; [file]
 * </pre>
 *
 * <p>With no file argument the text is read from stdin, so the tool
 * composes with pipes ({@code cat log.txt | java -jar ... kmp ERROR}).
 * Output is one line per run: {@code kmp: 3 match(es) at 0, 5, 12} or
 * {@code kmp: no matches}. Exit code 0 means the search ran (even with
 * no matches); 1 means a usage or I/O error.
 */
public final class Main {

    private static final List<SearchAlgorithm> ALL = List.of(
            new KnuthMorrisPratt(),
            new BoyerMooreHorspool(),
            new RabinKarp());

    private Main() {
    }

    public static void main(String[] args) {
        int pos = 0;
        boolean ignoreCase = false;
        while (pos < args.length && args[pos].startsWith("--")) {
            if (args[pos].equals("--ignore-case")) {
                ignoreCase = true;
            } else {
                die("unknown option: " + args[pos]);
            }
            pos++;
        }
        if (args.length - pos < 2 || args.length - pos > 3) {
            die("usage: [--ignore-case] <kmp|bmh|rabin-karp|all> <pattern> [file]");
        }
        String algorithmName = args[pos];
        String pattern = args[pos + 1];
        String file = args.length - pos > 2 ? args[pos + 2] : null;

        String text;
        try {
            text = file == null
                    ? new String(System.in.readAllBytes(), StandardCharsets.UTF_8)
                    : Files.readString(Path.of(file), StandardCharsets.UTF_8);
        } catch (IOException e) {
            die("cannot read input: " + e.getMessage());
            return;
        }

        if (ignoreCase) {
            text = text.toLowerCase(Locale.ROOT);
            pattern = pattern.toLowerCase(Locale.ROOT);
        }

        List<SearchAlgorithm> algorithms;
        if (algorithmName.equals("all")) {
            algorithms = ALL;
        } else {
            algorithms = ALL.stream()
                    .filter(a -> a.name().equals(algorithmName))
                    .toList();
            if (algorithms.isEmpty()) {
                die("unknown algorithm: " + algorithmName + " (expected kmp, bmh, rabin-karp or all)");
            }
        }

        List<Integer> reference = null;
        for (SearchAlgorithm algorithm : algorithms) {
            List<Integer> matches = algorithm.findAll(text, pattern);
            System.out.println(algorithm.name() + ": " + describe(matches));
            if (reference == null) {
                reference = matches;
            } else if (!reference.equals(matches)) {
                System.err.println("WARNING: " + algorithm.name()
                        + " disagrees with " + algorithms.get(0).name()
                        + " — this is a bug, please report it");
            }
        }
    }

    private static String describe(List<Integer> matches) {
        if (matches.isEmpty()) {
            return "no matches";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(matches.size()).append(matches.size() == 1 ? " match at " : " matches at ");
        for (int i = 0; i < matches.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(matches.get(i));
        }
        return sb.toString();
    }

    private static void die(String message) {
        System.err.println(message);
        System.exit(1);
    }
}
