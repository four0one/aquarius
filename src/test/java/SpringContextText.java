import com.rpcframework.service.DemoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @author wei.chen1
 * @since 2018/1/15
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-config-client.xml"})
public class SpringContextText {

	@Resource
	private DemoService demoService;

	@Test
	public void testMyDao() {

		demoService.hello();

	}

}
