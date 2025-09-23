package com.buyer.listeners;

import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ITestContext;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom TestNG Listener for enhanced test reporting and logging
 */
public class CustomTestListener implements ITestListener {
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public void onStart(ITestContext context) {
        System.out.println("========================================");
        System.out.println("🚀 Starting Test Suite: " + context.getName());
        System.out.println("📅 Start Time: " + LocalDateTime.now().format(DATE_FORMAT));
        System.out.println("🧪 Total Tests: " + context.getAllTestMethods().length);
        System.out.println("========================================");
    }
    
    @Override
    public void onFinish(ITestContext context) {
        int total = context.getAllTestMethods().length;
        int passed = context.getPassedTests().size();
        int failed = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();
        
        System.out.println("========================================");
        System.out.println("✅ Test Suite Completed: " + context.getName());
        System.out.println("📅 End Time: " + LocalDateTime.now().format(DATE_FORMAT));
        System.out.println("📊 Results:");
        System.out.println("   ✅ Passed: " + passed + "/" + total);
        System.out.println("   ❌ Failed: " + failed + "/" + total);
        System.out.println("   ⏭️  Skipped: " + skipped + "/" + total);
        System.out.println("🎯 Success Rate: " + String.format("%.2f%%", (passed * 100.0 / total)));
        System.out.println("========================================");
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("\n🧪 Starting Test: " + result.getMethod().getMethodName());
        System.out.println("📝 Description: " + result.getMethod().getDescription());
        System.out.println("🏷️  Groups: " + String.join(", ", result.getMethod().getGroups()));
        System.out.println("⏰ Started at: " + LocalDateTime.now().format(DATE_FORMAT));
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        System.out.println("✅ Test PASSED: " + result.getMethod().getMethodName());
        System.out.println("⏱️  Duration: " + duration + "ms");
        System.out.println("🎉 SUCCESS!");
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        System.out.println("❌ Test FAILED: " + result.getMethod().getMethodName());
        System.out.println("⏱️  Duration: " + duration + "ms");
        System.out.println("💥 Error: " + result.getThrowable().getMessage());
        System.out.println("📍 Stack Trace:");
        result.getThrowable().printStackTrace();
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("⏭️  Test SKIPPED: " + result.getMethod().getMethodName());
        if (result.getThrowable() != null) {
            System.out.println("🤔 Reason: " + result.getThrowable().getMessage());
        }
    }
}