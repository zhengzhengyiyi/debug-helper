package io.github.zhengzhengyiyi.tweak_api.api.util;

import io.github.zhengzhengyiyi.tweak_api.api.Debugger;
import io.github.zhengzhengyiyi.tweak_api.DebugHelper;

/**
 * A helper for register all stuff that define by this mod
 */
public class RegisterHelper {
	
	/**
	 * 
	 * @param debugger the debugger that need to registry
	 */
	public static void debugger(Debugger debugger) {
		DebugHelper.debuggers.add(debugger);
	}
}
