import com.exaroton.api.request.server.CreateServerRequest;
import com.exaroton.api.request.server.DeleteServerRequest;
import com.exaroton.api.request.server.GetServerSoftwareRequest;
import com.exaroton.api.request.server.InstallServerSoftwareRequest;
import com.exaroton.api.request.software.GetSoftwareListRequest;
import com.exaroton.api.server.CreateServerOptions;
import com.exaroton.api.server.ServerRegion;
import com.exaroton.api.ExarotonClient;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

public class RequestTest {
    private static final String SERVER_ID = "tgkm731xO7GiHt76";
    private static final String SOFTWARE_ID = "wdZhC3v1udSOxR1d";
    private final Gson gson = new Gson();
    private final ExarotonClient client = new ExarotonClient("token");

    private HttpRequest build(com.exaroton.api.APIRequest<?> request) throws Exception {
        return request.build(gson, HttpRequest.newBuilder(), URI.create("https://api.exaroton.com/v1/").toURL());
    }

    @Test
    void testCreateServerRequest() throws Exception {
        HttpRequest request = build(new CreateServerRequest(CreateServerOptions.withSoftwareType("example", "vanilla")));
        assertEquals("POST", request.method());
        assertEquals("https://api.exaroton.com/v1/servers/", request.uri().toString());
        assertEquals("application/json", request.headers().firstValue("Content-Type").orElse(null));
    }

    @Test
    void testCreateServerOptionsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> CreateServerOptions.withSoftwareType("", "vanilla"));
        assertThrows(IllegalArgumentException.class, () -> CreateServerOptions.withSoftwareType("example", ""));
        assertThrows(IllegalArgumentException.class, () -> CreateServerOptions.withSoftware("example", "invalid"));
    }

    @Test
    void testCreateServerOptionsSerialization() {
        CreateServerOptions options = CreateServerOptions.withSoftware("example", SOFTWARE_ID)
                .setMotd("My cool server!")
                .setTimezone(ZoneId.of("Europe/Berlin"))
                .setRegion(ServerRegion.EU);

        String expected = "{\"name\":\"example\",\"software\":\"" + SOFTWARE_ID + "\",\"motd\":\"My cool server!\"," +
                "\"timezone\":\"Europe/Berlin\",\"region\":\"eu\"}";
        assertEquals(gson.fromJson(expected, JsonElement.class), gson.toJsonTree(options));
    }

    @Test
    void testCreateServerOptionsSoftwareTypeSerialization() {
        CreateServerOptions options = CreateServerOptions.withSoftwareType("example", "vanilla");
        String expected = "{\"name\":\"example\",\"softwareType\":\"vanilla\"}";
        assertEquals(gson.fromJson(expected, JsonElement.class), gson.toJsonTree(options));
    }

    @Test
    void testDeleteServerRequest() throws Exception {
        HttpRequest request = build(new DeleteServerRequest(client, gson, SERVER_ID));
        assertEquals("DELETE", request.method());
        assertEquals("https://api.exaroton.com/v1/servers/" + SERVER_ID + "/", request.uri().toString());
    }

    @Test
    void testGetServerSoftwareRequest() throws Exception {
        HttpRequest request = build(new GetServerSoftwareRequest(client, gson, SERVER_ID, SOFTWARE_ID));
        assertEquals("GET", request.method());
        assertEquals("https://api.exaroton.com/v1/servers/" + SERVER_ID + "/software/" + SOFTWARE_ID + "/",
                request.uri().toString());
    }

    @Test
    void testInstallServerSoftwareRequest() throws Exception {
        HttpRequest request = build(new InstallServerSoftwareRequest(client, gson, SERVER_ID, SOFTWARE_ID, true));
        assertEquals("POST", request.method());
        assertEquals("https://api.exaroton.com/v1/servers/" + SERVER_ID + "/software/" + SOFTWARE_ID + "/",
                request.uri().toString());
    }

    @Test
    void testGetSoftwareListRequest() throws Exception {
        assertEquals("https://api.exaroton.com/v1/software/",
                build(new GetSoftwareListRequest(null, null)).uri().toString());
        assertEquals("https://api.exaroton.com/v1/software/?type=vanilla",
                build(new GetSoftwareListRequest("vanilla", null)).uri().toString());
        assertEquals("https://api.exaroton.com/v1/software/?type=modpack&launcher=Curse+Forge",
                build(new GetSoftwareListRequest("modpack", "Curse Forge")).uri().toString());
    }
}
