import java.util.ArrayList;

class ParameterNameTable 
{
    String macroName;
    ArrayList<String> pntab;

    ParameterNameTable(String macroName)
    {
        this.macroName = macroName;
        pntab = new ArrayList<String>();
    }
}