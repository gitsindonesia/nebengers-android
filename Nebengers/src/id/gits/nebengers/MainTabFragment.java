package id.gits.nebengers;

import id.gits.nebengers.twitter.TwitterApp;
import id.gits.nebengers.twitter.TwitterApp.TwDialogListener;
import id.gits.nebengers.twitter.TwitterSession;
import id.gits.nebengers.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;

public class MainTabFragment extends SherlockFragmentActivity {
	private static final String[] CONTENT = new String[] { "#CariTebengan",
		"#BeriTebengan", "#ShareTaxi" };
	List<LiveTwitFragment> listFragment = new ArrayList<LiveTwitFragment>();
	private TwitterApp mTwitter;
	private LiveTwitFragment mLiveTwit;
	private TwitterSession mTwitterSession;
	private CheckBox dokterdroidCheckBox;
	private CheckBox nebengersCheckBox;
	private CheckBox mTwitterBtn;
	private boolean postToTwitter = false;
	private boolean followUsers = false;
	Dialog dialog;
	EditText Tujuan, Jam, Term, txtSearch;
	String tujuan, jam, term, Hashtag, Hash, search;
	Button dlTweet, dlCancel;
	private String username = "";
	private final static int TWEETMODE_SOS = 1;
	private final static int TWEETMODE_TWEET = 2;
	int tweetMode;
	/*
	 * private RadioGroup radioHashGroup; private RadioButton radioHashButton,
	 * cari, beri, sharing;
	 */
	TextView dlStat, txtLimit, txtTerm;
	private int pagePosition;
	FragmentPagerAdapter adapter;
	PageIndicator mIndicator;
	public static final String PAGE_POS = "page_pos";

	ViewPager pager;

	public boolean checkInternetConnection() {
		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMgr.getActiveNetworkInfo() != null
				&& conMgr.getActiveNetworkInfo().isAvailable()
				&& conMgr.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar

		/*
		 * menu.add(0, 0, 0, "Search") .setIcon(R.drawable.ic_ab_search)
		 * .setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM |
		 * MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		 */

		// Menu SOS
		/*
		 * menu.add(0, 1, 0, "SOS") .setIcon(R.drawable.alerts_stat)
		 * .setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM |
		 * MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		 */

//		menu.add(0, 0, 0, "About")
//		.setActionView(R.layout.actionbar_search)
//		.setShowAsAction(
//				MenuItem.SHOW_AS_ACTION_ALWAYS);

		menu.add(0, 1, 0, "About")
		.setIcon(R.drawable.alerts_stat)
		.setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
				| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		menu.add(0, 2, 0, "Tweet")
		.setIcon(R.drawable.content_edit)
		.setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
				| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		// menu SOS
		/*
		 * case 1: if (mTwitter.hasAccessToken()) { postToTwitter = true;
		 * 
		 * String status = "@nebengers " + "SOS";
		 * 
		 * postReview(status);
		 * 
		 * tweetMode = TWEETMODE_SOS;
		 * 
		 * if (checkInternetConnection() == true) { if (postToTwitter)
		 * postToTwitter(status);
		 * 
		 * } else { Toast.makeText(MainTabFragment.this,
		 * "No Internet Connection / Disconnected", Toast.LENGTH_SHORT) .show();
		 * }
		 * 
		 * } else { tweetMode = TWEETMODE_SOS; postToTwitter = false; if
		 * (checkInternetConnection() == true) { mTwitter.authorize();
		 * 
		 * } else { Toast.makeText(MainTabFragment.this,
		 * "No Internet Connection / Disconnected", Toast.LENGTH_SHORT) .show();
		 * } } break;
		 */

		case 1:
			Intent intent = new Intent(MainTabFragment.this,
					AboutActivity.class);
			startActivity(intent);
			break;
		case 2:
			if (mTwitter.hasAccessToken()) {
				postToTwitter = true;

				tweetMode = TWEETMODE_TWEET;

				intent = new Intent(MainTabFragment.this, TweetActivity.class);
				intent.putExtra(PAGE_POS, pagePosition);
				startActivity(intent);

			} else {
				postToTwitter = false;
				tweetMode = TWEETMODE_TWEET;
				if (checkInternetConnection() == true) {
					mTwitter.authorize();

				} else {
					Toast.makeText(MainTabFragment.this,
							"No Internet Connection / Disconnected",
							Toast.LENGTH_SHORT).show();
				}
			}
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		View v = getLayoutInflater().inflate(R.layout.actionbar, null);
		getSupportActionBar().setCustomView(v);
		getSupportActionBar().setDisplayShowCustomEnabled(true);

		setContentView(R.layout.simple_tabs);

		txtSearch = (EditText) v.findViewById(R.id.txt_search);

		search = txtSearch.getText().toString();

		adapter = new LiveTwitAdapter(getSupportFragmentManager());

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);
		pager.setOffscreenPageLimit(2);

		txtSearch.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if ((event.getAction() == KeyEvent.ACTION_UP)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {

					search = txtSearch.getText().toString();

					Toast.makeText(MainTabFragment.this, search,
							Toast.LENGTH_SHORT).show();

					((LiveTwitFragment) adapter.getItem(0)).clearData(search);
					((LiveTwitFragment) adapter.getItem(1)).clearData(search);
					((LiveTwitFragment) adapter.getItem(2)).clearData(search);
					
					InputMethodManager imm = (InputMethodManager)getSystemService(
						      Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(txtSearch.getWindowToken(), 0);

					return true;
				}

				return false;
			}
		});

		TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);

		indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				pagePosition = position;
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		mLiveTwit = new LiveTwitFragment();

		mTwitterSession = new TwitterSession(this);

		mTwitter = new TwitterApp(this, Constants.TWITTER_CONSUMER_KEY,
				Constants.TWITTER_SECRET_KEY);

		mTwitter.setListener(mTwLoginDialogListener);

		if (mTwitter.hasAccessToken()) {
			postToTwitter = true;
			username = mTwitter.getUsername();

		}

	}

	class LiveTwitAdapter extends FragmentPagerAdapter {
		public LiveTwitAdapter(FragmentManager fm) {
			super(fm);

			if (!search.equals("")) {

				listFragment.add(LiveTwitFragment.newInstance(CONTENT[0]
						+ "%20" + search));
				listFragment.add(LiveTwitFragment.newInstance(CONTENT[1]
						+ "%20" + search));
				listFragment.add(LiveTwitFragment.newInstance(CONTENT[2]
						+ "%20" + search));

			} else {

				listFragment.add(LiveTwitFragment.newInstance(CONTENT[0]));
				listFragment.add(LiveTwitFragment.newInstance(CONTENT[1]));
				listFragment.add(LiveTwitFragment.newInstance(CONTENT[2]));
			}
		}

		@Override
		public Fragment getItem(int position) {
			return listFragment.get(position);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position];
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}
	}

	/*
	 * public boolean showDialog() { dialog = new Dialog(this,
	 * R.style.ThemeWithCorners); dialog.setCanceledOnTouchOutside(false);
	 * dialog.getWindow(); dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	 * dialog.setContentView(R.layout.dialog_update);
	 * 
	 * dlStat = (TextView) dialog.findViewById(R.id.inp_stat); Tujuan =
	 * (EditText) dialog.findViewById(R.id.inp_tujuan); Jam = (EditText)
	 * dialog.findViewById(R.id.inp_jam); Term = (EditText)
	 * dialog.findViewById(R.id.inp_term);
	 * 
	 * txtTerm = (TextView) dialog.findViewById(R.id.txt_term); txtLimit =
	 * (TextView) dialog.findViewById(R.id.tv_Limit);
	 * 
	 * radioHashGroup = (RadioGroup) dialog.findViewById(R.id.radioHash);
	 * 
	 * cari = (RadioButton) dialog.findViewById(R.id.radioCari); beri =
	 * (RadioButton) dialog.findViewById(R.id.radioBeri); sharing =
	 * (RadioButton) dialog.findViewById(R.id.radioSharing);
	 * 
	 * if (pagePosition == 0) { cari.setChecked(true); beri.setChecked(false);
	 * sharing.setChecked(false); Hash = "#CariTebengan";
	 * txtTerm.setText("Term Condition"); Term.setHint("cth : Tidak Merokok"); }
	 * else if (pagePosition == 1) { cari.setChecked(false);
	 * beri.setChecked(true); sharing.setChecked(false); Hash = "#BeriTebengan";
	 * txtTerm.setText("cth : Sisa Seat"); Term.setHint("cth : Sisa seat 3"); }
	 * else { cari.setChecked(false); beri.setChecked(false); Hash =
	 * "#ShareTaxi"; sharing.setChecked(true); }
	 * 
	 * radioHashGroup .setOnCheckedChangeListener(new OnCheckedChangeListener()
	 * {
	 * 
	 * @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
	 * 
	 * radioHashButton = (RadioButton) dialog .findViewById(checkedId);
	 * 
	 * Hash = radioHashButton.getText().toString();
	 * 
	 * updateTxtTweet();
	 * 
	 * // Toast.makeText(MainTabFragment.this, Hash, //
	 * Toast.LENGTH_LONG).show(); } });
	 * 
	 * TextWatcher watcher = new TextWatcher() {
	 * 
	 * @Override public void onTextChanged(CharSequence s, int start, int
	 * before, int count) { updateTxtTweet(); }
	 * 
	 * @Override public void beforeTextChanged(CharSequence s, int start, int
	 * count, int after) {
	 * 
	 * }
	 * 
	 * @Override public void afterTextChanged(Editable s) {
	 * 
	 * } };
	 * 
	 * Tujuan.addTextChangedListener(watcher);
	 * Jam.addTextChangedListener(watcher);
	 * Term.addTextChangedListener(watcher);
	 * 
	 * Button dlTweet = (Button) dialog.findViewById(R.id.dl_tweet);
	 * 
	 * dlTweet.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) {
	 * 
	 * String status = dlStat.getText().toString();
	 * 
	 * if (status.equals("")) return;
	 * 
	 * postReview(status);
	 * 
	 * if (postToTwitter) postToTwitter(status);
	 * 
	 * dialog.cancel();
	 * 
	 * } });
	 * 
	 * Button dlCancel = (Button) dialog.findViewById(R.id.dl_cancel);
	 * 
	 * dlCancel.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { dialog.cancel(); } });
	 * 
	 * dialog.show(); return false; }
	 */

	public boolean showDialogFollow() {
		dialog = new Dialog(this, R.style.ThemeWithCorners);
		dialog.setCanceledOnTouchOutside(false);
		dialog.getWindow();
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_follow);

		dokterdroidCheckBox = (CheckBox) dialog.findViewById(R.id.chk_dokter);
		nebengersCheckBox = (CheckBox) dialog.findViewById(R.id.chk_nebengers);

		Button dlFollow = (Button) dialog.findViewById(R.id.dl_follow);

		dlFollow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (dokterdroidCheckBox.isChecked()) {
					String screenName = "dokterdroid";

					if (screenName.equals(""))
						return;

					followReview(screenName);

					followUsers = true;

					if (checkInternetConnection() == true) {
						if (followUsers)
							followUsers(screenName);

					} else {
						Toast.makeText(MainTabFragment.this,
								"No Internet Connection / Disconnected",
								Toast.LENGTH_SHORT).show();
					}

				}

				if (nebengersCheckBox.isChecked()) {
					String screenName = "nebengers";

					if (screenName.equals(""))
						return;

					followReview(screenName);

					followUsers = true;

					if (checkInternetConnection() == true) {

						if (followUsers)
							followUsers(screenName);
					} else {
						Toast.makeText(MainTabFragment.this,
								"No Internet Connection / Disconnected",
								Toast.LENGTH_SHORT).show();
					}
				}

				dialog.cancel();

				if (tweetMode == TWEETMODE_TWEET) {
					Intent intent = new Intent(MainTabFragment.this,
							TweetActivity.class);
					intent.putExtra(PAGE_POS, pagePosition);
					startActivity(intent);
				} else {
					postToTwitter = true;

					String status = "@nebengers " + "SOS";

					postReview(status);

					tweetMode = TWEETMODE_SOS;
					if (checkInternetConnection() == true) {
						if (postToTwitter)
							postToTwitter(status);
					} else {
						Toast.makeText(MainTabFragment.this,
								"No Internet Connection / Disconnected",
								Toast.LENGTH_SHORT).show();
					}

				}

			}
		});

		Button dlCancel = (Button) dialog.findViewById(R.id.dl_cancel_follow);

		dlCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();

				if (tweetMode == TWEETMODE_TWEET) {
					Intent intent = new Intent(MainTabFragment.this,
							TweetActivity.class);
					intent.putExtra(PAGE_POS, pagePosition);
					startActivity(intent);

				} else {
					postToTwitter = true;

					String status = "@nebengers " + "SOS";

					postReview(status);

					tweetMode = TWEETMODE_SOS;

					if (checkInternetConnection() == true) {
						if (postToTwitter)
							postToTwitter(status);

					} else {
						Toast.makeText(MainTabFragment.this,
								"No Internet Connection / Disconnected",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

		dialog.show();
		return false;
	}

	/*
	 * private void updateTxtTweet() { tujuan = Tujuan.getText().toString(); jam
	 * = Jam.getText().toString(); term = Term.getText().toString();
	 * 
	 * String strTujuan = ""; String strJam = ""; String strTerm = "";
	 * 
	 * if ((tujuan != null) && (!tujuan.equals(""))) { strTujuan = tujuan +
	 * " | "; }
	 * 
	 * if ((jam != null) && (!jam.equals(""))) { strJam = jam + " | "; }
	 * 
	 * if ((term != null) && (!term.equals(""))) { strTerm = term + " | "; }
	 * 
	 * if ((tujuan == null) && (jam == null) && (term == null)) {
	 * 
	 * } dlStat.setText(strTujuan + strJam + strTerm + Hash + " @nebengers");
	 * 
	 * txtLimit.setText(String.valueOf(140 - dlStat.getText().length()));
	 * 
	 * }
	 */

	private void postToTwitter(final String status) {
		new Thread() {
			@Override
			public void run() {
				int what = 0;

				try {
					mTwitter.updateStatus(status);
				} catch (Exception e) {
					what = 1;
				}

				mHandler.sendMessage(mHandler.obtainMessage(what));
			}
		}.start();
	}

	private void postReview(String status) {
		// post to server

		// Toast.makeText(this, "Review posted", Toast.LENGTH_SHORT).show();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String text = (msg.what == 0) ? "Posted to Twitter"
					: "Post to Twitter failed";

			Toast.makeText(MainTabFragment.this, text, Toast.LENGTH_SHORT)
			.show();
		}
	};

	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {
		@Override
		public void onComplete(String value) {
			username = mTwitter.getUsername();
			username = (username.equals("")) ? "No Name" : username;

			postToTwitter = true;

			Toast.makeText(MainTabFragment.this,
					"Connected to Twitter as " + username, Toast.LENGTH_LONG)
					.show();
			showDialogFollow();
		}

		@Override
		public void onError(String value) {
			mTwitterBtn.setChecked(false);

			Toast.makeText(MainTabFragment.this, "Twitter connection failed",
					Toast.LENGTH_LONG).show();
		}
	};

	// Thread Follow
	private void followUsers(final String screenName) {
		new Thread() {
			@Override
			public void run() {
				int what = 0;

				try {
					mTwitter.followUser(screenName);
				} catch (Exception e) {
					what = 1;
				}

				mHandlers.sendMessage(mHandler.obtainMessage(what));
			}
		}.start();
	}

	private Handler mHandlers = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			/*
			 * String text = (msg.what == 0) ? "Follow success" :
			 * "Follow failed";
			 * 
			 * Toast.makeText(MainTabFragment.this, text, Toast.LENGTH_SHORT)
			 * .show();
			 */
		}
	};

	private void followReview(String status) {
		// post to server

		// Toast.makeText(this, "Follow", Toast.LENGTH_SHORT).show();
	}

}
