package com.example.batchprocessing;

public record Person(String firstName, String lastName) {
    // 自动生成构造函数：
    // public Person(String firstName, String lastName) { ... }
    
    // 自动生成 getter 方法：
    // public String firstName() { return firstName; }
    // public String lastName() { return lastName; }
    
    // 自动生成 toString 方法：
    // public String toString() { return "Person[firstName=" + firstName + ", lastName=" + lastName + "]"; }
    
    // 自动生成 equals 和 hashCode 方法
}

//
//1. Record 类介绍
//record 是 Java 14 引入的一个新特性（正式发布是在 Java 16），它用于简化类的定义。记录类是一种特殊的类，其字段不可变，并且自动生成了构造函数、getter 方法、``toString()、equals()、hashCode()` 等方法。
//在传统的 Java 类中，你需要手动编写这些方法，而 record 类型会自动生成这些方法，避免了重复工作。
//2. 构造函数：
//record Person(String firstName, String lastName) 自动生成了一个构造函数，它有两个参数：firstName 和 lastName。这个构造函数会用于创建 Person 对象。
//3. 字段和方法：
//record 类型的每个参数都会成为这个类的字段。因此，在这个例子中，firstName 和 lastName 会成为 Person 类的不可变字段。
//自动生成的 getter 方法名分别是 firstName() 和 lastName()，你无需显式编写。
//4. 不可变性：
//记录类（record）的字段是 不可变 的。一旦一个 Person 对象被创建，它的 firstName 和 lastName 字段就不能被更改。这使得 record 非常适用于需要不可变对象的场景。
//5. 自动生成方法：
//toString()：Person 类的 toString() 方法会自动生成，并返回 Person[firstName=<firstName>, lastName=<lastName>]。
//equals() 和 hashCode()：record 类型会根据所有字段自动生成 equals() 和 hashCode() 方法，这对于比较对象是否相等或用作哈希集合中的键是非常有用的。