package jp.co.saisk._30_itemprocessor_customize;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class User {
	private Long id;
	private String name;
	private int age;
}