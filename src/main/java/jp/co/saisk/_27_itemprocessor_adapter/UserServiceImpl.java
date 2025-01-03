package jp.co.saisk._27_itemprocessor_adapter;

import lombok.Data;

@Data
public class UserServiceImpl {
	public User toUppeCase(User user) {
		user.setName(user.getName().toUpperCase());
		return user;
	}
}