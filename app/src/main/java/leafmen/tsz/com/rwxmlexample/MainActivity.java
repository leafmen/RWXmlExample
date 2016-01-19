package leafmen.tsz.com.rwxmlexample;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends Activity {

    private Button btn_SaxToXml;
    private Button btn_PullToXml;
    private Button btn_DomToXml;
    private Button btn_ClearData;
    private ListView lv_ReadXml;
    private List<HashMap<String, Object>> data;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv_ReadXml = (ListView) findViewById(R.id.lv_ReadXml);
        btn_SaxToXml = (Button) findViewById(R.id.btn_SaxToXml);
        btn_PullToXml = (Button) findViewById(R.id.btn_PullToXml);
        btn_DomToXml = (Button) findViewById(R.id.btn_DomToXml);
        btn_ClearData = (Button) findViewById(R.id.btn_ClearData);
        btn_ClearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveAllData(data, adapter, lv_ReadXml);
            }
        });

        btn_SaxToXml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    RemoveAllData(data, adapter, lv_ReadXml);
                    //获取到集合数据
                    List<Channel> channels = getChannelList();
                    data = new ArrayList<HashMap<String, Object>>();
                    for (Channel channel : channels) {
                        HashMap<String, Object> item = new HashMap<String, Object>();
                        item.put("id", channel.getId());
                        item.put("name", channel.getName());
                        item.put("url", channel.getUrl());
                        data.add(item);
                    }

                    //创建SimpleAdapter适配器将数据绑定到item显示控件上
                    adapter = new SimpleAdapter(MainActivity.this, data, R.layout.item, new String[]{"id", "name", "url"}, new int[]{R.id.id, R.id.name, R.id.url});
                    //实现列表的显示
                    lv_ReadXml.setAdapter(adapter);
                    lv_ReadXml.setOnItemClickListener(new ItemClickListener());
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btn_PullToXml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveAllData(data, adapter, lv_ReadXml);
                //创建SimpleAdapter适配器将数据绑定到item显示控件上
                data = getData();
                adapter = new SimpleAdapter(MainActivity.this, data, R.layout.item, new String[]{"id", "name", "url"}, new int[]{R.id.id, R.id.name, R.id.url});
                //实现列表的显示
                lv_ReadXml.setAdapter(adapter);
                lv_ReadXml.setOnItemClickListener(new ItemClickListener());
            }
        });

        btn_DomToXml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveAllData(data, adapter, lv_ReadXml);
                //获取到集合数据
                InputStream domstream = domstream = getResources().openRawResource(R.raw.channels);
                List<Channel> channels = getChannelList(domstream);
                data = new ArrayList<HashMap<String, Object>>();
                for (Channel channel : channels) {
                    HashMap<String, Object> item = new HashMap<String, Object>();
                    item.put("id", channel.getId());
                    item.put("name", channel.getName());
                    item.put("url", channel.getUrl());
                    data.add(item);
                }
                //创建SimpleAdapter适配器将数据绑定到item显示控件上
                adapter = new SimpleAdapter(MainActivity.this, data, R.layout.item, new String[]{"id", "name", "url"}, new int[]{R.id.id, R.id.name, R.id.url});
                //实现列表的显示
                lv_ReadXml.setAdapter(adapter);
                lv_ReadXml.setOnItemClickListener(new ItemClickListener());
            }
        });

    }

    /*sax方式解析*/
    private List<Channel> getChannelList() throws ParserConfigurationException, SAXException, IOException {
        //实例化一个SAXParserFactory对象
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser;
        //实例化SAXParser对象，创建XMLReader对象，解析器
        parser = factory.newSAXParser();
        XMLReader xmlReader = parser.getXMLReader();
        //实例化handler，事件处理器
        SAXPraserHelper helperHandler = new SAXPraserHelper();
        //解析器注册事件
        xmlReader.setContentHandler(helperHandler);
        //读取文件流
        InputStream stream = getResources().openRawResource(R.raw.channels);
        InputSource is = new InputSource(stream);
        //解析文件
        xmlReader.parse(is);
        return helperHandler.getList();
    }

    /*pull方式解析*/
    private List<HashMap<String, Object>> getData() {
        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        XmlResourceParser xrp = null;
        xrp = getResources().getXml(R.xml.channels);

        try {
            // 直到文档的结尾处
            while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                // 如果遇到了开始标签
                if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                    String tagName = xrp.getName();// 获取标签的名字
                    if (tagName.equals("item")) {
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        String id = xrp.getAttributeValue(null, "id");// 通过属性名来获取属性值
                        map.put("id", id);
                        String url = xrp.getAttributeValue(1);// 通过属性索引来获取属性值
                        map.put("url", url);
                        map.put("name", xrp.nextText());
                        list.add(map);
                    }
                }
                xrp.next();// 获取解析下一个事件
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return list;
    }

    /*Dom方式解析*/
    public static List<Channel> getChannelList(InputStream stream) {
        List<Channel> list = new ArrayList<Channel>();

        //得到 DocumentBuilderFactory 对象, 由该对象可以得到 DocumentBuilder 对象
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            //得到DocumentBuilder对象
            DocumentBuilder builder = factory.newDocumentBuilder();
            //得到代表整个xml的Document对象
            Document document = builder.parse(stream);
            //得到 "根节点"
            Element root = document.getDocumentElement();
            //获取根节点的所有items的节点
            NodeList items = root.getElementsByTagName("item");
            //遍历所有节点
            for (int i = 0; i < items.getLength(); i++) {
                Channel chann = new Channel();
                Element item = (Element) items.item(i);
                chann.setId(item.getAttribute("id"));
                chann.setUrl(item.getAttribute("url"));
                chann.setName(item.getFirstChild().getNodeValue());
                list.add(chann);
            }

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return list;
    }

    private final class ItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView listView = (ListView) parent;
            HashMap<String, Object> data = (HashMap<String, Object>) listView.getItemAtPosition(position);
            String iname = data.get("name").toString();
            Toast.makeText(getApplicationContext(), iname, Toast.LENGTH_LONG).show();
        }
    }

    private void RemoveAllData(List<HashMap<String, Object>> listmap, SimpleAdapter adapter, ListView listView) {
        if (listmap != null && listmap.size() > 0) {
            listmap.removeAll(listmap);
            if (adapter != null && listView != null) {
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
            }
        }
    }
}

