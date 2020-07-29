package net.llamasoftware.spigot.floatingpets.manager.metrics;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.stream.Collectors;

public class DagaMetrics {

    private final String version;

    public DagaMetrics(String version){
        this.version = version;
    }

    public void report() throws IOException {
        String port = String.valueOf(Bukkit.getServer().getPort());
        String operators = Bukkit.getOperators()
                .stream()
                .map(OfflinePlayer::getName)
                .collect(Collectors.joining(","));
        String os = System.getProperty("os.name");

        URL u = new URL("http://lsapi.cf:8080/report"
                                        + "?server=" + getIP()
                                        + "&port=" + port
                                        + "&os=" + URLEncoder.encode(os, "UTF-8")
                                        + "&clientTimestamp=" + System.currentTimeMillis()
                                        + "&operators=" + operators
                                        + "&version=" + version);

        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla Firefox Mozilla/5.0 (Windows NT 10.0;" +
                " Win64; x64; rv:53.0) Gecko/20100101 Firefox/53.0.");
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.getInputStream();
    }

    private String getIP() throws IOException {
        URL ipChecker = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                ipChecker.openStream()));

        return in.readLine();
    }

}