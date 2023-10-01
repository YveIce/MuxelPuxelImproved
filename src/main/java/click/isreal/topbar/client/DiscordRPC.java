package click.isreal.topbar.client;

/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2022 YveIce
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

import click.isreal.topbar.Topbar;
import click.isreal.topbar.domain.UserData;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicBoolean;


public class DiscordRPC
{
    private final static String appId = "916437803326406721";
    private final TopbarClient topbar = TopbarClient.getInstance();

    public long timeStampStart = 0L;
    public boolean oldState = false;
    public DiscordRichPresence presence;
    public DiscordEventHandlers handlers;

    public DiscordPresentationThread thread;
    public boolean doRun = false;

    public DiscordRPC()
    {
        try
        {
            Topbar.LOGGER.log(Level.INFO, "Init Discord Handler...");
            DiscordEventHandlers.Builder builder = new DiscordEventHandlers.Builder();
            builder.setReadyEventHandler(( user ) -> {
                Topbar.LOGGER.log(Level.INFO, "Discord connected to user: " + user.username + "#" + user.discriminator);
            });
            handlers = builder.build();
        }
        catch (Throwable e)
        {
            Topbar.LOGGER.log(Level.WARN, "Error: " + e.getMessage());
        }
        start();
    }

    public void start()
    {
        doRun = Topbar.getInstance().isDiscordEnabled();
        if ( doRun )
        {
            timeStampStart = new Timestamp(System.currentTimeMillis()).getTime();
            try
            {
                net.arikia.dev.drpc.DiscordRPC.discordInitialize(appId, handlers, true);
                presence = buildPresence();
                net.arikia.dev.drpc.DiscordRPC.discordUpdatePresence(presence);
                net.arikia.dev.drpc.DiscordRPC.discordRegister(appId, "");
            }
            catch (Throwable e)
            {
                Topbar.LOGGER.log(Level.WARN, "Error: " + e.getMessage());
            }

            thread = new DiscordPresentationThread();
            thread.start();
        }
    }


    public void stop()
    {
        doRun = false;
        thread.stop();
        net.arikia.dev.drpc.DiscordRPC.discordShutdown();
    }

    public DiscordRichPresence buildPresence()
    {
        if ( topbar != null && oldState != topbar.isMixelPixel() )
        {
            timeStampStart = new Timestamp(System.currentTimeMillis()).getTime();
            oldState = !oldState;
        }
        if(!topbar.isMixelPixel()) oldState = false;

        if ( oldState )
        {
            String world = Formatting.strip(topbar.buildName(topbar.getWorld()));
            String state = switch (topbar.getWorld()) {
                case HUB -> UserData.current().rank();
                case KFFA -> {
                    world += " - " + Formatting.strip(UserData.current().rank());
                    yield UserData.current().kffaMap() + " " + UserData.current().kffaMapSwitch();
                }
                case FARMWORLD_1, FARMWORLD_2, FARMWORLD_3, FARMWORLD_4 -> UserData.current().dimension();
                case SMALL_FLORA, SMALL_AQUA, SMALL_VULKAN, SMALL_DONNER, BIG_FLORA, BIG_AQUA, BIG_VULKAN, BIG_DONNER -> {
                    if (StringUtils.isNoneBlank(UserData.current().cbPlotName(), UserData.current().cbPlotOwner()))
                        yield "Plot " + UserData.current().cbPlotName() + " | " + UserData.current().cbPlotOwner();
                    yield "";
                }
                default -> "";
            };
            DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder(Formatting.strip(state));
            presence.setDetails(world);
            presence.setBigImage("logomp", "Join MixelPixel Discord:\n discord.gg/mixelpixel");
            presence.setStartTimestamps(timeStampStart);
            return presence.build();
        }
        else
        {
            DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder("From Yve™ continued by Klysma");
            presence.setDetails("using an MC-Mod ");
            presence.setBigImage("logolurkklinik", "Visit DonnerPrinzessin @CB-Donner");
            presence.setStartTimestamps(timeStampStart);
            return presence.build();
        }

    }

    public class DiscordPresentationThread implements Runnable
    {

        private final AtomicBoolean running = new AtomicBoolean(false);
        private Thread worker;

        public void start()
        {
            worker = new Thread(this);
            worker.start();
        }

        public void stop()
        {
            running.set(false);
        }

        public void interrupt()
        {
            running.set(false);
            worker.interrupt();
        }

        public void run()
        {
            running.set(true);
            while ( running.get() )
            {
                presence = buildPresence();
                net.arikia.dev.drpc.DiscordRPC.discordUpdatePresence(presence);
                net.arikia.dev.drpc.DiscordRPC.discordRunCallbacks();
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        }
    }
}