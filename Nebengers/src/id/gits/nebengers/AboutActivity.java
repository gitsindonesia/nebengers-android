package id.gits.nebengers;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;


public class AboutActivity extends SherlockFragmentActivity {
	private static final String[] CONTENT = new String[] { "About Apps",
		"About GITS", "Disclaimer" };
	List<AboutFragment> aboutFragment = new ArrayList<AboutFragment>();
	private int pagePosition;
	FragmentPagerAdapter adapter;
	PageIndicator mIndicator;
	public static final String PAGE_POS = "page_pos";

	ViewPager pager;
	
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.simple_tabs);

		adapter = new AboutCatAdapter(getSupportFragmentManager());

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);

		indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// Toast.makeText(MainTabFragment.this, "Changed to page " +
				// position, Toast.LENGTH_SHORT).show();
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

	}

	class AboutCatAdapter extends FragmentPagerAdapter {
		public AboutCatAdapter(FragmentManager fm) {
			super(fm);
			aboutFragment.add(AboutFragment.newInstance(CONTENT[0]));
			aboutFragment.add(AboutFragment.newInstance(CONTENT[1]));
			aboutFragment.add(AboutFragment.newInstance(CONTENT[2]));
		}

		@Override
		public Fragment getItem(int position) {
			return aboutFragment.get(position);
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

}
