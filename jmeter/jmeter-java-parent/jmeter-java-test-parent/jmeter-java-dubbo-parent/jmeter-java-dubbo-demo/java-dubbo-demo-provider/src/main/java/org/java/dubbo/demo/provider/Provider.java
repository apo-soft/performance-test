package org.java.dubbo.demo.provider;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;

import cn.aposoft.dubbo.demo.DemoService;

/**
 * Created by ken.lj on 2017/7/31.
 */
public class Provider {
	private volatile static ServiceConfig<DemoService> service;

	public static void main(String[] args) throws Exception {
		// Prevent to get IPV6 address,this way only work in debug mode
		// But you can pass use -Djava.net.preferIPv4Stack=true,then it work
		// well whether in debug mode or not
		System.setProperty("java.net.preferIPv4Stack", "true");
		exportService();
		System.in.read(); // press any key to exit
		service.unexport();

	}

	private static void exportService() {
		// 服务实现
		DemoService xxxService = new DemoServiceImpl();
		// 当前应⽤配置
		ApplicationConfig application = new ApplicationConfig();
		application.setName("demo-service-provider");
		// 连接注册中⼼配置
		RegistryConfig registry = new RegistryConfig();

//		registry.setAddress("224.5.6.7");
//		registry.setProtocol("multicast");

		registry.setAddress("10.143.117.21:2182,10.143.117.21:2183");
		registry.setProtocol("zookeeper");
		
		// 服务提供者协议配置
		ProtocolConfig protocol = new ProtocolConfig();
		protocol.setName("dubbo");
		protocol.setPort(12345);
		protocol.setThreads(200);
		// 注意：ServiceConfig为重对象，内部封装了与注册中⼼的连接，以及开启服务端⼝
		// 服务提供者暴露服务配置 // 此实例很重，封装了与注册中⼼的连接，请⾃⾏缓存，否则可能造成内存和连接泄漏
		service = new ServiceConfig<DemoService>();

		service.setApplication(application);
		service.setRegistry(registry); // 多个注册中⼼可以⽤setRegistries()
		service.setProtocol(protocol); // 多个协议可以⽤setProtocols()
		service.setInterface(DemoService.class);
		service.setRef(xxxService);
		// service.setVersion("1.0.0");
		// 暴露及注册服务
		service.export();
	}

}
