package io.github.zhengzhengyiyi.tweak_api.example;

import org.slf4j.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import net.minecraft.util.Identifier;
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
 * 
 * // Static method usage
 * Example.quickLog("my_mod", "Server started successfully");
 * Example.quickProfile("my_mod", "network_processing", () -> {
 *     // your network code here
 * });
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
    
    // Static instance for quick methods
    private static final Logger STATIC_LOGGER = LoggerFactory.getLogger(Example.class);
    
    /**
     * Constructs a new Example instance with the specified mod ID.
     * 
     * @param modId the unique identifier for this mod
     */
    public Example(String modId) {
        this.modId = modId;
        this.profiler = new ProfilingDebugger(new Identifier(modId, "example_profiler"));
        this.events = new ArrayList<>();
        this.profilingData = new ArrayList<>();
        this.profiler.setEnabled(true);
        this.profiler.register();
    }
    
    // ==================== STATIC METHODS ====================
    
    /**
     * Quickly log a message to debug file without creating an instance.
     * 
     * @param modId the mod identifier
     * @param message the message to log
     * @return CompletableFuture that completes when the message is logged
     */
    public static CompletableFuture<Void> quickLog(String modId, String message) {
        String formattedMessage = "[" + java.time.LocalDateTime.now() + "] " + message;
        STATIC_LOGGER.info("Quick log: {}", formattedMessage);
        
        return DebugFileManager.writeDebugFile(modId + "_quick_log", formattedMessage)
            .thenAccept(path -> {
                STATIC_LOGGER.info("Quick log saved to: {}", path);
            });
    }
    
    /**
     * Quickly profile a code block and save results.
     * 
     * @param modId the mod identifier
     * @param operationName the name of the operation to profile
     * @param runnable the code block to profile
     * @return CompletableFuture that completes when profiling is done
     */
    public static CompletableFuture<Void> quickProfile(String modId, String operationName, Runnable runnable) {
        ProfilingDebugger tempProfiler = new ProfilingDebugger(new Identifier(modId, "quick_profiler"));
        tempProfiler.setEnabled(true);
        
        tempProfiler.startTiming(operationName);
        try {
            runnable.run();
        } finally {
            tempProfiler.stopTiming(operationName);
        }
        
        long avgTime = tempProfiler.getAverageTime(operationName);
        long callCount = tempProfiler.getCallCount(operationName);
        
        String content = String.format(
            "Quick Profile - Operation: %s | Calls: %d | Avg: %.2fms",
            operationName, callCount, avgTime / 1_000_000.0
        );
        
        STATIC_LOGGER.info(content);
        return DebugFileManager.writeDebugFile(modId + "_quick_profile", content)
            .thenAccept(path -> {
                STATIC_LOGGER.info("Quick profile saved to: {}", path);
            });
    }
    
    /**
     * Quickly save multiple lines to a debug file.
     * 
     * @param modId the mod identifier
     * @param filenameSuffix the suffix for the filename
     * @param lines the lines to save
     * @return CompletableFuture that completes when the file is saved
     */
    public static CompletableFuture<Void> quickSaveLines(String modId, String filenameSuffix, List<String> lines) {
        return DebugFileManager.writeDebugFile(modId + "_" + filenameSuffix, lines)
            .thenAccept(path -> {
                STATIC_LOGGER.info("Lines saved to: {}", path);
            });
    }
    
    /**
     * Quickly append to an existing debug file.
     * 
     * @param modId the mod identifier
     * @param filename the exact filename to append to
     * @param message the message to append
     * @return CompletableFuture that completes when the message is appended
     */
    public static CompletableFuture<Void> quickAppend(String modId, String filename, String message) {
        String formattedMessage = "[" + java.time.LocalDateTime.now() + "] " + message;
        return DebugFileManager.appendDebugFile(filename, formattedMessage)
            .thenAccept(path -> {
                STATIC_LOGGER.info("Message appended to: {}", path);
            });
    }
    
    /**
     * Get the default debug directory path for a mod.
     * 
     * @param modId the mod identifier
     * @return the debug directory path as a string
     */
    public static String getDebugDirectoryPath(String modId) {
        try {
            return DebugFileManager.getDebugDirectory().resolve(modId).toString();
        } catch (Exception e) {
            STATIC_LOGGER.warn("Failed to get debug directory path: {}", e.getMessage());
            return "./debug/" + modId;
        }
    }
    
    /**
     * Check if debug mode is enabled globally.
     * This checks the system property or environment variable.
     * 
     * @return true if debug mode is enabled
     */
    public static boolean isGlobalDebugEnabled() {
        return System.getProperty("debug.mode", "false").equalsIgnoreCase("true") ||
               System.getenv("DEBUG_MODE") != null;
    }
    
    /**
     * Create a quick timestamp string for logging.
     * 
     * @return formatted timestamp string
     */
    public static String getTimestamp() {
        return java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        );
    }
    
    // ==================== INSTANCE METHODS ====================
    
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
