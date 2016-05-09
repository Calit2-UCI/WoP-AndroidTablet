package calplug.wop;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import calplug.wop.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
 
public class Register extends Activity {
    

	private Button savecontent;
	private Button clearcontent;
	private EditText firstName;
	private EditText lastName;
	private EditText emailAdd;
	private EditText password;
	private EditText verifiedPassword;
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar
		//
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Remove notification bar
		//
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.register);
		
		firstName = (EditText) findViewById(R.id.firstName);
		lastName =(EditText) findViewById(R.id.lastName);
		emailAdd = (EditText) findViewById(R.id.email);
		password =(EditText) findViewById(R.id.inputPassword);
		verifiedPassword = (EditText) findViewById(R.id.changePassword);

		listener();
		
	}
 
	public void listener() {
		final Context context = this;
		savecontent =(Button) findViewById(R.id.savechage);
		savecontent.setTextColor(Color.WHITE);
		savecontent.getBackground().setColorFilter(new LightingColorFilter(0x006600, 0x006600));
		
		savecontent.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0){
				if(password.getText().toString().equals(verifiedPassword.getText().toString())){
				ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();  
				postParameters.add(new BasicNameValuePair("user_firstName", firstName.getText().toString()));  
				postParameters.add(new BasicNameValuePair("user_lastName", lastName.getText().toString()));
				postParameters.add(new BasicNameValuePair("user_emailAdd", emailAdd.getText().toString()));  
				postParameters.add(new BasicNameValuePair("user_password", password.getText().toString()));
				
				String emailCheck = emailAdd.getText().toString();
				if(!emailCheck.contains("@")){
					Toast.makeText(Register.this, "Please input valid email address", Toast.LENGTH_SHORT).show();
				}
				else{
			
				String response = null; 
				
				try {  
					Toast.makeText(Register.this,password.getText().toString() , Toast.LENGTH_SHORT).show();
					
					response = CustomHttpClient.executeHttpPost(getResources().getString(R.string.register_s), postParameters);
					String res=response.toString();  
					res= res.replaceAll("\\s+","");
					if(res!=null)
					{  
						Toast.makeText(Register.this,"Request submitted." , Toast.LENGTH_SHORT).show();
						// go back to login(main) screen after register
                        //
						System.out.println("APPInfo: Request submitted.");
						Intent intent = new Intent(context, Main.class);
						startActivity(intent); }
					
					if(res.equals("0"))  
					{  
						Toast.makeText(Register.this,"Invalid user. Please login again." , Toast.LENGTH_SHORT).show();  
					}  	
					
				} catch (Exception e) {
                    Toast.makeText(Register.this, "Server cannot be reached. Please check connection",
                            Toast.LENGTH_SHORT).show();
					System.out.println("APPError: " + e.toString());

				}
				}
				}
				else{
					Toast.makeText(Register.this,"Fail to verify your password." , Toast.LENGTH_SHORT).show();
				}
			}
		});

		clearcontent = (Button) findViewById(R.id.clear);
		clearcontent.setTextColor(Color.WHITE);
		clearcontent.getBackground().setColorFilter(new LightingColorFilter(Color.RED, 0xFFAA0000));

		emailAdd = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.inputPassword);
		emailAdd.getBackground().setAlpha(142);
		password.getBackground().setAlpha(142);
		
		// Clear all fields
		//
		clearcontent.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0){
				firstName.setText("");
				lastName.setText("");
				emailAdd.setText("");
				password.setText("");
				verifiedPassword.setText("");
				
			}
		});
	}
	

	public void onPause() {
		super.onPause();
	}
}

