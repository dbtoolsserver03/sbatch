package jp.co.saisk._22_itemreader_flat_mapper;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String name;
    private int age;
    private String address;   //地址
}