/**
 * 
 */
package cn.aposoft.dubbo.demo.consumer;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;

import cn.aposoft.dubbo.demo.DemoService;

/**
 * @author LiuJian
 *
 */
public class Consumer {

	private volatile static ReferenceConfig<DemoService> reference;

	public static void main(String[] args) {
		// Prevent to get IPV6 address,this way only work in debug mode
		// But you can pass use -Djava.net.preferIPv4Stack=true,then it work
		// well whether in debug mode or not
		System.setProperty("java.net.preferIPv4Stack", "true");

		DemoService demoService = getService();

		for (int i = 0; i < 100; i++) {
			try {
				Thread.sleep(1000);
				String hello = demoService.sayHello("world"); // call remote
																// method
				System.out.println(hello); // get result

			} catch (Throwable throwable) {
				throwable.printStackTrace();
			}

		}
		reference.destroy();

	}

	private static DemoService getService() {
		// 当前应⽤配置
		ApplicationConfig application = new ApplicationConfig();
		application.setName("demo-api-config-consumer");
		// 连接注册中⼼配置
		RegistryConfig registry = new RegistryConfig();
		// registry.setAddress("224.5.6.7");
		// registry.setProtocol("multicast");
		registry.setAddress("10.152.4.90:2181,10.152.4.90:2182,10.152.4.90:2183");
		registry.setProtocol("zookeeper");
		// 注意：ReferenceConfig为重对象，内部封装了与注册中⼼的连接，以及与服务提供⽅的连接
		// 引⽤远程服务
		reference = new ReferenceConfig<DemoService>();
		// 此实例很重，封装了与注册中⼼的连接以及与提供者的连接，请⾃⾏缓存，否则可能造成内存和连接泄漏
		reference.setApplication(application);
		reference.setRegistry(registry); // 多个注册中⼼可以⽤setRegistries()
		reference.setInterface(DemoService.class);
		// reference.setVersion("1.0.0");
		// 和本地bean⼀样使⽤xxxService
		DemoService xxxService = reference.get(); // 注意：此代理对象内部封装了所有通讯细节，对象较重，请缓存复用
		return xxxService;
	}
}
