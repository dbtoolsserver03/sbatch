package jp.co.saisk._29_itemprocessor_composite;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class User {
	private Long id;
	@NotBlank(message = "用户名不能为null或空串")
	private String name;
	private int age;
}