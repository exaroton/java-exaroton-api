import com.exaroton.api.server.config.options.BooleanConfigOption;
import com.exaroton.api.server.config.ConfigOption;
import com.exaroton.api.server.config.ConfigOptionTypeAdapter;
import com.exaroton.api.server.config.options.FloatConfigOption;
import com.exaroton.api.server.config.options.IntegerConfigOption;
import com.exaroton.api.server.config.options.MultiselectConfigOption;
import com.exaroton.api.server.config.OptionType;
import com.exaroton.api.server.config.options.StringConfigOption;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigOptionTypeAdapterTest {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(ConfigOption.class, new ConfigOptionTypeAdapter())

            .create();

    @Test
    public void testDeserializeBooleanOption() {
        String json = "{\"key\":\"spawn-monsters\",\"label\":\"Spawn Monsters\",\"type\":\"boolean\",\"value\":true,\"options\": null}";
        ConfigOption option = gson.fromJson(json, ConfigOption.class);
        Assertions.assertNotNull(option);
        Assertions.assertEquals("spawn-monsters", option.getKey());
        Assertions.assertEquals("Spawn Monsters", option.getLabel());
        Assertions.assertEquals(OptionType.BOOLEAN, option.getType());
        Assertions.assertEquals(true, option.getValue());
        Assertions.assertNull(option.getOptions());
    }

    @Test
    public void testSerializeBooleanOption() {
        BooleanConfigOption option = new BooleanConfigOption("spawn-monsters", true, "Spawn Monsters", null);
        String json = gson.toJson(option);
        Assertions.assertNotNull(json);
        Assertions.assertEquals("{\"value\":true,\"key\":\"spawn-monsters\",\"label\":\"Spawn Monsters\",\"type\":\"boolean\"}", json);
    }

    @Test
    public void testDeserializeFloatOption() {
        String json = "{\"key\":\"view-distance\",\"label\":\"View Distance\",\"type\":\"float\",\"value\":10.0,\"options\": null}";
        ConfigOption option = gson.fromJson(json, ConfigOption.class);
        Assertions.assertNotNull(option);
        Assertions.assertEquals("view-distance", option.getKey());
        Assertions.assertEquals("View Distance", option.getLabel());
        Assertions.assertEquals(OptionType.FLOAT, option.getType());
        Assertions.assertEquals(10.0, option.getValue());
        Assertions.assertNull(option.getOptions());
    }

    @Test
    public void testSerializeFloatOption() {
        ConfigOption option = new FloatConfigOption("view-distance", 10.0, "View Distance", null);
        String json = gson.toJson(option);
        Assertions.assertNotNull(json);
        Assertions.assertEquals("{\"value\":10.0,\"key\":\"view-distance\",\"label\":\"View Distance\",\"type\":\"float\"}", json);
    }

    @Test
    public void testDeserializeIntegerOption() {
        String json = "{\"key\":\"max-players\",\"label\":\"Max Players\",\"type\":\"integer\",\"value\":10,\"options\": null}";
        ConfigOption option = gson.fromJson(json, ConfigOption.class);
        Assertions.assertNotNull(option);
        Assertions.assertEquals("max-players", option.getKey());
        Assertions.assertEquals("Max Players", option.getLabel());
        Assertions.assertEquals(OptionType.INTEGER, option.getType());
        Assertions.assertEquals(10L, option.getValue());
        Assertions.assertNull(option.getOptions());
    }

    @Test
    public void testSerializeIntegerOption() {
        ConfigOption option = new IntegerConfigOption("max-players", 10L, "Max Players", null);
        String json = gson.toJson(option);
        Assertions.assertNotNull(json);
        Assertions.assertEquals("{\"value\":10,\"key\":\"max-players\",\"label\":\"Max Players\",\"type\":\"integer\"}", json);
    }

    @Test
    public void testDeserializeStringOption() {
        String json = "{\"key\":\"level-name\",\"label\":\"Level Name\",\"type\":\"string\",\"value\":\"world\",\"options\": null}";
        ConfigOption option = gson.fromJson(json, ConfigOption.class);
        Assertions.assertNotNull(option);
        Assertions.assertEquals("level-name", option.getKey());
        Assertions.assertEquals("Level Name", option.getLabel());
        Assertions.assertEquals(OptionType.STRING, option.getType());
        Assertions.assertEquals("world", option.getValue());
        Assertions.assertNull(option.getOptions());
    }

    @Test
    public void testSerializeStringOption() {
        ConfigOption option = new StringConfigOption("level-name", "world", "Level Name", null);
        String json = gson.toJson(option);
        Assertions.assertNotNull(json);
        Assertions.assertEquals("{\"value\":\"world\",\"key\":\"level-name\",\"label\":\"Level Name\",\"type\":\"string\"}", json);
    }

    @Test
    public void testDeserializeMultiselectOption() {
        String json = "{\"key\":\"enabled-datapacks\",\"label\":\"Enabled Datapacks\",\"type\":\"multiselect\",\"value\":[\"vanilla\",\"experiment-copper-golem\"],\"options\":[\"vanilla\",\"experiment-copper-golem\",\"experiment-penwing\",\"experiment-crab\"]}";
        ConfigOption option = gson.fromJson(json, ConfigOption.class);
        Assertions.assertNotNull(option);
        Assertions.assertEquals("enabled-datapacks", option.getKey());
        Assertions.assertEquals("Enabled Datapacks", option.getLabel());
        Assertions.assertEquals(OptionType.MULTISELECT, option.getType());
        Assertions.assertArrayEquals(new String[]{"vanilla", "experiment-copper-golem"}, (String[]) option.getValue());
        Assertions.assertArrayEquals(new String[]{"vanilla","experiment-copper-golem","experiment-penwing","experiment-crab"}, option.getOptions());
    }

    @Test
    public void testSerializeMultiselectOption() {
        ConfigOption option = new MultiselectConfigOption("enabled-datapacks", new String[]{"vanilla", "experiment-copper-golem"}, "Enabled Datapacks", new String[]{"vanilla","experiment-copper-golem","experiment-penwing","experiment-crab"});
        String json = gson.toJson(option);
        Assertions.assertNotNull(json);
        Assertions.assertEquals("{\"value\":[\"vanilla\",\"experiment-copper-golem\"],\"key\":\"enabled-datapacks\",\"label\":\"Enabled Datapacks\",\"type\":\"multiselect\",\"options\":[\"vanilla\",\"experiment-copper-golem\",\"experiment-penwing\",\"experiment-crab\"]}", json);
    }

    @Test
    public void testDeserializeSelectOption() {
        String json = "{\"key\":\"gamemode\",\"label\":\"Gamemode\",\"type\":\"select\",\"value\":\"survival\",\"options\":[\"survival\",\"creative\",\"adventure\",\"spectator\"]}";
        ConfigOption option = gson.fromJson(json, ConfigOption.class);
        Assertions.assertNotNull(option);
        Assertions.assertEquals("gamemode", option.getKey());
        Assertions.assertEquals("Gamemode", option.getLabel());
        Assertions.assertEquals(OptionType.SELECT, option.getType());
        Assertions.assertEquals("survival", option.getValue());
        Assertions.assertArrayEquals(new String[]{"survival","creative","adventure","spectator"}, option.getOptions());
    }

    @Test
    public void testSerializeSelectOption() {
        ConfigOption option = new StringConfigOption("gamemode", "survival", "Gamemode", new String[]{"survival","creative","adventure","spectator"});
        String json = gson.toJson(option);
        Assertions.assertNotNull(json);
        Assertions.assertEquals("{\"value\":\"survival\",\"key\":\"gamemode\",\"label\":\"Gamemode\",\"type\":\"string\",\"options\":[\"survival\",\"creative\",\"adventure\",\"spectator\"]}", json);
    }
}
