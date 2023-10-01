/*
 * MIT License
 *
 * Copyright (c) 2022-2023 YveIce, Enrico Messall
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
 */

package net.arikia.dev.drpc;

import com.sun.jna.Structure;
import net.arikia.dev.drpc.callbacks.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author Nicolas "Vatuu" Adamoglou
 * @version 1.5.1
 * <p>
 * Object containing references to all event handlers registered. No callbacks are necessary,
 * every event handler is optional. Non-assigned handlers are being ignored.
 */
public class DiscordEventHandlers extends Structure
{

  /**
   * Callback called when Discord-RPC was initialized successfully.
   */
  public ReadyCallback ready;
  /**
   * Callback called when the Discord connection was disconnected.
   */
  public DisconnectedCallback disconnected;
  /**
   * Callback called when a Discord error occurred.
   */
  public ErroredCallback errored;
  /**
   * Callback called when the player joins the game.
   */
  public JoinGameCallback joinGame;
  /**
   * Callback called when the player spectates a game.
   */
  public SpectateGameCallback spectateGame;
  /**
   * Callback called when a join request is received.
   */
  public JoinRequestCallback joinRequest;

  @Override
  public List<String> getFieldOrder()
  {
    return Arrays.asList("ready", "disconnected", "errored", "joinGame", "spectateGame", "joinRequest");
  }

  public static class Builder
  {

    DiscordEventHandlers h;

    public Builder()
    {
      h = new DiscordEventHandlers();
    }

    public Builder setReadyEventHandler(ReadyCallback r)
    {
      h.ready = r;
      return this;
    }

    public Builder setDisconnectedEventHandler(DisconnectedCallback d)
    {
      h.disconnected = d;
      return this;
    }

    public Builder setErroredEventHandler(ErroredCallback e)
    {
      h.errored = e;
      return this;
    }

    public Builder setJoinGameEventHandler(JoinGameCallback j)
    {
      h.joinGame = j;
      return this;
    }

    public Builder setSpectateGameEventHandler(SpectateGameCallback s)
    {
      h.spectateGame = s;
      return this;
    }

    public Builder setJoinRequestEventHandler(JoinRequestCallback j)
    {
      h.joinRequest = j;
      return this;
    }

    public DiscordEventHandlers build()
    {
      return h;
    }
  }
}
