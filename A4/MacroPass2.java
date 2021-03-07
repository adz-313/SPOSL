import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

class MyClass
{
    ArrayList<MacroNameTable> mntab;
    ArrayList<KeywordParameterDefaultTable> kpdtab;
    ArrayList<String> mdtab;
    ArrayList<ActualParameterTable> aptabList;

    MyClass()
    {
        mntab = new ArrayList<MacroNameTable>();
        kpdtab = new ArrayList<KeywordParameterDefaultTable>();
        mdtab = new ArrayList<String>();
        aptabList = new ArrayList<ActualParameterTable>();
    }

    public int getIndex(String keyword)
    {
        int index = -1;
        boolean flag = false;
        for (KeywordParameterDefaultTable kpdtp : kpdtab) 
        {
            index++;
            if(kpdtp.getKeywordParam().equals(keyword))
            {
                flag = true;
                break;
            }   
        }
        if(flag)
            return index;
        else
            return -1;
    }

    private void loadTables()
    {
        String temp = "";
        try
        {
            FileReader fr = new FileReader("C:\\Users\\Admin\\Desktop\\TE\\Practical\\SPOSL\\A4\\mntab.txt");
            BufferedReader br = new BufferedReader(fr);
            while ((temp = br.readLine()) != null) 
            {
                StringTokenizer tokenizer = new StringTokenizer(temp);
                while(tokenizer.hasMoreTokens())
                {
                    String macroName = tokenizer.nextToken();
                    int pp = Integer.parseInt(tokenizer.nextToken());
                    int kp = Integer.parseInt(tokenizer.nextToken());
                    int mdtp = Integer.parseInt(tokenizer.nextToken());
                    int kpdtp = Integer.parseInt(tokenizer.nextToken());
                    mntab.add(new MacroNameTable(macroName, pp, kp, mdtp, kpdtp));
                }
            } 
            fr.close();
            fr = new FileReader("C:\\Users\\Admin\\Desktop\\TE\\Practical\\SPOSL\\A4\\kpdtab.txt");
            br = new BufferedReader(fr);
            while ((temp = br.readLine()) != null) 
            {
                StringTokenizer tokenizer = new StringTokenizer(temp);
                String keywordParam = tokenizer.nextToken();
                String defaultValue = null;
                if(tokenizer.hasMoreTokens())
                {
                    defaultValue = tokenizer.nextToken();
                }
                kpdtab.add(new KeywordParameterDefaultTable(keywordParam, defaultValue));
            }
            fr.close();
            fr = new FileReader("C:\\Users\\Admin\\Desktop\\TE\\Practical\\SPOSL\\A4\\mdtab.txt");
            br = new BufferedReader(fr);
            while ((temp = br.readLine()) != null) 
            {
                mdtab.add(temp);
            }
            br.close();
            fr.close();
        }
        catch(IOException e)
        {
            System.out.println("File not found");
        }
    }
    public static void main(String[] args) {
        MyClass obj = new MyClass();
        obj.loadTables();
        
    }
}