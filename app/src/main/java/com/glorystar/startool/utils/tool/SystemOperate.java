package com.glorystar.startool.utils.tool;

/**
 * @className:SystemOperate
 * @author jinyuan
 */
public class SystemOperate {

	/**
	 * @title:rebootSystem
	 * @return void
	 */
	public void rebootSystem() {
		try {
			Runtime.getRuntime().exec(new String[] {"reboot" });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @title:rebootSystem
	 * @return void
	 */
	public void rebootSystem_su() {
		try {
			Rootcommands.runRootCommand("reboot");
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	/**
	 * @title:shutDown
	 * @return void
	 */
	public void shutDown() {
		try {
			Rootcommands.runRootCommand("reboot -p");
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	/**
	 * @title:screenShot
	 * @return void
	 */
//	public void screenShot(String path) {
//		try {
//			Runtime.getRuntime().exec(
//					new String[] {"screencap -p", path });
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * @title:screenShot
	 * @return void
	 */
	public void screenShot(String path) {
		try {
			Rootcommands.runRootCommand("screencap -p "+path);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
}
