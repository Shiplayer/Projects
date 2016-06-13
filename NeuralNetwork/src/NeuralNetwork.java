import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by anton on 04.06.16.
 */
public class NeuralNetwork {
    Random random;
    private Neuron neuron;
    ConsoleCommander cc;
    Dictionary dictionary;
    char baseChar;

    public static void main(String[] args) throws IOException {
        new NeuralNetwork().run();
    }

    private void run() throws IOException {
        cc = new ConsoleCommander(new InputStreamReader(System.in));
        label:
        while (true) {
            System.out.println("Enter your command: ");
            String command = cc.getCommand();
            switch (command) {
                case "exit":
                    break label;
                case "init":
                    init();
                    break;
                case "learn":
                    learn();
                    break;
                case "test":
                    test();
                    break;
            }
        }
    }

    private void test() throws IOException {
        System.out.println("testing");
        while(true) {
            char Ichar = cc.getSymbol();
            if(Ichar == 0){
                break;
            } else if((Ichar < 'A' || Ichar > 'Z') && (Ichar < 'a' && Ichar > 'z')){
                System.out.println("wrong input");
                continue;
            }
            int[] ch = dictionary.getMyCharByChar(Ichar).data;
            neuron.setInput(ch);
            neuron.Sum();
            System.out.println(neuron.Ans("test"));
            System.out.println("It's " + baseChar + "?");
        }
    }

    private void init() throws IOException {
        random = new Random();
        dictionary = new Dictionary();
        baseChar = cc.getSymbol();
        neuron = new Neuron(7);
        System.out.println("init");
    }

    private void learn() throws IOException {
        System.out.println("learning");
        int count = 0;
        Random random = new Random();
        while(true) {
            neuron.aChar = (char) (random.nextInt('z' - 'A' - 6) + 'A');
            if(neuron.aChar > 'Z')
                neuron.aChar += 6;
            //System.out.println(neuron.aChar);
            int[] ch = dictionary.getMyCharByChar(neuron.aChar).data;
            neuron.setInput(ch);
            neuron.Sum();
            boolean ans = neuron.Ans("learn");
            //System.out.println(ans);
            //System.out.println("It's " + baseChar + "?");
            if(ans && baseChar == neuron.aChar){
                neuron.decWidth();
            } else if(!ans && baseChar == neuron.aChar)
                neuron.incWidth();
            else if(ans && baseChar != neuron.aChar)
                neuron.incWidth();
            else if(!ans && baseChar != neuron.aChar)
                neuron.decWidth();
            count++;
            if(check()){
                break;
            }
        }
        System.out.println("learned(" + count + ")");
    }

    private boolean check() {
        for(MyChar myCh : dictionary.list){
            char ch = myCh.aChar;
            neuron.aChar = ch;
            //System.out.println(neuron.aChar);
            int[] data = dictionary.getMyCharByChar(neuron.aChar).data;
            neuron.setInput(data);
            neuron.Sum();
            boolean ans = neuron.Ans("learn");
            if(ans != (ch == baseChar))
                return false;
        }
        return true;
    }

    class MyChar{
        char aChar;
        int[] data;
        public final int length = 7;

        public MyChar(char ch){
            aChar = ch;
            data = processing(ch);
        }

        private int[] processing(char ch) {
            int[] buf = new int[length];
            int bufCh = ch * (random.nextInt(10) + 1);
            int i = length;
            while(bufCh != 0){
                buf[--i] = (bufCh % 3) * (random.nextInt(10) + 1);
                bufCh /= 3;
            }
            return buf;
        }
    }

    class Dictionary{
        List<MyChar> list;

        Dictionary(){
            list = new ArrayList<>();
            for(char i = 'A'; i <= 'Z'; i++){
                list.add(new MyChar(i));
            }
            for(char i = 'a'; i <= 'z'; i++){
                list.add(new MyChar(i));
            }
        }

        public MyChar getMyCharByChar(char ch){
            for(MyChar buf : list){
                if(buf.aChar == ch)
                    return buf;
            }
            return null;
        }
    }

    class Neuron {
        private int input[];
        private int width[];
        private int limit = 20;
        private int sum = 0;
        public char aChar;

        Neuron(int size){
            input = new int[size];
            width = new int[size];
        }

        /*Neuron(int[] input, int size, char aChar){
            this.input = input;
            width = new int[size];
            this.aChar = aChar;
        }*/

        public void setInput(int[] input){
            this.input = input;
        }

        public void Sum(){
            sum = 0;
            for (int i = 0; i < input.length; i++) {
                sum += input[i] * width[i];
            }
        }

        public void incWidth(){
            for(int i = 0; i < width.length; i++){
                width[i] += input[i];
            }
        }

        public void decWidth(){
            for(int i = 0; i < width.length; i++){
                width[i] -= input[i];
            }
        }

        public boolean Ans(String various){
            if(various.equals("test"))
                System.out.println(Arrays.toString(width) + " " + "Sum = " + sum + " input = " + Arrays.toString(input));
            return sum < limit;
        }
    }

    class ConsoleCommander {
        BufferedReader bf;
        ConsoleCommander(InputStreamReader inputStreamReader){
            bf = new BufferedReader(inputStreamReader);
        }

        public String getCommand() throws IOException {
            return bf.readLine().trim();
        }

        public char getSymbol() throws IOException {
            String buf = bf.readLine();
            if(buf.length() == 0)
                return 0;
            return buf.charAt(0);
        }
    }
}