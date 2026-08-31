package com.iitm.hosteldine.constant;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class CaptchaUtil {

    private static int width = 350;
    private static int height = 40;
    private static int lines = 50;

    public static BufferedImage genCaptcha(String code) {
        BufferedImage image = new BufferedImage(width -80, height, BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics();
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Times New Roman", Font.ROMAN_BASELINE, 18));
        g.setColor(getRandColor(110, 133));
        drowCode(g, code);
        for (int i = 0; i <= lines; i++) {
            drowLine(g);
        }
        g.dispose();
        return image;
    }

    private static Color getRandColor(int fc, int bc) {
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc - 16);
        int g = fc + random.nextInt(bc - fc - 14);
        int b = fc + random.nextInt(bc - fc - 18);
        return new Color(r, g, b);
    }

    private static String drowCode(Graphics g, String code) {
        int margin = (width / code.length()) - 15;
        for (int i = 0; i < code.length(); ++i) {
            g.setFont(getFont());
            g.setColor(getRandColor(0, 100));
            g.translate(random.nextInt(3), random.nextInt(3));
            g.drawString(String.valueOf(code.charAt(i)), margin * i, 25);
        }
        return code;
    }

    private static void drowLine(Graphics g) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(13);
        int yl = random.nextInt(15);
        Color c = new Color(211, 211, 211);
        g.setColor(c);
        g.drawLine(x, y, x + xl, y + yl);
    }

    private static Font getFont() {
        return new Font("Fixedsys", Font.CENTER_BASELINE, 30);
    }

    private static final Random random = new Random();

    public static String getCode(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            int j = random.nextInt(i * (i + 3) / 2 + 1);
            if (j == 1) {
                sb.append(getNumber());
            } else {
                sb.append(getEnglish());
            }
        }
        return sb.toString();
    }

    private static String getEnglish() {
        int temp = random.nextInt(25);
        char charList[] = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','P','Q','R','S','T','U','V','W','X','Y','Z'};
        return String.valueOf(charList[temp]);
    }

    private static String getNumber() {
        int numList[] = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        return String.valueOf(numList[random.nextInt(9)]);
    }
}
