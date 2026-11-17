import com.exaroton.api.request.account.GetAccountTransactionsRequest;
import com.exaroton.api.request.billing.pools.GetCreditPoolTransactionsRequest;
import com.exaroton.api.request.regions.GetRegionsRequest;
import com.exaroton.api.request.server.CreateServerRequest;
import com.exaroton.api.request.server.GetServerPropertiesRequest;
import com.exaroton.api.request.server.GetServerTransactionsRequest;
import com.exaroton.api.request.server.SetServerPropertiesRequest;
import com.exaroton.api.request.server.TransferServerRequest;
import com.exaroton.api.request.server.access.*;
import com.exaroton.api.request.server.backups.*;
import com.exaroton.api.request.server.domains.*;
import com.exaroton.api.server.AutostartType;
import com.exaroton.api.server.ServerProperties;
import com.exaroton.api.server.backups.BackupSettingsOptions;
import com.exaroton.api.server.domains.DomainProtocol;
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
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import static org.junit.jupiter.api.Assertions.*;

public class RequestTest {
    private static final String SERVER_ID = "tgkm731xO7GiHt76";
    private static final String SOFTWARE_ID = "wdZhC3v1udSOxR1d";
    private static final String POOL_ID = "N2t9gWOMpzRL37FI";
    private static final String ITEM_ID = "Xy7zAb3dEf6hIj9k";
    private static final String SERVER_URL = "https://api.exaroton.com/v1/servers/" + SERVER_ID + "/";
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

    private JsonElement body(HttpRequest request) {
        HttpRequest.BodyPublisher publisher = request.bodyPublisher().orElseThrow();
        List<ByteBuffer> buffers = new ArrayList<>();
        CompletableFuture<Void> done = new CompletableFuture<>();
        publisher.subscribe(new Flow.Subscriber<>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(ByteBuffer item) {
                buffers.add(item);
            }

            @Override
            public void onError(Throwable throwable) {
                done.completeExceptionally(throwable);
            }

            @Override
            public void onComplete() {
                done.complete(null);
            }
        });
        done.join();

        StringBuilder json = new StringBuilder();
        for (ByteBuffer buffer : buffers) {
            json.append(StandardCharsets.UTF_8.decode(buffer));
        }
        return gson.fromJson(json.toString(), JsonElement.class);
    }

    private void assertRequest(String method, String url, HttpRequest request) {
        assertEquals(method, request.method());
        assertEquals(url, request.uri().toString());
    }

    private void assertBody(String expected, HttpRequest request) {
        assertEquals("application/json", request.headers().firstValue("Content-Type").orElse(null));
        assertEquals(gson.fromJson(expected, JsonElement.class), body(request));
    }

    @Test
    void testGetRegionsRequest() throws Exception {
        assertRequest("GET", "https://api.exaroton.com/v1/regions/", build(new GetRegionsRequest()));
    }

    @Test
    void testTransactionRequests() throws Exception {
        assertRequest("GET", "https://api.exaroton.com/v1/account/transactions/?page=1",
                build(new GetAccountTransactionsRequest(1)));
        assertRequest("GET", "https://api.exaroton.com/v1/billing/pools/" + POOL_ID + "/transactions/?page=2",
                build(new GetCreditPoolTransactionsRequest(POOL_ID, 2)));
        assertRequest("GET", SERVER_URL + "transactions/?page=3",
                build(new GetServerTransactionsRequest(client, gson, SERVER_ID, 3)));
    }

    @Test
    void testInvalidPage() {
        assertThrows(IllegalArgumentException.class, () -> new GetAccountTransactionsRequest(0));
        assertThrows(IllegalArgumentException.class, () -> new GetBackupsRequest(client, gson, SERVER_ID, -1));
    }

    @Test
    void testServerPropertiesRequests() throws Exception {
        assertRequest("GET", SERVER_URL + "options/properties/",
                build(new GetServerPropertiesRequest(client, gson, SERVER_ID)));

        HttpRequest request = build(new SetServerPropertiesRequest(client, gson, SERVER_ID, new ServerProperties()
                .setAutostop(true)
                .setAutostopTime(10)
                .setAutostartType(AutostartType.WHITELISTED)));
        assertRequest("POST", SERVER_URL + "options/properties/", request);
        assertBody("{\"autostop\":true,\"autostop-time\":10,\"autostart-type\":\"whitelisted\"}", request);

        request = build(new SetServerPropertiesRequest(client, gson, SERVER_ID, Map.of("enable-lobby", false)));
        assertBody("{\"enable-lobby\":false}", request);
    }

    @Test
    void testTransferServerRequest() throws Exception {
        HttpRequest request = build(new TransferServerRequest(client, gson, SERVER_ID, "na"));
        assertRequest("POST", SERVER_URL + "transfer/", request);
        assertBody("{\"region\":\"na\"}", request);
        assertThrows(IllegalArgumentException.class, () -> new TransferServerRequest(client, gson, SERVER_ID, ""));
    }

    @Test
    void testBackupRequests() throws Exception {
        assertRequest("GET", SERVER_URL + "backups/?page=1", build(new GetBackupsRequest(client, gson, SERVER_ID, 1)));
        assertRequest("GET", SERVER_URL + "backups/" + ITEM_ID + "/",
                build(new GetBackupRequest(client, gson, SERVER_ID, ITEM_ID)));
        assertRequest("DELETE", SERVER_URL + "backups/" + ITEM_ID + "/",
                build(new DeleteBackupRequest(client, gson, SERVER_ID, ITEM_ID)));
        assertRequest("POST", SERVER_URL + "backups/" + ITEM_ID + "/restore/",
                build(new RestoreBackupRequest(client, gson, SERVER_ID, ITEM_ID)));
        assertRequest("GET", SERVER_URL + "backup-accounts/",
                build(new GetBackupAccountsRequest(client, gson, SERVER_ID)));
        assertRequest("GET", SERVER_URL + "backup-settings/",
                build(new GetBackupSettingsRequest(client, gson, SERVER_ID)));

        HttpRequest request = build(new CreateBackupRequest(client, gson, SERVER_ID, null, null));
        assertRequest("POST", SERVER_URL + "backups/", request);
        assertBody("{}", request);
        assertBody("{\"name\":\"Before the update\",\"backupAccount\":\"" + ITEM_ID + "\"}",
                build(new CreateBackupRequest(client, gson, SERVER_ID, "Before the update", ITEM_ID)));

        request = build(new RenameBackupRequest(client, gson, SERVER_ID, ITEM_ID, ""));
        assertRequest("PUT", SERVER_URL + "backups/" + ITEM_ID + "/", request);
        assertBody("{\"name\":\"\"}", request);

        request = build(new UpdateBackupSettingsRequest(client, gson, SERVER_ID, new BackupSettingsOptions()
                .setEnabled(true)
                .setInterval(Duration.ofHours(12))));
        assertRequest("POST", SERVER_URL + "backup-settings/", request);
        assertBody("{\"enabled\":true,\"interval\":43200}", request);

        assertThrows(IllegalArgumentException.class, () -> new GetBackupRequest(client, gson, SERVER_ID, ""));
    }

    @Test
    void testDomainRequests() throws Exception {
        assertRequest("GET", SERVER_URL + "domains/", build(new GetDomainsRequest(client, gson, SERVER_ID)));
        assertRequest("GET", SERVER_URL + "domains/" + ITEM_ID + "/",
                build(new GetDomainRequest(client, gson, SERVER_ID, ITEM_ID)));
        assertRequest("DELETE", SERVER_URL + "domains/" + ITEM_ID + "/",
                build(new DeleteDomainRequest(client, gson, SERVER_ID, ITEM_ID)));
        assertRequest("POST", SERVER_URL + "domains/" + ITEM_ID + "/check/",
                build(new CheckDomainRequest(client, gson, SERVER_ID, ITEM_ID)));

        HttpRequest request = build(new AddDomainRequest(client, gson, SERVER_ID, "play.example.com"));
        assertRequest("POST", SERVER_URL + "domains/", request);
        assertBody("{\"domain\":\"play.example.com\"}", request);

        request = build(new UpdateDomainRequest(client, gson, SERVER_ID, ITEM_ID, DomainProtocol.HTTPS, 8080));
        assertRequest("PUT", SERVER_URL + "domains/" + ITEM_ID + "/", request);
        assertBody("{\"protocol\":\"https\",\"port\":8080}", request);
        assertBody("{\"port\":25565}",
                build(new UpdateDomainRequest(client, gson, SERVER_ID, ITEM_ID, null, 25565)));

        assertThrows(IllegalArgumentException.class,
                () -> new UpdateDomainRequest(client, gson, SERVER_ID, ITEM_ID, null, null));
    }

    @Test
    void testAccessRequests() throws Exception {
        assertRequest("GET", SERVER_URL + "access/", build(new GetAccessListRequest(client, gson, SERVER_ID)));
        assertRequest("GET", SERVER_URL + "access/" + ITEM_ID + "/",
                build(new GetAccessRequest(client, gson, SERVER_ID, ITEM_ID)));
        assertRequest("DELETE", SERVER_URL + "access/" + ITEM_ID + "/",
                build(new DeleteAccessRequest(client, gson, SERVER_ID, ITEM_ID)));

        HttpRequest request = build(new AddAccessRequest(client, gson, SERVER_ID, "example", null));
        assertRequest("POST", SERVER_URL + "access/", request);
        assertBody("{\"username\":\"example\"}", request);
        assertBody("{\"username\":\"example\",\"permissions\":[\"server\",\"stop\"]}",
                build(new AddAccessRequest(client, gson, SERVER_ID, "example", List.of("server", "stop"))));

        request = build(new UpdateAccessRequest(client, gson, SERVER_ID, ITEM_ID, List.of("server")));
        assertRequest("PUT", SERVER_URL + "access/" + ITEM_ID + "/", request);
        assertBody("{\"permissions\":[\"server\"]}", request);
    }
}
