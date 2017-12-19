/**
 * 
 */
package cn.aposoft.dubbo.demo.jmeter.consumer;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;

/**
 * 
 * Reference 管理
 * 
 * @author LiuJian
 *
 */
public class DubboReferenceManager {
	private volatile ReferenceConfig<?> reference = null;

	private volatile JavaSamplerContext context;
	private volatile Class<?> clazz;
	private AtomicInteger invokerCount = new AtomicInteger(0);

	public void setup(JavaSamplerContext context, Class<?> clazz) {
		this.context = context;
		this.clazz = clazz;
		if (reference == null) {
			init();
		}
	}

	private synchronized void init() {
		if (reference != null) {
			return;
		}
		// 当前应⽤配置
		ApplicationConfig application = new ApplicationConfig();
		application.setName(context.getParameter("name"));
		// 连接注册中⼼配置
		RegistryConfig registry = new RegistryConfig();
		registry.setAddress(context.getParameter("address"));
		registry.setProtocol(context.getParameter("protocol"));
		// 注意：ReferenceConfig为重对象，内部封装了与注册中⼼的连接，以及与服务提供⽅的连接
		// 引⽤远程服务
		reference = new ReferenceConfig<>();
		// 此实例很重，封装了与注册中⼼的连接以及与提供者的连接，请⾃⾏缓存，否则可能造成内存和连接泄漏
		reference.setApplication(application);
		reference.setRegistry(registry); // 多个注册中⼼可以⽤setRegistries()

		reference.setInterface(clazz);
		// reference.setVersion("1.0.0");
		// 和本地bean⼀样使⽤xxxService
		invokerCount.set(0);
	}

	@SuppressWarnings("unchecked")
	public synchronized <T> T getService() {
		if (reference == null) {
			init();
		}
		invokerCount.incrementAndGet();
		return (T) reference.get();
	}

	public synchronized void release(JavaSamplerContext context) {
		if (invokerCount.decrementAndGet() == 0) {
			shutdown();
		}
	}

	public synchronized void shutdown() {
		reference.destroy();
		reference = null;
	}

}
