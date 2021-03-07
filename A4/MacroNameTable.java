public class MacroNameTable 
{
    private String macroName;
    private int positionalParam;
    private int keywordParam;
    private int mdtPointer;
    private int kpdtPointer;

    MacroNameTable(String macroName, int positionalParam, int keywordParam, int mdtPointer, int kpdtPointer)
    {
        this.macroName = macroName;
        this.positionalParam = positionalParam;
        this.keywordParam = keywordParam;
        this.mdtPointer = mdtPointer;
        this.kpdtPointer = kpdtPointer;
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
}
