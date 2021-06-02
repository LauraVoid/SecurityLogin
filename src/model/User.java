package model;

public class User {
	
	private String name;
	
	private boolean isAdmin;
	
	private String password;
	
	private String lastLogin;
	
	
	public User(String name, boolean isAdmin, String password, String lastLogin) {
		super();
		this.name = name;
		this.isAdmin = isAdmin;
		this.password = password;
		this.lastLogin = lastLogin;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isAdmin() {
		return isAdmin;
	}
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}
	

}
