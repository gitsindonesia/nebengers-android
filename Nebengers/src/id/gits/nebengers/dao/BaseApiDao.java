package id.gits.nebengers.dao;

public class BaseApiDao {
	public BaseApiDao() {
	}
	public BaseApiDao(String error) {
		this.message = error;
		this.status = false;
	}
	
	private String message;
	private boolean status = true;
	
	
	public boolean getStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	/**
	 * @return the error
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param error the error to set
	 */
	public void setMessage(String error) {
		this.message = error;
	}

}
