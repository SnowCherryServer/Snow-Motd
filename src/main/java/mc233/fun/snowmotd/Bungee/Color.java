package mc233.fun.snowmotd.Bungee;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Color {
    public Color() {
    }

    static String applyGradients(String message) {
        Pattern pattern = Pattern.compile("\\{#([A-Fa-f0-9]{6})>}(.+?)\\{#([A-Fa-f0-9]{6})<}");
        Matcher matcher = pattern.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while(matcher.find()) {
            String startColor = matcher.group(1);
            String text = matcher.group(2);
            String endColor = matcher.group(3);
            boolean isBold = text.contains("&l");
            if (isBold) {
                text = text.replace("&l", "");
            }

            String replacement = applyGradient(text, startColor, endColor, isBold);
            matcher.appendReplacement(buffer, replacement);
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String applyGradient(String message, String startColor, String endColor, boolean isBold) {
        StringBuilder result = new StringBuilder();

        for(int i = 0; i < message.length(); ++i) {
            float progress = (float)i / (float)(message.length() - 1);
            String color = interpolateColor(startColor, endColor, progress);
            result.append("{#").append(color).append("}");
            if (isBold) {
                result.append("&l");
            }

            if (message.contains("&k")) {
                result.append("&k");
                message = message.replace("&k", "");
            }

            if (message.contains("&m")) {
                result.append("&m");
                message = message.replace("&m", "");
            }

            if (message.contains("&n")) {
                result.append("&n");
                message = message.replace("&n", "");
            }

            result.append(message.charAt(i));
        }

        return result.toString();
    }

    private static String interpolateColor(String startColor, String endColor, float progress) {
        int startRed = Integer.parseInt(startColor.substring(0, 2), 16);
        int startGreen = Integer.parseInt(startColor.substring(2, 4), 16);
        int startBlue = Integer.parseInt(startColor.substring(4, 6), 16);
        int endRed = Integer.parseInt(endColor.substring(0, 2), 16);
        int endGreen = Integer.parseInt(endColor.substring(2, 4), 16);
        int endBlue = Integer.parseInt(endColor.substring(4, 6), 16);
        int red = (int)((float)startRed + (float)(endRed - startRed) * progress);
        int green = (int)((float)startGreen + (float)(endGreen - startGreen) * progress);
        int blue = (int)((float)startBlue + (float)(endBlue - startBlue) * progress);
        return String.format("%02x%02x%02x", red, green, blue);
    }

    static String translateHexColorCodes(String message) {
        Pattern pattern = Pattern.compile("\\{#([A-Fa-f0-9]{6})\\}");
        Matcher matcher = pattern.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while(matcher.find()) {
            String color = matcher.group(1);
            String replacement = "§x";

            for(int i = 0; i < color.length(); ++i) {
                replacement = replacement + "§" + color.charAt(i);
            }

            matcher.appendReplacement(buffer, replacement);
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
