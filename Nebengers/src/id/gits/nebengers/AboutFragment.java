package id.gits.nebengers;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockFragment;

public class AboutFragment extends SherlockFragment {
	public String mHashtag;

	WebView web_view;
	public String getmHashtag() {
		return mHashtag;
	}

	public static AboutFragment newInstance(String hashtag) {
		AboutFragment fragment = new AboutFragment();
		fragment.mHashtag = hashtag;

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.about_fragment, null);

		web_view = (WebView)v.findViewById(R.id.html_view);
		web_view.setBackgroundColor(Color.TRANSPARENT);

		WebSettings settings = web_view.getSettings();
		settings.setDefaultTextEncodingName("UTF-8");

		if(getmHashtag().equals("About Apps")){
			web_view.loadUrl("file:///android_asset/about.html");
		}else if(getmHashtag().equals("About GITS")){
			web_view.loadUrl("file:///android_asset/aboutgits.html");
		}else{
			web_view.loadUrl("file:///android_asset/disclaimer.html");
		}

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

}
