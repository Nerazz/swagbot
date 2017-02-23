package dbot.listeners;

import dbot.util.Poster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.UserJoinEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * Created by Niklas on 23.02.2017.
 */
public class UserJoinListener implements IListener<UserJoinEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.listeners.UserJoinListener");

	@Override
	public void handle(UserJoinEvent event) {
		try {
			event.getUser().addRole(event.getGuild().getRolesByName("Newfags").get(0));
			LOGGER.info("added role(Newfags) to {}", event.getUser().getName());
			Poster.post(	"Willkommen auf dem nicesten Discord-Server ever :)" +
					"\nWenn du Lust hast, schau doch mal im #botspam vorbei, hier kann man ne nice Runde gamblen und co :)" +
					"\nZusätzlich solltest du #botspam auf @mention stellen (oder muten)" +
					"\nBei Fragen am Besten an @DPD oder @Stammboys wenden.", event.getUser());
		} catch(MissingPermissionsException | DiscordException | RateLimitException e) {
			LOGGER.error("Error while adding role to {} (or couldn't send message)", event.getUser().getName(), e);
		}
	}

}
