package fr.prados.pluginview.provider1;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ProviderService extends Service
{

	@Override
	public IBinder onBind(Intent intent)
	{
		return new RemoteViewProviderImpl(this);
	}

}
