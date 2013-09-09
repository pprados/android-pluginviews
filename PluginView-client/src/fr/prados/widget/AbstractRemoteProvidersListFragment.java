package fr.prados.widget;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ListFragment;

/**
 * An abstract fragment to manage remote providers.
 * When the fragment is attached, a process start to bind all providers.
 * When the fragment is detached, all providers are unbounded.
 *  
 * @author pprados
 *
 * @param <T> The aidl interface
 */
public abstract class AbstractRemoteProvidersListFragment<T extends android.os.IInterface> 
extends ListFragment
implements BindProviders.CallBack<T>
{
	
	private BindProviders<T> mProviderBinder;
	private final Class<T> mParameterType;
	private final Intent mIntent;
	/**
	 * @param clazz The class of the class parameter T.
	 * @param providerIntent The intent to start all providers.
	 */
	public AbstractRemoteProvidersListFragment(Class<T> clazz,Intent providerIntent)
	{
		mParameterType=clazz;
		mIntent=providerIntent;
	}
	/**
	 * {@inheritDoc}
	 * Start to bind all providers.
	 */
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		// Connect to all providers
		mProviderBinder=new BindProviders<T>(activity,mIntent,mParameterType,this);
		mProviderBinder.open();
	}

	/**
	 * {@inheritDoc}
	 * Unbind all providers.
	 */
	@Override
	public void onDetach()
	{
		super.onDetach();
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

	/**
	 * Invoked when all providers are binded.
	 * It's a good place to invoker notifyDataSetChanged() in Adapter.
	 */
//	@Override
//	public void onProvidersBinded(List<T> providers)
//	{
//		
//	}
	
}
