package io.github.zhengzhengyiyi.tweak_api.api.debuggers;

import io.github.zhengzhengyiyi.tweak_api.api.Debugger;
import io.github.zhengzhengyiyi.tweak_api.DebugHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 
 * This code was generated with the assistance of AI tools.
 * The core logic and structure were conceived by the author,
 * with AI providing implementation suggestions and optimizations.
 * 
 * <p>Performance profiling debugger for monitoring and recording code execution times.
 * This debugger tracks method execution times and provides statistical analysis.</p>
 * 
 * <p><b>Usage example:</b>
 * <pre>{@code
 * // Create profiler instance
 * ProfilingDebugger profiler = new ProfilingDebugger();
 * profiler.setEnabled(true);
 * profiler.register();
 * 
 * // Manual timing
 * profiler.startTiming("block_rendering");
 * // ... your code ...
 * profiler.stopTiming("block_rendering");
 * 
 * // Automatic timing (recommended)
 * try (ProfilingDebugger.TimingScope scope = profiler.timeScope("entity_ai")) {
 *     // ... your code ...
 * }
 * 
 * // Get statistics
 * long avgTime = profiler.getAverageTime("block_rendering");
 * Map<String, ProfilingDebugger.TimingStats> stats = profiler.getStats();
 * }</pre>
 * 
 * @author zhengzhengyiyi
 * @since 1.0.2
 */

public class ProfilingDebugger implements Debugger {
    private final Identifier id;
    private boolean enabled = true;
    private final Map<String, TimingData> timings = new HashMap<>();
    private long lastTickTime = 0;
    private int tickCount = 0;
    
    /**
     * Internal class for storing timing data.
     */
    private static class TimingData {
        long totalTime = 0;
        long callCount = 0;
        long lastStartTime = 0;
        
        /**
         * Starts timing for an operation.
         */
        void start() {
            lastStartTime = System.nanoTime();
        }
        
        /**
         * Stops timing and records the duration.
         */
        void stop() {
            if (lastStartTime > 0) {
                totalTime += System.nanoTime() - lastStartTime;
                callCount++;
                lastStartTime = 0;
            }
        }
        
        /**
         * Calculates the average execution time.
         * @return Average time in nanoseconds
         */
        long getAverageTime() {
            return callCount > 0 ? totalTime / callCount : 0;
        }
    }
    
    /**
     * Constructs a ProfilingDebugger with specified identifier.
     * @param id The unique identifier for this debugger
     */
    public ProfilingDebugger(Identifier id) {
        this.id = id;
    }
    
    /**
     * Constructs a ProfilingDebugger with default identifier.
     */
    public ProfilingDebugger() {
        this(Identifier.of("debug", "profiler"));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Identifier getId() {
        return id;
    }
    
    /**
     * Checks if profiling is currently enabled.
     * @return true if profiling is enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Enables or disables profiling.
     * @param enabled true to enable profiling, false to disable
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            clear();
        }
    }
    
    /**
     * Starts timing measurement for a specific operation.
     * @param operationName The name of the operation to time
     */
    public void startTiming(String operationName) {
        if (!enabled) return;
        
        timings.computeIfAbsent(operationName, k -> new TimingData())
               .start();
    }
    
    /**
     * Stops timing measurement for a specific operation.
     * @param operationName The name of the operation to stop timing
     */
    public void stopTiming(String operationName) {
        if (!enabled) return;
        
        TimingData data = timings.get(operationName);
        if (data != null) {
            data.stop();
        }
    }
    
    /**
     * Creates a timing scope for automatic measurement.
     * Usage: try (TimingScope scope = profiler.timeScope("operation")) { ... }
     * @param operationName The name of the operation to time
     * @return A TimingScope instance for try-with-resources
     */
    public TimingScope timeScope(String operationName) {
        return new TimingScope(operationName);
    }
    
    /**
     * AutoCloseable timing scope for automatic measurement.
     * Automatically starts timing on creation and stops on close.
     */
    public class TimingScope implements AutoCloseable {
        private final String operationName;
        
        /**
         * Creates a new timing scope and starts timing.
         * @param operationName The operation name to track
         */
        public TimingScope(String operationName) {
            this.operationName = operationName;
            startTiming(operationName);
        }
        
        /**
         * Stops timing when the scope is closed.
         */
        @Override
        public void close() {
            stopTiming(operationName);
        }
    }
    
    /**
     * Clears all collected timing data and resets counters.
     */
    public void clear() {
        timings.clear();
        tickCount = 0;
        lastTickTime = 0;
    }
    
    /**
     * Gets the average execution time for an operation.
     * @param operationName The name of the operation
     * @return Average execution time in nanoseconds, 0 if not found
     */
    public long getAverageTime(String operationName) {
        TimingData data = timings.get(operationName);
        return data != null ? data.getAverageTime() : 0;
    }
    
    /**
     * Gets the number of times an operation was called.
     * @param operationName The name of the operation
     * @return Call count, 0 if not found
     */
    public long getCallCount(String operationName) {
        TimingData data = timings.get(operationName);
        return data != null ? data.callCount : 0;
    }
    
    /**
     * Gets a snapshot of all collected timing statistics.
     * @return Map containing timing statistics for all operations
     */
    public Map<String, TimingStats> getStats() {
        Map<String, TimingStats> stats = new HashMap<>();
        for (Map.Entry<String, TimingData> entry : timings.entrySet()) {
            TimingData data = entry.getValue();
            stats.put(entry.getKey(), new TimingStats(
                data.totalTime,
                data.callCount,
                data.getAverageTime()
            ));
        }
        return stats;
    }
    
    /**
     * Record class containing timing statistics for an operation.
     * @param totalTime Total execution time in nanoseconds
     * @param callCount Number of times the operation was called
     * @param averageTime Average execution time in nanoseconds
     */
    public record TimingStats(long totalTime, long callCount, long averageTime) {
        /**
         * Gets total execution time in milliseconds.
         * @return Total time in milliseconds
         */
        public double getTotalMillis() {
            return TimeUnit.NANOSECONDS.toMillis(totalTime);
        }
        
        /**
         * Gets average execution time in milliseconds.
         * @return Average time in milliseconds
         */
        public double getAverageMillis() {
            return TimeUnit.NANOSECONDS.toMillis(averageTime);
        }
    }
    
    /**
     * {@inheritDoc}
     * Called every server tick to update profiling data and generate reports.
     */
    @Override
    public void tick(MinecraftServer server) {
        if (!enabled) return;
        
        tickCount++;
        long currentTime = System.nanoTime();
        
        if (tickCount % 20 == 0) {
            printPerformanceReport(server);
        }
        
        lastTickTime = currentTime;
    }
    
    /**
     * Prints a performance report to the console.
     * @param server The Minecraft server instance
     */
    private void printPerformanceReport(MinecraftServer server) {
        if (timings.isEmpty()) return;
        
        System.out.println("=== Performance Profiling Report ===");
        System.out.println("Server Tick: " + tickCount);
        
        for (Map.Entry<String, TimingData> entry : timings.entrySet()) {
            TimingData data = entry.getValue();
            if (data.callCount > 0) {
                double avgMs = TimeUnit.NANOSECONDS.toMillis(data.getAverageTime());
                double totalMs = TimeUnit.NANOSECONDS.toMillis(data.totalTime);
                
                System.out.printf("Operation: %s | Calls: %d | Avg: %.2fms | Total: %.2fms%n",
                    entry.getKey(), data.callCount, avgMs, totalMs);
            }
        }
        System.out.println("===================================");
    }
    
    /**
     * {@inheritDoc}
     * Registers this debugger to receive server tick events.
     */
    @Override
    public void register() {
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            if (DebugHelper.debug(null) && enabled) {
                tick(server);
            }
        });
    }
}