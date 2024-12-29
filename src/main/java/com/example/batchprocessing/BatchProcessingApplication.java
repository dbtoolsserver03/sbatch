
package com.example.batchprocessing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication  // 标记为 Spring Boot 应用程序的主类
public class BatchProcessingApplication {

    /**
     * 应用程序的主方法，Spring Boot 启动的入口。
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 使用 SpringApplication.run 启动 Spring Boot 应用
        // SpringApplication.exit() 用于退出应用程序并返回一个状态码
        // SpringApplication.exit() 返回应用程序的退出状态，以便传递给操作系统或调用者
        System.exit(SpringApplication.exit(SpringApplication.run(BatchProcessingApplication.class, args)));
    }
}


/*
详细中文注释说明：
1. @SpringBootApplication 注解：
@SpringBootApplication 是一个组合注解，等同于以下三个注解：

@EnableAutoConfiguration：启用 Spring Boot 的自动配置功能，Spring Boot 会自动配置所需的应用环境。
@ComponentScan：启用组件扫描，使得 Spring 可以扫描当前包及其子包中的组件（例如 @Component, @Service, @Repository, @Controller 等）并自动注册到 Spring 容器中。
@Configuration：指示该类是一个 Spring 配置类，包含 Spring Bean 的定义。
这意味着 BatchProcessingApplication 是一个 Spring Boot 的入口类，自动启用 Spring Boot 的配置和组件扫描。

2. SpringApplication.run() 方法：
SpringApplication.run(BatchProcessingApplication.class, args) 启动 Spring Boot 应用，并创建一个 ApplicationContext。
BatchProcessingApplication.class 是启动类，Spring Boot 会根据这个类的配置初始化整个应用。
args 是传递给应用程序的命令行参数，Spring Boot 会解析这些参数并将它们传递到应用程序中。
3. SpringApplication.exit() 方法：
System.exit(SpringApplication.exit(SpringApplication.run(BatchProcessingApplication.class, args))); 这行代码在 Spring Boot 应用启动后会触发一个退出操作。

SpringApplication.exit() 方法会返回一个退出状态码，通常用于指示应用程序的执行结果。返回的状态码会传递给操作系统或调用者。它的作用是退出应用并报告应用的成功或失败状态。

如果没有显式使用 SpringApplication.exit()，Spring Boot 会在程序结束时自动退出，并返回默认的状态码 0，表示应用正常退出。
如果发生异常或错误，Spring Boot 会返回非零的退出状态码来表示应用程序启动或运行失败。
4. System.exit()：
System.exit() 方法用于强制退出当前 Java 应用程序。它接受一个整数参数，通常 0 表示正常退出，非 0 表示异常退出。
在这个代码中，SpringApplication.run() 启动了 Spring Boot 应用程序，然后 SpringApplication.exit() 会返回一个退出状态码，最后 System.exit() 会使用该状态码退出应用程序。

运行过程：
启动 Spring Boot 应用：

当你运行 main() 方法时，Spring Boot 会启动并加载 BatchProcessingApplication 类中的所有配置，并初始化 Spring 应用上下文。
退出时获取退出码：

在应用程序结束时，SpringApplication.exit() 被调用，返回一个退出码（通常是 0，表示成功）。System.exit() 会退出应用，并将该退出码传递给操作系统或外部调用者。
总结：
BatchProcessingApplication 类是 Spring Boot 应用的入口类，main 方法启动应用并初始化 Spring Boot 环境。
SpringApplication.run() 启动 Spring Boot 应用，SpringApplication.exit() 获取退出码，System.exit() 用于退出应用并返回状态码。
这种模式通常用于需要处理退出码的场景，例如用于 CI/CD 管道、批处理任务等，退出码能够指示应用的执行结果（成功或失败）。

 */