package com.sam_chordas.android.stockhawk;

import android.util.Log;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dilpreet on 9/8/16.
 */
public class Utility {
	public static String parseDate(String time){
		String date="";
		DateFormat parseDate=new SimpleDateFormat("yyyyMMdd");
		DateFormat requiredFormat=new SimpleDateFormat("dd MMM,yy");
		try{
			date=requiredFormat.format(parseDate.parse(time));
		}catch (ParseException e){
			Log.d("mytag",e.toString());
		}
		return date;
	}
}
