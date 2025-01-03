package jp.co.saisk._29_itemprocessor_composite;

public class UserServiceImpl {
	public Person toUppeCase(Person user) {
		user.setName(user.getName().toUpperCase());
		return user;
	}
}