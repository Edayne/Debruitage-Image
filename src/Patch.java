import java.awt.image.BufferedImage;
import java.util.*;

public class Patch extends Photo{
    public void extractPatchs(BufferedImage photo, int s) {
        int l = photo.getHeight();
        int c = photo.getWidth();
        int [][][] Y = new int[l-s+1][c-s+1][s*s];
        int [][] Pos_patchs = new int[l][c];
        List<Integer> listL = new ArrayList<Integer>();
        List<Integer> listC = new ArrayList<Integer>();
        for(int i=1; i<l-s+1;i++){
            for(int j=1; j<c-s+1;j++){
                for(int k=i-1;i<=i+s-2;k++){
                    listL.add(k%l + 1);
                }
                for(int a=j-1; a<=j+s-2;a++){
                    listC.add(a%l + 1);
                }
                Y[i][j][]= 
            }
        } 
    }
}