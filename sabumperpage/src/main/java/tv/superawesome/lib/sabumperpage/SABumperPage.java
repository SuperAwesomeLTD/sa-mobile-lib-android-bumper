package tv.superawesome.lib.sabumperpage;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.net.URLEncoder;

public class SABumperPage extends Activity {

    //
    // private constants of sorts
    private static final int BIG_LOGO_ID = 0x1201012;
    private static final int SMALL_LOGO_ID = 0x212121;
    private static final int SMALL_TEXT_ID = 0x212151;
    private static final int BIG_TEXT_ID = 0x212751;

    private static final String defaultBigTextString = "Bye! You’re now leaving this app.";
    private static final String bigTextString = "Bye! You’re now leaving\n";
    private static final String smallTextString = "A completely new site will open in %ld seconds. Remember to stay safe online and don’t share your username or password with anyone!";

    private static final int MAX_TIMER = 3;

    //
    // private instance vars

    private Handler handler = null;
    private Runnable runnable = null;

    //
    // Private vars that need to be set

    private static String appName = null;
    private static Drawable appIcon = null;
    private static Interface listener = new SABumperPage.Interface() { @Override public void didEndBumper() {} };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        float density = getResources().getDisplayMetrics().density;

        //
        // background
        SABumperPageBackground background = new SABumperPageBackground(this);
        background.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        background.setBackground(new BitmapDrawable(getResources(), SABumperPageImageUtils.backgroundImage()));

        //
        // big logo
        Drawable fullDrawable =
                appIcon != null ? appIcon :
                        new BitmapDrawable(getResources(), SABumperPageImageUtils.defaultLogo());

        ImageView bigLogo = new ImageView(this);
        bigLogo.setId(BIG_LOGO_ID);
        bigLogo.setImageDrawable(fullDrawable);

        RelativeLayout.LayoutParams bigLogoLayout =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        (int)(density * 40));
        bigLogoLayout.setMargins((int)(12 * density), (int)(12 * density), (int)(12 * density), (int)(12 * density));

        bigLogo.setLayoutParams(bigLogoLayout);

        //
        // small logo
        ImageView smallLogo = new ImageView(this);
        smallLogo.setId(SMALL_LOGO_ID);
        smallLogo.setImageDrawable(new BitmapDrawable(getResources(), SABumperPageImageUtils.poweredByImage()));
        smallLogo.setBaselineAlignBottom(true);
        smallLogo.setVisibility(appIcon != null ? View.VISIBLE : View.INVISIBLE);

        RelativeLayout.LayoutParams smallLogoLayout =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        (int)(density * 20));
        smallLogoLayout.setMargins((int)(12 * density), (int)(12 * density), (int)(12 * density), (int)(12 * density));
        smallLogoLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        smallLogo.setLayoutParams(smallLogoLayout);

        //
        // small text
        final TextView smallText = new TextView(this);
        smallText.setId(SMALL_TEXT_ID);
        smallText.setText(smallTextString.replace("%ld", "" + MAX_TIMER));
        smallText.setTextColor(0xffffffff);
        smallText.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams smallTextLayout =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        smallTextLayout.setMargins((int)(24 * density), 0, (int) (24 * density), (int) (10 * density));
        smallTextLayout.addRule(RelativeLayout.ABOVE, SMALL_LOGO_ID);

        smallText.setLayoutParams(smallTextLayout);

        //
        // big text
        String fullText = appName != null ?
                (bigTextString + appName) :
                (getAppLabel(this) != null ?
                        (bigTextString + getAppLabel(this)) : defaultBigTextString);

        TextView bigText = new TextView(this);
        bigText.setId(BIG_TEXT_ID);
        bigText.setText(fullText);
        bigText.setTextColor(0xffffffff);
        bigText.setTextSize(16);
        bigText.setTypeface(null, Typeface.BOLD);
        bigText.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams bigTextLayout =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        bigTextLayout.setMargins((int)(24 * density), 0, (int) (24 * density), (int) (10 * density));
        bigTextLayout.addRule(RelativeLayout.ABOVE, SMALL_TEXT_ID);

        bigText.setLayoutParams(bigTextLayout);

        //
        // assemble them all together
        background.addView(bigLogo);
        background.addView(smallLogo);
        background.addView(smallText);
        background.addView(bigText);

        setContentView(background);

        //
        // create the timer
        final int[] countdown = {MAX_TIMER};
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                if (countdown[0] <= 0) {
                    listener.didEndBumper();
                    SABumperPage.this.finish();
                } else {
                    countdown[0]--;
                    smallText.setText(smallTextString.replace("%ld", "" + countdown[0]));
                    handler.postDelayed(runnable, 1000);
                }
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Public methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void play (Activity activity) {
        Intent intent = new Intent(activity, SABumperPage.class);
        activity.startActivity(intent);
    }

    public static void overrideName (String name) {
        appName = name;
    }

    public static void overrideLogo (Drawable drawable) {
        appIcon = drawable;
    }

    public static void setListener (Interface lis) {
        listener = lis;
    }

    public interface Interface {
        void didEndBumper();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Aux methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static String getAppLabel(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getApplicationInfo().packageName, 0);
            String name = (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "Unknown");
            name = URLEncoder.encode(name, "UTF-8");
            return name;
        } catch (Exception e) {
            return null;
        }
    }
}
