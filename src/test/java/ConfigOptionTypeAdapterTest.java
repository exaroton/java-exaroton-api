import com.exaroton.api.server.config.ConfigOption;
import com.exaroton.api.server.config.ConfigOptionTypeAdapterFactory;
import com.exaroton.api.server.config.OptionType;
import com.exaroton.api.server.config.options.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class ConfigOptionTypeAdapterTest {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new ConfigOptionTypeAdapterFactory())
            .create();

    @Test
    public void testDeserializeBooleanOption() {
        String json = "{\"key\":\"spawn-monsters\",\"label\":\"Spawn Monsters\",\"type\":\"boolean\",\"value\":true,\"options\": null}";
        ConfigOption<?> option = gson.fromJson(json, ConfigOption.class);
        Assertions.assertNotNull(option);
        Assertions.assertEquals("spawn-monsters", option.getKey());
        Assertions.assertEquals("Spawn Monsters", option.getLabel());
        Assertions.assertEquals(OptionType.BOOLEAN, option.getType());
        Assertions.assertEquals(true, option.getValue());
    }

    @Test
    public void testSerializeBooleanOption() {
        BooleanConfigOption option = new BooleanConfigOption("spawn-monsters", true, "Spawn Monsters");
        JsonElement json = gson.toJsonTree(option);
        Assertions.assertNotNull(json);
        String expectedJson = "{\"value\":true,\"key\":\"spawn-monsters\",\"label\":\"Spawn Monsters\",\"type\":\"boolean\"}";
        Assertions.assertEquals(gson.fromJson(expectedJson, JsonElement.class), json);
    }

    @Test
    public void testDeserializeFloatOption() {
        String json = "{\"key\":\"view-distance\",\"label\":\"View Distance\",\"type\":\"float\",\"value\":10.0,\"options\": null}";
        ConfigOption<?> option = gson.fromJson(json, ConfigOption.class);
        Assertions.assertNotNull(option);
        Assertions.assertEquals("view-distance", option.getKey());
        Assertions.assertEquals("View Distance", option.getLabel());
        Assertions.assertEquals(OptionType.FLOAT, option.getType());
        Assertions.assertEquals(10.0, option.getValue());
    }

    @Test
    public void testSerializeFloatOption() {
        ConfigOption<?> option = new FloatConfigOption("view-distance", 10.0, "View Distance");
        JsonElement json = gson.toJsonTree(option);
        Assertions.assertNotNull(json);
        String expectedJson = "{\"value\":10.0,\"key\":\"view-distance\",\"label\":\"View Distance\",\"type\":\"float\"}";
        Assertions.assertEquals(gson.fromJson(expectedJson, JsonElement.class), json);
    }

    @Test
    public void testDeserializeIntegerOption() {
        String json = "{\"key\":\"max-players\",\"label\":\"Max Players\",\"type\":\"integer\",\"value\":10,\"options\": null}";
        ConfigOption<?> option = gson.fromJson(json, ConfigOption.class);
        Assertions.assertNotNull(option);
        Assertions.assertEquals("max-players", option.getKey());
        Assertions.assertEquals("Max Players", option.getLabel());
        Assertions.assertEquals(OptionType.INTEGER, option.getType());
        Assertions.assertEquals(10L, option.getValue());
    }

    @Test
    public void testSerializeIntegerOption() {
        ConfigOption<?> option = new IntegerConfigOption("max-players", 10L, "Max Players");
        JsonElement json = gson.toJsonTree(option);
        Assertions.assertNotNull(json);
        String expectedJson = "{\"value\":10,\"key\":\"max-players\",\"label\":\"Max Players\",\"type\":\"integer\"}";
        Assertions.assertEquals(gson.fromJson(expectedJson, JsonElement.class), json);
    }

    @Test
    public void testDeserializeStringOption() {
        String json = "{\"key\":\"level-name\",\"label\":\"Level Name\",\"type\":\"string\",\"value\":\"world\",\"options\": null}";
        ConfigOption<?> option = gson.fromJson(json, ConfigOption.class);
        Assertions.assertNotNull(option);
        Assertions.assertEquals("level-name", option.getKey());
        Assertions.assertEquals("Level Name", option.getLabel());
        Assertions.assertEquals(OptionType.STRING, option.getType());
        Assertions.assertEquals("world", option.getValue());
    }

    @Test
    public void testSerializeStringOption() {
        ConfigOption<?> option = new StringConfigOption("level-name", "world", "Level Name");
        JsonElement json = gson.toJsonTree(option);
        Assertions.assertNotNull(json);
        Assertions.assertEquals(gson.fromJson("{\"value\":\"world\",\"key\":\"level-name\",\"label\":\"Level Name\",\"type\":\"string\"}", JsonElement.class), json);
    }

    @Test
    public void testDeserializeMultiselectOption() {
        String json = "{\"key\":\"enabled-datapacks\",\"label\":\"Enabled Datapacks\",\"type\":\"multiselect\",\"value\":[\"vanilla\",\"experiment-copper-golem\"],\"options\":[\"vanilla\",\"experiment-copper-golem\",\"experiment-penwing\",\"experiment-crab\"]}";
        ConfigOption<?> option = gson.fromJson(json, ConfigOption.class);
        Assertions.assertNotNull(option);

        Assertions.assertInstanceOf(MultiselectConfigOption.class, option);
        MultiselectConfigOption multiselect = (MultiselectConfigOption) option;

        Assertions.assertEquals("enabled-datapacks", option.getKey());
        Assertions.assertEquals("Enabled Datapacks", option.getLabel());
        Assertions.assertEquals(OptionType.MULTISELECT, option.getType());
        Assertions.assertEquals(Set.of("vanilla", "experiment-copper-golem"), multiselect.getValue());
        Assertions.assertEquals(Set.of("vanilla","experiment-copper-golem","experiment-penwing","experiment-crab"), multiselect.getOptions());
    }

    @Test
    public void testSerializeMultiselectOption() {
        ConfigOption<?> option = new MultiselectConfigOption("enabled-datapacks", Set.of("vanilla", "experiment-copper-golem"), "Enabled Datapacks", Set.of("vanilla","experiment-copper-golem","experiment-penwing","experiment-crab"));
        JsonElement json = gson.toJsonTree(option);
        Assertions.assertNotNull(json);
        String expectedJson = "{\"value\":[\"vanilla\",\"experiment-copper-golem\"],\"key\":\"enabled-datapacks\",\"label\":\"Enabled Datapacks\",\"type\":\"multiselect\",\"options\":[\"vanilla\",\"experiment-copper-golem\",\"experiment-penwing\",\"experiment-crab\"]}";
        assertEqualsIgnoreListOrder(gson.fromJson(expectedJson, JsonElement.class), json);
    }

    @Test
    public void testDeserializeSelectOption() {
        String json = "{\"key\":\"gamemode\",\"label\":\"Gamemode\",\"type\":\"select\",\"value\":\"survival\",\"options\":[\"survival\",\"creative\",\"adventure\",\"spectator\"]}";
        ConfigOption<?> option = gson.fromJson(json, ConfigOption.class);
        Assertions.assertNotNull(option);
        Assertions.assertEquals("gamemode", option.getKey());
        Assertions.assertEquals("Gamemode", option.getLabel());
        Assertions.assertEquals(OptionType.SELECT, option.getType());

        Assertions.assertInstanceOf(SelectConfigOption.class, option);
        SelectConfigOption select = (SelectConfigOption) option;

        Assertions.assertEquals("survival", option.getValue());
        Assertions.assertEquals(Set.of("survival","creative","adventure","spectator"), select.getOptions());
    }

    @Test
    public void testSerializeSelectOption() {
        ConfigOption<?> option = new SelectConfigOption("gamemode", "survival", "Gamemode", Set.of("survival","creative","adventure","spectator"));
        JsonElement json = gson.toJsonTree(option);
        Assertions.assertNotNull(json);
        String expectedJson = "{\"value\":\"survival\",\"key\":\"gamemode\",\"label\":\"Gamemode\",\"type\":\"select\",\"options\":[\"survival\",\"creative\",\"adventure\",\"spectator\"]}";
        assertEqualsIgnoreListOrder(gson.fromJson(expectedJson, JsonElement.class), json);
    }

    private void assertEqualsIgnoreListOrder(JsonElement expected, JsonElement actual) {
        if (expected.isJsonArray()) {
            if (!actual.isJsonArray()) {
                Assertions.fail("Expected array but got " + actual);
            }

            if (expected.getAsJsonArray().size() != actual.getAsJsonArray().size()) {
                Assertions.fail("Expected array size " + expected.getAsJsonArray().size() + " but got " + actual.getAsJsonArray().size());
            }

            for (JsonElement item : expected.getAsJsonArray()) {
                boolean found = false;
                for (JsonElement actualItem : actual.getAsJsonArray()) {
                    try {
                        assertEqualsIgnoreListOrder(item, actualItem);
                        found = true;
                        break;
                    } catch (AssertionError e) {
                        // ignore
                    }
                }

                if (!found) {
                    Assertions.fail("Expected item not found: " + item);
                }
            }

        } else if (expected.isJsonObject()) {
            if (!actual.isJsonObject()) {
                Assertions.fail("Expected object but got " + actual);
            }

            expected.getAsJsonObject().entrySet().forEach(entry -> {
                String key = entry.getKey();
                JsonElement expectedValue = entry.getValue();
                JsonElement actualValue = actual.getAsJsonObject().get(key);
                assertEqualsIgnoreListOrder(expectedValue, actualValue);
            });
        } else {
            Assertions.assertEquals(expected, actual);
        }
    }
}
