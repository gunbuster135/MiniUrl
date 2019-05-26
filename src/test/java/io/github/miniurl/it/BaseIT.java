package io.github.miniurl.it;

import io.github.miniurl.Environment;
import io.github.miniurl.MiniUrlApplication;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MiniUrlApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = BaseIT.Initializer.class)
public abstract class BaseIT {
    @ClassRule
    public static GenericContainer redis = new GenericContainer("redis:5.0.5").withExposedPorts(Environment.REDIS_PORT);

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues values = TestPropertyValues.of(
                    "spring.redis.host=" + redis.getContainerIpAddress(),
                    "spring.redis.port=" + redis.getMappedPort(6379)
            );
            values.applyTo(configurableApplicationContext);
        }
    }

}
