
package id.gits.nebengers.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;


public class Utils
{
	public final static int RC_SETTING_LOCATION = 99;

	public static void CopyStream(InputStream is, OutputStream os)
	{
		final int buffer_size = 1024;
		try
		{
			byte[] bytes = new byte[buffer_size];
			for (;;)
			{
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1) break;
				os.write(bytes, 0, count);
			}
		}
		catch (Exception ex)
		{
		}
	}

	/**
	 * Invoke "search" action, triggering a default search.
	 */
	public static void goSearch(Activity activity)
	{
		activity.startSearch(null, false, Bundle.EMPTY, false);
	}


	public static HashMap<String, String> jsonToCheck(JSONObject jObj, HashMap<String, String> map)
	{
		HashMap<String, String> temp = map;
		try
		{
			temp.put("poiID", Html.fromHtml(jObj.getString("poiID")).toString());
			temp.put("loginTime", Html.fromHtml(jObj.getString("loginTime")).toString());
			temp.put("poiName", Html.fromHtml(jObj.getString("poiName")).toString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return temp;
	}

	public static String getJsonResult(String address)
	{
		StringBuilder result = new StringBuilder();
		result.append("");
		try
		{
			URL url = new URL(address);
			System.setProperty("http.keepAlive", "false");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setUseCaches(false);
			conn.setConnectTimeout(30000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.connect();
			BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = r.readLine()) != null)
			{
				result.append(line);
			}
			r.close();
		}
		catch (Exception e)
		{
			Log.e("TT", e.getMessage());
		}
		return result.toString();
	}

	public static Drawable getImageFromUrl(Context ctx, String address)
	{
		try
		{
			URL url = new URL(address);
			InputStream is = url.openStream();
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static String capitalize(String s) {
		if (s.length() == 0) return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
	
	public static String DiffDateToStringFromTime(String strDate) {
		try {
			DateFormat formatter;
			Date date;
			formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			date = (Date) formatter.parse(strDate);
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			cal.setTime(date);
			cal.setTimeZone(TimeZone.getDefault());
			return Utils.DiffDateToString(cal.getTime(), new Date());
		} catch (ParseException e) {
			return "";
		}
	}

	public static String DiffDateToString(Date start, Date end) {
		long diffInSeconds = (end.getTime() - start.getTime()) / 1000;
		long diff[] = new long[] { 0, 0, 0, 0, 0, 0 };
		/* sec */diff[5] = (diffInSeconds >= 60 ? diffInSeconds % 60
				: diffInSeconds);
		/* min */diff[4] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60
				: diffInSeconds;
		/* hours */diff[3] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24
				: diffInSeconds;
		/* days */diff[2] = (diffInSeconds = (diffInSeconds / 24));
		/* month */diff[1] = (diffInSeconds = (5 * diffInSeconds / 152));// rata2
		// bulan
		/* years */diff[0] = (diffInSeconds = (diffInSeconds / 12));
		if (diff[0] > 0) {
			return String.format("%d year%s ago", diff[0], diff[0] > 1 ? "s"
					: "");
		} else if (diff[1] > 0) {
			return String.format("%d month%s ago", diff[1], diff[1] > 1 ? "s"
					: "");
		} else if (diff[2] > 0) {
			return String.format("%d day%s ago", diff[2], diff[2] > 1 ? "s"
					: "");
		} else if (diff[3] > 0) {
			return String.format("%d hour%s ago", diff[3], diff[3] > 1 ? "s"
					: "");
		} else if (diff[4] > 0) {
			return String.format("%d minute%s ago", diff[4], diff[4] > 1 ? "s"
					: "");
		} else if (diff[5] > 0) {
			return String.format("%d second%s ago", diff[5], diff[5] > 1 ? "s"
					: "");
		} else {
			return "now";
		}
	}
}
