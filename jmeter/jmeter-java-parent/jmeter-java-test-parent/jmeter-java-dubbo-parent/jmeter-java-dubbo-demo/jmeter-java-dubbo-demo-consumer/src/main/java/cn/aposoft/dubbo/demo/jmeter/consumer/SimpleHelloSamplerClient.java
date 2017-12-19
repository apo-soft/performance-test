/**
 * 
 */
package cn.aposoft.dubbo.demo.jmeter.consumer;

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
		params.addArgument("address", "10.143.117.21:2182,10.143.117.21:2183");

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
