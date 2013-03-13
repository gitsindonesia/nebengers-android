package id.gits.nebengers.dao;

import java.util.List;

public class SearchAPIDao {
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