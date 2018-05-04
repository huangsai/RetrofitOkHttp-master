package cn.hyman.network.module;

import android.app.Activity;

import java.util.Stack;

/**
 * @className: AppManager
 * @classDescription:Activity管理类
 * @author: hyman
 */
public class AppManager {
    // 单例
    private static AppManager instance;
    // Activity堆栈
    private static Stack<Activity> activityStack;

    private AppManager() {
    }

    /**
     * 单例
     */
    public static AppManager getInstance() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activity == null)
            return;
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 从堆栈移除Activity
     */
    public void removeActivity(Activity activity) {
        if (activityStack != null && activity != null) {
            activityStack.remove(activity);
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = null;
        if (activityStack == null)
            return activity;
        try {
            if (activityStack.size() > 0) {
                activity = activityStack.lastElement();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        if (activityStack == null)
            return;
        try {
            Activity activity = activityStack.lastElement();
            finishActivity(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activityStack == null || activity == null)
            return;
        try {
            if (activity != null) {
                activityStack.remove(activity);
                activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束所有Activity,除了当前Activity
     */
    public void finishAllActivityExceptOne(Class activityCls) {
        if (activityStack == null || activityCls == null)
            return;
        for (int i = 0; i < activityCounts(); i++) {
            Activity activity = activityStack.get(i);
            if (!activity.getClass().equals(activityCls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity,除了当前Activity
     */
    public void finishAllActivityExceptCurrent() {
        Activity currentActivity = currentActivity();
        if (currentActivity != null) {
            finishAllActivityExceptOne(currentActivity.getClass());
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        if (activityStack == null || cls == null)
            return;
        try {
            for (Activity activity : activityStack) {
                if (activity.getClass().equals(cls)) {
                    finishActivity(activity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (activityStack == null)
            return;
        try {
            for (int i = 0; i < activityStack.size(); i++) {
                if (null != activityStack.get(i)) {
                    activityStack.get(i).finish();
                }
            }
            activityStack.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拿到应用程序当前活跃Activity的个数
     *
     * @return counts Activity的个数
     */
    public int activityCounts() {
        int counts = 0;
        if (activityStack != null && activityStack.size() > 0) {
            counts = activityStack.size();
        }
        return counts;
    }

    /**
     * 退出应用程序
     */
    public void exit() {
        try {
            if (currentActivity() != null)
                finishAllActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断某一个activity是否为当前activity
     */
    public boolean isCurrent(Activity activity) {
        if (activity == null || currentActivity() == null)
            return false;
        if (activity == currentActivity())
            return true;
        else
            return false;
    }
}
