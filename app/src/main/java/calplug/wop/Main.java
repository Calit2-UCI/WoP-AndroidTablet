package calplug.wop;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import calplug.wop.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
 
public class Main extends Activity {
  
    private EditText username;  
    private EditText password;  

	private Button login;
	private Button register;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		//Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//set the activity's view
		setContentView(R.layout.login);
		
		username = (EditText)findViewById(R.id.editText1);  
        password = (EditText)findViewById(R.id.editText2);
		username.getBackground().setAlpha(142);
		password.getBackground().setAlpha(142);
		username.setTextColor(Color.DKGRAY);
		password.setTextColor(Color.DKGRAY);
		
		addListenerOnButton();
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
	}
 
	private void addListenerOnButton() {
 
		final Context context = this;

		
		login = (Button) findViewById(R.id.button2);
		
		register = (Button) findViewById(R.id.button1);
		 
		login.setTextColor(Color.WHITE);
		login.getBackground().setColorFilter(new LightingColorFilter(0x006600, 0x006600));
		register.setTextColor(Color.WHITE);
		register.getBackground().setColorFilter(new LightingColorFilter(0x006600, 0x006600));
		
		
		register.setOnClickListener(new OnClickListener() {
 
			public void onClick(View arg0) {
			    Intent intent = new Intent(context, Register.class);
                startActivity(intent);
			}
 
		});
 
		login.setOnClickListener(new OnClickListener() {
 
			public void onClick(View arg0) {
				
				
				ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();  
				postParameters.add(new BasicNameValuePair("username", username.getText().toString()));  
				postParameters.add(new BasicNameValuePair("password", password.getText().toString())); 
				String response = null; 
				
				try {  
					response = CustomHttpClient.executeHttpPost(getResources().getString(R.string.login_s),
                            postParameters);
					String res=response.toString();
					
					res= res.replaceAll("\\s+","");
					
					//log into control mode
					//
					if(res.equals("1"))
					{  
		                System.out.println("You are in control mode.");  
		                Intent intent = new Intent(context, ControlMode.class);
		                startActivity(intent);
					}
					//invalid user
					if(res.equals("0"))  
					{  
		                 Toast.makeText(Main.this,"Invalid user. Please login again." , Toast.LENGTH_SHORT).show();  
					}
									
				
			}catch (Exception e) {
					Toast.makeText(Main.this, "Server cannot be reached. Please check connection",
							Toast.LENGTH_SHORT).show();
					System.out.println("APPError: " + e.toString());

				}
			}
 		});
 
	}
	public void onPause() {
		super.onPause();
	}

 
}


