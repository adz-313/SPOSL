public class KeywordParameterDefaultTable 
{
    private String keywordParam;
    private String defaultValue;

    KeywordParameterDefaultTable(String keywordParam, String defaultValue)
    {
        this.keywordParam = keywordParam;
        this.defaultValue = defaultValue;
    }

    public String getKeywordParam()
    {
        return keywordParam;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

}
