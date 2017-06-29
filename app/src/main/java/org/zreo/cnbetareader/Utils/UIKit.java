package org.zreo.cnbetareader.Utils;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.ListView;
import java.lang.reflect.Method;


public class UIKit {

    public static ListView getHideListView(PreferenceFragment fragment) {
        ListView listView = null;
        try {
            Method getListView = fragment.getClass().getMethod("getListView");
            listView = (ListView) getListView.invoke(fragment);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listView;
    }

    public static void setBackIcon(ActionBar actionbar, Drawable backIcon) {
        try {
            Method method = Class.forName("android.app.ActionBar").getMethod(
                    "setBackButtonDrawable", new Class[]{Drawable.class});
            method.invoke(actionbar, backIcon);
        } catch (Exception ignored) {
        }
    }

    /**
     *  dp转成为 px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /** 对TextView设置不同状态时的文字颜色。 */
    public static ColorStateList createColorStateList(int normal, int actived) {
        int[] colors = new int[] {actived,normal};
        int[][] states = new int[2][];
        states[0] = new int[] { android.R.attr.state_activated };
        states[1] = new int[] {};
        return new ColorStateList(states, colors);
    }
    /** 对TextView设置不同状态时的文字颜色。 */
    public static ColorStateList createColorStateList(int normal, int actived,boolean passed) {
        if(!passed){
            return  createColorStateList(normal, actived);
        }
        int[] colors = new int[] {actived,actived,normal};
        int[][] states = new int[3][];
        states[0] = new int[] { android.R.attr.state_pressed };
        states[1] = new int[] { android.R.attr.state_focused };
        states[2] = new int[] {};
        return new ColorStateList(states, colors);
    }

    @SuppressLint("NewApi")
    public  static void setBackgroundCompt(View view,Drawable drawable){
        if(Build.VERSION_CODES.JELLY_BEAN>=Build.VERSION.SDK_INT){
            view.setBackground(drawable);
        }else{
            view.setBackgroundDrawable(drawable);
        }
    }

    public static int getFontHeight(Context context, float fontSize) {
        // Convert Dp To Px
        float px = context.getResources().getDisplayMetrics().density * fontSize + 0.5f;
        // Use Paint to get font height
        Paint p = new Paint();
        p.setTextSize(px);
        Paint.FontMetrics fm = p.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.ascent);
    }
}
