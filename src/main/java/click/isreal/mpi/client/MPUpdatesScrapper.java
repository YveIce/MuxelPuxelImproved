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

package click.isreal.mpi.client;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MPUpdatesScrapper
{
  private static MPUpdatesScrapper instance;
  private List<Text> updates = null;

  {
    instance = this;
  }

  public static MPUpdatesScrapper getInstance()
  {
    if (instance == null)
    {
      instance = new MPUpdatesScrapper();
    }
    return instance;

  }

  private static String[] splitString(String text, int maxLength) {
    // Split the string on spaces
    String[] words = text.split("\\s+");

    // Build substrings ensuring each substring is less than or equal to maxLength
    StringBuilder currentSubstring = new StringBuilder();
    int currentLength = 0;
    int currentIndex = 0;
    int totalWords = words.length;
    String[] substrings = new String[totalWords]; // Assuming each word is <= maxLength

    for (String word : words) {
      if (currentLength + word.length() + (currentSubstring.length() > 0 ? 1 : 0) <= maxLength) {
        if (currentSubstring.length() > 0) {
          currentSubstring.append(" ");
          currentLength++;
        }
        currentSubstring.append(word);
        currentLength += word.length();
      } else {
        substrings[currentIndex++] = currentSubstring.toString();
        currentSubstring.setLength(0);
        currentLength = 0;

        // Add the current word to the new substring
        currentSubstring.append(word);
        currentLength += word.length();
      }
    }

    if (currentSubstring.length() > 0) {
      substrings[currentIndex++] = currentSubstring.toString();
    }

    // Resize the array to eliminate null elements
    return Arrays.copyOf(substrings, currentIndex);
  }


  @Nullable
  public List<Text> getUpdates()
  {
    List<Text> result = new ArrayList<>();

    if (this.updates != null)
    {
      return this.updates;
    }
    else
    {
      try
      {
        URL url = new URL("https://mixelpixel.net/updates");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)))
        {
          StringBuilder content = new StringBuilder();
          String line;

          while ((line = reader.readLine()) != null)
          {
            content.append(line).append("\n");
          }

          // Parse HTML content without using Jsoup
          String htmlContent = content.toString();

          // Use regex or other methods to extract information
          Pattern pattern = Pattern.compile("<div class=\"update_reihe_1.*?</div>.*?</div>.*?</div>", Pattern.DOTALL);
          Matcher matcher = pattern.matcher(htmlContent);

          int count = 0;
          while (matcher.find() && count < 10)
          {
            String updateElementHtml = matcher.group(0);
            //Mpi.LOGGER.info(updateElementHtml);
            // Extract information from updateElementHtml
            Pattern datePattern = Pattern.compile("<p class=\"erstell_datum_text\">(.*?)</p>");
            Matcher dateMatcher = datePattern.matcher(updateElementHtml);

            String erstellDatum = dateMatcher.find() ? dateMatcher.group(1) : "";

            Pattern infoPattern = Pattern.compile("<p class=\"konzept_info\">(.*?)</p>");
            Matcher infoMatcher = infoPattern.matcher(updateElementHtml);

            String konzeptInfo = infoMatcher.find() ? infoMatcher.group(1) : "";

            Pattern paragraphPattern = Pattern.compile("<p class=\"paragraph-91\">(.*?)</p>");
            Matcher paragraphMatcher = paragraphPattern.matcher(updateElementHtml);

            String paragraphText = paragraphMatcher.find() ? paragraphMatcher.group(1) : "";


            // List<Text> entry = new ArrayList<>();
            if (0 < count)
            {result.add(Text.of(Formatting.BLACK + "" + Formatting.STRIKETHROUGH + "                                                       "));}
            result.add(Text.of(Formatting.GOLD + "" + Formatting.BOLD + erstellDatum + " - " + konzeptInfo ));
            for (String substring : splitString(paragraphText.trim(), 50))
            {
              if (substring != null)
              {//result.add(Text.of(" " + Formatting.GRAY + substring + " "));
              }
            }
            result.add(Text.of(Formatting.GRAY + paragraphText.trim()));
            //result.add(entry);
            count++;
          }
        }
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }

      this.updates = result;
      return result;
    }
  }
}
