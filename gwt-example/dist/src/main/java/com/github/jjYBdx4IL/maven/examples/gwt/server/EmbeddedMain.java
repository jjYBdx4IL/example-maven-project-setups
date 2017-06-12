package com.github.jjYBdx4IL.maven.examples.gwt.server;

import com.github.jjYBdx4IL.aop.tx.H2FrontendRunner;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.nio.file.Paths;
import org.apache.tomcat.SimpleInstanceManager;
import org.eclipse.jetty.deploy.DeploymentManager;
import org.eclipse.jetty.deploy.PropertiesConfigurationManager;
import org.eclipse.jetty.deploy.bindings.DebugListenerBinding;
import org.eclipse.jetty.deploy.providers.WebAppProvider;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.plus.jndi.Resource;
import org.eclipse.jetty.server.DebugListener;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.LowResourceMonitor;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnectionStatistics;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.webapp.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http://www.eclipse.org/jetty/documentation/current/embedding-jetty.html
 * https://www.eclipse.org/jetty/documentation/9.3.x/embedded-examples.html#embedded-webapp-jsp
 * http://www.eclipse.org/jetty/documentation/current/configuring-virtual-hosts.html
 *
 * @author jjYBdx4IL
 */
public class EmbeddedMain {

    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedMain.class);

    private final String cwd = Paths.get(System.getProperty("appserver.base", "."))
            .toAbsolutePath().normalize().toString();
    private final int portOffset = Integer.parseInt(System.getProperty("jetty.port.offset", "0"));

    public EmbeddedMain(String[] args) {
    }

    public static void main(String[] args) {
        try {
            new EmbeddedMain(args).run();
        } catch (Exception ex) {
            LOG.error("", ex);
        }
    }

    public void run() throws Exception {
        LOG.info("cwd = " + cwd);
        LOG.info("port offset = " + portOffset);

        System.setProperty("jetty.home", cwd);
        System.setProperty("jetty.base", cwd);

        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMaxThreads(500);

        Server server = new Server(threadPool);

        server.addBean(new ScheduledExecutorScheduler());

        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(portOffset + 443);
        http_config.setOutputBufferSize(32768);
        http_config.setRequestHeaderSize(8192);
        http_config.setResponseHeaderSize(8192);
        http_config.setSendServerVersion(false);
        http_config.setSendDateHeader(false);

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[]{contexts, new DefaultHandler()});
        
        server.setHandler(handlers);

        server.setDumpAfterStart(true);
        server.setDumpBeforeStop(false);
        server.setStopAtShutdown(true);
        server.setStopTimeout(30000L);

        // JMX
        MBeanContainer mbContainer = new MBeanContainer(
                ManagementFactory.getPlatformMBeanServer());
        server.addBean(mbContainer);

        ServerConnector http = new ServerConnector(server,
                new HttpConnectionFactory(http_config));
        http.setPort(portOffset + 80);
        http.setIdleTimeout(30000);
        server.addConnector(http);

        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(cwd + "/data/keystore");
        sslContextFactory.setKeyStorePassword("password");
        sslContextFactory.setKeyManagerPassword("password");
        sslContextFactory.setTrustStorePath(cwd + "/data/keystore");
        sslContextFactory.setTrustStorePassword("password");
        sslContextFactory.setExcludeCipherSuites("SSL_RSA_WITH_DES_CBC_SHA",
                "SSL_DHE_RSA_WITH_DES_CBC_SHA", "SSL_DHE_DSS_WITH_DES_CBC_SHA",
                "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
                "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
                "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
                "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");

        HttpConfiguration https_config = new HttpConfiguration(http_config);
        https_config.addCustomizer(new SecureRequestCustomizer());

        ServerConnector sslConnector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(https_config));
        sslConnector.setPort(portOffset + 443);
        server.addConnector(sslConnector);

        DeploymentManager deployer = new DeploymentManager();
        DebugListener debug = new DebugListener(System.err, true, true, true);
        server.addBean(debug);
        deployer.addLifeCycleBinding(new DebugListenerBinding(debug));
        deployer.setContexts(contexts);
        deployer.setContextAttribute(
                "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                ".*/[^g]+[^/]*jar$");
        deployer.setContextAttribute("org.apache.tomcat.InstanceManager",
                new SimpleInstanceManager());

        WebAppProvider webAppProvider = new WebAppProvider();
        webAppProvider.setMonitoredDirName(cwd + "/apps");
        //webapp_provider.setDefaultsDescriptor(jetty_home + "/etc/webdefault.xml");
        webAppProvider.setScanInterval(1);
        webAppProvider.setExtractWars(false);
        webAppProvider.setConfigurationManager(new PropertiesConfigurationManager());

        deployer.addAppProvider(webAppProvider);
        server.addBean(deployer);

        // === setup jetty plus ==
        Configuration.ClassList classList = Configuration.ClassList.setServerDefault(server);
        classList.addBefore(
                org.eclipse.jetty.webapp.JettyWebXmlConfiguration.class.getName(),
                org.eclipse.jetty.annotations.AnnotationConfiguration.class.getName());
        classList.addAfter(
                org.eclipse.jetty.webapp.FragmentConfiguration.class.getName(),
                org.eclipse.jetty.plus.webapp.EnvConfiguration.class.getName(),
                org.eclipse.jetty.plus.webapp.PlusConfiguration.class.getName());

        // === jetty-stats.xml ===
        StatisticsHandler stats = new StatisticsHandler();
        stats.setHandler(server.getHandler());
        server.setHandler(stats);
        ServerConnectionStatistics.addToAllConnectors(server);

        // === Rewrite Handler
//        RewriteHandler rewrite = new RewriteHandler();
//        rewrite.setHandler(server.getHandler());
//        server.setHandler(rewrite);
        // === jetty-requestlog.xml ===
        NCSARequestLog requestLog = new NCSARequestLog();
        requestLog.setFilename(cwd + "/data/log/yyyy_mm_dd.request.log");
        requestLog.setFilenameDateFormat("yyyy_MM_dd");
        requestLog.setRetainDays(90);
        requestLog.setAppend(true);
        requestLog.setExtended(true);
        requestLog.setLogCookies(false);
        requestLog.setLogTimeZone("GMT");
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        requestLogHandler.setRequestLog(requestLog);
        handlers.addHandler(requestLogHandler);

        // === jetty-lowresources.xml ===
        LowResourceMonitor lowResourcesMonitor = new LowResourceMonitor(server);
        lowResourcesMonitor.setPeriod(1000);
        lowResourcesMonitor.setLowResourcesIdleTimeout(200);
        lowResourcesMonitor.setMonitorThreads(true);
        lowResourcesMonitor.setMaxConnections(0);
        lowResourcesMonitor.setMaxMemory(0);
        lowResourcesMonitor.setMaxLowResourcesTime(5000);
        server.addBean(lowResourcesMonitor);

        String jdbcUrl = "jdbc:h2:" + new File(cwd, "data/db/h2").getAbsolutePath();
        new Resource(server, "jdbc/url", jdbcUrl);
        // lifecycle handlers run outside webapps, so can't make use of jndi
        server.addManaged(new H2FrontendRunner(jdbcUrl));
        
        // === test-realm.xml ===
//        HashLoginService login = new HashLoginService();
//        login.setName("Test Realm");
//        login.setConfig(jetty_base + "/etc/realm.properties");
//        login.setHotReload(false);
//        server.addBean(login);
        // Start the server
        server.start();
        server.join();
    }
}
