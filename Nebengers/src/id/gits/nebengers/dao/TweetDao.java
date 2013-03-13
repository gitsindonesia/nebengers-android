package id.gits.nebengers.dao;

public class TweetDao {

	
 	private String id;
	private String from_user_name;
	private String created_at;
	private String from_user;
	private String from_user_id_str;
	private String id_str;
	private String text;
	private String profile_image_url;
	

	public String getId() {
		return id;
	}

	public String getFromUserName() {
		return from_user_name;
	}
	
	public String getCreatedAt() {
		return created_at;
	}
	
	public String getFromUser() {
		return from_user;
	}
	
	public String getFromUserIdStr() {
		return from_user_id_str;
	}
	
	public String getIdStr() {
		return id_str;
	}
	
	public String getText() {
		return text;
	}
	
	public String getProfileImageUrl() {
		return profile_image_url;
	}
}