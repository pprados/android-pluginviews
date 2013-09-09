package fr.prados.pluginview.provider1;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.widget.RemoteViews;

public class RemoteViewProviderImpl extends fr.prados.pluginview.provider.RemoteViewProvider.Stub
{

	private Context mContext;
	
	RemoteViewProviderImpl(Context context)
	{
		mContext=context;
	}
	@Override
	public RemoteViews getLoadingView(Bundle bundle) throws RemoteException
	{
		final RemoteViews remoteView=new RemoteViews(RemoteViewProviderImpl.class.getPackage().getName(),R.layout.provider1);
		int i=1;
		if (bundle!=null) i=bundle.getInt("id", 1);
		remoteView.setCharSequence(R.id.text, "setText", "Provider1 #"+i);
		final Intent intent=new Intent(mContext,Provider1Activity.class);
		intent.putExtra("id", i);
		final PendingIntent pendingIntent=PendingIntent.getActivity(mContext, 
			i, //request code must be different for each
			intent, 0);
		remoteView.setOnClickPendingIntent(R.id.provider1, pendingIntent);

//		remoteView.setOnClickPendingIntent(R.id.button1, pendingIntent);
		return remoteView;
	}

	@Override
	public int getSize(Bundle bundle) throws RemoteException
	{
		return 3; // arbitrary number of items
	}

}
