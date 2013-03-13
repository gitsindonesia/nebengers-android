package id.gits.nebengers;

import id.gits.nebengers.twitter.TwitterApp;
import id.gits.nebengers.twitter.TwitterApp.TwDialogListener;
import id.gits.nebengers.utils.Constants;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class TweetActivity extends SherlockActivity {

	public static final String PAGE_POS = "page_pos";
	private String username = "";
	private TwitterApp mTwitter;
	private RadioGroup radioHashGroup;
	private RadioButton radioHashButton, cari, beri, sharing;
	TextView dlStat, txtLimit, txtTerm;
	EditText Tujuan, Jam, Term;
	private int pagePosition, limit;
	String tujuan, jam, term, Hashtag, Hash;
	private boolean postToTwitter = true;

	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, 1, 0, "Tweet")
		.setIcon(R.drawable.ab_accept)
		.setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
				| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case 1:

			if(limit < 0){

				Toast.makeText(TweetActivity.this, "your text too much", Toast.LENGTH_LONG).show();

			} else{


				String status = dlStat.getText().toString();

				if (status.equals(""))

					postReview(status);


				if (checkInternetConnection() == true) {
					if (postToTwitter)
						postToTwitter(status);

				} else {
					Toast.makeText(TweetActivity.this,
							"No Internet Connection / Disconnected", Toast.LENGTH_SHORT)
							.show();
				}
			}
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
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);

		setContentView(R.layout.dialog_update);

		mTwitter = new TwitterApp(this, Constants.TWITTER_CONSUMER_KEY,
				Constants.TWITTER_SECRET_KEY);

		mTwitter.setListener(mTwLoginDialogListener);

		if (mTwitter.hasAccessToken()) {
			postToTwitter = true;
			username = mTwitter.getUsername();

		}

		Intent intent = getIntent();
		pagePosition = intent.getIntExtra(PAGE_POS, 0);

		dlStat = (TextView) findViewById(R.id.inp_stat);
		Tujuan = (EditText) findViewById(R.id.inp_tujuan);
		Jam = (EditText) findViewById(R.id.inp_jam);
		Term = (EditText) findViewById(R.id.inp_term);

		txtTerm = (TextView) findViewById(R.id.txt_term);
		txtLimit = (TextView) findViewById(R.id.tv_Limit);

		radioHashGroup = (RadioGroup) findViewById(R.id.radioHash);

		cari = (RadioButton) findViewById(R.id.radioCari);
		beri = (RadioButton) findViewById(R.id.radioBeri);
		sharing = (RadioButton) findViewById(R.id.radioSharing);

		if (pagePosition == 0) {
			cari.setChecked(true);
			beri.setChecked(false);
			sharing.setChecked(false);
			Hash = "#CariTebengan";
			txtTerm.setText("Ketentuan");
			Term.setHint("cth : Sharing Makanan, Sharing Cerita");
		} else if (pagePosition == 1) {
			cari.setChecked(false);
			beri.setChecked(true);
			sharing.setChecked(false);
			Hash = "#BeriTebengan";
			txtTerm.setText("Sisa Seat | Ketentuan");
			Term.setHint("cth : 3 org | Sharing bensin");
		} else {
			cari.setChecked(false);
			beri.setChecked(false);
			Hash = "#ShareTaxi";
			sharing.setChecked(true);
			txtTerm.setText("Sisa Seat | Ketentuan");
			Term.setHint("cth : 3 org | Sharing argo");
		}

		radioHashGroup
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				radioHashButton = (RadioButton) findViewById(checkedId);

				Hash = radioHashButton.getText().toString();
				if (Hash.equals("#CariTebengan")) {
					txtTerm.setText("Ketentuan");
					Term.setHint("cth : Sharing Makanan, Sharing Cerita");
				} else if (Hash.equals("#BeriTebengan")) {
					txtTerm.setText("Sisa Seat | Ketentuan");
					Term.setHint("cth : 3 org | Sharing bensin");
				} else {
					txtTerm.setText("Sisa Seat | Ketentuan");
					Term.setHint("cth : 3 org | Sharing argo");
				}

				updateTxtTweet();

			}
		});

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

		Tujuan.addTextChangedListener(watcher);
		Jam.addTextChangedListener(watcher);
		Term.addTextChangedListener(watcher);


	}

	private void updateTxtTweet() {
		tujuan = Tujuan.getText().toString();
		jam = Jam.getText().toString();
		term = Term.getText().toString();

		String strTujuan = "";
		String strJam = "";
		String strTerm = "";

		if ((tujuan != null) && (!tujuan.equals(""))) {
			strTujuan = tujuan + " | ";
		}

		if ((jam != null) && (!jam.equals(""))) {
			strJam = jam + " | ";
		}

		if ((term != null) && (!term.equals(""))) {
			strTerm = term + " | ";
		}

		dlStat.setText(strTujuan + strJam + strTerm + Hash + " @nebengers");

		txtLimit.setText(String.valueOf(140 - dlStat.getText().length()));
		limit = Integer.parseInt(txtLimit.getText().toString());		
		/*		if(Limit < 0){
			dlTweet.setEnabled(false);
			//Toast.makeText(TweetActivity.this, "your text too much", Toast.LENGTH_LONG).show();

		} else{
			dlTweet.setEnabled(true);
		}
		 */
	}

	private void postReview(String status) {
		// post to server

	}

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

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String text = (msg.what == 0) ? "Posted to Twitter"
					: "Post to Twitter failed";

			Toast.makeText(TweetActivity.this, text, Toast.LENGTH_SHORT)
			.show();

			startActivity(new Intent(TweetActivity.this, MainTabFragment.class));
		}
	};

	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {
		@Override
		public void onComplete(String value) {
			username = mTwitter.getUsername();
			username = (username.equals("")) ? "No Name" : username;

			postToTwitter = true;

			Toast.makeText(TweetActivity.this,
					"Connected to Twitter as " + username, Toast.LENGTH_LONG)
					.show();
		}

		@Override
		public void onError(String value) {
			postToTwitter = false;

			Toast.makeText(TweetActivity.this, "Twitter connection failed",
					Toast.LENGTH_LONG).show();
		}
	};

}
