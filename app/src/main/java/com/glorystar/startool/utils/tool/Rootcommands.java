package com.glorystar.startool.utils.tool;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class Rootcommands {
    public static boolean runRootCommand(String command) throws Throwable {
        Exception e;
        Throwable th;
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            DataOutputStream os2 = new DataOutputStream(process.getOutputStream());
            try {
                os2.writeBytes(new StringBuilder(String.valueOf(command)).append("\n").toString());
                os2.flush();
                os2.writeBytes("exit\n");
                os2.flush();
                process.waitFor();
                if (os2 != null) {
                    try {
                        os2.close();
                    } catch (Exception e2) {
                    }
                }
                process.destroy();
                os = os2;
                return true;
            } catch (Exception e3) {
                e = e3;
                os = os2;
                try {
                    Log.d("*** DEBUG ***", "Unexpected error: " + e.getMessage());
                    if (os != null) {
                        try {
                            os.close();
                        } catch (Exception e4) {
                            return false;
                        }
                    }
                    process.destroy();
                    return false;
                } catch (Throwable th2) {
                    th = th2;
                    if (os != null) {
                        try {
                            os.close();
                        } catch (Exception e5) {
                            throw th;
                        }
                    }
                    process.destroy();
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                os = os2;
                if (os != null) {
                    os.close();
                }
                process.destroy();
                throw th;
            }
        } catch (Exception e6) {
            e = e6;
            Log.d("*** DEBUG ***", "Unexpected error: " + e.getMessage());
            if (os != null) {
                os.close();
            }
            process.destroy();
            return false;
        }
    }

    public static boolean installOrUninstallApk(String apkPath, String cmd) {
        boolean result = false;
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        try {
            // 申请su权限
            Process process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            // 执行pm install命令
            String command = cmd + apkPath + "\n";
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String msg = "";
            String line;
            // 读取命令的执行结果
            while ((line = errorStream.readLine()) != null) {
                msg += line;
            }
            Log.d("TAG", "install msg is " + msg);
            // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功
            if (!msg.contains("Failure") && !msg.contains("failed")) {
                result = true;
            }
        } catch (Exception e) {
            Log.e("TAG", e.getMessage(), e);
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                Log.e("TAG", e.getMessage(), e);
            }
        }
        return result;
    }

    public static String execRootCmd(String cmd) {
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;

        try {
            Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());

            //Log.i(TAG, cmd);
            dos.writeBytes(cmd + "\n");
            dos.flush();
            //dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null) {
                Log.d("result", line);
                result += line;
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

//    public static void bootCommand() {
//        String path = BrushfirmCommand + "/data/media/0" + "/glory_update.zip";
//        SWfiletosd.savefile(path,CommandPath);
//        //复制内存卡的command到cache
//        try {
//            Rootcommands.runRootCommand("mount -o rw,remount /cache");
//            Rootcommands.runRootCommand("cp -rf "+CommandPath+" /cache/recovery/command");
//            Rootcommands.runRootCommand("chmod 777 /cache/recovery/command");
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//
//        try {
//            Rootcommands.runRootCommand("reboot recovery");
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//    }

}
