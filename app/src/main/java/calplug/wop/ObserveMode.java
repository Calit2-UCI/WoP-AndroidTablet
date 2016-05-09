package calplug.wop;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import calplug.wop.R;
import de.mjpegsample.MjpegView.MjpegInputStream;
import de.mjpegsample.MjpegView.MjpegView;

public class ObserveMode extends Activity {
	private MjpegView mv;
	private RelativeLayout rl;
	private WebView stats;
	private Button Logout;
	private Handler mHandler = new Handler();
	private int PD1=0;
	private int PD2=0;
	private int PD3=0;
	private int All=0;
	private ImageView  icon;
	
	public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        rl = new RelativeLayout(this);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

      	//Remove notification bar
      	this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
      	this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Resources res = getResources(); //resource handle
    	Drawable drawable = res.getDrawable(R.drawable.background); 
    	
        rl.setBackgroundDrawable(drawable);
        
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
       	     RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        	layoutParams.setMargins(0, 50, 0, 0);
        	layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        	icon = new ImageView(this);
            icon.setLayoutParams(layoutParams);
            icon.setImageResource(R.drawable.largelogo);
            
            layoutParams = new RelativeLayout.LayoutParams(
              	     RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

               	layoutParams.setMargins(770, 200, 0, 0);
               	layoutParams.height=400;
               	layoutParams.width=380;
               	//layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
               	stats = new WebView(this);
                   stats.setLayoutParams(layoutParams);
           		stats.loadUrl(getResources().getString(R.string.stats_s));
        		stats.setWebChromeClient(new WebChromeClient());
                stats.addJavascriptInterface(new DemoJavaScriptInterface(), "stats");
                   WebSettings websetting = stats.getSettings();
                   websetting.setBuiltInZoomControls(false);
                   websetting.setJavaScriptEnabled(true);//OPEN JavaScript for WEB
                   stats.setVerticalScrollBarEnabled(false);
                   stats.setOnTouchListener(new View.OnTouchListener() {
                   	
       				public boolean onTouch(View v, MotionEvent event) {
       					return true;
       				}
       			});

       layoutParams = new RelativeLayout.LayoutParams(
       	     RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        	layoutParams.setMargins(50, 180, 0, 0);
        	layoutParams.height=480;
        	layoutParams.width=640;
        	mv = new MjpegView(this);
            mv.setLayoutParams(layoutParams);
            
            
            Logout = new Button(this);
            Logout.setText("Logout");
            Logout.setOnClickListener(new OnClickListener() {
            	 
    			public void onClick(View arg0) {
    				String response=null;
    				try {  
    					response = CustomHttpClient.executeHttpGet(getResources().getString(R.string.logout_s));
    				
    			}catch (Exception e) {  
    					  
    					System.out.println(e.toString()+response);
    					}
    				Intent intent = new Intent(Intent.ACTION_MAIN);
    				intent.addCategory(Intent.CATEGORY_HOME);
    				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    				startActivity(intent);
    			}
     
    		});
            layoutParams = new RelativeLayout.LayoutParams(
             	     RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(1200, 20, 0, 0);
            Logout.setLayoutParams(layoutParams);
            
            rl.addView(icon);
            rl.addView(stats);
            rl.addView(mv);
            rl.addView(Logout);
            setContentView(rl);
		try {
			mv.setSource(MjpegInputStream.read(getResources().getString(R.string.camera_s)));
			mv.showFps(true);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	//Javascript interface here
	final class DemoJavaScriptInterface {

        DemoJavaScriptInterface() {
        }
        /**
         * This is not called on the UI thread. Post a runnable to invoke
         * loadUrl on the UI thread.
         */
        public void clickOnAndroid() {
            mHandler.post(new Runnable() {
                public void run() {
                	stats.loadUrl("javascript:updateValue("+PD1+","+PD2+","+PD3+","+All+")");
                }
            });
            //return PD1;
        }  
    }
    
    final class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            //Log.d(LOG_TAG, message);
            result.confirm();
            return true;
        }
    }
	public void onPause() {
		super.onPause();
		mv.stopPlayback();
	}
}
