package io.github.ollama4j.integrationtests;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.samples.AnnotatedTool;
import io.github.ollama4j.tools.annotations.OllamaToolService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.NginxContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@OllamaToolService(providers = {AnnotatedTool.class})
@TestMethodOrder(OrderAnnotation.class)
@SuppressWarnings({"HttpUrlsUsage", "SpellCheckingInspection", "resource", "ResultOfMethodCallIgnored"})
public class WithAuth {

    private static final Logger LOG = LoggerFactory.getLogger(WithAuth.class);
    private static final int NGINX_PORT = 80;
    private static final int OLLAMA_INTERNAL_PORT = 11434;
    private static final String OLLAMA_VERSION = "0.6.1";

    private static OllamaContainer ollama;
    private static GenericContainer<?> nginx;
    private static OllamaAPI api;

    @BeforeAll
    public static void setUp() {
        ollama = createOllamaContainer();
        ollama.start();

        nginx = createNginxContainer(ollama.getMappedPort(OLLAMA_INTERNAL_PORT));
        nginx.start();

        LOG.info("Using Testcontainer Ollama host...");

        api = new OllamaAPI("http://" + nginx.getHost() + ":" + nginx.getMappedPort(NGINX_PORT));
        api.setRequestTimeoutSeconds(120);
        api.setVerbose(true);
        api.setNumberOfRetriesForModelPull(3);
    }

    private static OllamaContainer createOllamaContainer() {
        OllamaContainer container = new OllamaContainer("ollama/ollama:" + OLLAMA_VERSION);
        container.addExposedPort(OLLAMA_INTERNAL_PORT);
        return container;
    }

    private static String generateNginxConfig(int ollamaPort) {
        return String.format("events {}\n" +
                "\n" +
                "http {\n" +
                "    server {\n" +
                "        listen 80;\n" +
                "\n" +
                "        location / {\n" +
                "            set $auth_header $http_authorization;\n" +
                "\n" +
                "            if ($auth_header != \"Bearer secret-token\") {\n" +
                "                return 401;\n" +
                "            }\n" +
                "\n" +
                "            proxy_pass http://host.docker.internal:%s/;\n" +
                "            proxy_set_header Host $host;\n" +
                "            proxy_set_header X-Real-IP $remote_addr;\n" +
                "            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;\n" +
                "            proxy_set_header X-Forwarded-Proto $scheme;\n" +
                "        }\n" +
                "    }\n" +
                "}\n", ollamaPort);
    }

    public static GenericContainer<?> createNginxContainer(int ollamaPort) {
        File nginxConf;

        try {
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "nginx-auth");
            if (!tempDir.exists()) tempDir.mkdirs();

            nginxConf = new File(tempDir, "nginx.conf");
            try (FileWriter writer = new FileWriter(nginxConf)) {
                writer.write(generateNginxConfig(ollamaPort));
            }

            return new NginxContainer<>(DockerImageName.parse("nginx:1.23.4-alpine"))
                    .withExposedPorts(NGINX_PORT)
                    .withCopyFileToContainer(
                            MountableFile.forHostPath(nginxConf.getAbsolutePath()),
                            "/etc/nginx/nginx.conf"
                    )
                    .withExtraHost("host.docker.internal", "host-gateway")
                    .waitingFor(
                            Wait.forHttp("/")
                                    .forStatusCode(401)
                                    .withStartupTimeout(Duration.ofSeconds(30))
                    );
        } catch (IOException e) {
            throw new RuntimeException("Failed to create nginx.conf", e);
        }
    }

    @Test
    @Order(1)
    void testEndpoint() throws InterruptedException {
        String ollamaUrl = "http://" + ollama.getHost() + ":" + ollama.getMappedPort(OLLAMA_INTERNAL_PORT);
        String nginxUrl = "http://" + nginx.getHost() + ":" + nginx.getMappedPort(NGINX_PORT);
        System.out.printf("Ollama service at %s is now accessible through the Nginx proxy at %s%n", ollamaUrl, nginxUrl);
        api.setBearerAuth("secret-token");
        Thread.sleep(1000);
        assertTrue(api.ping(), "OllamaAPI failed to ping through NGINX with auth.");
    }
}
