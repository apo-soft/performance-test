/**
 * 
 */
package cn.aposoft.dubbo.demo.jmeter.sonsumer;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

//import com.alibaba.dubbo.config.ReferenceConfig;

import cn.aposoft.dubbo.demo.DemoService;

/**
 * 
 * @author LiuJian
 *
 */
public class SimpleHelloSamplerClient extends AbstractJavaSamplerClient {
	// private volatile ReferenceConfig<DemoService> reference = null;
	private static final String DEFAULT_MESSAGE = "SUCCESS";
	private static final String DEFAULT_NAME = "SIMPLE-HELLO-JMETER-JAVA-SONSUMER";

	private static volatile DubboReferenceManager refManager = null;
	private static final Object refLock = new Object();

	private static volatile DemoService service = null;

	@Override
	public void setupTest(JavaSamplerContext context) {

		// // 当前应⽤配置
		// ApplicationConfig application = new ApplicationConfig();
		// application.setName("demo-api-config-consumer");
		// // 连接注册中⼼配置
		// RegistryConfig registry = new RegistryConfig();
		// registry.setAddress("224.5.6.7");
		// registry.setProtocol("multicast");
		//
		// // 注意：ReferenceConfig为重对象，内部封装了与注册中⼼的连接，以及与服务提供⽅的连接
		// // 引⽤远程服务
		// reference = new ReferenceConfig<DemoService>();
		// // 此实例很重，封装了与注册中⼼的连接以及与提供者的连接，请⾃⾏缓存，否则可能造成内存和连接泄漏
		// reference.setApplication(application);
		// reference.setRegistry(registry); // 多个注册中⼼可以⽤setRegistries()
		// reference.setInterface(DemoService.class);
		// // reference.setVersion("1.0.0");
		// // 和本地bean⼀样使⽤xxxService
		// service = reference.get(); // 注意：此代理对象内部封装了所有通讯细节，对象较重，请缓存复用

		synchronized (refLock) {
			if (refManager == null) {
				refManager = new DubboReferenceManager();
				refManager.setup(context, DemoService.class);
			}
		}
		service = refManager.getService();
	}

	@Override
	public void teardownTest(JavaSamplerContext context) {
		refManager.release(context);
		service = null;
	}

	@Override
	public Arguments getDefaultParameters() {
		Arguments params = new Arguments();
		params.addArgument("result", "Hello SUCCESS");
		params.addArgument("message", DEFAULT_MESSAGE);
		params.addArgument("name", DEFAULT_NAME);
		// params.addArgument("protocol", "multicast");
		// params.addArgument("address", "224.5.6.7");

		params.addArgument("protocol", "zookeeper");
		params.addArgument("address", "10.152.4.90:2181,10.152.4.90:2182,10.152.4.90:2183");

		return params;
	}

	@Override
	public SampleResult runTest(JavaSamplerContext context) {
		String result = context.getParameter("result");
		try {

			// 创建SampleResult对象，用于记录执行结果的状态，并返回
			SampleResult sampleResult = new SampleResult();

			// 获取JMeter中输入的用户参数

			// 开始
			sampleResult.sampleStart();
			String param = context.getParameter("message");
			String message = service.sayHello(param);

			// 暂停
			// sampleResult.samplePause();

			// 重启
			// sampleResult.sampleResume();

			// 结束
			sampleResult.sampleEnd();

			sampleResult.setSuccessful(message != null && message.length() >= result.length()
					&& result.equals(message.substring(0, result.length())));
			// 返回
			return sampleResult;
		} finally {
		}

	}

}
