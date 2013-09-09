package fr.prados.pluginview.client;

import static fr.prados.pluginview.provider.Providers.sIntentProvider;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RemoteViews;
import android.widget.TextView;
import fr.prados.pluginview.provider.RemoteViewProvider;
import fr.prados.widget.AbstractRemoteProvidersListFragment;

/**
 * A sample fragment to show a list with widget generated by the providers.
 * 
 * @author pprados
 *
 */
public class RemoteListFragment 
extends AbstractRemoteProvidersListFragment<RemoteViewProvider>
{
	// Adapter to manage one items from each provider
	class SimpleRemoteViewAdapter extends BaseAdapter
	{

		private Context mContext;
		
		public SimpleRemoteViewAdapter(Context context)
		{
			mContext=context;
		}
		@Override
		public int getCount()
		{
			return getProviders().size();
		}

		@Override
		public Object getItem(int pos)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int pos)
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int pos, View old, ViewGroup parent)
		{
			try
			{
				final Bundle bundle=new Bundle();
				final RemoteViews remoteViews = getProviders().get(pos).getLoadingView(bundle);
				return remoteViews.apply(mContext, parent);
			}
			catch (RemoteException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null; // Return empty view ?
			}
		}
		
	}

	// Adapter to manage many items from each provider
	class ComplexRemoteViewAdapter extends BaseAdapter
	{
		private Context mContext;
		private ArrayList<RemoteViewProvider> mItems=new ArrayList<RemoteViewProvider>();
		private Bundle mBundle=new Bundle();
		

		@Override
		public void notifyDataSetChanged()
		{
			super.notifyDataSetChanged();
			updateData();
		}

		private void updateData()
		{
			for (RemoteViewProvider provider:getProviders())
			{
				try
				{
					final int size = provider.getSize(mBundle);
					for (int i=0;i<size;++i)
						mItems.add(provider);
				}
				catch (RemoteException e)
				{
					// Ignore. Bug with the provider
				}
			}
		}
		public ComplexRemoteViewAdapter(Context context)
		{
			mContext=context;
			updateData();
		}
		@Override
		public int getCount()
		{
			return mItems.size();
		}

		@Override
		public Object getItem(int arg0)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0)
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int pos, View old, ViewGroup parent)
		{
			try
			{
				final Bundle bundle=new Bundle();
				bundle.putInt("id", pos);
				final RemoteViews remoteViews = mItems.get(pos).getLoadingView(bundle);
				// View with theme for Android 4.1.2
				final View view=remoteViews.apply(mContext,parent);
// Astuce qui ne fonctionne plus :-(				
//				final View view=remoteViews.apply(new ContextWrapper(mContext)
//				{
//					public Context createPackageContext(String packageName, int flags) throws NameNotFoundException
//					{
//						Context context= new ContextWrapper(getBaseContext().createPackageContext(packageName, flags))
//						{
//							// Delegate the theme to the context for 4.2. Bug with 4.3 :-(
//							@Override
//							public Theme getTheme()
//							{
//								return mContext.getTheme();
//							}
//						};
//						return context;
//					}
//					
//				}, parent);
				return view;
			}
			catch (RemoteException e)
			{
				e.printStackTrace();
				TextView tv=new TextView(parent.getContext());
				tv.setText("ERROR"); // FIXME
				return tv;
			}
		}
	}
	
	private BaseAdapter mAdapter;

	public RemoteListFragment()
	{
		super(RemoteViewProvider.class,sIntentProvider);
	}
	@Override
	public void onProvidersBinded(List<RemoteViewProvider> providers)
	{
		if (mAdapter!=null)
			mAdapter.notifyDataSetChanged();
	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
//    	mAdapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1,new String[] { "one", "two", "three" });
//    	mAdapter=new SimpleRemoteViewAdapter(getActivity());
    	mAdapter=new ComplexRemoteViewAdapter(getActivity());
    	
        /** Setting the list adapter for the ListFragment */
        setListAdapter(mAdapter);
 
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
