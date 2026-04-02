package com.middleware.org.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

public class CaptchaUtil {
    private static final Random RANDOM = new Random();
    private static final String CODES = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";

    public static CaptchaResult generate() {
        int width = 120;
        int height = 40;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        String code = generateCode(4);
        g.setFont(new Font("Arial", Font.BOLD, 28));

        for (int i = 0; i < code.length(); i++) {
            g.setColor(randomColor());
            g.drawString(String.valueOf(code.charAt(i)), 20 + i * 25, 28);
        }

        for (int i = 0; i < 5; i++) {
            g.setColor(randomColor());
            g.drawLine(RANDOM.nextInt(width), RANDOM.nextInt(height),
                      RANDOM.nextInt(width), RANDOM.nextInt(height));
        }

        g.dispose();

        String base64 = imageToBase64(image);
        return new CaptchaResult(code, base64);
    }

    private static String generateCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(CODES.charAt(RANDOM.nextInt(CODES.length())));
        }
        return sb.toString();
    }

    private static Color randomColor() {
        return new Color(RANDOM.nextInt(150), RANDOM.nextInt(150), RANDOM.nextInt(150));
    }

    private static String imageToBase64(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            return "";
        }
    }

    public static class CaptchaResult {
        private final String code;
        private final String image;

        public CaptchaResult(String code, String image) {
            this.code = code;
            this.image = image;
        }

        public String getCode() { return code; }
        public String getImage() { return image; }
    }
}
