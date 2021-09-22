namespace RemoteServer.Library.Catalog
{
    public class AmazonString
    {
        public string Language { get; set; }
        public string Value { get; set; }

        public AmazonString(string value)
        {
            Value = value;
            Language = "en";
        }
    }
}