package jp.co.saisk._27_itemprocessor_adapter;

public class UserServiceImpl {
	public User toUppeCase(User user) {
		user.setName(user.getName().toUpperCase());
		return user;
	}
}