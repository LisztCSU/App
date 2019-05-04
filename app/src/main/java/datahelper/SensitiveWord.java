package datahelper;
import android.content.Context;

import com.liszt.wesee.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *  敏感词过滤 工具类   -- 【匹配度高，可以使用】
 *  《高效精准》敏感字&词过滤：http://blog.csdn.net/hubiao_0618/article/details/45076871
 * @author hubiao
 * @version 0.1
 * @CreateDate 2015年4月16日 15:28:32
 */
public class SensitiveWord {
    private StringBuilder replaceAll;//初始化
    private static final String encoding = "UTF-8";
    private static final String replceStr = "*";
    private Context context;
    private String content;
    private int replceSize = 500;
    private List<String> arrayList;
    public Set<String> sensitiveWordSet;//包含的敏感词列表，过滤掉重复项
    public List<String> sensitiveWordList;//包含的敏感词列表，包括重复项，统计次数
public SensitiveWord(Context context,String content){
    this.context = context;
    this.content = content;
}


    public String filterInfo(String str)
    {  	sensitiveWordSet = new HashSet<String>();
        sensitiveWordList= new ArrayList<>();
        StringBuilder buffer = new StringBuilder(str);
        HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>(arrayList.size());
        String temp;
        for(int x = 0; x < arrayList.size();x++)
        {
            temp = arrayList.get(x);
            int findIndexSize = 0;
            for(int start = -1;(start=buffer.indexOf(temp,findIndexSize)) > -1;)
            {
                //System.out.println("###replace="+temp);
                findIndexSize = start+temp.length();//从已找到的后面开始找
                Integer mapStart = hash.get(start);//起始位置
                if(mapStart == null || (mapStart != null && findIndexSize > mapStart))//满足1个，即可更新map
                {
                    hash.put(start, findIndexSize);
                    //System.out.println("###敏感词："+buffer.substring(start, findIndexSize));
                }
            }
        }
        Collection<Integer> values = hash.keySet();
        for(Integer startIndex : values)
        {
            Integer endIndex = hash.get(startIndex);
            //获取敏感词，并加入列表，用来统计数量
            String sensitive = buffer.substring(startIndex, endIndex);
            //System.out.println("###敏感词："+sensitive);
            if (!sensitive.contains("*")) {//添加敏感词到集合
                sensitiveWordSet.add(sensitive);
                sensitiveWordList.add(sensitive);
            }
            buffer.replace(startIndex, endIndex, replaceAll.substring(0,endIndex-startIndex));
        }
        hash.clear();
        return buffer.toString();
    }
    /**
     *   初始化敏感词库
     */
    public void InitializationWork()
    {
        replaceAll = new StringBuilder(replceSize);
        for(int x=0;x < replceSize;x++)
        {
            replaceAll.append(replceStr);
        }
        //加载词库
        arrayList = new ArrayList<String>();
        InputStreamReader read = null;
        BufferedReader bufferedReader = null;
        InputStream inputStream =context.getResources().openRawResource(R.raw.censorwords);
        try {
            read = new InputStreamReader(inputStream,encoding);
            bufferedReader = new BufferedReader(read);
            for(String txt = null;(txt = bufferedReader.readLine()) != null;){
                if(!arrayList.contains(txt))
                    arrayList.add(txt);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if(null != bufferedReader)
                    bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if(null != read)
                    read.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public StringBuilder getReplaceAll() {
        return replaceAll;
    }
    public void setReplaceAll(StringBuilder replaceAll) {
        this.replaceAll = replaceAll;
    }
    public String getReplceStr() {
        return replceStr;
    }

    public int getReplceSize() {
        return replceSize;
    }
    public void setReplceSize(int replceSize) {
        this.replceSize = replceSize;
    }

    public List<String> getArrayList() {
        return arrayList;
    }
    public void setArrayList(List<String> arrayList) {
        this.arrayList = arrayList;
    }
    public String getEncoding() {
        return encoding;
    }


    public boolean  check(){

        this.InitializationWork();
        this.filterInfo(content);
        if (sensitiveWordList.size()>0){
            return false;
        }
        else {
            return true;
        }
    }
    public String replace(){
        this.InitializationWork();
        return filterInfo(content);
    }

}

