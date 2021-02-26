import java.io.*;
import java.util.*;

class OpCode
{
    //Opcodes for generation of intermidiate code
    //Using string instead of int for mnemonic beacuse "The literal 08 of type int is out of range Java(536871066)" :(
    String opcodeClass;
    String mnemonic;
    OpCode(){}
    OpCode(String opcodeClass, String mnemonic)
    {
        this.opcodeClass = opcodeClass;
        this.mnemonic = mnemonic;
    }
}

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

class IntermidiateCode
{
    OpCode opCodeField;
    int registerField;
    OpCode symbolField;
    IntermidiateCode(OpCode opCode, int register, OpCode symbol)
    {
        opCodeField = opCode;
        registerField = register;
        symbolField = symbol;
    }
}

class MyClass
{
    private Hashtable<String, OpCode> opcodeTable; 
    private Hashtable<String, Integer> registerTable;
    private Hashtable<String, Integer> conditionTable; 
    private LinkedHashMap<String, Integer> symtab;
    private ArrayList<Literal> littab;
    private ArrayList<String> offsetCounter;
    private ArrayList<IntermidiateCode> intermidiateCode;
    private ArrayList<Integer> pooltab;
    private ArrayList<Integer> locationCounter;
    private int address;
    private boolean assignValuesToLiterals;
    private IntermidiateCode ic;

    MyClass()
    {
        assignValuesToLiterals = false;
        address = 0;
        opcodeTable = new Hashtable<String, OpCode>();
        registerTable = new Hashtable<String, Integer>();
        conditionTable = new Hashtable<String, Integer>();
        symtab = new LinkedHashMap<String, Integer>();
        littab = new ArrayList<Literal>();
        intermidiateCode = new ArrayList<IntermidiateCode>();
        pooltab = new ArrayList<Integer>();
        offsetCounter = new ArrayList<String>();
        locationCounter = new ArrayList<Integer>();
        initOpCodeTable();
        initRegisterTable();
        initConditionTable();
    }

    private void initRegisterTable()
    {
        registerTable.put("AREG", 1);
        registerTable.put("BREG", 2);
        registerTable.put("CREG", 3);
        registerTable.put("DREG", 4);
    }

    private void initConditionTable()
    {
        conditionTable.put("LT", 1);
        conditionTable.put("LE", 2);
        conditionTable.put("EQ", 3);
        conditionTable.put("GT", 4);
        conditionTable.put("GE", 5);
        conditionTable.put("ANY", 6);
    }

    private void initOpCodeTable()
    {
        opcodeTable.put("STOP", new OpCode("IS", "00"));
        opcodeTable.put("ADD", new OpCode("IS", "01"));
        opcodeTable.put("SUB", new OpCode("IS", "02"));
        opcodeTable.put("MULT", new OpCode("IS", "03"));
        opcodeTable.put("MOVER", new OpCode("IS", "04"));
        opcodeTable.put("MOVEM", new OpCode("IS", "05"));
        opcodeTable.put("COMP", new OpCode("IS", "06"));
        opcodeTable.put("BC", new OpCode("IS", "07"));
        opcodeTable.put("DIV", new OpCode("IS", "08"));
        opcodeTable.put("READ", new OpCode("IS", "09"));
        opcodeTable.put("PRINT", new OpCode("IS", "10"));
        opcodeTable.put("START", new OpCode("AD", "01"));
        opcodeTable.put("END", new OpCode("AD", "02"));
        opcodeTable.put("ORIGIN", new OpCode("AD", "03"));
        opcodeTable.put("EQU", new OpCode("AD", "04"));
        opcodeTable.put("LTORG", new OpCode("AD", "05"));
        opcodeTable.put("DC", new OpCode("DL", "01"));
        opcodeTable.put("DS", new OpCode("DL", "02"));
    }

    //Return index from linkedhashmap using symbol/literal name. Index starts from 1.
    private int getIndex(LinkedHashMap<String, Integer> temp, String key)
    {
        int cntr = 0;
        for(Map.Entry<String, Integer> m: temp.entrySet())
        {
            cntr++;
            if(m.getKey().equals(key))
            {
                break;
            }
        }
        return cntr;
    }

    //Return index from array list using symbol/literal name. Index starts from 0.
    private int getIndex(ArrayList<Literal>temp, String key)
    {
        for(int i=0; i<temp.size(); i++)
        {
            if(temp.get(i).literal.equals(key))
            {
                //When returning index from littab, check pooltab
                if(temp == littab)
                {
                    //If pooltab is empty, return index
                    if(pooltab.size() == 0)
                    {
                        return i;
                    }
                    //Else return index of literal from last pooltab only
                    else 
                    {
                        if(i > pooltab.get(pooltab.size() - 1))
                        {
                            return i;
                        }
                    }
                }
                else
                {
                    return i;
                }
            }
        }
        return -1;
    }

    private String[] getTokens(StringTokenizer tokenizer)
    {
        String[] strings = new String[tokenizer.countTokens()];
        for(int i=0; i<strings.length; i++)
        {
            strings[i] = tokenizer.nextToken();
        }
        return strings;
    }

    //Assign addresses to literals after LTORG statement
    private void ltorgSet()
    {
        int flag = 1;
        String[] temp = null;
        for(int i=0; i<littab.size(); i++)
        {
            if(littab.get(i).address == -1)
            {
                if(flag == 1)
                {
                    pooltab.add(i+1);
                    flag = 0;
                }
                littab.get(i).address = address;
                locationCounter.add(address);
                temp = littab.get(i).literal.split("'");
                intermidiateCode.add(new IntermidiateCode(new OpCode("DL", "01"), 0, new OpCode("C", temp[1])));
                address++;
            }
        }
        assignValuesToLiterals = false;
    }

    private IntermidiateCode getIntermidiateCode(String[] operation) throws NullPointerException
    {
        IntermidiateCode ic;
        OpCode opCode = opcodeTable.get(operation[0]);
        int register = 0;
        OpCode symbol = null;
        String tempAddress = "";
        if(assignValuesToLiterals)
        {
            ltorgSet();
        }
        //Label field has value 
        if(opCode == null)
        {
            opCode = opcodeTable.get(operation[1]);
        }
        if(opCode.opcodeClass.equals("AD"))
        {
            locationCounter.add(-1);
            if(operation[0].equals("START"))
            {
                if(operation.length > 1)
                {
                    address = Integer.parseInt(operation[1]);
                    symbol = new OpCode("C", operation[1]);
                }
                else
                {
                    address = 0;
                    symbol = null;
                }
            }
            else if(operation[0].equals("END") || operation[0].equals("LTORG"))
            {
                assignValuesToLiterals = true;
            }
            else if(operation[0].equals("ORIGIN"))
            {
                String[] arr = null;
                if(operation[1].contains("+"))
                {
                    arr = operation[1].split("\\+");
                    address = symtab.get(arr[0]) + Integer.parseInt(arr[1]);
                    //For ORIGIN, store the offset in register field after multplying with -1 and print later
                    register = -1;
                    offsetCounter.add("+" + arr[1]);
                    
                }
                else if(operation[1].contains("-"))
                {
                    arr = operation[1].split("-");
                    address = symtab.get(arr[0]) - Integer.parseInt(arr[1]);
                    //For ORIGIN, store the offset in register field after multplying with -1 and print later
                    register = -1;
                    offsetCounter.add("-" + arr[1]);
                }
                else
                {
                    arr = new String[1];
                    arr[0] = operation[1];
                    address = symtab.get(arr[0]);
                }        
                tempAddress = getIndex(symtab, arr[0]) < 10 ? "0" + String.valueOf(getIndex(symtab, arr[0])) : String.valueOf(getIndex(symtab, arr[0]));       
                symbol = new OpCode("S", tempAddress);  
            }
            else if(operation.length > 1 && operation[1].equals("EQU"))
            {
                if(symtab.get(operation[2]) != null)
                {
                    if(symtab.containsKey(operation[0]))
                    {
                        symtab.replace(operation[0], symtab.get(operation[2]));
                    }
                    else
                    {
                        symtab.put(operation[0], symtab.get(operation[2]));
                    }
                    tempAddress = getIndex(symtab, operation[2]) < 10 ? "0" + String.valueOf(getIndex(symtab, operation[2])) : String.valueOf(getIndex(symtab, operation[2]));
                }
                else
                {
                    String[] temp = null;
                    if(operation[2].contains("+"))
                    {
                        temp = operation[2].split("\\+");
                        if(symtab.containsKey(operation[0]))
                        {
                            symtab.replace(operation[0], symtab.get(temp[0]) + Integer.parseInt(temp[1]));
                        }
                        else
                        {
                            symtab.put(operation[0], symtab.get(temp[0]) + Integer.parseInt(temp[1]));
                        }
                        offsetCounter.add("+" + temp[1]);
                    }
                    else if(operation[2].contains("-"))
                    {
                        temp = operation[2].split("-");
                        if(symtab.containsKey(operation[0]))
                        {
                            symtab.replace(operation[0], symtab.get(temp[0]) - Integer.parseInt(temp[1]));
                        }
                        else
                        {
                            symtab.put(operation[0], symtab.get(temp[0]) - Integer.parseInt(temp[1]));
                        }
                        offsetCounter.add("-" + temp[1]);
                    }                  
                    tempAddress = getIndex(symtab, temp[0]) < 10 ? "0" + String.valueOf(getIndex(symtab, temp[0])) : String.valueOf(getIndex(symtab, temp[0]));
                    //For EQU, store the offset in register field after multplying with -1 and print later
                    register = -1;
                }                
                symbol = new OpCode("S", tempAddress);  
            }
        }
        else if(opCode.opcodeClass.equals("IS"))
        {
            locationCounter.add(address);
            int posn = 0;
            if(opcodeTable.get(operation[posn]) == null)
            {
                if(symtab.containsKey(operation[posn]))
                {
                    symtab.replace(operation[posn], address);
                }
                else
                {
                    symtab.put(operation[posn], address);
                }
                posn++;
            }
            if(operation[posn].equals("STOP"))
            {
                register = 0;
                symbol = null;
            }
            if(operation.length >= 2)
            {
                if(operation[posn].equals("BC"))
                {
                    //To get conditional from Condition table
                    register = conditionTable.get(operation[posn+1]);
                }
                else
                {
                    if(registerTable.get(operation[posn+1].split(",")[0]) == null)
                    {
                        register = 0;
                        symtab.putIfAbsent(operation[posn+1], -1);
                    }
                    else
                    {
                        register = registerTable.get(operation[posn+1].split(",")[0]);
                    }
                }
                if(operation.length >= 3)
                {
                    if(operation.length == 3 && posn == 1)
                    {                        
                        symtab.putIfAbsent(operation[posn+1], -1);
                        tempAddress = getIndex(symtab, operation[posn+1]) < 10 ? "0" + String.valueOf(getIndex(symtab, operation[posn+1])) : String.valueOf(getIndex(symtab, operation[posn+1]));
                        symbol = new OpCode("S", tempAddress);
                        System.out.println("S" +tempAddress);
                    }
                    /*if(operation.length == 3)
                    {
                        if(posn == 1)
                        {
                            symtab.putIfAbsent(operation[posn+1], -1);
                            tempAddress = getIndex(symtab, operation[posn+1]) < 10 ? "0" + String.valueOf(getIndex(symtab, operation[posn+1])) : String.valueOf(getIndex(symtab, operation[posn+1]));
                            symbol = new OpCode("S", tempAddress);
                        }
                        else
                        {
                            int index = getIndex(littab, operation[posn+1]);
                            if(index == -1 || littab.get(index).address != -1)
                            {
                                littab.add(new Literal(operation[posn+1], -1));
                            }
                            tempAddress = getIndex(littab, operation[posn+1]) + 1 < 10 ? "0" + String.valueOf(getIndex(littab, operation[posn+1]) + 1) : String.valueOf(getIndex(littab, operation[posn+1]) + 1);
                            symbol = new OpCode("L", tempAddress);
                        }
                    }*/
                    else
                    {
                        if(!operation[posn+2].startsWith("="))
                        {
                            symtab.putIfAbsent(operation[posn+2], -1);
                            tempAddress = getIndex(symtab, operation[posn+2]) < 10 ? "0" + String.valueOf(getIndex(symtab, operation[posn+2])) : String.valueOf(getIndex(symtab, operation[posn+2]));
                            symbol = new OpCode("S", tempAddress);
                        }
                        else
                        {
                            int index = getIndex(littab, operation[posn+2]);
                            System.out.println("literal: " + operation[posn+2] + " index: " + index);
                            if(index == -1 || littab.get(index).address != -1)
                            {
                                littab.add(new Literal(operation[posn+2], -1));
                            }
                            System.out.println(getIndex(littab, operation[posn+2]) + 1);
                            tempAddress = getIndex(littab, operation[posn+2]) + 1 < 10 ? "0" + String.valueOf(getIndex(littab, operation[posn+2]) + 1) : String.valueOf(getIndex(littab, operation[posn+2]) + 1);
                            symbol = new OpCode("L", tempAddress);
                            System.out.println("L" +tempAddress);
                        }
                    }
                }
                else
                {
                    tempAddress = getIndex(symtab, operation[posn+1]) < 10 ? "0" + String.valueOf(getIndex(symtab, operation[posn+1])) : String.valueOf(getIndex(symtab, operation[posn+1]));
                    symbol = new OpCode("S", tempAddress);  
                }
            }
            /*if(operation.length == 1)
            {
                register = 0;
                symbol = null;
            }
            else if(operation.length == 2)
            {
                register = 0;
                symtab.putIfAbsent(operation[1], -1);
                tempAddress = getIndex(symtab, operation[1]) < 10 ? "0" + String.valueOf(getIndex(symtab, operation[1])) : String.valueOf(getIndex(symtab, operation[1]));
                symbol = new OpCode("S", tempAddress);  
            }
            else 
            {
                int operand = 0;
                int flag = 0;             
                if(operation.length == 4)
                {
                    operand++;
                    flag = 1;
                }
                if(operation[operand].equals("BC"))
                {
                    //To get conditional from Condition table
                    register = conditionTable.get(operation[operand+1]);
                }
                else
                {
                    register = registerTable.get(operation[operand+1].split(",")[0]);
                }
                if(!operation[operand+2].startsWith("="))
                {
                    symtab.putIfAbsent(operation[operand+2], -1);
                    tempAddress = getIndex(symtab, operation[operand+2]) < 10 ? "0" + String.valueOf(getIndex(symtab, operation[operand+2])) : String.valueOf(getIndex(symtab, operation[operand+2]));
                    symbol = new OpCode("S", tempAddress);
                }
                else
                {
                    int index = getIndex(littab, operation[operand+2]);
                    if(index == -1 || littab.get(index).address != -1)
                    {
                        littab.add(new Literal(operation[operand+2], -1));
                    }
                    tempAddress = getIndex(littab, operation[operand+2]) + 1 < 10 ? "0" + String.valueOf(getIndex(littab, operation[operand+2]) + 1) : String.valueOf(getIndex(littab, operation[operand+2]) + 1);
                    symbol = new OpCode("L", tempAddress);
                }
                if(flag == 1)
                {
                    if(symtab.containsKey(operation[operand-1]))
                    {
                        symtab.replace(operation[operand-1], address);
                    }
                    else
                    {
                        symtab.put(operation[operand-1], address);
                    }
                    flag = 0;
                }
            }*/
            address++;
        }
        else
        {
            if(operation[1].equals("DC"))
            {
                symtab.put(operation[0], address);
                register = 0;
                symbol = new OpCode("C", operation[2]);
                locationCounter.add(address);
                address++;
            }
            else
            {
                symtab.put(operation[0], address);
                register = 0;
                symbol = new OpCode("C", operation[2]);
                locationCounter.add(address);
                address = address + Integer.parseInt(operation[2]);
            }
        }
        ic = new IntermidiateCode(opCode, register, symbol);
        return ic;
    }

    private void printToConsole()
    {
        System.out.println("*****PASS ONE*****");
        System.out.println("");
        System.out.println("");
        System.out.println("***SYMTAB***");
        for(Map.Entry<String, Integer> m: symtab.entrySet())
        {
            System.out.println(m.getKey() + " " + m.getValue());
        }
        System.out.println("");
        System.out.println("");
        System.out.println("***LITTAB***");
        for(int i=0; i<littab.size(); i++)
        {
            System.out.println(littab.get(i).literal + " " + littab.get(i).address);
        }
        System.out.println("");
        System.out.println("");
        System.out.println("***POOLTAB***");
        for(int i=0; i<pooltab.size(); i++)
        {
            System.out.println(pooltab.get(i));
        }
        System.out.println("");
        System.out.println("");
        System.out.println("*LC*   ***IC***");
        int cntr = 0;
        for(int i=0; i<intermidiateCode.size(); i++)
        {
            System.out.print(locationCounter.get(i) + "  ");
            ic = intermidiateCode.get(i);
            System.out.print("(" + ic.opCodeField.opcodeClass + ", " + ic.opCodeField.mnemonic + ")");
            if(ic.registerField > 0)
            {
                System.out.print("(" + ic.registerField + ")");
            }
            if(ic.symbolField != null)
            {
                System.out.print("(" + ic.symbolField.opcodeClass + ", " + ic.symbolField.mnemonic + ")");
            }
            //For ORIGIN and EQU statements only
            if(ic.registerField < 0)
            {
                System.out.print(offsetCounter.get(cntr));
                cntr++;
            }
            System.out.print("\n");
        }
    }

    private void saveToFile()
    {
        try 
        {
            FileWriter fw = new FileWriter("C:\\Users\\Admin\\Desktop\\TE\\Practical\\SPOSL\\A2\\symtab.txt");
            for(Map.Entry<String, Integer> m: symtab.entrySet())
            {
                fw.write(m.getKey() + " " + m.getValue() + "\n");
            }
            fw.close();
            fw = new FileWriter("C:\\Users\\Admin\\Desktop\\TE\\Practical\\SPOSL\\A2\\littab.txt");
            for(int i=0; i<littab.size(); i++)
            {
                fw.write(littab.get(i).literal + " " + littab.get(i).address + "\n");
            }
            fw.close();
            fw = new FileWriter("C:\\Users\\Admin\\Desktop\\TE\\Practical\\SPOSL\\A2\\pooltab.txt");
            for(int i=0; i<pooltab.size(); i++)
            {
                fw.write(pooltab.get(i) + "\n");
            }
            fw.close();
            fw = new FileWriter("C:\\Users\\Admin\\Desktop\\TE\\Practical\\SPOSL\\A2\\intermidiatecode.txt");
            int cntr = 0;
            for(int i=0; i<intermidiateCode.size(); i++)
            {
                fw.write(locationCounter.get(i) + " ");
                ic = intermidiateCode.get(i);
                fw.write("(" + ic.opCodeField.opcodeClass + ", " + ic.opCodeField.mnemonic + ")");
                if(ic.registerField > 0)
                {
                    fw.write("(" + ic.registerField + ")");
                }
                if(ic.symbolField != null)
                {
                    fw.write("(" + ic.symbolField.opcodeClass + ", " + ic.symbolField.mnemonic + ")");
                }
                //For ORIGIN statements only
                if(ic.registerField < 0)
                {
                    fw.write(offsetCounter.get(cntr));
                    cntr++;
                }
                fw.write("\n");
            }
            fw.close();
        } 
        catch (IOException e) 
        {
            System.out.println("File not found");
        }
    }

    public static void main(String[] args) 
    {
        //MyClass object to access non static member functions
        MyClass obj = new MyClass();
        //Read assembly file
        String temp = "";
        String code = ""; 
        try
        {
            FileReader fr = new FileReader("C:\\Users\\Admin\\Desktop\\TE\\Practical\\SPOSL\\A1\\assemblyCode3.asm");
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
        //Break asembly code statement wise
        String[] command = obj.getTokens(new StringTokenizer(code, "\n"));
        /*for(String s: command)
        {
            System.out.println(s);
        }*/
        //String[] command = code.split("\n");
        for(int i=0; i<command.length; i++)
        {

            try {
                obj.ic = obj.getIntermidiateCode(obj.getTokens(new StringTokenizer(command[i])));
            } catch (NullPointerException e) {
                //TODO: handle exception
                System.out.println(e);
            } catch(ArrayIndexOutOfBoundsException ex)
            {
                System.out.println(ex);
            }
            if(obj.ic != null)
            {
                obj.intermidiateCode.add(obj.ic);
            }
        }
        //Assign addresses to literals after end statement
        if(obj.assignValuesToLiterals)
        {
            obj.ltorgSet();
        }
        //Display output
        obj.printToConsole();
        obj.saveToFile();
    }
}