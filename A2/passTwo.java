import java.io.*;
import java.util.*;

class Literal
{
    String literal;
    int address;
    Literal(String literal, int address)
    {
        this.literal = literal;
        this.address = address;
    }
}

class Instruction
{
    int lc;
    char sign;
    String opcode, memory;
    int register;
    Instruction(int lc, char sign, String opcode, int register, String memory)
    {
        this.lc = lc;
        this.sign = sign;
        this.opcode = opcode;
        this.register = register;
        this.memory = memory;
    }
}

class SortByLC implements Comparator<Instruction>
{
    public int compare(Instruction o1, Instruction o2) 
    {
        return o1.lc - o2.lc;
    }
}

class MyClass
{
    private ArrayList<String> symtab;
    private ArrayList<String> littab;

    MyClass()
    {
        symtab = new ArrayList<String>();
        littab = new ArrayList<String>();
        loadSYMTAB();
        loadLITTAB();
    }

    void loadSYMTAB()
    {
        try 
        {
            String temp;
            FileReader fr = new FileReader("C:\\Users\\Admin\\Desktop\\TE\\Practical\\SPOSL\\A2\\symtab.txt");
            BufferedReader br = new BufferedReader(fr);
            while((temp = br.readLine()) != null) 
            {
                symtab.add(temp.split(" ")[1]);
            } 
            br.close();
            fr.close();
        } 
        catch (IOException e) 
        {
            System.out.println("File not found");
        }
    }

    void loadLITTAB()
    {
        try 
        {
            String temp;
            FileReader fr = new FileReader("C:\\Users\\Admin\\Desktop\\TE\\Practical\\SPOSL\\A2\\littab.txt");
            BufferedReader br = new BufferedReader(fr);
            while((temp = br.readLine()) != null) 
            {
                littab.add(temp.split(" ")[1]);
            } 
            br.close();
            fr.close();
        } 
        catch (IOException e) 
        {
            System.out.println("File not found");
        }
    }

    Instruction getMachineInstruction(String code)
    {
        String[] temp = code.split(" ", 2);
        String opcode = "", memory = "";
        int register = 0;
        String[] memoryInfo = null;
        int lc = Integer.parseInt(temp[0]);
        if(lc == -1)
        {
            return null;
        }
        String ic = temp[1];
        ArrayList<Integer> openList = new ArrayList<Integer>();
        ArrayList<Integer> closeList = new ArrayList<Integer>();
        for(int i=0; i<ic.length(); i++)
        {
            if(ic.charAt(i) == '(')
            {
                openList.add(i);
            }
            if(ic.charAt(i) == ')')
            {
                closeList.add(i);
            }
        }
        String[] opcodeInfo = ic.substring(openList.get(0), closeList.get(0)).split(",");
        if(opcodeInfo[0].contains("DL") && opcodeInfo[1].contains("02"))
        {
            return null;
        }
        else if(opcodeInfo[0].contains("DL") && opcodeInfo[1].contains("01"))
        {
            opcode = "00";
            memoryInfo = ic.substring(openList.get(1)+1, closeList.get(1)).split(",");
            if(memoryInfo[1].substring(1).length() < 2)
            {
                memory = "00" + memoryInfo[1].trim();
            }
            else
            {
                memory = "0" + memoryInfo[1].trim();
            }
        }
        else
        {
            opcode = opcodeInfo[1].trim();
        }
        boolean flag = false;
        if(openList.size() == 1) 
        {
            memory = "000";
        }
        else if(openList.size() == 2)
        {
            flag = true;
            memoryInfo = ic.substring(openList.get(1)+1, closeList.get(1)).split(",");
        }
        else 
        {
            flag = true;
            register = Integer.parseInt(ic.substring(openList.get(1)+1, closeList.get(1)));
            memoryInfo = ic.substring(openList.get(2)+1, closeList.get(2)).split(",");
        }
        if(flag)
        {
            if(memoryInfo[0].equals("S"))
            {
                memory = String.valueOf(symtab.get(Integer.parseInt(memoryInfo[1].substring(1))-1));
            }
            if(memoryInfo[0].equals("L"))
            {
                memory = String.valueOf(littab.get(Integer.parseInt(memoryInfo[1].substring(1))-1));
            }
        }
        Instruction instruction = new Instruction(lc, '+', opcode, register, memory);
        return instruction;
    }
    
    public static void main(String[] args) 
    {
        MyClass obj = new MyClass();
        String intermidiateCode="", temp;
        ArrayList<Instruction> passTwo = new ArrayList<Instruction>();
        try
        {
            FileReader fr = new FileReader("C:\\Users\\Admin\\Desktop\\TE\\Practical\\SPOSL\\A2\\intermidiatecode.txt");
            BufferedReader br = new BufferedReader(fr);
            while((temp = br.readLine()) != null) 
            {
                intermidiateCode += temp + "\n"; 
            } 
            br.close();
            fr.close();
            for(String code: intermidiateCode.split("\n"))
            {
                Instruction i =  obj.getMachineInstruction(code);
                if(i != null)
                {
                    passTwo.add(i);
                }
            }
            Collections.sort(passTwo, new SortByLC());
            for(Instruction i : passTwo)
            {
                System.out.println(i.lc + ". " + i.sign + " " + i.opcode + " " + i.register + " " + i.memory);
            }
        }
        catch(IOException e)
        {
            System.out.println("File not found");
        }
    }
}