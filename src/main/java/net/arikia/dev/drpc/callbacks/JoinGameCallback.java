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

package net.arikia.dev.drpc.callbacks;

import com.sun.jna.Callback;

/**
 * @author Nicolas "Vatuu" Adamoglou
 * @version 1.5.1
 * <p>
 * Interface to be implemented in classes that will be registered as "JoinGameCallback" Event Handler.
 * @see net.arikia.dev.drpc.DiscordEventHandlers
 **/
public interface JoinGameCallback extends Callback
{

  /**
   * Method called when joining a game.
   *
   * @param joinSecret Unique String containing information needed to let the player join.
   */
  void apply(String joinSecret);
}
