import java.util.ArrayList;

class ActualParameterTable 
{
    String macroName;
    ArrayList<String> aptab;

    ActualParameterTable (String macroName)
    {
        this.macroName = macroName;
        aptab = new ArrayList<String>();
    }
}