package net.llamasoftware.spigot.floatingpets.manager.metrics;

import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class DagaMetrics {

    private final String version;
    private static final String SERVICE_URL = "http://lsapi.cf:16301/report";

    public DagaMetrics(String version){
        this.version = version;
    }

    public void report() throws IOException {
        String os = System.getProperty("os.name");
        String javaVersion = System.getProperty("java.version");
        Runtime runtime = Runtime.getRuntime();

        URL u = new URL(SERVICE_URL
                                        + "?clientTimestamp=" + System.currentTimeMillis()
                                        + "&playerCount=" + Bukkit.getOfflinePlayers().length
                                        + "&os=" + URLEncoder.encode(os, "UTF-8")
                                        + "&pluginCount=" + Bukkit.getPluginManager().getPlugins().length
                                        + "&opCount=" + Bukkit.getOperators().size()
                                        + "&javaVersion=" + javaVersion
                                        + "&pluginVersion=" + version
                                        + "&memory=" + runtime.maxMemory() / Math.pow(10, 6)
                                        + "&serverName=" + Bukkit.getName()
                                        + "&cores=" + runtime.availableProcessors());

        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestProperty("User-Agent", "Mozilla Firefox Mozilla/5.0 (Windows NT 10.0;" +
                " Win64; x64; rv:53.0) Gecko/20100101 Firefox/53.0.");
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.getInputStream();
    }

}