package calplug.wop;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class ControlMode extends Activity {
	private WebView cameraView;
	private WebView monitorView;
	private WebView stats;
	private TextView switchText;
	private Handler mHandler = new Handler();
	private int PD1=0;
	private int PD2=0;
	private int PD3=0;
	private int All=0;
	private int VTV=42;
	private int VLaptop=55;
	private int VSTB=16;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Remove notification bar
		//
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.control);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		// Activate interface widgets
		// IP camera
		//
		cameraView = (WebView) findViewById(R.id.camera);
		cameraView.getSettings().setJavaScriptEnabled(true);
		cameraView.loadUrl(getResources().getString(R.string.camera_Img));
		cameraView.setWebChromeClient(new WebChromeClient());
		cameraView.addJavascriptInterface(new DemoJavaScriptInterface(), "stats");
		WebSettings settings = cameraView.getSettings();
		settings.setUseWideViewPort(true); 
	    settings.setLoadWithOverviewMode(true);
	    cameraView.setBackgroundColor(0x00000000);
	    
	    // Virtual environment
	    //
	    monitorView = (WebView) findViewById(R.id.monitor);
	    monitorView.getSettings().setJavaScriptEnabled(true);
	    monitorView.loadUrl(getResources().getString(R.string.monitor_s));
	    monitorView.setWebChromeClient(new WebChromeClient());
	    monitorView.addJavascriptInterface(new DemoJavaScriptInterface(), "stats");
		WebSettings monitorSettings = monitorView.getSettings(); 
		monitorSettings.setUseWideViewPort(true); 
		monitorSettings.setLoadWithOverviewMode(true);
		monitorView.setBackgroundColor(0x00000000);
	    
		// Meter
		//
		stats = (WebView) findViewById(R.id.stats);
		stats.getSettings().setJavaScriptEnabled(true);
		stats.loadUrl("file:///android_asset/pages/stats1.html");
		stats.setWebChromeClient(new WebChromeClient());
        stats.addJavascriptInterface(new DemoJavaScriptInterface(), "stats");
        stats.setBackgroundColor(0x00000000);
        
        // Import switches
        //
        final LinearLayout switchLayout1 = (LinearLayout) findViewById(R.id.VirtualSwitches);
        final LinearLayout switchLayout2 = (LinearLayout) findViewById(R.id.Switches);
        switchText = (TextView)findViewById(R.id.switchLabel);
        switchText.setText("Realtime View");
        Switch tv_switch = (Switch) findViewById(R.id.tv_switch);
        Switch vtv_switch = (Switch) findViewById(R.id.vtv_switch);
        Switch vstb_switch = (Switch) findViewById(R.id.vstb_switch);
        Switch vlaptop_switch = (Switch) findViewById(R.id.vlaptop_switch);
        Switch lamp_switch = (Switch) findViewById(R.id.lamp_switch);
        Switch speaker_switch = (Switch) findViewById(R.id.speaker_switch);
        //onCreateCheck(tv_switch, speaker_switch, lamp_switch);
        
        // Set webview visibility
        //
        monitorView.setVisibility(View.VISIBLE);
        stats.setVisibility(View.VISIBLE);
        cameraView.setVisibility(View.INVISIBLE);
        switchLayout1.setVisibility(View.VISIBLE);
        switchLayout2.setVisibility(View.VISIBLE);
		
        // Update meter every 5 seconds
        //
		Timer autoUpdate = new Timer();
        autoUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                    	String response1 = null;
                    	String response2 = null;
                    	String response3 = null;
        				try {  
        					// TV
        					//
        					response1 = CustomHttpClient.executeHttpGet(getResources().getString(R.string.getPower_base) + "q=2");
        					String res1=response1.toString();
        					res1= res1.replaceAll("\\s+","");  
        					PD1 = Integer.parseInt(res1);
        					
        					// Lamp
        					//
        					response2 = CustomHttpClient.executeHttpGet(getResources().getString(R.string.getPower_base) + "q=3");
        					String res2=response2.toString();
        					res2= res2.replaceAll("\\s+","");  
        					PD2 = Integer.parseInt(res2);
        					
        					// Speaker
        					//
        					response3 = CustomHttpClient.executeHttpGet(getResources().getString(R.string.getPower_base) + "q=16");
        					String res3=response3.toString();
        					res3= res3.replaceAll("\\s+","");  
        					PD3 = Integer.parseInt(res3);
        					
        					// All three devices
        					//
        					All = PD1+PD2+PD3;
        					} catch (Exception e) { 
        					System.out.println(e.toString());  
        					}
        				stats.loadUrl("javascript:updateValue("+PD1+","+PD2+","+PD3+","+All+")");
                    }
                });
            }
        }, 0, 5000);

        // Define buttons
		// Virtual TV Switch
        //
		vtv_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.v("VTV Switch State=", ""+isChecked);
				String res=null;
				if(isChecked){
					// turn virtual TV on
					//
					try{
						res = CustomHttpClient.executeHttpGet(getResources().getString(R.string.getPower_base) + "q=10");
						stats.loadUrl("javascript:setVTV("+VTV+")");
					}catch (Exception e){
						System.out.println(e.toString()+res);
					}
				}else{
					// turn virtual TV off
					//	
					try{
						stats.loadUrl("javascript:setVTV(0)");
						res = CustomHttpClient.executeHttpGet(getResources().getString(R.string.getPower_base) + "q=11");
					}catch (Exception e){
						System.out.println(e.toString());
					}
				}
			}       
		
		});  
		
		//Virtual STB Switch
		vstb_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		
			public void onCheckedChanged(CompoundButton buttonView,
                 boolean isChecked) {
              	 String res=null;
                   if(isChecked){
                	   //turn virtual STB on
                	   //
                	   try{
                		   stats.loadUrl("javascript:setVSTB("+VSTB+")");
                		   res = CustomHttpClient.executeHttpGet(getResources().getString(R.string.getPower_base) + "q=12");
                	   }catch (Exception e){
                		   System.out.println(e.toString());
                	   }
                   }else{
                	   //turn virtual STB off
                	   //
                	   try{
                		   stats.loadUrl("javascript:setVSTB(0)");
                		   res = CustomHttpClient.executeHttpGet(getResources().getString(R.string.getPower_base) + "q=13");
                	   }catch (Exception e){
                		   System.out.println(e.toString()+res);
                	   }
                	   //tvStateofToggleButton.setText("OFF");
                   }
			}
		});
		
		//Virtual laptop Switch
		//
		vlaptop_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView,
        		 boolean isChecked) {
				String res=null;
				if(isChecked){
					// turn virtual laptop on
					//
					try{
						stats.loadUrl("javascript:setVLaptop("+VLaptop+")");
						res = CustomHttpClient.executeHttpGet(getResources().getString(R.string.getPower_base) + "q=14");
					}catch (Exception e){
						System.out.println(e.toString());
					}
				}else{
					// turn virtual laptop off
					//
					try{
						stats.loadUrl("javascript:setVLaptop(0)");
						res = CustomHttpClient.executeHttpGet(getResources().getString(R.string.getPower_base) + "q=15");
					}catch (Exception e){
						System.out.println(e.toString()+res);
					}
					//tvStateofToggleButton.setText("OFF");
				}
				
			}
		});

		// TV switch
		//
		tv_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
        		 boolean isChecked) {
				
				String res=null;
				if(isChecked){
					// turn TV on
					//
					try{
						res = CustomHttpClient.executeHttpGet(getResources().getString(R.string.control_base) + "q=5");
					}catch (Exception e){
						System.out.println(e.toString());
					}
				}else{
					// turn TV off
					//
					try{
						res = CustomHttpClient.executeHttpGet(getResources().getString(R.string.control_base) + "q=6");
					}catch (Exception e){
						System.out.println(e.toString()+res);
					}
					//tvStateofToggleButton.setText("OFF");
				}
				
			}
		});
		
		// Lamp switch
		//
		lamp_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

         public void onCheckedChanged(CompoundButton buttonView,
        		 boolean isChecked) {
        	 
        	 String res=null;
        	 if(isChecked){
        		 //turn lamp on
        		 //
        		 try{
        			 res = CustomHttpClient.executeHttpGet(getResources().getString(R.string.control_base) + "q=1");
        			 System.out.println(res);
        		 }catch (Exception e){
        			 System.out.println(e.toString());
        		 }
        	 }else{
        		 //turn lamp off
        		 //
        		 try{
        			 res = CustomHttpClient.executeHttpGet(getResources().getString(R.string.control_base) + "q=2");
        			 System.out.println(res);
        		 }catch (Exception e){
        			 System.out.println(e.toString());
        		 }
        		 //tvStateofToggleButton.setText("OFF");
        	 }

         }
		});
        
		// speaker switch
		//
		speaker_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				
				String res=null;
				if(isChecked){
					//turn speaker on
					//
					try{
						res = CustomHttpClient.executeHttpGet(getResources().getString(R.string.control_base) + "q=3");
					}catch (Exception e){
						System.out.println(e.toString());
					}
				}else{
					//turn speaker off
					//
					try{
						res = CustomHttpClient.executeHttpGet(getResources().getString(R.string.control_base) + "q=4");
					}catch (Exception e){
						System.out.println(e.toString()+res);
					}
					//tvStateofToggleButton.setText("OFF");
				}

			}
		});
		
		// view switch
		//
		Switch viewSwitch = (Switch) findViewById(R.id.viewSwitch);
		viewSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

	         public void onCheckedChanged(CompoundButton buttonView,
	           boolean isChecked) {
	        	 
	             if(isChecked){
	            	 // virtual view
	            	 //
	            	 handler.removeCallbacks(runnable);
	            	 monitorView.setVisibility(View.INVISIBLE);
	            	 stats.setVisibility(View.INVISIBLE);
	            	 cameraView.setVisibility(View.VISIBLE);
	            	 switchLayout1.setVisibility(View.INVISIBLE);
	            	 switchLayout2.setVisibility(View.INVISIBLE);
	            	 switchText.setText("Virtual View");
                     handler.postDelayed(runnable, 1000);
	             }else{
	            	 // real view
	            	 //
	            	 monitorView.setVisibility(View.VISIBLE);
	            	 stats.setVisibility(View.VISIBLE);
	            	 cameraView.setVisibility(View.INVISIBLE);
	            	 switchLayout1.setVisibility(View.VISIBLE);
	            	 switchLayout2.setVisibility(View.VISIBLE);
	            	 handler.postDelayed(runnable,1000);
	            	 switchText.setText("Realtime View");
	             }
	         }
		});
		
	}
	
	// Check device status upon create
	private void onCreateCheck(Switch tvSwitch, Switch speakerSwitch, Switch lampSwitch){

    	// initial buttons
		//
    	String TVSta = null;
    	String laptopSta = null;
    	String speakerSta = null;
    	
    	// TV 90-94 watt
    	//
    	try {
    		TVSta = CustomHttpClient.executeHttpGet(getResources().getString(R.string.getPower_base) + "q=2");
    	} catch (Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}  
    	boolean tvStates = false;
    	String res1=TVSta.toString();
    	res1= res1.replaceAll("\\s+","");
    	int int_power1 = Integer.parseInt(res1);
    	if(int_power1 >= 60){
    		tvStates = true;
    	}
    	
    	// lamp 61 watt
    	//
    	try {
    		laptopSta = CustomHttpClient.executeHttpGet(getResources().getString(R.string.getPower_base) + "q=3");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	boolean laptopStates = false;
    	String res2=laptopSta.toString();
    	res2= res2.replaceAll("\\s+","");  
    	int int_power2 = Integer.parseInt(res2);
    	if(int_power2 >= 40){
    		laptopStates = true;
    	}
			
    	// speaker 10 watt
    	try {
    		speakerSta = CustomHttpClient.executeHttpGet(getResources().getString(R.string.getPower_base) + "q=16");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}  
    	boolean speakerStates = false;
    	String res3=speakerSta.toString();
    	res3= res3.replaceAll("\\s+","");  
    	int int_power3 = Integer.parseInt(res3);
    	if(int_power3 >= 6){
    		speakerStates = true;
    	}
    	
    	tvSwitch.setChecked(tvStates);
    	speakerSwitch.setChecked(speakerStates);
    	lampSwitch.setChecked(laptopStates);
	}
	
	// Javascript interface
	//
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
            result.confirm();
            return true;
        }
    }
    
    

private Handler handler = new Handler();

// refresh IP camera
//
private Runnable runnable = new Runnable() {
	public void run () {
		try{
			cameraView.loadUrl(getResources().getString(R.string.camera_Img));
		}catch (Exception e){
			e.printStackTrace();
		}
		handler.postDelayed(this, 1000);
	}
};

}