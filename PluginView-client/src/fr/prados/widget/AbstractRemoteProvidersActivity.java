package fr.prados.widget;

import java.util.List;

import android.app.Activity;
import android.content.Intent;

/**
 * Base for activity with remote providers.
 * 
 * @author pprados
 *
 * @param <T>
 */
public abstract class AbstractRemoteProvidersActivity<T extends android.os.IInterface> extends Activity
implements BindProviders.CallBack<T>
{
	private BindProviders<T> mProviderBinder;
	private final Class<T> mParameterType;
	private final Intent mIntent;

	/**
	 * @param clazz The class of the class parameter T.
	 * @param providerIntent The intent to start all providers.
	 */
	public AbstractRemoteProvidersActivity(Class<T> clazz,Intent providerIntent)
	{
		mParameterType=clazz;
		mIntent=providerIntent;
	}
	/**
	 * {@inheritDoc}
	 * Start to bind all providers.
	 */
	@Override
	public void onResume()
	{
		super.onResume();
		// Connect to all providers
		mProviderBinder=new BindProviders<T>(this,mIntent,mParameterType,this);
		mProviderBinder.open();
	}

	/**
	 * {@inheritDoc}
	 * Unbind all providers.
	 */
	@Override
	public void onPause()
	{
		super.onPause();
		// Deconnect all providers
		mProviderBinder.close();
		mProviderBinder=null;
	}

	/**
	 * @return the currents providers.
	 */
	protected List<T> getProviders()
	{
		return mProviderBinder.getProviders();
	}
	
}

