package io.github.zhengzhengyiyi.tweak_api.api.debuggers;

import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 
 * This code was generated with the assistance of AI tools.
 * The core logic and structure were conceived by the author,
 * with AI providing implementation suggestions and optimizations.
 * 
 * <p>Utility class for handling debug file operations in the Minecraft debug directory.
 * Provides methods to create, write, and manage debug files asynchronously.</p>
 * 
 * <p><b>Usage example:</b>
 * <pre>{@code
 * // Write text to a debug file
 * DebugFileManager.writeDebugFile("performance", "Block rendering took 16ms");
 * 
 * // Write lines to a debug file with timestamp
 * List<String> lines = List.of("Line 1", "Line 2", "Line 3");
 * DebugFileManager.writeDebugFile("events", lines);
 * 
 * // Append to an existing file
 * DebugFileManager.appendDebugFile("performance", "Entity update took 8ms");
 * 
 * // Create a file with custom name pattern
 * DebugFileManager.createDebugFile("custom_prefix", "file_content");
 * }</pre>
 * 
 * 
 * Human-AI Collaboration: [Your Name] + AI
 * Generation Date: 2025-09-07
 * 
 * @author zhengzhengyiyi
 * @since 1.0.2
 */
public final class DebugFileManager {
    private static final Executor FILE_IO_EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "Debug-File-IO");
        thread.setDaemon(true);
        return thread;
    });
    
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    
    private DebugFileManager() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Gets the debug directory path within the Minecraft game directory.
     * Creates the directory if it doesn't exist.
     *
     * @return Path to the debug directory
     * @throws IOException if the directory creation fails
     */
    @NotNull
    public static Path getDebugDirectory() throws IOException {
        Path gameDir = FabricLoader.getInstance().getGameDir();
        Path debugDir = gameDir.resolve("debug");
        
        if (!Files.exists(debugDir)) {
            Files.createDirectories(debugDir);
        }
        
        return debugDir;
    }
    
    /**
     * Generates a debug filename with timestamp and optional prefix.
     *
     * @param prefix Optional filename prefix, can be null
     * @param extension File extension without the dot
     * @return Generated filename string
     */
    @NotNull
    public static String generateDebugFilename(@Nullable String prefix, @NotNull String extension) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        
        if (prefix == null || prefix.trim().isEmpty()) {
            return String.format("debug_%s.%s", timestamp, extension);
        }
        
        return String.format("%s_%s.%s", prefix.trim(), timestamp, extension);
    }
    
    /**
     * Writes content to a debug file asynchronously.
     *
     * @param filenamePrefix Prefix for the filename
     * @param content Content to write to the file
     * @return CompletableFuture that completes with the created file path
     */
    @NotNull
    public static CompletableFuture<Path> writeDebugFile(@Nullable String filenamePrefix, @NotNull String content) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path debugDir = getDebugDirectory();
                String filename = generateDebugFilename(filenamePrefix, "txt");
                Path filePath = debugDir.resolve(filename);
                
                Files.writeString(filePath, content);
                return filePath;
            } catch (IOException e) {
                throw new RuntimeException("Failed to write debug file", e);
            }
        }, FILE_IO_EXECUTOR);
    }
    
    /**
     * Writes multiple lines to a debug file asynchronously.
     *
     * @param filenamePrefix Prefix for the filename
     * @param lines List of lines to write
     * @return CompletableFuture that completes with the created file path
     */
    @NotNull
    public static CompletableFuture<Path> writeDebugFile(@Nullable String filenamePrefix, @NotNull List<String> lines) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path debugDir = getDebugDirectory();
                String filename = generateDebugFilename(filenamePrefix, "txt");
                Path filePath = debugDir.resolve(filename);
                
                Files.write(filePath, lines);
                return filePath;
            } catch (IOException e) {
                throw new RuntimeException("Failed to write debug file lines", e);
            }
        }, FILE_IO_EXECUTOR);
    }
    
    /**
     * Appends content to an existing debug file asynchronously.
     * Creates the file if it doesn't exist.
     *
     * @param filename Exact filename to append to (without path)
     * @param content Content to append
     * @return CompletableFuture that completes with the file path
     */
    @NotNull
    public static CompletableFuture<Path> appendDebugFile(@NotNull String filename, @NotNull String content) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path debugDir = getDebugDirectory();
                Path filePath = debugDir.resolve(filename);
                
                if (Files.exists(filePath)) {
                    String existingContent = Files.readString(filePath);
                    Files.writeString(filePath, existingContent + "\n" + content);
                } else {
                    Files.writeString(filePath, content);
                }
                
                return filePath;
            } catch (IOException e) {
                throw new RuntimeException("Failed to append to debug file", e);
            }
        }, FILE_IO_EXECUTOR);
    }
    
    /**
     * Creates an empty debug file and returns its path.
     *
     * @param filenamePrefix Prefix for the filename
     * @return CompletableFuture that completes with the created file path
     */
    @NotNull
    public static CompletableFuture<Path> createDebugFile(@Nullable String filenamePrefix) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path debugDir = getDebugDirectory();
                String filename = generateDebugFilename(filenamePrefix, "txt");
                Path filePath = debugDir.resolve(filename);
                
                Files.createFile(filePath);
                return filePath;
            } catch (IOException e) {
                throw new RuntimeException("Failed to create debug file", e);
            }
        }, FILE_IO_EXECUTOR);
    }
    
    /**
     * Reads content from a debug file asynchronously.
     *
     * @param filename Exact filename to read (without path)
     * @return CompletableFuture that completes with the file content
     */
    @NotNull
    public static CompletableFuture<String> readDebugFile(@NotNull String filename) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path debugDir = getDebugDirectory();
                Path filePath = debugDir.resolve(filename);
                
                if (!Files.exists(filePath)) {
                    throw new RuntimeException("Debug file not found: " + filename);
                }
                
                return Files.readString(filePath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read debug file", e);
            }
        }, FILE_IO_EXECUTOR);
    }
    
    /**
     * Deletes a debug file asynchronously.
     *
     * @param filename Exact filename to delete (without path)
     * @return CompletableFuture that completes with true if deleted successfully
     */
    @NotNull
    public static CompletableFuture<Boolean> deleteDebugFile(@NotNull String filename) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path debugDir = getDebugDirectory();
                Path filePath = debugDir.resolve(filename);
                
                return Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete debug file", e);
            }
        }, FILE_IO_EXECUTOR);
    }
    
    /**
     * Lists all files in the debug directory asynchronously.
     *
     * @return CompletableFuture that completes with list of filenames
     */
    @NotNull
    public static CompletableFuture<List<String>> listDebugFiles() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path debugDir = getDebugDirectory();
                
                return Files.list(debugDir)
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .toList();
            } catch (IOException e) {
                throw new RuntimeException("Failed to list debug files", e);
            }
        }, FILE_IO_EXECUTOR);
    }
    
    /**
     * Gets the full path of a debug file.
     *
     * @param filename The filename (without directory path)
     * @return Full path to the debug file
     * @throws IOException if the debug directory cannot be accessed
     */
    @NotNull
    public static Path getDebugFilePath(@NotNull String filename) throws IOException {
        Path debugDir = getDebugDirectory();
        return debugDir.resolve(filename);
    }
}
