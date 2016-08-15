package com.sam_chordas.android.stockhawk.Widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.Utility;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.ui.DetailActivity;

/**
 * Created by dilpreet on 11/8/16.
 */
public class StockWidgetService extends RemoteViewsService {

	private String[]  columns={QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
			QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP};
	private final int INDEX_ID=0;
	private final int INDEX_SYMBOL=1;
	private final int INDEX_BIDPRICE=2;
	private final int INDEX_PERCENT_CHANGE=3;
	private final int INDEX_CHANGE=4;
	private final int INDEX_ISUP=5;


	@Override
	public RemoteViewsFactory onGetViewFactory(final Intent intent) {
		Log.d("mytag","remoteviewfactory");
		return new RemoteViewsFactory() {
			private Cursor cursorData=null;
			@Override
			public void onCreate() {

			}

			@Override
			public void onDataSetChanged() {
				if(cursorData!=null){
					cursorData.close();
				}

				final long identityToken = Binder.clearCallingIdentity();
				cursorData=getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,columns,QuoteColumns.ISCURRENT + " = ?",
						new String[]{"1"},null);
				Binder.restoreCallingIdentity(identityToken);
			}

			@Override
			public void onDestroy() {
				if(cursorData!=null){
					cursorData.close();
					cursorData=null;
				}
			}

			@Override
			public int getCount() {
				return cursorData==null?0:cursorData.getCount();
			}

			@Override
			public RemoteViews getViewAt(int position) {
				if (position == AdapterView.INVALID_POSITION ||
						cursorData == null || !cursorData.moveToPosition(position)) {
					return null;
				}
				RemoteViews remoteViews=new RemoteViews(getPackageName(),R.layout.list_item_quote);


				String symbols,bidPrice;
				symbols=cursorData.getString(INDEX_SYMBOL);
				bidPrice=cursorData.getString(INDEX_BIDPRICE);
				int isUp=cursorData.getInt(INDEX_ISUP);
				remoteViews.setTextViewText(R.id.stock_symbol,symbols);
				remoteViews.setTextViewText(R.id.bid_price,bidPrice);

				if (isUp == 1){

						remoteViews.setInt(R.id.change,"setBackgroundResource",R.drawable.percent_change_pill_green);

				} else{
					remoteViews.setInt(R.id.change,"setBackgroundResource",R.drawable.percent_change_pill_red);
				}



					remoteViews.setTextViewText(R.id.change,cursorData.getString(INDEX_PERCENT_CHANGE));


				Intent detailIntent=new Intent(getApplicationContext(), DetailActivity.class);
				detailIntent.putExtra(getString(R.string.intent_symbol),symbols);
				remoteViews.setOnClickFillInIntent(R.id.stock_item_layout,detailIntent);


//				if(isUp==1)
//					remoteViews.setImageViewResource(R.id.);
				return remoteViews;
			}

			@Override
			public RemoteViews getLoadingView() {
				return new RemoteViews(getPackageName(), R.layout.list_item_quote);
			}

			@Override
			public int getViewTypeCount() {
				return 1;
			}

			@Override
			public long getItemId(int position) {
				if(cursorData.moveToPosition(position))
					return cursorData.getLong(INDEX_ID);
				return position;
			}

			@Override
			public boolean hasStableIds() {
				return true;
			}
		};
	}
}
