package crawler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import historyobject.*;
public class FestivalWikiCrawler extends Crawler {
    private JSONArray festivalList;

    public FestivalWikiCrawler() {
        setWebLink("https://vi.wikipedia.org");
        setFolder("data/FestivalWiki.json");
        setStartLink("/wiki/L%E1%BB%85_h%E1%BB%99i_Vi%E1%BB%87t_Nam");
    }

    @Override
    public void scrapePage(String pageToScrape) {
        Document doc = getDocument(getWebLink() + pageToScrape);
        if (doc != null) {
            Elements tableRows = doc.select("table.wikitable > tbody > tr");

            for (int i = 1; i < tableRows.size(); i++) {
                Element row = tableRows.get(i);

                FestivalWiki festival = new FestivalWiki();

                String name = row.select("td:nth-of-type(2) > a").text();
                String id = getWebLink() + row.select("td:nth-of-type(2) > a").attr("href");

                festival.setName(name);
                festival.setUrl(id);

                String startDate = row.select("td:nth-of-type(1)").text();
                if (!startDate.isEmpty()) {
                    festival.getInfo().put("Start Date (Lunar)", startDate);
                }

                String position = row.select("td:nth-of-type(2)").text();
                if (!position.isEmpty()) {
                    festival.getInfo().put("Position", position);
                }

                String firstTime = row.select("td:nth-of-type(3)").text();
                if (!firstTime.isEmpty()) {
                    festival.getInfo().put("First Time", firstTime);
                }

                String connectedCharacters = row.select("td:nth-of-type(4)").text();
                if (!connectedCharacters.isEmpty()) {
                    festival.setConnect(connectedCharacters);
                }

                scrapeInformation(id, "div.mw-parser-output > p:first-of-type", festival);

                festivalList.put(festival.toJSONObject());
            }
        }
    }

    private Document getDocument(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public void scrapeInformation(String url, String cssQuery, FestivalWiki festival) {
        Document doc = getDocument(url);
        if (doc != null) {
            Element element = doc.selectFirst(cssQuery);
            String information = element != null ? element.text() : "";

            festival.setDescription(information);
        }
    }

    public void crawlData() {
        festivalList = new JSONArray();
        scrapePage(getStartLink());

        // Hiển thị dữ liệu trên console
        System.out.println(festivalList.toString(4));

        // Lưu dữ liệu vào file JSON
        saveData();
    }

    public void saveData() {
        FileWriter file = null;
        try {
            file = new FileWriter(getFolder());
            file.write(festivalList.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (file != null) {
                    file.flush();
                    file.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        FestivalWikiCrawler crawler = new FestivalWikiCrawler();
        crawler.crawlData();
    }
}
