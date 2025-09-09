package io.github.zhengzhengyiyi.tweak_api.api.util;

import io.github.zhengzhengyiyi.tweak_api.api.Debugger;
import io.github.zhengzhengyiyi.tweak_api.Debughelper;

/**
 * <h2>A helper for register all stuff that define by this mod</h2>
 * 
 * Register debugger, without print into the file
 * 
 * @author zhengzhengyiyi
 * @since 1.0.1
 */
public class RegisterHelper {
	
	/**
	 * 
	 * @param debugger the debugger that need to registry
	 */
	public static void debugger(Debugger debugger) {
		Debughelper.debuggers.add(debugger);
	}
}
