import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;

class MyClass
{
    ArrayList<MacroNameTable> mntab;
    ArrayList<KeywordParameterDefaultTable> kpdtab;
    ArrayList<String> mdtab;
    ArrayList<ActualParameterTable> aptabList;
    ArrayList<ParameterNameTable> pntabList;

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

    String removeMacroDefinition(String code)
    {
        StringTokenizer tokenizer = new StringTokenizer(code, "\n");
        Stack<String> stack = new Stack<>();
        String codeWithoutMacros = "";
        String temp = "";
        while(tokenizer.hasMoreTokens())
        {
            temp = tokenizer.nextToken();
            if(temp.equals("MACRO"))
            {
                stack.push("MACRO");
            }
            else if(temp.equals("MEND"))
            {
                stack.pop();
            }
            else if(stack.size() == 0)
            {
                codeWithoutMacros += temp + "\n";
            }
        }
        return codeWithoutMacros;
    }

    String generatePass2(String codeWithoutMacros)
    {
        StringTokenizer tokenizer = new StringTokenizer(codeWithoutMacros, "\n");
        String temp = "";
        String passTwo = "";
        boolean flag = false;
        while(tokenizer.hasMoreTokens())
        {
            temp = tokenizer.nextToken();
            StringTokenizer tempTokenizer = new StringTokenizer(temp);
            String macroName = tempTokenizer.nextToken();
            flag = false;
            for(MacroNameTable mnt: mntab)
            {
                if(mnt.getMacroName().equals(macroName))
                {
                    ArrayList<String> arguments = new ArrayList<>();
                    while(tempTokenizer.hasMoreTokens())
                    {
                        arguments.add(tempTokenizer.nextToken());
                    }
                    passTwo += getMacroDefinition(arguments, macroName);
                    flag = true;
                    break;
                }
            }
            if(!flag)
            {
                passTwo += temp + "\n";
            }
        }
        return passTwo;
    }

    private String getMacroDefinition(ArrayList<String> arguments, String macroName)
    {
        ActualParameterTable aptab = new ActualParameterTable(macroName);
        for(String str: arguments)
        {
            if(str.startsWith("&"))
            {
                String[] temp = str.split("=");
                str = temp[1];
            }
            if(str.contains(","))
            {
                str = str.substring(0, str.length()-1);
            }
            aptab.aptab.add(str);
        }
        aptabList.add(aptab);
        String macroDefinition = "";
        for(MacroNameTable mnt: mntab)
        {
            if(mnt.getMacroName().equals(macroName))
            {
                int mdtp = mnt.getMDTPointer();
                String temp = "";
                String temp2 = "";
                for(int i=mdtp-1; i<mdtab.size(); i++)
                {
                    temp = mdtab.get(i);
                    if(temp.contains("MEND"))
                    {
                        break;
                    }
                    // temp is in form of MOVER (P,3), (P,1)
                    // tokenize this string
                    String addToDefinition = "";
                    StringTokenizer tokenizer = new StringTokenizer(temp);
                    while(tokenizer.hasMoreTokens())
                    {
                        temp2 = tokenizer.nextToken();
                        if(temp2.contains("(P,"))
                        {
                            temp2 = temp2.substring(3, 4);
                            // temp2 contains index of parameter from aptab
                            addToDefinition += aptab.aptab.get(Integer.parseInt(temp2)-1) + ", ";
                        }
                        else
                        {
                            addToDefinition += temp2 + " ";
                        }
                    }
                    if(addToDefinition.endsWith(", "))
                    {
                        addToDefinition = addToDefinition.substring(0, addToDefinition.length()-2);
                    }
                    addToDefinition += "\n";
                    macroDefinition += addToDefinition;
                }
            }
        }
        return macroDefinition;
    }

    public static void main(String[] args) {
        MyClass obj = new MyClass();
        obj.loadTables();
        String temp = "";
        String code = "";
        try
        {
            FileReader fr = new FileReader("C:\\Users\\Admin\\Desktop\\TE\\Practical\\SPOSL\\A4\\macroInput.asm");
            BufferedReader br = new BufferedReader(fr);
            while ((temp = br.readLine()) != null) {
                code += temp + "\n";
            }
            br.close();
            fr.close();
        }
        catch(IOException e)
        {
            System.out.println("File not found");
        }
        String codeWithoutMacros = obj.removeMacroDefinition(code);
        String passTwo = obj.generatePass2(codeWithoutMacros);
        System.out.println("****PASS TWO****");
        System.out.println(passTwo);
        for(ActualParameterTable aptab: obj.aptabList)
        {
            System.out.println("Name: " + aptab.macroName);
            for(String str: aptab.aptab)
            {
                System.out.println(str);
            }
        }
    }
}