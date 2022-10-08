package com.example.blognpc.tools.DSP;

import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class DSPJSONGenerator {
    /**
     * 注意：生成的数据中 "formula" 部分可能包含以下例外
     * 1. "costTime" 不仅包含通常的时间，如：2s，还包含分馏器的生成概率 1%，蓄电器在电力枢纽中充电速率 ? (英文字符）
     * 2. "fromNum", "toNum" 对于没有标记物品数量的公式，如 蓄电器 → 蓄电器（满），原公式是没有标记物品数量的（原因不明），json 中默认保存为1
     *
     * @param args
     */
    public static void main(String[] args) {
        Map<String, Object> mapDSP = new HashMap<>();

        // 读取公式
        mapDSP.put("formula", getFormula());

        // 读取组件
        mapDSP.put("component", getComponent());

        // 读取建筑
        mapDSP.put("facility", getFacility());

        saveAsJSON(mapDSP);
    }

    /**
     * 使用 Jsoup 读取公式
     * https://wiki.biligame.com/dsp/公式合集
     *
     * @return
     */
    private static List<Map> getFormula() {
        Document document = null;
        try {
//            document = Jsoup.parse(new File("src/main/java/com/example/blognpc/tools/DSP/html/DSPFormula.html"), "utf-8");
            document = Jsoup.connect("https://wiki.biligame.com/dsp/公式合集").get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Element table = document.getElementsByTag("table").get(0);
        // 跳过表格第一行
        List<Element> formulas = table.getElementsByTag("tr").stream().skip(1L).collect(Collectors.toList());

        List<Map> formulaList = new ArrayList<>();
        for (Element formula : formulas) {
            Elements td = formula.getElementsByTag("td");

            Element body = td.get(0);    // 公式主体
            String facility = td.get(1).html();   // 使用的设施
            boolean manual = td.get(2).html().equals("合成面板") ? true : false;// 是否可以手动制作

            Elements items = body.children().get(0).children().get(0).children();

            Map<String, Object> formulaMap = new HashMap<>();
            List<String> fromList = new ArrayList<>();
            List<Integer> fromNumList = new ArrayList<>();
            List<String> toList = new ArrayList<>();
            List<Integer> toNumList = new ArrayList<>();
            String costTime = null;
            Boolean fromItem = true;
            for (Element item : items) {
                String itemName = item.getElementsByTag("img").get(0).attr("alt");
                if ("箭头.png".equals(itemName)) {
                    costTime = item.getElementsByClass("formula-arrow-t-time").get(0).html().replace("？", "?");
                    fromItem = false;
                    continue;
                }
                Integer itemNum;
                try {
                    itemNum = Integer.parseInt(item.getElementsByClass("icon-num").get(0).html());
                } catch (java.lang.IndexOutOfBoundsException e) {
                    itemNum = 1;
                }
                if (fromItem) {
                    fromList.add(itemName);
                    fromNumList.add(itemNum);
                } else {
                    toList.add(itemName);
                    toNumList.add(itemNum);
                }

            }

            formulaMap.put("from", fromList);
            formulaMap.put("fromNum", fromNumList);
            formulaMap.put("to", toList);
            formulaMap.put("toNum", toNumList);
            formulaMap.put("via", facility);
            formulaMap.put("manual", manual);
            formulaMap.put("costTime", costTime);

            formulaList.add(formulaMap);
        }

        return formulaList;
    }

    /**
     * https://wiki.biligame.com/dsp/首页
     * @return
     */
    private static List<String> getComponent() {
        Document document = null;
        try {
//            document = Jsoup.parse(new File("src/main/java/com/example/blognpc/tools/DSP/html/DSPItem.html"), "utf-8");
            document = Jsoup.connect("https://wiki.biligame.com/dsp/首页").get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Element table = document.getElementsByTag("table").get(0);

        List<Element> tds = table.getElementsByTag("td").stream().filter(td -> td.children().size() != 0 && td.child(0).children().size() != 0).collect(Collectors.toList());

        List<String> itemList = new ArrayList<>();
        for (Element td : tds) {
            String itemName = td.getElementsByTag("img").get(0).attr("alt");
            itemList.add(itemName);
        }

        return itemList;
    }

    /**
     * https://wiki.biligame.com/dsp/首页
     * @return
     */
    private static List<String> getFacility() {
        Document document = null;
        try {
//            document = Jsoup.parse(new File("src/main/java/com/example/blognpc/tools/DSP/html/DSPItem.html"), "utf-8");
            document = Jsoup.connect("https://wiki.biligame.com/dsp/首页").get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Element table = document.getElementsByTag("table").get(1);

        List<Element> tds = table.getElementsByTag("td").stream().filter(td -> td.children().size() != 0 && td.child(0).children().size() != 0).collect(Collectors.toList());

        List<String> itemList = new ArrayList<>();
        for (Element td : tds) {
            String itemName = td.getElementsByTag("img").get(0).attr("alt");
            itemList.add(itemName);
        }

        return itemList;
    }

    private static void saveAsJSON(Map map) {
        try {
            // 保证创建一个新文件
            File file = new File("src/main/java/com/example/blognpc/tools/DSP/json/DSP.json");
            if (file.exists()) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Override File DSP.json ? (Y/N)");
                String confirm = scanner.nextLine();
                if (!confirm.equalsIgnoreCase("y")) {
                    return;
                }
            }
            file.createNewFile();

            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll(map);

            Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            write.write(jsonObject.toJSONString());
            write.flush();
            write.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
