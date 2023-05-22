
    public static double calculatePSNR(BufferedImage X, BufferedImage Y){
        double MSE = calculateMSE(X, Y);
        double PSNR = 10*Math.log10(255/MSE);
        return PSNR;
    }
}
public class PatchExtractor {
    public static int extractPatches(BufferedImage X, int s) {
        int l = X.getHeight();
        int c = X.getWidth();
        int[][][] Y = new int[l - s + 1][c - s + 1][s * s];
        int[][] Pospatchs = new int[l][c];

        for (int i = 0; i < l - s + 1; i++) {
            for (int j = 0; j < c - s + 1; j++) {
                int[] listeL = Arrays.copyOfRange(X[i], j, j + s);
                int[] listeC = new int[s * s];

                for (int k = 0; k < s; k++) {
                    System.arraycopy(X[i + k], j, listeC, k * s, s);
                }

                for (int k = 0; k < s * s; k++) {
                    Y[i][j][k] = X[listeL[k]][listeC[k]];
                }

                for (int k = 0; k < s; k++) {
                    for (int m = 0; m < s; m++) {
                        Pospatchs[listeL[k]][listeC[m]]++;
                    }
                }
            }
        }

        return Y;
    }
}

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatchExtractor {
    public static Map<String, List<Integer[][]>> ExtractPatches(int[][] X, int s) {
        int height = X.length;
        int width = X[0].length;
        int patchSize = s;
        Map<String, List<Integer[][]>> patches = new HashMap<>();

        for (int y = 0; y < height; y += patchSize) {
            for (int x = 0; x < width; x += patchSize) {
                String key = String.format("(%d, %d)", x, y);
                List<Integer[][]> patchList = patches.getOrDefault(key, new ArrayList<>());
                Integer[][] patch = getPatch(X, x, y, patchSize);
                patchList.add(patch);
                patches.put(key, patchList);
            }
        }

        return patches;
    }

    private static Integer[][] getPatch(int[][] X, int startX, int startY, int patchSize) {
        int endX = startX + patchSize;
        int endY = startY + patchSize;
        Integer[][] patch = new Integer[patchSize][patchSize];

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                patch[y - startY][x - startX] = X[y][x];
            }
        }

        return patch;
    }
}
