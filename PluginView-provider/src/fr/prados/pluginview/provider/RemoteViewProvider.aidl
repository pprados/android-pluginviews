package fr.prados.pluginview.provider;

interface RemoteViewProvider
{
		int getSize(in Bundle bundle);
		android.widget.RemoteViews getLoadingView(in Bundle bundle);
}