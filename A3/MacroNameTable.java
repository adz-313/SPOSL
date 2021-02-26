public class MacroNameTable 
{
    private String macroName;
    private int positionalParam;
    private int keywordParam;
    private int mdtPointer;
    private int kpdtPointer;
    private int expansionTimeVar;
    private int sstPointer;

    MacroNameTable(String macroName, int positionalParam, int keywordParam, int mdtPointer, int kpdtPointer)
    {
        this.macroName = macroName;
        this.positionalParam = positionalParam;
        this.keywordParam = keywordParam;
        this.mdtPointer = mdtPointer;
        this.kpdtPointer = kpdtPointer;
        this.expansionTimeVar = 0;
        this.sstPointer = 0;
    }

    public String getMacroName()
    {
        return macroName;
    }

    public int getPositionalParam()
    {
        return positionalParam;
    }

    public int getKeywordParam()
    {
        return keywordParam;
    }

    public int getMDTPointer()
    {
        return mdtPointer;
    }

    public int getKPDPointer()
    {
        return kpdtPointer;
    } 

    public int getEV()
    {
        return expansionTimeVar;
    } 

    public int getSSTPointer()
    {
        return sstPointer;
    } 

    public void setEV()
    {
        this.expansionTimeVar++;
    }
    
    public void setSSTPointer(int sstPointer)
    {
        this.sstPointer = sstPointer;
    }
}
