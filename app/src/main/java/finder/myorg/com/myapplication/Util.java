import java.io.*;

public class Util {
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String s;
        while ((s = in.readLine()) != null) {
            System.out.println(sortString(s));

        }
    }

    static String findRepeatCount(String s, char c, String finalString){
        int temp=0;
        for(int i=0;i<s.length();i++){
            if(String.valueOf(c).equalsIgnoreCase(String.valueOf(s.charAt(i)))){
                temp++;
            }
        }
        return finalString + c + String.valueOf(temp);
    }
    static String sortString(String s){
        String finalString="";
        for(char temp='A';temp <= 'Z';temp++){
            if(String.valueOf(temp).equalsIgnoreCase(s)){
                finalString=findRepeatCount(s,temp,finalString);
            }
        }
        return finalString;
    }

}
