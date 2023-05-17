import java.awt.image.BufferedImage;

public class Seuil {
    
    public double VisuShrink(int l, double s){

        return(s*Math.sqrt(2*Math.log(l)));

    }


    public double BayesShrink(double s, BufferedImage photo){
        int n;
        n=0;
        int p;
        p=0;
        for(int j=0;j<photo.getHeight();j++){
            for (int i=0;i <photo.getWidth();i++){
                n+=photo.getRGB(i,j);
                p+=(photo.getRGB(i,j)*photo.getRGB(i,j));
            }   	
        }
        double moyenne;
        double moy;
        double variance;
        moyenne=n/(photo.getHeight()*photo.getWidth());
        moy=p/(photo.getHeight()*photo.getWidth());
        variance=moy-moyenne;

        if (variance-s <=0){
            return(0);


        }else{
            return(Math.sqrt(variance-s));

        }

    }
}
