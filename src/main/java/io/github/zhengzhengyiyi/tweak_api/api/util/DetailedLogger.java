package io.github.zhengzhengyiyi.tweak_api.api.util;

import org.quiltmc.loader.api.QuiltLoader;
import net.minecraft.server.MinecraftServer;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import io.github.zhengzhengyiyi.tweak_api.api.debuggers.DebugFileManager;

/**
 * Extended logger for detailed server diagnostics and performance monitoring.
 * This class provides comprehensive server data logging without modifying existing Example class.
 * 
 * <p><b>Usage example:</b>
 * <pre>{@code
 * DetailedLogger detailedLogger = new DetailedLogger("my_mod");
 * detailedLogger.logServerDetails(server);
 * detailedLogger.logSystemMetrics();
 * }</pre>
 * 
 * @author zhengzhengyiyi
 * @since 1.0.3
 */
public class DetailedLogger {
    private final String modId;
    
    public DetailedLogger(String modId) {
        this.modId = modId;
    }
    
    /**
     * Logs comprehensive server details including configuration and performance metrics.
     */
    public CompletableFuture<Path> logServerDetails(MinecraftServer server) {
        StringBuilder content = new StringBuilder();
        
        content.append("=== Server Detailed Report ===\n");
        content.append("Generated: ").append(java.time.LocalDateTime.now()).append("\n\n");
        
        // Server configuration
        content.append("--- Server Configuration ---\n");
        content.append("Minecraft Version: ").append(QuiltLoader.getRawGameVersion()).append("\n");
        content.append("Mod Loader: Fabric\n");
        content.append("Server Version: ").append(server.getVersion()).append("\n");
        content.append("Player Count: ").append(server.getPlayerCount()).append("/")
               .append(server.getMaxPlayerCount()).append("\n");
        content.append("World Name: ").append(server.getSaveProperties().getWorldName()).append("\n");
        content.append("Difficulty: ").append(server.getSaveProperties().getDifficulty()).append("\n");
        content.append("Game Mode: ").append(server.getDefaultGameMode()).append("\n\n");
        
        // Performance metrics
        content.append("--- Performance Metrics ---\n");
//        content.append("TPS: ").append(server.getTickTime()).append("\n");
//        content.append("Average Tick Time: ").append(server.getTickTime()).append("ms\n");
        content.append("Memory Usage: ").append(getMemoryUsage()).append("\n");
        content.append("Uptime: ").append(ManagementFactory.getRuntimeMXBean().getUptime() / 1000).append("s\n\n");
        
        return DebugFileManager.writeDebugFile(modId + "_server_details", content.toString());
    }
    
    /**
     * Logs detailed system performance metrics.
     */
    public CompletableFuture<Path> logSystemMetrics() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();
        
        StringBuilder content = new StringBuilder();
        content.append("=== System Metrics Report ===\n");
        content.append("Timestamp: ").append(java.time.LocalDateTime.now()).append("\n\n");
        
        content.append("--- Memory Usage ---\n");
        content.append("Heap Memory: ").append(heapUsage.getUsed() / 1024 / 1024).append("MB/")
               .append(heapUsage.getMax() / 1024 / 1024).append("MB\n");
        content.append("Non-Heap Memory: ").append(nonHeapUsage.getUsed() / 1024 / 1024).append("MB/")
               .append(nonHeapUsage.getMax() / 1024 / 1024).append("MB\n");
        
        content.append("--- Thread Information ---\n");
        content.append("Thread Count: ").append(ManagementFactory.getThreadMXBean().getThreadCount()).append("\n");
        content.append("Peak Thread Count: ").append(ManagementFactory.getThreadMXBean().getPeakThreadCount()).append("\n");
        
        content.append("--- Operating System ---\n");
        content.append("OS: ").append(System.getProperty("os.name")).append(" ").append(System.getProperty("os.version")).append("\n");
        content.append("Architecture: ").append(System.getProperty("os.arch")).append("\n");
        content.append("Available Processors: ").append(Runtime.getRuntime().availableProcessors()).append("\n");
        
        return DebugFileManager.writeDebugFile(modId + "_system_metrics", content.toString());
    }
    
    /**
     * Logs detailed garbage collection information.
     */
    public CompletableFuture<Path> logGarbageCollectionStats() {
        StringBuilder content = new StringBuilder();
        content.append("=== Garbage Collection Report ===\n");
        content.append("Timestamp: ").append(java.time.LocalDateTime.now()).append("\n\n");
        
        for (var gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            content.append("GC: ").append(gcBean.getName()).append("\n");
            content.append("  Collection Count: ").append(gcBean.getCollectionCount()).append("\n");
            content.append("  Collection Time: ").append(gcBean.getCollectionTime()).append("ms\n");
        }
        
        return DebugFileManager.writeDebugFile(modId + "_gc_stats", content.toString());
    }
    
    private String getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        return usedMemory + "MB/" + maxMemory + "MB";
    }
}
