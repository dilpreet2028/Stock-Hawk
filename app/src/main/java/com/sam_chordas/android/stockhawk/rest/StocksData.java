package com.sam_chordas.android.stockhawk.rest;

import android.util.Log;

import com.sam_chordas.android.stockhawk.Utility;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by dilpreet on 8/8/16.
 */
public class StocksData {

	private ValueLineSeries valueLineSeries;
	private OkHttpClient okHttpClient;
	private String symbol,interval;
	private StockCallBack stockCallBack;
	public StocksData(String symbol, String interval, final StockCallBack stockCallBack) {
		this.symbol=symbol;
		this.interval=interval;
		okHttpClient=new OkHttpClient();
		valueLineSeries=new ValueLineSeries();
		this.stockCallBack=stockCallBack;

		new Thread(new Runnable() {
			@Override
			public void run() {
				stockCallBack.onRequestStart();
				provideData();
			}
		}).start();

	}



	public void provideData(){
		Request request=new Request.Builder()
						.url("http://chartapi.finance.yahoo.com/instrument/1.0/"+symbol+"/chartdata;type=quote;range="+interval+"/json")
						.build();

		Response response=null;
		try{
			response=okHttpClient.newCall(request).execute();
			getData(response.body().string());
		}catch (IOException e){
			e.printStackTrace();
		}


	}

	private void getData(String data) {
		String jsonStr=data.split("finance_charts_json_callback\\(")[1].split("\\)")[0];
		JSONObject jsonObject=null;
		try{
			valueLineSeries.setColor(0xFF56B7F1);
			jsonObject=new JSONObject(jsonStr);
			JSONArray array=jsonObject.getJSONArray("series");
			for(int i=0;i<array.length();i++){
				String date= Utility.parseDate(array.getJSONObject(i).getString("Date"));
				valueLineSeries.addPoint(new ValueLinePoint(date,array.getJSONObject(i).getInt("close")));
			}
			stockCallBack.onRequestEnd(valueLineSeries);

		}catch (JSONException e){
			Log.d("mytag",e.toString());
		}
	}

	public interface StockCallBack{
		public void onRequestStart();
		public void onRequestEnd(ValueLineSeries valueLineSeries);
	}
}
