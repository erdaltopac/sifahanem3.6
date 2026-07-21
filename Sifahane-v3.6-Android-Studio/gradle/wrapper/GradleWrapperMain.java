package org.gradle.wrapper;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.util.zip.*;

/**
 * Minimal, auditable Gradle bootstrap used only because the source package did not
 * contain the standard wrapper JAR. It validates HTTPS, host and SHA-256 before use.
 */
public final class GradleWrapperMain {
    private static final Set<String> ALLOWED_HOSTS = Set.of("services.gradle.org", "downloads.gradle.org");

    public static void main(String[] args) throws Exception {
        Path projectDir = locateProjectDir();
        Properties properties = new Properties();
        Path propertiesFile = projectDir.resolve("gradle/wrapper/gradle-wrapper.properties");
        try (InputStream in = Files.newInputStream(propertiesFile)) { properties.load(in); }

        String rawUrl = required(properties, "distributionUrl").replace("\\:", ":");
        URI uri = URI.create(rawUrl);
        if (!"https".equalsIgnoreCase(uri.getScheme()) || !ALLOWED_HOSTS.contains(uri.getHost())) {
            throw new SecurityException("Gradle dağıtım adresi güvenli değil: " + uri);
        }
        String expectedSha = required(properties, "distributionSha256Sum").toLowerCase(Locale.ROOT);
        String fileName = Path.of(uri.getPath()).getFileName().toString();
        String version = fileName.replace("gradle-", "").replace("-bin.zip", "").replace("-all.zip", "");

        Path userHome = Path.of(System.getProperty("gradle.user.home",
                Path.of(System.getProperty("user.home"), ".gradle").toString()));
        Path distRoot = userHome.resolve("wrapper/dists/sifahane-gradle-" + version);
        Path gradleHome = distRoot.resolve("gradle-" + version);
        Path executable = gradleHome.resolve(isWindows() ? "bin/gradle.bat" : "bin/gradle");

        if (!Files.isRegularFile(executable)) {
            Files.createDirectories(distRoot);
            Path zip = distRoot.resolve(fileName);
            if (!Files.isRegularFile(zip) || !sha256(zip).equals(expectedSha)) {
                Files.deleteIfExists(zip);
                download(uri.toURL(), zip);
                String actual = sha256(zip);
                if (!actual.equals(expectedSha)) {
                    Files.deleteIfExists(zip);
                    throw new SecurityException("Gradle SHA-256 doğrulaması başarısız. Beklenen=" + expectedSha + " Gerçek=" + actual);
                }
            }
            Path staging = distRoot.resolve(".extracting");
            deleteTree(staging);
            Files.createDirectories(staging);
            unzip(zip, staging);
            Path extracted = staging.resolve("gradle-" + version);
            if (!Files.isDirectory(extracted)) throw new IOException("Gradle dağıtımı beklenen klasörü içermiyor.");
            deleteTree(gradleHome);
            Files.move(extracted, gradleHome, StandardCopyOption.ATOMIC_MOVE);
            deleteTree(staging);
            if (!isWindows()) executable.toFile().setExecutable(true, true);
        }

        List<String> command = new ArrayList<>();
        command.add(executable.toString());
        command.addAll(Arrays.asList(args));
        Process process = new ProcessBuilder(command)
                .directory(projectDir.toFile())
                .inheritIO()
                .start();
        System.exit(process.waitFor());
    }

    private static Path locateProjectDir() {
        Path current = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        while (current != null) {
            if (Files.isRegularFile(current.resolve("gradle/wrapper/gradle-wrapper.properties"))) return current;
            current = current.getParent();
        }
        throw new IllegalStateException("gradle-wrapper.properties bulunamadı.");
    }

    private static String required(Properties p, String key) {
        String value = p.getProperty(key);
        if (value == null || value.isBlank()) throw new IllegalStateException(key + " tanımlı değil.");
        return value.trim();
    }

    private static void download(URL initialUrl, Path target) throws IOException {
        URL current = initialUrl;
        for (int redirect = 0; redirect <= 5; redirect++) {
            if (!"https".equalsIgnoreCase(current.getProtocol()) || !ALLOWED_HOSTS.contains(current.getHost())) {
                throw new SecurityException("Gradle indirme yönlendirmesi güvenli değil: " + current);
            }
            HttpURLConnection connection = (HttpURLConnection) current.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(30_000);
            int status = connection.getResponseCode();
            if (status >= 300 && status < 400) {
                String location = connection.getHeaderField("Location");
                connection.disconnect();
                if (location == null) throw new IOException("Gradle yönlendirme adresi eksik.");
                current = new URL(current, location);
                continue;
            }
            if (status != HttpURLConnection.HTTP_OK) {
                connection.disconnect();
                throw new IOException("Gradle indirilemedi. HTTP " + status);
            }
            try (InputStream in = new BufferedInputStream(connection.getInputStream());
                 OutputStream out = new BufferedOutputStream(Files.newOutputStream(target, StandardOpenOption.CREATE_NEW))) {
                in.transferTo(out);
            } finally {
                connection.disconnect();
            }
            return;
        }
        throw new IOException("Gradle indirmesinde çok fazla yönlendirme oluştu.");
    }

    private static void unzip(Path zipPath, Path destination) throws IOException {
        try (ZipInputStream zip = new ZipInputStream(new BufferedInputStream(Files.newInputStream(zipPath)))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                Path target = destination.resolve(entry.getName()).normalize();
                if (!target.startsWith(destination)) throw new SecurityException("Gradle ZIP geçersiz yol içeriyor.");
                if (entry.isDirectory()) Files.createDirectories(target);
                else {
                    Files.createDirectories(target.getParent());
                    try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(target))) { zip.transferTo(out); }
                }
                zip.closeEntry();
            }
        }
    }

    private static String sha256(Path path) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream in = Files.newInputStream(path)) {
            byte[] buffer = new byte[64 * 1024];
            int count;
            while ((count = in.read(buffer)) >= 0) digest.update(buffer, 0, count);
        }
        StringBuilder out = new StringBuilder();
        for (byte b : digest.digest()) out.append(String.format("%02x", b));
        return out.toString();
    }

    private static void deleteTree(Path root) throws IOException {
        if (!Files.exists(root)) return;
        try (var stream = Files.walk(root)) {
            stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                try { Files.deleteIfExists(path); }
                catch (IOException e) { throw new UncheckedIOException(e); }
            });
        } catch (UncheckedIOException e) { throw e.getCause(); }
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
    }
}
