import java.util.ArrayList;
import java.util.List;
import java.awt.font.ImageGraphicAttribute;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.Random;


public class Photo {
    //Attributes
    private BufferedImage photo;
    private BufferedImage photoBruitee;
    private int nbL;
    private int nbC;

    //Constructeurs
    public Photo(String chemin) {
        try {
            this.photo = ImageIO.read(new File(chemin)); 
            this.nbC = this.photo.getWidth();
            this.nbL = this.photo.getHeight();
        } catch (IOException e) {
            System.out.println("Fichier introuvable, réessayez !");
        }
    }

    //Methodes
    /**
     * @return BufferedImage return the photo
     */
    public BufferedImage getPhoto() {
        return photo;
    }

    /**
     * @param photo the photo to set
     */
    public void setPhoto(BufferedImage photo) {
        this.photo = photo;
    }
    /**
     * @return BufferedImage return the photoBruitee
     */
    public BufferedImage getPhotoBruitee() {
        return photoBruitee;
    }

    /**
     * @param photoBruitee the photoBruitee to set
     */
    public void setPhotoBruitee(BufferedImage photoBruitee) {
        this.photoBruitee = photoBruitee;
    }

    /**
     * @return int return the nbL
     */
    public int getNbL() {
        return nbL;
    }

    /**
     * @param nbL the nbL to set
     */
    public void setNbL(int nbL) {
        this.nbL = nbL;
    }

    /**
     * @return int return the nbC
     */
    public int getNbC() {
        return nbC;
    }

    /**
     * @param nbC the nbC to set
     */
    public void setNbC(int nbC) {
        this.nbC = nbC;
    }

    /**
     * @return BufferedImage return la photo bruitée
     */
    public void noising(BufferedImage photo, double sigma) {
        nbL = photo.getHeight();
        nbC = photo.getWidth();
        this.photoBruitee = new BufferedImage(nbC, nbL, BufferedImage.TYPE_INT_RGB);
        for(int i=0; i<nbL;i++){
            for(int j=0; j<nbC;j++){
                Random random = new Random();
                int newPixel ;
                newPixel = (photo.getRGB(i,j)& 0xff) +(int) (random.nextGaussian()*sigma);
                if(newPixel < 0){
                    newPixel=0;
                }else if(newPixel>255){
                    newPixel=255;
                }
                int newGreyPixel =  (newPixel << 16) | (newPixel << 8) | newPixel;
                photoBruitee.setRGB(i, j, newGreyPixel);
            }
        }
    }

    /**
     * Convertit la valeur d'un pixel en un entier compréhensible entre 0 et 255
     * @param image L'image manipulée
     * @param l nombre de lignes de image
     * @param c nombre de colonnes de image
     * @return Entier correspondant au channel rouge de l'image
     */
    public int getPixelValue(BufferedImage image, int l, int c) {
        int rgb = image.getRGB(c, l);
        return (rgb >> 16) & 0xFF; // Extracting red channel value (assuming 8-bit/channel grayscale image)
    }

    /**
     * Extrait une collection de patchs d'une image bruitée
     * @param s Entier représentant la taille d'un patch
     * @return une liste dynamique de patchs
     */
    public ArrayList<int[][]> extractPatchs(BufferedImage photo, int s) {
        int l = photo.getHeight();
        int c = photo.getWidth();
        ArrayList<int[][]> listPatches = new ArrayList<>();
        int [][] pospat = new int[l][c];
        for(int i=0; i<l-s;i++){
            for(int j=0; j<c-s;j++){
                ArrayList<Integer> listL = new ArrayList<>();
                ArrayList<Integer> listC = new ArrayList<>();
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

                listPatches.add(patch);
                updatePospat(pospat,listL,listC);
            }
        }     
        return listPatches;
    }

    /**
     * Extrait la position d'une image bruitée (meme fonction que extractPatchs, pas le meme retour)
     * @param s Entier représentant la taille d'un patch
     * @return La position des patchs
     */
    public int[][] extractPosPatchs(BufferedImage photo, int s) {
        int l = photo.getHeight();
        int c = photo.getWidth();
        ArrayList<int[][]> listPatches = new ArrayList<>();
        int [][] pospat = new int[l][c];
        for(int i=0; i<l-s;i++){
            for(int j=0; j<c-s;j++){
                ArrayList<Integer> listL = new ArrayList<>();
                ArrayList<Integer> listC = new ArrayList<>();
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

                listPatches.add(patch);
                updatePospat(pospat,listL,listC);
            }
        }     
        return pospat;
    }

    /**
     * Fonction secondaire de extractPatchs() permettant de mettre à jour la position des patchs
     * @param pospat Matrice contenant la position des patchs
     * @param listL 
     * @param listC
     */
    public static void updatePospat(int[][] pospat, ArrayList<Integer> listL, ArrayList<Integer> listC) {
        for (int i = 0; i < listL.size(); i++) {
            for (int j = 0; j < listC.size(); j++) {
                pospat[listL.get(i)][listC.get(j)]++;
            }
        }
    }
    
    /**
     * Convertit une matrice en BufferedImage
     * @param matrix matrice d'entiers
     * @return Une BufferedImage
     */
    public BufferedImage arrayToImage(int[][] matrix) {
        int width = matrix.length;
        int height = matrix[0].length;
        System.out.println("width =" + width + "\theight = " + height);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                int pixel=matrix[i][j];
                System.out.println("The pixel in Matrix: "+pixel);
                bufferedImage.setRGB(i, j, pixel);
                System.out.println("The pixel in BufferedImage: " + bufferedImage.getRGB(i, j));
            }
        }
        return bufferedImage;
    }

    /**
     * Convertit une BufferedImage en matric
     * @param bufferedImage une image
     * @return une matrice d'entiers
     */
    public int[][] imageToArray(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth(null);
        int height = bufferedImage.getHeight(null);
        int[][] pixels = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixels[i][j] = bufferedImage.getRGB(i, j);
            }
        }
        return pixels;
    }

    /**
     * Reconstruit une image à partir d'une collection de patchs
     * @param listPatchs Liste des patchs extraits
     * @param posPatchs Matrice contenant le nombre de patchs dans lequel le pixel à la coordonnée associée apparait
     * @return L'image recréée
     */
    public BufferedImage reconstructPatch(ArrayList<int[][]> listPatchs, int[][] posPatchs) {
        int s = listPatchs.get(0).length; //Taille d'un patch
        int L = posPatchs.length;
        int C = posPatchs[0].length;
        int[][] sommePatch = new int[L][C];
        for (int j = 0; j < C-s; j++){
            for (int i = 0; i < L-s; i++) {
                int[][] patchActuel = new int[s][s];
                for (int iPatch = 0; iPatch<s; iPatch++){
                    for (int jPatch = 0; jPatch<s; jPatch++) {
                        sommePatch[i][j] += patchActuel[iPatch][jPatch];
                    }
                }
            }
        }
        for (int i = 0; i < L; i++) {
            for (int j = 0; j < C; j++) {
                sommePatch[i][j] /= posPatchs[i][j];
            }
        }
        
        BufferedImage image = arrayToImage(sommePatch); //On convertit la matrice en un format d'image
        return image;
    }

    /**
     * Découpe l'image en une collection d'images de taille W
     * @param photo L'image initiale
     * @param tailleW La taille des imagettes extraites
     * @return Une collection de petites images
     */
    public ArrayList<BufferedImage> decoupeImage(BufferedImage photo, int tailleW){
        ArrayList<BufferedImage> listImagette = new ArrayList<>();
        int L = photo.getHeight();
        int C = photo.getWidth();
        int nbImL = L/tailleW; 
        int nbImC = C/tailleW;
        for (int i = 0; i < nbImL; i++) {
            for (int j = 0; j < nbImC; j++) {
                BufferedImage imagette = photo.getSubimage(i*tailleW, j*tailleW, tailleW, tailleW);
                listImagette.add(imagette);
            }
        }
        return listImagette;
    }

    /**
     * Transforme une liste de int[][] en une liste de int[], soit une vectorisation de matrice en s
     * @param listPatchs Liste de matrices
     * @return La liste des patchs vectorisés
     */
    public ArrayList<int[]> vectorPatchs(ArrayList<int[][]> listPatchs) {
        ArrayList<int[]> listPatchVect = new ArrayList<>();
        for (int[][] patch : listPatchs) {
            int s = patch.length;
            int[] patchVect = new int[s*s];

            int k=0; //Indice de parcours de la version vectorisée du patch
            for (int i=0; i<s; i++){
                for (int j=0; j<s; j++){
                    patchVect[k] = patch[i][j];
                    k++;
                }
            }
            listPatchVect.add(patchVect);
        }
        return listPatchVect;
    }
    
    /**
     * 
     * @param coefPostSeuil Matrice des coefficients de la projection des patchs vectorisés dans la base de l'ACP après le seuillage
     * @param mV Vecteur moyen des patchs
     * @param base Matrice de la base orthnormale donnée par l'ACP
     * @return Liste des nouveaux patchs
     */
    public List<double[]> ImageDebr(double[][] coefPostSeuil, double[] mV, double[][] base) {
    	List<double[]> nvPatchList = new ArrayList<>();
        int L = coefPostSeuil.length;
        int C = coefPostSeuil[0].length;

        for (int i = 0; i < L; i++) {
            double[] nvVecteur = new double[mV.length]; //correspond au Z_i de l'énoncé
            for (int j = 0; j < C; j++) {
                nvVecteur[j] = mV[j] + coefPostSeuil[i][j]*base[i][j];
            }
            nvPatchList.add(nvVecteur);
        }

        return nvPatchList;
    }

    /**
     * Convertit une liste de vecteurs de réels en une image visible
     * @param listeVect Liste de vecteurs réels
     * @return Image sous le format BufferedImage
     */
    public BufferedImage toBufferedImage(List<double[]> listeVect) {
        BufferedImage image = new BufferedImage(nbL, nbC, 3);

        for(int i=0; i<nbL; i++) {
            double[] vecteur = listeVect.get(i);
            for(int j = 0; j < nbC; j++) {
                int rgb = (int)vecteur[j]<<16 | (int)vecteur[j] << 8 | (int)vecteur[j];
                image.setRGB(i, j, rgb);
            }
        }
        try {
            ImageIO.write(image, "Doublearray", new File("../donned/Doublearray.jpg"));
            System.out.println("end");
        } catch (Exception e) {
            System.err.println("IMPOSSIBLE D'ECRIRE ICI");
        }
        return image;
    }

/*    
    //  /**
    //  * @param chemin c'est le chemin d'accès npour le fichier
    //  * @param sigma valeur de la variance du bruit gaussien
    //  * @param choixPatch définie la méthode patch, il y'en a deux "local" et "global"
    //  * @param taillePatch définie la taille du patch 
    //  * @param tailleImagette définie la taille de l'imagette dans le cas d'une acp local ou il faut decouper l'image
    //  * @param typeSeuil définie le type de seuil choisi pour la rceconstruction
    //  * @param seuillage
    //  * @return
    //  */
    
    // //l'erreur ici provient du afit que je n'ai pas encore mis le retour de la fonction
    // public  BufferedImage ImageDen(String chemin, double sigma,String choixPatch, int taillePatch,int tailleImagette, String typeSeuil, String seuillage) {
    // 	ACP function=new ACP();
    // 	Photo photo=new Photo(chemin);
    // 	//brutage de la photo
    // 	photo.noising(sigma);
    // 	//extraction des patch de taille s en fonction du choix de la fonctin de patch local ou global
    // 	if(!(choixPatch.equalsIgnoreCase("local") || choixPatch.equalsIgnoreCase("global") )) {
    // 		System.out.print("Choix de la fonction pour l'extraction de patch inconnu !");
    // 	}else {
    // 		if(choixPatch.equalsIgnoreCase("GLOBAL")) {
    // 			//extraction de patch sur l'image global
    // 	    	List<int[][]> listPatchs=photo.extractPatchs(taillePatch);
    // 	    	//vectorisation des patch
    // 	    	List<int[]> vectorPatchD=photo.vectorPatchs(listPatchs);
    // 	    	//converstion des list de patch en double[][]
    // 	    	double [][] vectorPatch;
    // 	    	vectorPatch=function.convertListIntabDouble(vectorPatchD);
    // 	    	//centrage des patch vectorisé
    // 	    	double[][] vecteurCentre=function.calculerVecteursCentres(vectorPatch);
    // 	    	//ACp sur les vecteurs centrée
    // 	    	double[][] baseAcp=function.acp(vecteurCentre);
    // 	    	//projection des vecteurs
    // 	    	double[][] vectProj=function.Proj(baseAcp, vecteurCentre);
    // 	    	//disjonction des cas en fonction du choix du seuillage
    // 	    	if(seuillage.equalsIgnoreCase("dur") || seuillage.equalsIgnoreCase("doux")) {
    // 	    		if(seuillage.equalsIgnoreCase("dur")) {
    // 	    			//disjonction des cas en fonctions du choix du type de seuil
    // 	    			if(typeSeuil.equalsIgnoreCase("seuilV") || typeSeuil.equalsIgnoreCase("seuilB") ) {
    // 	    				if(typeSeuil.equalsIgnoreCase("seuilV")) {
    // 	    					double seuil= function.VisuShrink(photo.nbPixel, sigma);
    // 	    					double[][] 	vectSeuill=function.seuillageDur(seuil, vectProj);
    // 	    				}else {
    // 	    					double seuil= function.BayesShrink(taillePatch, photo.getPhoto());
    // 	    					double[][] 	vectSeuill=function.seuillageDur(seuil, vectProj);
    // 	    				}
    // 	    			}else {
    // 	    	    		System.out.print("Choix du type de seuil inconnu seuil inconnu");
    // 	    			}
    // 	    		}else {
    // 	    			//disjonction des cas en fonctions du choix du type de seuil
    // 	    			if(typeSeuil.equalsIgnoreCase("seuilV") || typeSeuil.equalsIgnoreCase("seuilB") ) {
    // 	    				if(typeSeuil.equalsIgnoreCase("seuilV")) {
    // 	    					double seuil= function.VisuShrink(photo.nbPixel, sigma);
    // 	    					double[][] 	vectSeuill=function.seuillageDoux(seuil, vectProj);
    // 	    				}else {
    // 	    					double seuil= function.BayesShrink(taillePatch, photo.getPhoto());
    // 	    					double[][] 	vectSeuill=function.seuillageDoux(seuil, vectProj);
    // 	    				}
    // 	    			}else {
    // 	    	    		System.out.print("Choix du type de seuil inconnu seuil inconnu");
    // 	    			}
    	    			
    // 	    		}
    // 	    	}else {
    // 	    		System.out.print("Choix de la fonction seuil inconnu");

    // 	    	}
    // 	    	//apres avoir patché et seuillé on passe à la reconstruction de l'image
    // 	    	//il faut d'abord dévectocterirse les patch 
    // 	    	//puis reconstruire les patch , par contre la fonctiojn reconstructPatch nécessite 
    // 	    	//la position des patch et celle ci n'est donnée par aucune fonction(elle devrait etre donnees par la fonction extractPatch)
    // 	    	//donc je suis un peu bloqué
    // 	    	//et il faudrait penser à changer tous les "int" en "double" parce que c'est assez restrictif 
    // 	    	//de considérer que des int en sachant que des doubles sont aussi des int
    // 	    	//ça évitera des conflits et des problèmes de conversion au niveau des tableaux    	    	
    	    		
    	    
    	    	
    // 		}
    // 		//en effet il faut distinguer une acp local et une acp global ce cas est pour l'acp local
    // 		//on découpe l'image en plusieurs imagettes
    // 		//on extractPatch sur chacune des imagettes
    // 		//on vectoriser
    // 		//puis faire une acp sur chacune des imagettes
    // 		//puis faire uns euillage
    // 		//puis reconstruire les imagettes
    // 		//et enfin reconstruire l'image
    // 		//on gros ce bloc reprendra énormément des éléments du bloc précédent
    // 		//il serait donc intéréssant de afctoriser le code pour éviter les répétitions
    // 		else {
    // 			ArrayList<BufferedImage> listImagette =photo.decoupeImage(tailleImagette);
    // 			for(BufferedImage image: listImagette) {
    //     	    	List<int[][]> patch=photo.extractPatchs(taillePatch);
    // 			}

    // 		}
    // 	}
    // }
}