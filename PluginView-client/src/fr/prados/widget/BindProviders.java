package fr.prados.widget;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.util.Log;

/**
 * Bind and manage many providers with the same Intent.
 * Maintain the list of remote provider. The callback was invoked when all connection are done.
 * 
 * The caller MUST invoke clear() to unbind all providers.
 * 
 * @author pprados
 *
 * @param <T> The aidl interface
 */
public class BindProviders<T extends android.os.IInterface> 
{
	private static final String TAG = "Providers";
	private static final boolean D=false;
	private static final boolean E=false;

	/**
	 * A callback called when all providers are connected.
	 * @author pprados
	 *
	 * @param <T>
	 */
	public interface CallBack<T>
	{
		/**
		 * Call-back invoked when all providers are connected.
		 * @param providers
		 */
		void onProvidersBinded(List<T> providers);
	}
	
	interface ProviderServiceConnection<T> extends ServiceConnection
	{
		T getProvider();
	}

	private final Context mContext;
	private final Intent mIntent;
	private final Class<T> mParameterType;
	private final CallBack<T> mCallback;
	// Providers and connections
	private List<ProviderServiceConnection<T>> mProvidersConnections=new ArrayList<ProviderServiceConnection<T>>();
	// Only providers
	private List<T> mProviders=new ArrayList<T>();
	private int mMax;
	
	public BindProviders(Context context,Intent intent,Class<T> parameterType,CallBack<T> callback)
	{
		mContext=context;
		mIntent=intent;
		mParameterType=parameterType;
		mCallback=callback;
	}
	
	public void open()
	{
		final List<ProviderServiceConnection<T>> providersConnection=Collections.synchronizedList(new ArrayList<ProviderServiceConnection<T>>());
		final List<ResolveInfo> providersInfo = mContext.getPackageManager().queryIntentServices(mIntent, 0);
		for (ResolveInfo info : providersInfo)
		{
			final ServiceInfo serviceInfo = info.serviceInfo;
			if (serviceInfo == null)
				continue;
			if (D) Log.d(TAG, "Found plugin " + info);
			final Intent intent=new Intent(mIntent);
			// Select specific package
			intent.setClassName(serviceInfo.packageName, serviceInfo.name);

			ProviderServiceConnection<T> conn=new ProviderServiceConnection<T>()
			{
				private T mProvider;

				public T getProvider()
				{
					return mProvider;
				}
				@SuppressWarnings("unchecked")
				@Override
				public void onServiceConnected(ComponentName className, IBinder service)
				{
					try
					{
						if (D) Log.d(TAG, "onServiceConnected "+className);

						// mProvider = (T)Invoke T.Stub.asInterface(service)
						Class<?> clazz=Class.forName(mParameterType.getName()+"$Stub");
						Method asInterface=clazz.getMethod("asInterface", android.os.IBinder.class);
						mProvider = (T)asInterface.invoke(null, service);
						if (--mMax==0)
						{
							onFinishToBind();
						}
					}
					catch (Exception e)
					{
						if (E) Log.e(TAG,"Ignore provider. Invalide type "+mParameterType.getName()+" in "+getClass(),e);
					}
				}

				@Override
				public void onServiceDisconnected(ComponentName className)
				{
					if (D) Log.d(TAG, "onServiceDisconnected");
					providersConnection.remove(this);
					onProviderDisconnected(mProvider);
					mProvider = null;
				}
			};
			try
			{
				if (D) Log.d(TAG, "bind ...");
				boolean rc = mContext.bindService(
					intent, conn, Context.BIND_AUTO_CREATE);
				if (rc) providersConnection.add(conn); // Warning, it's may be not connected !
				else if (E) Log.e(TAG, "Impossible to initialize drivers list.");
			}
			catch (SecurityException e)
			{
				Log.wtf(TAG,"Impossible to bind the provider \""+info.serviceInfo.name+"\" in application \""+info.serviceInfo.applicationInfo.packageName+"\". "+
						"Add <uses-permission/> in the application \""+mContext.getApplicationInfo().packageName+"\" "+
						"and/or add exported=\"true\" if the applications do not share the same process.");
			}
		}
		mMax=providersConnection.size(); // Wait size onServiceConnected
		mProvidersConnections=providersConnection;
	}
	private void onFinishToBind()
	{
		if (D) Log.d(TAG,"onFinishToBind with "+mProvidersConnections.size()+" items");
		// Create the provider list
		for (ProviderServiceConnection<T> conn:mProvidersConnections)
		{
			if (conn.getProvider()!=null)
				mProviders.add(conn.getProvider());
		}
		if (mCallback!=null)
			mCallback.onProvidersBinded(mProviders);
	}
	private void onProviderDisconnected(T provider)
	{
		mProviders.remove(provider);
	}
	/**
	 * Close connection with all plugins.
	 */
	public void close()
	{
		if (D) Log.d(TAG,"Clear "+mProvidersConnections.size()+" binded interface");
		for (ProviderServiceConnection<T> conn:mProvidersConnections)
			mContext.unbindService(conn);
		mProvidersConnections.clear();
		mProviders.clear();
		mMax=0;
	}
	/**
	 * Return current list of providers.
	 * 
	 * @return List of providers.
	 */
	public List<T> getProviders()
	{
		return mProviders;
	}
}
