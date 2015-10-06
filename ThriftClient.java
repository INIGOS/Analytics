
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TSSLTransportFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.List;

public class ThriftClient
{
    String ip;
    int port;
    TTransport transport = null;
    PyInterface.Client client = null;
    TProtocol protocol = null;
    static Pattern hashtagPattern = Pattern.compile("#(\\w+|\\W+)");

    public ThriftClient(String s, int p){
        this.ip= s;
        this.port = p;
        if (transport == null)
            transport = new TSocket(this.ip, this.port);
        if (protocol == null)
        {
            protocol = new TBinaryProtocol(transport);
            client = new PyInterface.Client(protocol);
            try {
                transport.open();
            } catch (TTransportException e) {
                e.printStackTrace();
            }
        }

    }

    //public String getSentimentScore(String mainText,String textType)
    public String getSentimentScore(String mainText)
    {
        try {
            SentiRequestObject obj = new SentiRequestObject();
            obj.setMainText(mainText);
            //obj.setTextType(textType);
            
            int senti = client.getSentimentScore(obj);
            return ("" + senti);
        } catch (TTransportException e) {
            e.printStackTrace();
            transport.close();
        } catch (TException e) {
            e.printStackTrace();
            transport.close();
        }
        transport.close();
        return "Connection to " + this.ip + ":" + this.port + " failed!";
    }

    public String getSentimentScore(String mainText,String title,String topDomain,String subDomain)
    {
        try {
            SentiRequestObject obj = new SentiRequestObject();
            obj.setMainText(mainText);
            obj.setTextType("reviews");
            obj.setTitle(title);
            obj.setTopDomain(topDomain);
            obj.setSubDomain(subDomain);
            int senti = client.getSentimentScore(obj);
            return ("" + senti);
        } catch (TTransportException e) {
            e.printStackTrace();
            transport.close();
        } catch (TException e) {
            e.printStackTrace();
            transport.close();
        }
        transport.close();
        return "Connection to " + this.ip + ":" + this.port + " failed!";
    }
    public String getSentimentScore(String mainText,String title,String middleParas,String lastPara, int diffBlog)
    {
        // "diffBlog" parameter can be set with any integer (added as a dummy parameter to support method overloading)
        try {

            SentiRequestObject obj = new SentiRequestObject();
            obj.setMainText(mainText);
            obj.setTextType("blogs_news");
            obj.setTitle(title);
            obj.setMiddleParas(middleParas);
            obj.setLastPara(lastPara);

            int senti = client.getSentimentScore(obj);

            return ("" + senti);

        } catch (TTransportException e) {
            e.printStackTrace();
            transport.close();
        } catch (TException e) {
            e.printStackTrace();
            transport.close();
        }
        transport.close();
        return "Connection to " + this.ip + ":" + this.port + " failed!";
    }
    public List<String> getTopics(String mainText)
    {
        try
        {
            List<String> topics = client.getTopics(mainText);
            return topics;

        }catch (TTransportException e) {
            e.printStackTrace();
            transport.close();
        } catch (TException e) {
            e.printStackTrace();
            transport.close();
        }
        transport.close();
        return null;
    }
    public String getAcronyms(String mainText)
    {
        try
        {
	   String Acronyms=client.ExAcro(mainText);
	   return Acronyms;
        }catch (TTransportException e) {
            e.printStackTrace();
            transport.close();
        } catch (TException e) {
            e.printStackTrace();
            transport.close();
        }
        transport.close();
        return null;
    }
    public List<String> extractHashtags(String text)
    {
        Matcher mat = hashtagPattern.matcher(text);
        List<String> hashtags=new ArrayList<String>();
        while (mat.find())
        {
            hashtags.add(mat.group());
        }
        return hashtags;
    }
    public String getEmoticons(String mainText)
    {
        try
        {
	   String Emoticons=client.ExEmo(mainText);
	   return Emoticons;
        }catch (TTransportException e) {
            e.printStackTrace();
            transport.close();
        } catch (TException e) {
            e.printStackTrace();
            transport.close();
        }
        transport.close();
        return null;
    }
	

   public static void main(String [] Args)
   {
     ThriftClient c = new ThriftClient("localhost",8002);
     String text="hey lol lysm idiot #health ,hey bro :) long tym :p no #tweet c :( ";
     System.out.println(c.getSentimentScore(text));
    
    System.out.println(c.getTopics(text));
    System.out.println(c.getAcronyms(text));
    System.out.println(c.getEmoticons(text));
    System.out.println(c.extractHashtags(text));
    
    
   }

}
