package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

    // 使用 SLF4J 创建日志记录器，用于输出日志
    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

    /**
     * 实现 ItemProcessor 接口的 process 方法，处理每个 Person 对象
     * @param person 输入的 Person 对象
     * @return 处理后的 Person 对象
     */
    @Override
    public Person process(final Person person) {
        // 获取输入对象的 firstName 和 lastName，并将它们转换为大写
        final String firstName = person.firstName().toUpperCase();
        final String lastName = person.lastName().toUpperCase();

        // 创建一个新的 Person 对象，将转换后的 firstName 和 lastName 赋值给它
        final Person transformedPerson = new Person(firstName, lastName);

        // 输出日志，显示转换前后的 Person 对象
        log.info("Converting (" + person + ") into (" + transformedPerson + ")");

        // 返回转换后的 Person 对象
        return transformedPerson;
    }
}

/*
详细中文注释说明：
1. PersonItemProcessor 类：
这个类实现了 Spring Batch 中的 ItemProcessor 接口。ItemProcessor 是 Spring Batch 提供的一个接口，允许我们在批处理的过程中对读取到的数据进行处理和转换。ItemProcessor 接口的泛型类型 <T, R> 表示输入和输出的数据类型。在这个例子中，输入和输出的数据类型都是 Person。
2. 日志记录 (Logger 和 LoggerFactory)：
使用 SLF4J 提供的 LoggerFactory 来创建日志记录器 log。SLF4J 是一种抽象的日志框架，它支持多种日志实现（如 Logback、Log4j 等）。通过日志记录，我们可以追踪数据处理的过程。
在 process 方法中，我们使用 log.info() 输出日志，显示每个 Person 对象在处理前后的状态。日志输出格式为：Converting (<原对象>) into (<转换后的对象>)。
3. process 方法：
process 方法是 ItemProcessor 接口的核心方法，它接收一个 Person 对象作为输入，进行处理，并返回处理后的 Person 对象。
处理逻辑：
通过 person.firstName() 和 person.lastName() 获取 Person 对象的 firstName 和 lastName 属性值。
使用 toUpperCase() 方法将名字和姓氏转换为大写字母。
创建一个新的 Person 对象，将转换后的名字和姓氏传递给构造函数，生成一个新的 Person 对象 transformedPerson。
4. 返回处理后的 Person 对象：
处理后的 Person 对象 transformedPerson 会被返回。这个对象的 firstName 和 lastName 都被转换成了大写字母。
5. 日志输出：
在处理过程中，我们输出了日志信息，显示原始的 Person 对象和转换后的 Person 对象。这样可以方便地跟踪数据的变化过程。
比如：Converting (Person[firstName=John, lastName=Doe]) into (Person[firstName=JOHN, lastName=DOE])

*/