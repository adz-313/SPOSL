import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

class MyClass
{
    ArrayList<MacroNameTable> mntab;
    ArrayList<KeywordParameterDefaultTable> kpdtab;
    ArrayList<String> mdtab;
    ArrayList<ParameterNameTable> pntabList;
    ArrayList<String> evntab;
    ArrayList<String> ssntab;

    MyClass()
    {
        mntab = new ArrayList<MacroNameTable>();
        kpdtab = new ArrayList<KeywordParameterDefaultTable>();
        mdtab = new ArrayList<String>();
        pntabList = new ArrayList<ParameterNameTable>();
        evntab = null;
        ssntab = null;
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

    void processMacro(ArrayList<String> macro)
    {
        StringTokenizer tokenizer;
        if(!macro.get(0).equals("MACRO"))
        {
            return;
        }
        String macroDefinition = macro.get(1);
        tokenizer = new StringTokenizer(macroDefinition);
        String macroName = tokenizer.nextToken();
        boolean flag = true;
        int pnCntr = 0, kpdCntr = 0;
        int mdtp = mdtab.size(), kpdtp = kpdtab.size();
        boolean advMacro = false;
        while(tokenizer.hasMoreTokens())
        {
            String token = tokenizer.nextToken();            
            if(!token.contains("="))
            {
                pnCntr++;
                if(flag)
                {
                    pntabList.add(new ParameterNameTable(macroName));
                    flag = false;
                }
                if(token.endsWith(","))
                {
                    pntabList.get(pntabList.size()-1).pntab.add(token.substring(1, token.length()-1));
                }
                else
                {
                    pntabList.get(pntabList.size()-1).pntab.add(token.substring(1, token.length()));
                }
            }
            else
            {
                kpdCntr++;
                String[] temp = token.split("=");    
                temp[0] = temp[0].substring(1, temp[0].length());
                if(flag)
                {
                    pntabList.add(new ParameterNameTable(macroName));
                    flag = false;
                }
                pntabList.get(pntabList.size()-1).pntab.add(temp[0]);
                if(temp.length > 1)
                    if(temp[1].endsWith(","))
                        kpdtab.add(new KeywordParameterDefaultTable(temp[0], temp[1].substring(0, temp[1].length()-1)));
                    else
                        kpdtab.add(new KeywordParameterDefaultTable(temp[0], temp[1]));
                else    
                    kpdtab.add(new KeywordParameterDefaultTable(temp[0], null));
            }
        }        
        mntab.add(new MacroNameTable(macroName, pnCntr, kpdCntr, mdtp+1, kpdtp+1));
        for(int i=2; i<macro.size(); i++)
        {
            tokenizer = new StringTokenizer(macro.get(i));
            String mdtString = "";
            boolean isLastElement = false;
            while(tokenizer.hasMoreTokens())
            {
                String temp = tokenizer.nextToken();    
                if(!tokenizer.hasMoreTokens())
                {
                    isLastElement = true;
                }            
                if(temp.startsWith("&"))
                {
                    temp = temp.substring(1, temp.length());
                }
                if(temp.endsWith(","))
                {
                    temp = temp.substring(0, temp.length()-1);
                }
                if(advMacro)
                {
                    if(evntab == null)
                    {
                        evntab = new ArrayList<String>();
                    }
                    if(ssntab == null)
                    {
                        ssntab = new ArrayList<String>();
                    }
                    evntab.add(temp);
                    mdtString += "(E," + String.valueOf(evntab.indexOf(temp)+1) + ")";
                    if(!isLastElement)
                    {
                        mdtString += ", ";
                    }
                    else
                    {
                        mdtString += " ";
                    }
                    advMacro = false;
                }
                else if(pntabList.get(pntabList.size()-1).pntab.contains(temp))
                {                    
                    mdtString += "(P," + String.valueOf(pntabList.get(pntabList.size()-1).pntab.indexOf(temp)+1) + ")"; 
                    if(!isLastElement)
                    {
                        mdtString += ", ";
                    }
                    else
                    {
                        mdtString += " ";
                    }
                }
                else if(temp.equals("LCL") || temp.equals("GBL")) 
                {
                    mdtString += temp;
                    advMacro = true;
                }
                else
                {
                    mdtString += temp + " ";
                }                
            }
            mdtab.add(mdtString);
        }
    }
    
    public static void main(String[] args) 
    {
        MyClass obj = new MyClass();
        String temp = "";
        String code = ""; 
        try
        {
            FileReader fr = new FileReader("C:\\Users\\Admin\\Desktop\\TE\\Practical\\SPOSL\\A3\\macroInput.asm");
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
        ArrayList<String> macro = new ArrayList<String>();
        int flag = 0;
        StringTokenizer tokenizer = new StringTokenizer(code, "\n");
        while(tokenizer.hasMoreTokens())
        {
            temp = tokenizer.nextToken();
            if(temp.equals("MACRO"))
            {
                flag++;
            }
            if(temp.equals("MEND"))
            {
                macro.add(temp);
                flag--;
                obj.processMacro(macro);
                macro.clear();
            }
            if(flag > 0)
            {
                macro.add(temp);                
            }    
        }      

        //Print output
        for(int i=0; i<obj.pntabList.size(); i++)
        {
            System.out.println("PNTAB for " + obj.pntabList.get(i).macroName);
            for(String s: obj.pntabList.get(i).pntab)
            {
                System.out.println(s);
            }
            System.out.println("");
            System.out.println("");
        }     
        System.out.println("KPDTAB");
        System.out.println("Name    Value");  
        for(KeywordParameterDefaultTable s: obj.kpdtab)
        {
            System.out.println(s.getKeywordParam() + "  " + s.getDefaultValue());
        }
        System.out.println("");
        System.out.println("");
        System.out.println("MNT");
        for(MacroNameTable s: obj.mntab)
        {
            System.out.println(s.getMacroName() + " " + s.getPositionalParam() + " " + s.getKeywordParam() + " " + s.getMDTPointer() + " " + s.getKPDPointer());
        }
        System.out.println("");
        System.out.println("");
        System.out.println("MDT");
        int cntr = 1;
        for(String s: obj.mdtab)
        {
            System.out.println(cntr+ "  " + s);
            cntr++;
        }
        System.out.println("");
        System.out.println("");
        System.out.println("EVNTAB");
        for(String s: obj.evntab)
        {
            System.out.println(s);
        }
        /*for(String s: macro)
        {
            System.out.println(s);
        }*/
    }
}