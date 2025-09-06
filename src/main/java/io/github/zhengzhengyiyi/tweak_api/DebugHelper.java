package io.github.zhengzhengyiyi.tweak_api;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.github.zhengzhengyiyi.tweak_api.api.Debugger;

import net.fabricmc.api.ModInitializer;

public class DebugHelper implements ModInitializer {
	
	/**
	 * The mod id
	 */
	public static final String MOD_ID = "debughelper";
	
	/**
	 * The LOGGER
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	/**
	 * private value, can be change using setter and get with getter. enable debug mode
	 */
	private static boolean _debug = false;
	
	/**
	 * Sets the debug mode if the provided value is not null, and returns the current debug state.
	 *
	 * @param v the new debug state to set, or {@code null} to leave the current state unchanged
	 * @return the current debug state ({@code true} for enabled, {@code false} for disabled)
	 */
	
	/**
	 * The debuggers. no need for append just into the list
	 */
	public static List<Debugger> debuggers = new ArrayList<>();
	
	public static boolean debug(@Nullable Boolean v) {
		if (v) {
			_debug = v;
		}
		
		return _debug;
	}
	
	/**
	 * entry point
	 */
	@Override
	public void onInitialize() {
	}
}
