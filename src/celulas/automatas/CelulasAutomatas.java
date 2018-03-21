package celulas.automatas;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import static java.lang.Integer.parseInt;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

public class CelulasAutomatas implements ActionListener, Runnable {

    JFrame frame_del_proyecto;
    
    JTextArea donde_dibujo;
    
    JTextField Velocidad_de_actualización;
    JTextField Ubicacion;

    JPanel numero_de_celulitas;
    JPanel Bienvenida;
    
    JLabel numero_celulitas;
    JLabel Bienvenida1;

    JButton CasoRandom;
    JButton CasoDeArchivo;

    JButton Salir;
    JButton Reiniciar;

    Thread x;
    
    String Archivo;
    
    int Velocidad_de_actualizacion;

    boolean[][] celulas;
    boolean[][] celulas2;

    boolean parar = false;

    public CelulasAutomatas() { //Constructor, parte de las ventanas y llamado a celulitas

        x = new Thread(this);

        celulas = new boolean[40][90];

        frame_del_proyecto = new JFrame("Autómatas Celulares");

        frame_del_proyecto.setSize(1250, 710);
        frame_del_proyecto.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        donde_dibujo = new JTextArea();
        
        numero_de_celulitas = new JPanel();
        
        Velocidad_de_actualización = new JTextField("Escriba la velocidad de actualizacion en milisegundos");
        Velocidad_de_actualización.setBounds(475, 150, 300, 50);
        
        Ubicacion = new JTextField("Coloque aquí la ubicación del archivo");
        Ubicacion.setBounds(475, 550, 300, 50);

        CasoRandom = new JButton("Caso Random");
        CasoRandom.setBounds(525, 250, 200, 100);
        CasoRandom.addActionListener(this);

        CasoDeArchivo = new JButton("Caso de Archivo");
        CasoDeArchivo.setBounds(525, 400, 200, 100);
        CasoDeArchivo.addActionListener(this);

        Salir = new JButton("Salir");
        Salir.addActionListener(this);

        Reiniciar = new JButton("Reiniciar");
        Reiniciar.addActionListener(this);

        numero_de_celulitas.setBorder(new BevelBorder(BevelBorder.LOWERED));
        numero_de_celulitas.setSize(new Dimension(frame_del_proyecto.getWidth(), 25));

        numero_celulitas = new JLabel("");

        numero_de_celulitas.add(Reiniciar);
        numero_de_celulitas.add(numero_celulitas);
        numero_de_celulitas.add(Salir);

        Bienvenida = new JPanel();
        Bienvenida.setBounds(525, 100, 200, 100);
        Bienvenida1 = new JLabel("Bienvenido al mundo de los Autómatas Celulares");

        Bienvenida.add(Bienvenida1, BorderLayout.CENTER);

        frame_del_proyecto.add(Ubicacion);
        frame_del_proyecto.add(Velocidad_de_actualización);
        frame_del_proyecto.add(CasoDeArchivo);
        frame_del_proyecto.add(CasoRandom);
        frame_del_proyecto.add(Bienvenida);
        
        frame_del_proyecto.setVisible(true);
        frame_del_proyecto.setResizable(false);

        numero_de_celulitas.setSize(250, 50);
        numero_de_celulitas.setVisible(true);

    }

    static String dibujar_celulas(boolean[][] x) { //Crea la cadena
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[i].length; j++) {
                if ((i == 0 || j == 0) || (i == x.length - 1 || j == x[0].length - 1)) {
                    sb.append("■ ");
                } else if (x[i][j]) {
                    sb.append("■ ");
                } else {
                    sb.append("□ ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    static int contador_aux(boolean[][] x, int i, int j) {
        if (i == 0 || j == 0) {
            if (x[i][j]) {
                return 1;
            } else {
                return 0;
            }
        } else if (i > j) {
            if (x[i][j]) {
                return 1 + contador_aux(x, i, j - 1);
            } else {
                return contador_aux(x, i, j - 1);
            }
        } else if (j > i) {
            if (x[i][j]) {
                return 1 + contador_aux(x, i - 1, j);
            } else {
                return contador_aux(x, i - 1, j);
            }
        } else {
            if (x[i][j]) {
                return 1 + contador_aux(x, i - 1, j - 1) + contador_aux(x, i - 1, j) + contador_aux(x, i, j - 1);
            } else {
                return contador_aux(x, i - 1, j - 1) + contador_aux(x, i - 1, j) + contador_aux(x, i, j - 1);
            }
        }
    }

    static String contador(boolean[][] x) {//Función que da el número de células vivas
        int acumulador;
        StringBuilder sb = new StringBuilder();
        acumulador = contador_aux(x, x.length - 1, x.length - 1);
        sb.append(acumulador);
        return sb.toString();
    }

    static boolean[][] funcion_machetazo(boolean[][] x) {
        boolean[][] y;
        if (x.length > x[0].length) {
            y = new boolean[x.length][x.length];
            for (int i = 0; i < x.length; i++) {
                for (int j = 0; j < x[0].length; j++) {
                    y[i][j] = x[i][j];
                }
            }
        } else {
            y = new boolean[x[0].length][x[0].length];
            for (int i = 0; i < x.length; i++) {
                for (int j = 0; j < x[0].length; j++) {
                    y[i][j] = x[i][j];
                }
            }
        }
        return y;
    }

    static int vecinos(boolean[][] x, int i, int j) {//Cuenta la cantidad de vecinos de una celda
        int vecinos = 0;
        if (x[i - 1][j - 1]) {
            vecinos++;
        }
        if (x[i][j - 1]) {
            vecinos++;
        }
        if (x[i + 1][j - 1]) {
            vecinos++;
        }
        if (x[i - 1][j]) {
            vecinos++;
        }
        if (x[i + 1][j]) {
            vecinos++;
        }
        if (x[i - 1][j + 1]) {
            vecinos++;
        }
        if (x[i][j + 1]) {
            vecinos++;
        }
        if (x[i + 1][j + 1]) {
            vecinos++;
        }
        return vecinos;
    }

    static boolean[][] actualizacion(boolean[][] x) { //Hace una nueva matriz
        boolean[][] y = new boolean[x.length][x[0].length];
        for (int i = 1; i < (x.length - 1); i++) {
            for (int j = 1; j < (x[0].length - 1); j++) {
                if (x[i][j]) {
                    y[i][j] = (vecinos(x, i, j) == 2 || vecinos(x, i, j) == 3);

                } else {
                    y[i][j] = (vecinos(x, i, j) == 3);
                }
            }
        }
        return y;
    }

    static void esperar(int milisegundos) {//Para el hilo
        try {
            Thread.sleep(milisegundos);
        } catch (Exception e) {

        }
        //http://www.guillerweb.com/bytes/2009/02/02/esperar-un-tiempo-en-java/comment-page-1/
    }

    static boolean[][] caso_random() {//Hace una matriz booleana c:
        boolean[][] x = new boolean[40][95];
        Random rm = new Random();
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[0].length; j++) {
                x[i][j] = rm.nextBoolean();
            }
        }
        return x;
    }

    static boolean[][] caso_de_archivo(String File) {//Hace una matriz booleana con un archivo
        boolean[][] x = new boolean[40][95];
        FileReader fr = null;

        try {
            fr = new FileReader(File);
            int ch;
            int ch2 = 0;
            boolean flag = true;
            int i = 0;
            int j = 0;
            StringBuilder numeritos = new StringBuilder();
            while ((ch = fr.read()) != -1) {
                if (((ch - 48) <= 10) && ((ch - 48) >= 0)) {
                    numeritos.append((char) ch);
                } else if (((ch2 - 48) <= 10) && ((ch2 - 48) >= 0)) {
                    if (flag) {
                        j = parseInt(numeritos.toString());
                        numeritos.setLength(0);
                        flag = false;
                    } else {
                        i = parseInt(numeritos.toString());
                        x[i][j] = true;
                        numeritos.setLength(0);
                        flag = true;
                    }
                }
                ch2 = ch;
            }
        } catch (Exception e) {

        }
        return x;
    }
    
    void deja_todo_lindo(){
        try{
            Velocidad_de_actualizacion = parseInt(Velocidad_de_actualización.getText());
            }catch(Exception e){
                Velocidad_de_actualizacion = 150;
            }
            frame_del_proyecto.remove(CasoDeArchivo);
            frame_del_proyecto.remove(CasoRandom);
            frame_del_proyecto.remove(Bienvenida);
            frame_del_proyecto.remove(Velocidad_de_actualización);
            frame_del_proyecto.remove(Ubicacion);
            frame_del_proyecto.add(donde_dibujo, BorderLayout.CENTER);
            frame_del_proyecto.add(numero_de_celulitas, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        new CelulasAutomatas();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == CasoRandom) {
            deja_todo_lindo();
            celulas = caso_random();
            celulas2 = celulas;
            x.start();
        } else if (ae.getSource() == CasoDeArchivo) {
            deja_todo_lindo();
            Archivo = Ubicacion.getText();
            celulas = caso_de_archivo(Archivo);
            celulas2 = celulas;
            x.start();
        } else if (ae.getSource() == Salir) {
            System.exit(0);
        } else if (ae.getSource() == Reiniciar) {
            parar = true;
            celulas = celulas2;
            parar = false;
        }

    }

    @Override
    public void run() {
        while (!(parar)) {
            donde_dibujo.setText(dibujar_celulas(celulas));
            celulas = actualizacion(celulas);
            esperar(Velocidad_de_actualizacion);
            numero_celulitas.setText("\t" + "Número de celulas: " + contador(funcion_machetazo(celulas)) + "\t");
        }

    }

}
