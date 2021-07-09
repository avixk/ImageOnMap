package fr.moribus.imageonmap.image;

// from :
// http://stackoverflow.com/questions/5940188/how-to-convert-a-24-bit-png-to-3-bit-png-using-floyd-steinberg-dithering

import java.awt.Color;
import java.awt.image.BufferedImage;

class Ditherer {

    private static C3 findClosestPaletteColor(C3 c, C3[] palette) {
        C3 closest = palette[0];

        for (C3 n : palette) {
            if (n.diff(c) < closest.diff(c)) {
                closest = n;
            }
        }
        return closest;
    }

    public static BufferedImage floydSteinbergDithering(BufferedImage img) {
        C3[] palette = new C3[] {
                new C3(127,178,56), new C3(247,233,163), new C3(188,188,188),
                new C3(255,0,0), new C3(160,160,225), new C3(167,167,167),
                new C3(0,124,0), new C3(255,255,255), new C3(164,168,184),
                new C3(151,109,77), new C3(112,112,112), new C3(64,64,255),
                new C3(143,119,72), new C3(255,252,245), new C3(216,127,51),
                new C3(178,76,216), new C3(102,153,216), new C3(229,229,51),
                new C3(127,204,25), new C3(242,127,165), new C3(76,76,76),
                new C3(153,153,153), new C3(76,127,153), new C3(127,63,178),
                new C3(51,76,178), new C3(102,76,51), new C3(102,127,5),
                new C3(153,51,51), new C3(25,25,25), new C3(250,238,77),
                new C3(92,219,213), new C3(74,128,255), new C3(0,217,58),
                new C3(129,86,49), new C3(112,2,0), new C3(209,177,161),
                new C3(159,82,36), new C3(149,87,108), new C3(112,108,138),
                new C3(186,113,36), new C3(103,117,53), new C3(160,77,78),
                new C3(57,41,35), new C3(135,107,98), new C3(87,92,92),
                new C3(122,73,88), new C3(76,62,92), new C3(76,50,35),
                new C3(76,82,42), new C3(142,60,46), new C3(37,22,16),
                new C3(189,48,49), new C3(148,63,97), new C3(92,25,29),
                new C3(22,126,134), new C3(58,142,140), new C3(86,44,62),
                new C3(20,180,133) // loneli
        };

        int w = img.getWidth();
        int h = img.getHeight();

        C3[][] d = new C3[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                d[y][x] = new C3(img.getRGB(x, y));
            }
        }

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {

                C3 oldColor = d[y][x];
                C3 newColor = findClosestPaletteColor(oldColor, palette);
                img.setRGB(x, y, newColor.toColor().getRGB());

                C3 err = oldColor.sub(newColor);

                if (x + 1 < w) {
                    d[y][x + 1] = d[y][x + 1].add(err.mul(7. / 16));
                }

                if (x - 1 >= 0 && y + 1 < h) {
                    d[y + 1][x - 1] = d[y + 1][x - 1].add(err.mul(3. / 16));
                }

                if (y + 1 < h) {
                    d[y + 1][x] = d[y + 1][x].add(err.mul(5. / 16));
                }

                if (x + 1 < w && y + 1 < h) {
                    d[y + 1][x + 1] = d[y + 1][x + 1].add(err.mul(1. / 16));
                }
            }
        }
        return img;
    }

    static class C3 {
        int red = 0;
        int green = 0;
        int blue = 0;

        public C3(int c) {
            Color color = new Color(c);
            red = color.getRed();
            green = color.getGreen();
            blue = color.getBlue();
        }

        public C3(int r, int g, int b) {
            this.red = r;
            this.green = g;
            this.blue = b;
        }

        public C3 add(C3 o) {
            return new C3(red + o.red, green + o.green, blue + o.blue);
        }

        public int clamp(int c) {
            return Math.max(0, Math.min(255, c));
        }

        public int diff(C3 o) {
            int diffRed = o.red - red;
            int diffGreen = o.green - green;
            int diffBlue = o.blue - blue;
            int distanceSquared = diffRed * diffRed + diffGreen * diffGreen + diffBlue * diffBlue;
            return distanceSquared;
        }

        public C3 mul(double d) {
            return new C3((int) (d * red), (int) (d * green), (int) (d * blue));
        }

        public C3 sub(C3 o) {
            return new C3(red - o.red, green - o.green, blue - o.blue);
        }

        public Color toColor() {
            return new Color(clamp(red), clamp(green), clamp(blue));
        }

        public int toRGB() {
            return toColor().getRGB();
        }
    }
}