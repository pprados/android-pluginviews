package fr.prados.pluginview.provider2;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.TextView;

public class Provider2Activity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Cool
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) 
		    getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		setContentView(R.layout.activity_main);
		int i=getIntent().getIntExtra("id", 1);
		TextView msg=(TextView)findViewById(R.id.msg);
		msg.setText(msg.getText()+" item #"+i);
		
	}

//	private void updateTitle()
//	{
//		try
//		{
//			PackageManager pm=getPackageManager();
//			ApplicationInfo appInfo=pm.getApplicationInfo(getCallingPackage(), 0);
//			Resources resources= pm.getResourcesForApplication(appInfo);
//			String title=null;
//			if (appInfo!=null)
//			{
//				if (appInfo.labelRes!=0)
//					title=resources.getString(appInfo.labelRes);
//				else
//					title=appInfo.name;
//			}
//			if (title!=null) setTitle(title);
//		}
//		catch (NameNotFoundException e)
//		{
//			// Ignore
//		}
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(
			R.menu.main, menu);
		return true;
	}
	@Override
	protected void onPause()
	{
		super.onPause();
		overridePendingTransition(0, 0);
	}
}
