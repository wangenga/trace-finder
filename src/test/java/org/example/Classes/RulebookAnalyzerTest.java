package org.example.Classes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RulebookAnalyzerTest {

    private RulebookAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new RulebookAnalyzer();
    }

    /** Helper: builds a well-formed log line "date|severity|ip|action|user". */
    private String log(String severity, String ip) {
        return "2024-01-01 10:00:00|" + severity + "|" + ip + "|action|user";
    }

    @Test
    void checkLevel_classifiesWarnErrorAlertAndInfo() {
        List<String> valid = List.of(
                log("WARN",  "1.1.1.1"),
                log("INFO",  "2.2.2.2"),
                log("ERROR", "3.3.3.3"),
                log("ALERT", "4.4.4.4")
        );
        List<String> numbered = List.of("1", "2", "3", "4");

        analyzer.checkLevel(valid, numbered);

        // Only WARN, ERROR, ALERT go to flaggedLogs
        assertEquals(3, analyzer.getFlaggedEntries().size());
        assertTrue(analyzer.getFlaggedEntries().contains(log("WARN",  "1.1.1.1")));
        assertTrue(analyzer.getFlaggedEntries().contains(log("ERROR", "3.3.3.3")));
        assertTrue(analyzer.getFlaggedEntries().contains(log("ALERT", "4.4.4.4")));
        assertTrue(analyzer.getUnknownLogs().isEmpty());

        Map<String, Integer> stats = analyzer.getStats();
        assertEquals(1, stats.get("WARN"));
        assertEquals(1, stats.get("INFO"));
        assertEquals(1, stats.get("ERROR"));
        assertEquals(1, stats.get("ALERT"));
    }

    @Test
    void checkLevel_infoLogsAreNotFlagged() {
        List<String> valid = List.of(
                log("INFO", "1.1.1.1"),
                log("INFO", "2.2.2.2")
        );
        List<String> numbered = List.of("1", "2");

        analyzer.checkLevel(valid, numbered);

        assertTrue(analyzer.getFlaggedEntries().isEmpty());
        assertEquals(2, analyzer.getStats().get("INFO"));
    }

    @Test
    void checkLevel_unknownSeverityGoesToUnknownLogs() {
        List<String> valid = List.of(log("MYSTERY", "1.1.1.1"));
        List<String> numbered = List.of("Line #42: MYSTERY entry");

        analyzer.checkLevel(valid, numbered);

        assertTrue(analyzer.getFlaggedEntries().isEmpty());
        assertEquals(1, analyzer.getUnknownLogs().size());
        // The numbered version must be stored, NOT the original
        assertEquals("Line #42: MYSTERY entry", analyzer.getUnknownLogs().get(0));
        // Nothing was counted
        Map<String, Integer> stats = analyzer.getStats();
        assertEquals(0, stats.get("WARN"));
        assertEquals(0, stats.get("ERROR"));
        assertEquals(0, stats.get("ALERT"));
        assertEquals(0, stats.get("INFO"));
    }

    @Test
    void checkLevel_severityIsTrimmedBeforeMatching() {
        List<String> valid = List.of("a| WARN |1.1.1.1|x|y");
        List<String> numbered = List.of("1");

        analyzer.checkLevel(valid, numbered);

        assertEquals(1, analyzer.getFlaggedEntries().size());
        assertEquals(1, analyzer.getStats().get("WARN"));
    }

    @Test
    void checkLevel_emptyListProducesZeroCounts() {
        analyzer.checkLevel(Collections.emptyList(), Collections.emptyList());

        Map<String, Integer> stats = analyzer.getStats();
        assertEquals(0, stats.get("WARN"));
        assertEquals(0, stats.get("INFO"));
        assertEquals(0, stats.get("ERROR"));
        assertEquals(0, stats.get("ALERT"));
        assertTrue(analyzer.getFlaggedEntries().isEmpty());
        assertTrue(analyzer.getUnknownLogs().isEmpty());
    }

    @Test
    void checkLevel_multipleSameSeverityAccumulate() {
        List<String> valid = List.of(
                log("WARN", "1.1.1.1"),
                log("WARN", "2.2.2.2"),
                log("WARN", "3.3.3.3")
        );
        List<String> numbered = List.of("1", "2", "3");

        analyzer.checkLevel(valid, numbered);

        assertEquals(3, analyzer.getStats().get("WARN"));
        assertEquals(3, analyzer.getFlaggedEntries().size());
    }

    @Test
    void checkLevel_flaggedLogsKeepInsertionOrder() {
        List<String> valid = List.of(
                log("WARN",  "1.1.1.1"),
                log("ERROR", "2.2.2.2"),
                log("ALERT", "3.3.3.3")
        );
        List<String> numbered = List.of("1", "2", "3");

        analyzer.checkLevel(valid, numbered);

        List<String> flagged = analyzer.getFlaggedEntries();
        assertEquals(log("WARN",  "1.1.1.1"), flagged.get(0));
        assertEquals(log("ERROR", "2.2.2.2"), flagged.get(1));
        assertEquals(log("ALERT", "3.3.3.3"), flagged.get(2));
    }

    @Test
    void suspiciousIPs_countsDuplicateIps() {
        List<String> valid = List.of(
                log("WARN",  "1.1.1.1"),
                log("ERROR", "2.2.2.2"),
                log("WARN",  "1.1.1.1"),
                log("ALERT", "1.1.1.1")
        );
        List<String> numbered = List.of("1", "2", "3", "4");

        analyzer.checkLevel(valid, numbered);
        analyzer.suspiciousIPs();

        Map<String, Integer> counts = analyzer.getSuspiciousIp();
        assertEquals(3, counts.get("1.1.1.1"));
        assertEquals(1, counts.get("2.2.2.2"));
    }

    @Test
    void suspiciousIPs_ignoresInfoLogs() {
        List<String> valid = List.of(
                log("INFO", "9.9.9.9"),
                log("WARN", "1.1.1.1")
        );
        List<String> numbered = List.of("1", "2");

        analyzer.checkLevel(valid, numbered);
        analyzer.suspiciousIPs();

        Map<String, Integer> counts = analyzer.getSuspiciousIp();
        assertFalse(counts.containsKey("9.9.9.9"));
        assertTrue(counts.containsKey("1.1.1.1"));
    }

    @Test
    void suspiciousIPs_emptyWhenNoFlaggedLogs() {
        analyzer.checkLevel(List.of(log("INFO", "1.1.1.1")), List.of("1"));
        analyzer.suspiciousIPs();

        assertTrue(analyzer.getSuspiciousIp().isEmpty());
    }

    @Test
    void getSuspiciousIp_returnsDescendingOrderByCount() {
        List<String> valid = List.of(
                log("WARN", "1.1.1.1"),
                log("WARN", "2.2.2.2"),
                log("WARN", "2.2.2.2"),
                log("WARN", "2.2.2.2"),
                log("WARN", "3.3.3.3"),
                log("WARN", "3.3.3.3")
        );
        List<String> numbered = List.of("1", "2", "3", "4", "5", "6");

        analyzer.checkLevel(valid, numbered);
        analyzer.suspiciousIPs();

        List<String> orderedIps = new ArrayList<>(analyzer.getSuspiciousIp().keySet());
        // 2.2.2.2 -> 3, 3.3.3.3 -> 2, 1.1.1.1 -> 1
        assertEquals(List.of("2.2.2.2", "3.3.3.3", "1.1.1.1"), orderedIps);
    }

    @Test
    void getStats_containsAllFourCategoriesEvenWhenEmpty() {
        Map<String, Integer> stats = analyzer.getStats();

        assertTrue(stats.containsKey("INFO"));
        assertTrue(stats.containsKey("WARN"));
        assertTrue(stats.containsKey("ERROR"));
        assertTrue(stats.containsKey("ALERT"));
        // No logs processed yet → all zero
        assertEquals(0, stats.get("INFO"));
        assertEquals(0, stats.get("WARN"));
        assertEquals(0, stats.get("ERROR"));
        assertEquals(0, stats.get("ALERT"));
    }

    @Test
    void getFlaggedEntries_returnsFlaggedLogs() {
        List<String> valid = List.of(log("WARN", "1.1.1.1"));
        analyzer.checkLevel(valid, List.of("1"));

        List<String> flagged = analyzer.getFlaggedEntries();
        assertEquals(1, flagged.size());
        assertEquals(log("WARN", "1.1.1.1"), flagged.get(0));
    }

    @Test
    void getUnknownLogs_returnsNumberedVersion() {
        List<String> valid = List.of(log("BLAH", "1.1.1.1"));
        analyzer.checkLevel(valid, List.of("numbered-1"));

        List<String> unknown = analyzer.getUnknownLogs();
        assertEquals(1, unknown.size());
        assertEquals("numbered-1", unknown.get(0));
    }
}