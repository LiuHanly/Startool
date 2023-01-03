package com.glorystar.startool.utils.tool;

/**
 * @className:SystemOperate
 * @author jinyuan
 * @desc 系统关机和重启
 */
public class SystemOperate {

	/**
	 * @title:rebootSystem
	 * @desc:重启系统
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
	 * @desc:重启系统
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
	 * @desc:关机
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
	 * @desc:系统截屏
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
	 * @desc:系统截屏
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
