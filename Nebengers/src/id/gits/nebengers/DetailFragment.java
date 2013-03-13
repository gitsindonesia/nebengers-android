package id.gits.nebengers;

import id.gits.nebengers.twitter.TwitterApp;
import id.gits.nebengers.twitter.TwitterApp.TwDialogListener;
import id.gits.nebengers.utils.Constants;
import id.gits.nebengers.utils.Utils;
import id.gits.nebengers.utils.imageloader.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

@SuppressLint("HandlerLeak")
public class DetailFragment extends SherlockActivity implements OnClickListener {

	public static final String FROM = "from";
	public static final String TEXT = "text";
	public static final String URL = "url";
	public static final String DATE = "date";
	private static final String NAME = "name";
	public static final String ID = "id";

	Intent in;
	public ImageLoader imageLoader;
	private boolean postToTwitter = false;
	private String username = "";

	TextView detText, detFrom, detDate, detName, txtLimit;
	private TwitterApp mTwitter;
	Dialog dialog, dialogs;
	EditText dlStat;
	ImageView detImageProfile;
	String det_from, det_text, det_url, det_date, det_id, det_name;
	Button dlTweet;
	private final static int TWEETMODE_RETWEET = 1;
	private final static int TWEETMODE_REPLY = 2;
	
	int tweetMode;
	private boolean followUsers = false;

	private CheckBox dokterdroidCheckBox;
	private CheckBox nebengersCheckBox;

	String stat;

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return true;
	}

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

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_detail);

		detFrom = (TextView) findViewById(R.id.det_from);
		detName = (TextView) findViewById(R.id.det_name);
		detText = (TextView) findViewById(R.id.det_text);
		detDate = (TextView) findViewById(R.id.det_date);
		detImageProfile = (ImageView) findViewById(R.id.det_image);

		in = getIntent();
		det_from = in.getStringExtra(FROM);
		det_name = in.getStringExtra(NAME);
		det_text = in.getStringExtra(TEXT);
		det_url = in.getStringExtra(URL);
		det_date = in.getStringExtra(DATE);
		det_id = in.getStringExtra(ID);

		detFrom.setText("@" + det_from);
		detName.setText(det_name);
		detText.setText(Html.fromHtml(det_text));

		Date d = new Date(det_date);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		detDate.setText(Utils.DiffDateToStringFromTime(sdf.format(d)));

		imageLoader = new ImageLoader(this);
		imageLoader.DisplayImage(det_url, this, detImageProfile);

		mTwitter = new TwitterApp(this, Constants.TWITTER_CONSUMER_KEY,
				Constants.TWITTER_SECRET_KEY);

		mTwitter.setListener(mTwLoginDialogListener);

		if (mTwitter.hasAccessToken()) {
			postToTwitter = true;
			username = mTwitter.getUsername();

		}

		findViewById(R.id.det_retweet).setOnClickListener(this);
		findViewById(R.id.det_cancel).setOnClickListener(this);
		findViewById(R.id.det_reply).setOnClickListener(this);

		setTitle(det_from);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.det_retweet:
			tweetMode = TWEETMODE_RETWEET;
			if (mTwitter.hasAccessToken()) {
				postToTwitter = true;
				showDialogRetweet();
			} else {
				postToTwitter = false;
				mTwitter.authorize();
			}

			break;

		case R.id.det_reply:
			tweetMode = TWEETMODE_REPLY;
			if (mTwitter.hasAccessToken()) {
				postToTwitter = true;
				showDialogReply();
			} else {
				postToTwitter = false;
				mTwitter.authorize();
			}

			break;
		case R.id.det_cancel:

			if (mTwitter.hasAccessToken()) {
				postToTwitter = true;
				tweetMode = TWEETMODE_RETWEET;
				showDialogReply();
			} else {
				postToTwitter = false;
				mTwitter.authorize();
			}

			break;

		default:
			break;
		}
	}

	private boolean showDialogRetweet() {
		dialogs = new Dialog(this, R.style.ThemeWithCorners);
		dialogs.setCanceledOnTouchOutside(false);
		dialogs.getWindow();
		dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogs.setContentView(R.layout.dialog_retweet);

		TextView title = (TextView) dialogs.findViewById(R.id.txt_retweet);
		title.setText(det_text);

		Button dlReTweet = (Button) dialogs.findViewById(R.id.dl_tweet_ret);

		dlReTweet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mTwitter.hasAccessToken()) {
					postToTwitter = true;

					String status = det_text;

					long statusId;
					statusId = Long.parseLong(det_id);

					if (status.equals(""))
						return;

					retweetReview(status);

					if (checkInternetConnection() == true) {
						if (postToTwitter)
							retweetToTwitter(statusId);

					} else {
						Toast.makeText(DetailFragment.this,
								"No Internet Connection / Disconnected",
								Toast.LENGTH_SHORT).show();
					}

				} else {
					postToTwitter = false;
					mTwitter.authorize();
				}
				dialogs.cancel();
			}
		});

		Button dlEdit = (Button) dialogs.findViewById(R.id.dl_cancels_ret);

		dlEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogs.cancel();
			}
		});

		dialogs.show();
		return false;
	}

	public boolean showDialogReply() {
		dialog = new Dialog(this, R.style.ThemeWithCorners);
		dialog.setCanceledOnTouchOutside(false);
		dialog.getWindow();
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_reply);

		TextView title = (TextView) dialog.findViewById(R.id.txt_dlLabel_det);
		title.setText("Reply to (" + det_from + ")");

		dlStat = (EditText) dialog.findViewById(R.id.inp_stat);

		if (tweetMode == TWEETMODE_REPLY) {
			String user = "";
			Pattern p = Pattern.compile("@([A-Za-z0-9_]+)");
			// TODO matcher
			Matcher matcher = p.matcher(det_text);
			while (matcher.find()) {
				user = user + matcher.group() + " ";
			}
			if(!user.toLowerCase().contains(det_from.toLowerCase()))
				user = user + "@" + det_from + " "; 
			dlStat.setText(user);

			int position = dlStat.length();
			Editable etext = dlStat.getText();
			Selection.setSelection(etext, position);

		} else {
			dlStat.setText(" " + "RT" + " @" + det_from + " " + det_text);
		}

		txtLimit = (TextView) dialog.findViewById(R.id.tv_Limit);

		dlTweet = (Button) dialog.findViewById(R.id.dl_tweet_det);

		updateTxtTweet();

		TextWatcher watcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				updateTxtTweet();

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		};

		dlStat.addTextChangedListener(watcher);

		dlTweet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mTwitter.hasAccessToken()) {
					postToTwitter = true;
					String status = dlStat.getText().toString();

					long statusId;
					statusId = Long.parseLong(det_id);

					if (status.equals(""))
						return;

					postReview(status);

					if (checkInternetConnection() == true) {
						if (postToTwitter)
							postToTwitter(status, statusId);

					} else {
						Toast.makeText(DetailFragment.this,
								"No Internet Connection / Disconnected",
								Toast.LENGTH_SHORT).show();
					}

				} else {
					postToTwitter = false;
					if (checkInternetConnection() == true) {
						mTwitter.authorize();

					} else {
						Toast.makeText(DetailFragment.this,
								"No Internet Connection / Disconnected",
								Toast.LENGTH_SHORT).show();
					}
				}
				dialog.cancel();

			}
		});

		Button dlCancel = (Button) dialog.findViewById(R.id.dl_cancel_det);

		dlCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});

		dialog.show();
		return false;
	}

	private void postReview(String status) {
		// post to server

		// Toast.makeText(this, "Review posted", Toast.LENGTH_SHORT).show();
	}

	private void retweetReview(String status) {
		// post to server

		// Toast.makeText(this, "Status retweeted", Toast.LENGTH_SHORT).show();
	}

	private void postToTwitter(final String status, final long statusId) {
		new Thread() {
			@Override
			public void run() {
				int what = 0;

				try {
					mTwitter.replyStatus(status, statusId);
				} catch (Exception e) {
					what = 1;
				}

				mHandler.sendMessage(mHandler.obtainMessage(what));
			}
		}.start();
	}

	private void retweetToTwitter(final long statusId) {
		new Thread() {
			@Override
			public void run() {
				int what = 0;

				try {
					mTwitter.retweetStatus(statusId);
					// what = 1;
				} catch (Exception e) {
					Log.e("TWIT", statusId + "");
					e.printStackTrace();
					what = 1;
				}

				mHandler.sendMessage(mHandler.obtainMessage(what));
			}
		}.start();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String text = (msg.what == 0) ? "Posted to Twitter"
					: "Post to Twitter failed";

			Toast.makeText(DetailFragment.this, text, Toast.LENGTH_SHORT)
					.show();
		}
	};

	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {
		@Override
		public void onComplete(String value) {
			username = mTwitter.getUsername();
			username = (username.equals("")) ? "No Name" : username;

			postToTwitter = true;

			Toast.makeText(DetailFragment.this,
					"Connected to Twitter as " + username, Toast.LENGTH_LONG)
					.show();

			showDialogFollow();
			// cek setelah dari retweet atau review

		}

		@Override
		public void onError(String value) {
			postToTwitter = false;

			Toast.makeText(DetailFragment.this, "Twitter connection failed",
					Toast.LENGTH_LONG).show();
		}
	};

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
						Toast.makeText(DetailFragment.this,
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
						Toast.makeText(DetailFragment.this,
								"No Internet Connection / Disconnected",
								Toast.LENGTH_SHORT).show();
					}

				}

				dialog.cancel();
				if (tweetMode == TWEETMODE_REPLY) {
					showDialogReply();
				} else {
					showDialogRetweet();
				}

			}
		});

		Button dlCancel = (Button) dialog.findViewById(R.id.dl_cancel_follow);

		dlCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
				if (tweetMode == TWEETMODE_REPLY) {
					showDialogReply();
				} else {
					showDialogRetweet();
				}
			}
		});

		dialog.show();
		return false;
	}

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
			 * Toast.makeText(DetailFragment.this, text, Toast.LENGTH_SHORT)
			 * .show();
			 */
		}
	};

	private void followReview(String status) {
		// post to server

		// Toast.makeText(this, "Follow", Toast.LENGTH_SHORT).show();
	}

	private void updateTxtTweet() {
		stat = dlStat.getText().toString();

		// dlStat.setText(stat);

		txtLimit.setText(String.valueOf(140 - dlStat.getText().length()));

		int Limit = Integer.parseInt(txtLimit.getText().toString());

		if (Limit < 0) {
			dlTweet.setEnabled(false);

		} else {
			dlTweet.setEnabled(true);
		}

	}
}