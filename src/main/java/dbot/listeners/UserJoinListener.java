package dbot.listeners;

import dbot.Statics;
import dbot.sql.UserData;
import dbot.util.Poster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.UserJoinEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * Created by Niklas on 23.02.2017.
 */
public final class UserJoinListener implements IListener<UserJoinEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.listeners.UserJoinListener");

	@Override
	public void handle(UserJoinEvent event) {
		IUser user = event.getUser();
		IGuild guild = event.getGuild();
		int ref = Statics.GUILD_LIST.getRef(guild);
		UserData.addUser(user, ref);
		try {
			switch(ref) {//TODO: in DB packen?
				case 0:
					user.addRole(guild.getRolesByName("Newfags").get(0));
					//LOGGER.info("added role(Newfags) to {}", user.getName());
					Poster.post(	"Willkommen auf dem nicesten Discord-Server ever :)" +
							"\nWenn du Lust hast, schau doch mal im #botspam vorbei, hier kann man ne nice Runde gamblen und co :)" +
							"\nZusätzlich solltest du #botspam auf @mention stellen (oder muten)" +
							"\nBei Fragen am Besten an @DPD oder @Stammboys wenden.", user);
					break;

				case 1:
					user.addRole(guild.getRolesByName("Newfags").get(0));
					break;

				default:

			}

		} catch(MissingPermissionsException | DiscordException | RateLimitException e) {
			LOGGER.error("Error while adding role to {} (or couldn't send message)", user.getName(), e);
		}
	}

}
