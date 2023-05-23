import java.awt.image.BufferedImage;
import java.util.*;

public class Patch extends Photo{
        

    private static int getPixelValue(BufferedImage image, int l, int c) {
        int rgb = image.getRGB(c, l);
        return (rgb >> 16) & 0xFF; // Extracting red channel value (assuming 8-bit/channel grayscale image)
    }

    public List<int[][]> extractPatchs(BufferedImage photo, int s) {
        int l = photo.getHeight();
        int c = photo.getWidth();
        List<int[][]> patches = new ArrayList<>();
        int [][] pospat = new int[l][c];
        for(int i=0; i<l-s+1;i++){
            for(int j=0; j<c-s+1;j++){
                List<Integer> listL = new ArrayList<>();
                List<Integer> listC = new ArrayList<>();
                for(int k=i; k< i+s;k++){
                    listL.add((k%l)+1);
                }
                for(int k=j ; k<j+s; k++){
                    listC.add((k%c)+1);
                }

                int [][] patch = new int[s][s];
                for(int k=0; k< s; k++){
                    for(int m=0; m< s; m++){
                        patch[k][m] = getPixelValue(photo, listL.get(k)-1, listC.get(m)-1);
                    }
                }

                patches.add(patch);
                updatePospat(pospat,listL,listC);
            }
        }
        return patches;
    }

    private static void updatePospat(int[][] pospat, List<Integer> listL, List<Integer> listC) {
        for (int i = 0; i < listL.size(); i++) {
            for (int j = 0; j < listC.size(); j++) {
                pospat[listL.get(i)][listC.get(j)]++;
            }
        }
    }
}