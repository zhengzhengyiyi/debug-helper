package io.github.zhengzhengyiyi.tweak_api.example;

import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.LoggerFactory;

import io.github.zhengzhengyiyi.tweak_api.api.debuggers.DebugFileManager;
import io.github.zhengzhengyiyi.tweak_api.api.debuggers.ProfilingDebugger;

/**
 * Example class demonstrating the usage of debug utilities.
 * This class provides practical examples of how to use the DebugFileManager
 * and ProfilingDebugger in a Minecraft Fabric mod.
 * 
 * This file is made with **deepseek**
 * 
 * <p><b>Usage example:</b>
 * <pre>{@code
 * // Create an example instance
 * Example example = new Example("my_mod");
 * 
 * // Start profiling an operation
 * example.startProfiling("chunk_loading");
 * 
 * // Perform some operations...
 * 
 * // Stop profiling and collect results
 * example.stopProfiling("chunk_loading");
 * 
 * // Save all data to a single debug file
 * example.saveDebugData();
 * }</pre>
 * 
 * @author zhengzhengyiyi
 * @since 1.0.2
 */
public class Example {
    private final String modId;
    private final ProfilingDebugger profiler;
    private final List<String> events;
    private final List<String> profilingData;
    private final Logger LOGGER = LoggerFactory.getLogger(Example.class);
    
    /**
     * Constructs a new Example instance with the specified mod ID.
     * 
     * @param modId the unique identifier for this mod
     */
    public Example(String modId) {
        this.modId = modId;
        this.profiler = new ProfilingDebugger(Identifier.of(modId, "example_profiler"));
        this.events = new ArrayList<>();
        this.profilingData = new ArrayList<>();
        this.profiler.setEnabled(true);
        this.profiler.register();
    }
    
    /**
     * Starts profiling a specific operation.
     * 
     * @param operationName the name of the operation to profile
     */
    public void startProfiling(String operationName) {
        profiler.startTiming(operationName);
    }
    
    /**
     * Stops profiling a specific operation and collects the data.
     * 
     * @param operationName the name of the operation to stop profiling
     */
    public void stopProfiling(String operationName) {
        profiler.stopTiming(operationName);
        
        // Collect profiling data instead of saving immediately
        long avgTime = profiler.getAverageTime(operationName);
        long callCount = profiler.getCallCount(operationName);
        profilingData.add(String.format("Operation: %s | Calls: %d | Avg: %.2fms",
            operationName, callCount, avgTime / 1_000_000.0));
    }
    
    /**
     * Logs an event message to memory.
     * 
     * @param message the event message to log
     */
    public void logEvent(String message) {
        String formattedMessage = "[" + java.time.LocalDateTime.now() + "] " + message;
        events.add(formattedMessage);
        LOGGER.info("Event logged: " + formattedMessage);
    }
    
    /**
     * Saves all collected debug data to a single file.
     * 
     * @return CompletableFuture that completes when the file is saved
     */
    public CompletableFuture<Void> saveDebugData() {
        StringBuilder content = new StringBuilder();
        
        // Add header
        content.append("=== Debug Data Report ===\n");
        content.append("Mod: ").append(modId).append("\n");
        content.append("Generated: ").append(java.time.LocalDateTime.now()).append("\n\n");
        
        // Add profiling data
        if (!profilingData.isEmpty()) {
            content.append("--- Performance Profiling ---\n");
            for (String data : profilingData) {
                content.append(data).append("\n");
            }
            content.append("\n");
        }
        
        // Add events
        if (!events.isEmpty()) {
            content.append("--- Event Log ---\n");
            for (String event : events) {
                content.append(event).append("\n");
            }
        }
        
        content.append("=== End of Report ===\n");
        
        return DebugFileManager.writeDebugFile(modId + "_debug", content.toString())
            .thenAccept(path -> {
            	LOGGER.info("All debug data saved to: " + path);
                // Clear data after successful save
                events.clear();
                profilingData.clear();
            });
    }
    
    /**
     * Performs a complete example workflow including profiling and logging.
     * This method demonstrates a typical usage pattern and saves to a single file.
     */
    public void runExampleWorkflow() {
        try {
            // Example 1: Performance profiling
            startProfiling("mod_initialization");
            Thread.sleep(50); // Simulate initialization work
            stopProfiling("mod_initialization");
            
            startProfiling("data_loading");
            Thread.sleep(30); // Simulate data loading
            stopProfiling("data_loading");
            
            // Example 2: Event logging
            logEvent("Mod initialization started");
            logEvent("Configuration loaded");
            logEvent("Event system initialized");
            
            // Example 3: Save everything to a single file
            saveDebugData().get();
            
            logEvent("Debug data saved successfully");
            
        } catch (InterruptedException | ExecutionException e) {
        	LOGGER.error("Example workflow failed: " + e.getMessage());
            logEvent("Workflow failed: " + e.getMessage());
        }
    }
    
    /**
     * Clears all collected data without saving.
     */
    public void clearData() {
        events.clear();
        profilingData.clear();
        profiler.clear();
    }
    
    /**
     * Gets the number of logged events.
     */
    public int getEventCount() {
        return events.size();
    }
    
    /**
     * Gets the number of profiling records.
     */
    public int getProfilingRecordCount() {
        return profilingData.size();
    }
}