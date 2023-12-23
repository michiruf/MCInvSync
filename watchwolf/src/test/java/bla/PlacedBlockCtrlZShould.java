package bla;

import dev.watchwolf.entities.Position;
import dev.watchwolf.entities.blocks.Block;
import dev.watchwolf.entities.blocks.Blocks;
import dev.watchwolf.entities.items.Item;
import dev.watchwolf.entities.items.ItemType;
import dev.watchwolf.tester.AbstractTest;
import dev.watchwolf.tester.ExtendedClientPetition;
import dev.watchwolf.tester.TesterConnector;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * All the tests that we expect to fulfill the plugin's requirements.
 */
@ExtendWith(PlacedBlockCtrlZShould.class)
public class PlacedBlockCtrlZShould extends AbstractTest {

    @Override
    public String getConfigFile() {
        return "src/test/resources/watchwolf.yml";
    }

    /**
     * We'll place one dirt block, run the ctrl-z command, and then there should be air in that place
     * and the player should have his block back.
     */
    @ParameterizedTest
    @ArgumentsSource(PlacedBlockCtrlZShould.class)
    public void restoreTheLastBrokenBlock(TesterConnector connector) throws IOException {
        /* decide where/who will interact with the event */
        String client = connector.getClients()[0];
        ExtendedClientPetition clientConnector = connector.getClientPetition(client);
        Position whereToPlaceTheBlock = clientConnector.getPosition().add(1, 0, 0); // block next to the user
        Block whatBlockWillBePlaced = Blocks.DIRT;

        /* prepare the environment (make sure the player can place the block there) */
        connector.server.setBlock(whereToPlaceTheBlock, Blocks.AIR);

        /* prepare the client (give him the block to place) */
        Item placedBlockItem = new Item(whatBlockWillBePlaced.getItemType());
        connector.server.giveItem(client, placedBlockItem);

        /* start the test */
        clientConnector.setBlock(whatBlockWillBePlaced, whereToPlaceTheBlock); // place the block
        clientConnector.runCommand("ctrl-z"); // undo

        assertEquals(Blocks.AIR, connector.server.getBlock(whereToPlaceTheBlock)); // we expect air in the place we've put the block
        assertEquals(1, getItemAmounts(clientConnector.getInventory().getItems()).get(whatBlockWillBePlaced.getItemType())); // we expect 1 dirt block back
    }

    /**
     * Given a container items it returns all the found items and the amount
     *
     * @param items All the items in the container
     * @return {DIRT:2,WHEAT:6,...}
     */
    private static HashMap<ItemType, Integer> getItemAmounts(Item... items) {
        HashMap<ItemType, Integer> r = new HashMap<>();

        for (Item i : items) {
            if (i == null) continue;

            Integer acum = r.get(i.getType());
            if (acum == null) r.put(i.getType(), (int) i.getAmount());
            else r.put(i.getType(), acum + i.getAmount());
        }

        return r;
    }
}