package kazumi.selinux.fucker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private Toast toast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 申请 Root 权限
        if (!RootCommand("chmod 777 " + getPackageCodePath())) {
            Toast("Root Failed!");
            finishApp();
        } else {
            // 关闭 SELinux
            if (RootCommand("setenforce 0")) {
                Toast("SELinux Disabled!");
                finishApp();
            } else {
                Toast("Disable Failed!");
            }
        }
    }

    // 结束应用
    private void finishApp() {
        finish();
        System.gc();
        System.currentTimeMillis();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                cancel();
                System.exit(0);
            }
        }, 5000);
    }

    // 执行命令, 返回 boolean 表示是否成功
    private boolean RootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    // 防止创建多条 Toast
    private void Toast(String message) {
        if (toast == null) {
            toast = Toast.makeText(MainActivity.this, "", Toast.LENGTH_LONG);
        }
        toast.setText(message);
        toast.show();
    }
}
