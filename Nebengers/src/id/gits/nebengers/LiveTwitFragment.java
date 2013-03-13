package id.gits.nebengers;

import id.gits.nebengers.dao.TweetDao;
import id.gits.nebengers.utils.Utils;
import id.gits.nebengers.utils.imageloader.ImageLoader;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullAndLoadListView.OnLoadMoreListener;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;
import com.google.myjson.Gson;

public class LiveTwitFragment extends SherlockListFragment {
	private List<TweetDao> listTweet = new ArrayList<TweetDao>();
	GetJsonStringTask tweetTask;
	RefreshJsonStringTask refreshTask;
	LoadMoreDataTask loadMoreData;
	TweetDao tweetDao;
	Dialog dialog;
	ProgressDialog progressDialog;
	private static final String FROM = "from";
	private static final String NAME = "name";
	private static final String TEXT = "text";
	private static final String URL = "url";
	private static final String DATE = "date";
	private static final String ID = "id";
	private static final String TWIT_URI = "http://search.twitter.com/search.json";
	public String mHashtag;
	SearchAPIDao searchAPIDao;
	MyListAdapter adapter;
	
	public ImageLoader imageLoader;

	public boolean checkInternetConnection() {
		ConnectivityManager conMgr = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMgr.getActiveNetworkInfo() != null
				&& conMgr.getActiveNetworkInfo().isAvailable()
				&& conMgr.getActiveNetworkInfo().isConnected()) {
			return true;
		} else { 
			return false;
		}
	}

	public String getmHashtag() {
		return mHashtag;
	}

	public static LiveTwitFragment newInstance(String hashtag) {
		LiveTwitFragment fragment = new LiveTwitFragment();
		fragment.mHashtag = hashtag.replace("#", "");

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.activity_tweet_view, null);
		initListView();
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		final String url = TWIT_URI + "?q=%23" + mHashtag + "&rpp=50";

		if (checkInternetConnection() == true) {
			tweetTask = new GetJsonStringTask();
			tweetTask.execute(url);
		} else {
			Toast.makeText(getActivity(),
					"No Internet Connection / Disconnected", Toast.LENGTH_SHORT)
					.show();
		}

		// to Long Click Listener
		/*
		 * mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
		 * 
		 * public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int
		 * arg2, long arg3) { Toast.makeText(getActivity(),
		 * "On long click listener", Toast.LENGTH_LONG).show(); return true; }
		 * 
		 * });
		 */

		((PullAndLoadListView) getListView())
		.setOnRefreshListener(new OnRefreshListener() {

			public void onRefresh() {
				// Do work to refresh the list here.
				String url_refresh =  url + searchAPIDao.getRefresh_url(); 
				refreshTask = new RefreshJsonStringTask();
				refreshTask.execute(url_refresh);
			}
		});
		
		((PullAndLoadListView) getListView())
		.setOnLoadMoreListener(new OnLoadMoreListener() {

			public void onLoadMore() {
				// Do the work to load more items at the end of list
				// here

				if(mHashtag.equals("ShareTaxi")){
					((PullAndLoadListView) getListView()).onLoadMoreComplete();
				}else{

				if(searchAPIDao != null){
					loadMoreData = new LoadMoreDataTask();
					String load_more_url;
					load_more_url = TWIT_URI + searchAPIDao.getNext_page();
					loadMoreData.execute(load_more_url);
				}

				}
			}
		});

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		int positions = position - 1;
		String l_from = listTweet.get(positions).getFromUser();
		String l_text = listTweet.get(positions).getText();
		String l_img_url = listTweet.get(positions).getProfileImageUrl();
		String l_date = listTweet.get(positions).getCreatedAt();
		String l_name = listTweet.get(positions).getFromUserName();
		String l_id = listTweet.get(positions).getId();

		Intent intent = new Intent(getActivity(), DetailFragment.class);
		intent.putExtra(FROM, l_from);
		intent.putExtra(NAME, l_name);
		intent.putExtra(TEXT, l_text);
		intent.putExtra(URL, l_img_url);
		intent.putExtra(DATE, l_date);
		intent.putExtra(ID, l_id);
		startActivity(intent);
	}

	public void clearData(String search){
		try {
			mHashtag = getmHashtag() + "%20" + URLEncoder.encode(search.trim(),"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String urls = TWIT_URI + "?q=%23" + mHashtag + "&rpp=50";
		
		tweetTask = new GetJsonStringTask();
		tweetTask.execute(urls);
		
	}
	
	private void initListView() {
		adapter = new MyListAdapter(getActivity(), listTweet);
		setListAdapter(adapter);

	}


	private class MyListAdapter extends BaseAdapter {
		List<TweetDao> lists = new ArrayList<TweetDao>();
		private LayoutInflater inflater;
		private ViewHolder holder;

		public MyListAdapter(Context context, List<TweetDao> list) {
			/*super(context, list, R.layout.row_tweet_list, new String[] {},
					new int[] {});*/
			super();
			this.inflater = LayoutInflater.from(context);
			this.lists = list;
			imageLoader = new ImageLoader(context);
		}

		@Override
		public int getCount() {
			return lists.size();
		}

		@Override
		public Object getItem(int position) {
			return lists.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View vi = convertView;

			if (convertView == null) {
				vi = getActivity().getLayoutInflater().inflate(
						R.layout.row_tweet_list, null);
				holder = new ViewHolder();
				holder.tvFrom = (TextView) vi.findViewById(R.id.tweet_from);
				holder.tvText = (TextView) vi.findViewById(R.id.tweet_text);
				holder.tvDate = (TextView) vi.findViewById(R.id.tweet_date);
				holder.image = (ImageView) vi.findViewById(R.id.tweet_image);
				vi.setTag(holder);
			} else
				holder = (ViewHolder) vi.getTag();

			holder.tvText.setText(Html.fromHtml(lists.get(position).getText()));
			holder.tvFrom.setText(lists.get(position).getFromUserName());

			Date d = new Date(lists.get(position).getCreatedAt());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			holder.tvDate.setText(Utils.DiffDateToStringFromTime(sdf.format(d)));

			// Category Image
			String urlImage = lists.get(position).getProfileImageUrl();
			holder.image.setTag(urlImage);
			
			imageLoader.DisplayImage(urlImage, getActivity(), holder.image);

			return vi;
		}

	}

	public static class ViewHolder {
		public TextView tvText;
		public TextView tvFrom;
		public TextView tvDate;
		public ImageView image;
	}

	private class GetJsonStringTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			
		}

		@Override
		protected String doInBackground(String... params) {
			int tryCount = 0;
			String jString = "";
			while (jString.equals("") && tryCount < 5) {

				jString = Utils.getJsonResult(params[0]);
				tryCount++;
			}
			return jString;

		}

		@Override
		protected void onPostExecute(String results) {
			try {
				listTweet.clear();

				parseJsonTweet(results);
				//initListView();
			} catch (Exception e) {
/*				Toast.makeText(getActivity(), "Error Load Tweet",
						Toast.LENGTH_LONG).show();*/
			}
		}
	}

	private class RefreshJsonStringTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... params) {
			int tryCount = 0;
			String jString = "";
			while (jString.equals("") && tryCount < 5) {

				jString = Utils.getJsonResult(params[0]);
				tryCount++;
			}
			return jString;

		}

		@Override
		protected void onPostExecute(String results) {
			try {
				parseJsonTweet(results);
				adapter.notifyDataSetChanged();


				//initListView();
			} catch (Exception e) {
				Toast.makeText(getActivity(), "Error Load Tweet",
						Toast.LENGTH_LONG).show();
			}
			((PullAndLoadListView) getListView()).onRefreshComplete();
		}

		protected void onCancelled() {
			((PullAndLoadListView) getListView()).onRefreshComplete();
		}

	}

	private class LoadMoreDataTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... params) {
			int tryCount = 0;
			String jString = "";
			while (jString.equals("") && tryCount < 5) {

				jString = Utils.getJsonResult(params[0]);
				tryCount++;
			}
			return jString;
		}

		@Override
		protected void onPostExecute(String results) {
			try {
				parseJsonTweet(results);
				//listTweet.add(tweetDao);
			} catch (Exception e) {
				Toast.makeText(getActivity(), "Error Load Tweet",
						Toast.LENGTH_LONG).show();
			}
			adapter.notifyDataSetChanged();
			((PullAndLoadListView) getListView()).onLoadMoreComplete();
		}

		protected void onCancelled() {
			((PullAndLoadListView) getListView()).onLoadMoreComplete();
		}
	}

	class SearchAPIDao {
		private String max_id_str;
		private String next_page;
		private String previous_page;
		private String query;
		private String page;
		private String results_per_page;
		private String refresh_url;

		private List<TweetDao> results;

		public String getMax_id_str() {
			return max_id_str;
		}

		public String getNext_page() {
			return next_page;
		}

		public String getPrevious_page() {
			return previous_page;
		}

		public String getQuery() {
			return query;
		}

		public String getPage() {
			return page;
		}

		public String getResults_per_page() {
			return results_per_page;
		}

		public String getRefresh_url() {
			return refresh_url;
		}

		public List<TweetDao> getResults() {
			return results;
		}

	}

	public void parseJsonTweet(String response) throws JSONException {
		Gson gson = new Gson();
		searchAPIDao = gson.fromJson(response, SearchAPIDao.class);
		//listTweet.addAll(searchAPIDao.getResults());

		int i=0;
		/*while(jika i masih lebih kecil dari ukuran array result){
			if(yg ngetweet nebengers){
				masukkan isi dari array result ke listTweet
			}
			i++;
		}*/

		while (i < searchAPIDao.getResults().size()) {
			if(searchAPIDao.getResults().get(i).getFromUser().equals("nebengers")){
				listTweet.add(searchAPIDao.getResults().get(i));
			}
			i++;
		} 

		adapter.notifyDataSetChanged();
	}

}
