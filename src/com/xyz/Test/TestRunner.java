package com.xyz.Test;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

/**
 * TestRunner class for executing the Doctor Channeling System test suite.
 * Measures execution time manually to address the absence of getTotalTime() in TestExecutionSummary.
 */
public class TestRunner {

    public static void main(String[] args) {
        System.out.println("🚀 Running Doctor Channeling System Test Suite");
        System.out.println("=".repeat(60));

        // Record start time
        long startTime = System.currentTimeMillis();

        // Build test discovery request for the com.xyz.Test package
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectPackage("com.xyz.Test"))
                .build();

        // Create launcher and listener
        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();

        // Register listener and execute tests
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        // Record end time and calculate duration
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Get test summary
        TestExecutionSummary summary = listener.getSummary();

        // Print summary
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📊 TEST EXECUTION SUMMARY");
        System.out.println("=".repeat(60));
        System.out.printf("Tests Run: %d%n", summary.getTestsStartedCount());
        System.out.printf("✅ Passed: %d%n", summary.getTestsSucceededCount());
        System.out.printf("❌ Failed: %d%n", summary.getTestsFailedCount());
        System.out.printf("⏭️ Skipped: %d%n", summary.getTestsSkippedCount());
        System.out.printf("⏱️ Total Time: %d ms%n", duration);

        // Print details of failed tests
        if (summary.getTestsFailedCount() > 0) {
            System.out.println("\n❌ FAILED TESTS:");
            summary.getFailures().forEach(failure -> {
                System.out.println("- " + failure.getTestIdentifier().getDisplayName());
                System.out.println("  Error: " + failure.getException().getMessage());
            });
        }

        System.out.println("\n🎉 Test Suite Execution Complete!");
    }
}