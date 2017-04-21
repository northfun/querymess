import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class QueryAPI {
	public QueryAPI() {
		this.loadCityCode("data.txt");
	}

	public String queryWeather(String city) {
		String cCode = this.cityCode.get(city);
		if (cCode.length() == 0) {
			return "";
		}

		String urlStr = "http://www.weather.com.cn/weather/" + cCode + ".shtml";

		String htmlLine = this.findFromHtml(urlStr, "hidden_title");
		htmlLine = this.valueFromHtmlLine(htmlLine);
		// System.out.println(htmlLine);
		return htmlLine;
	}

	private HashMap<String, String> cityCode;

	private void loadCityCode(String fileName) {
		if (this.cityCode == null) {
			this.cityCode = new HashMap<String, String>();
		} else {
			this.cityCode.clear();
		}
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String[] strSlc = tempString.split("\t");
				if (strSlc.length >= 2) {
					this.cityCode.put(strSlc[0], strSlc[1]);
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		// System.out.printf("num:%d\n", this.cityCode.size());
	}

	private String findFromHtml(String rurl, String findStr) {
		String htmlLine = null;
		String findResult = "not find";
		try {
			String url = rurl;
			BufferedReader in = new BufferedReader(
					new InputStreamReader(new URL(url).openConnection().getInputStream(), "utf-8"));// GB2312可以根据需要替换成要读取网页的编码
			while ((htmlLine = in.readLine()) != null) {
				if (htmlLine.contains(findStr)) {
					findResult = htmlLine;
					break;
				}
				// System.out.println("***" + htmlLine);
			}
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		}
		return findResult;
	}

	private String valueFromHtmlLine(String line) {
		String ret = line.trim();
		int index;
		index = ret.indexOf("value");
		if (index > 0) {
			ret = ret.substring(index);
			index = ret.indexOf("\"");
			if (index > 0 && index + 1 < ret.length()) {
				ret = ret.substring(index + 1);
				index = ret.indexOf("\"");
				if (index > 0) {
					ret = ret.substring(0, index);
				}
			}
		}
		return ret;
	}

	public static void main(String[] args) {
		QueryAPI q = new QueryAPI();
		System.out.println(q.queryWeather("上海"));
		System.out.println(q.queryWeather("北京"));
		System.out.println(q.queryWeather("成都"));
		System.out.println(q.queryWeather("郑州"));
	}
}
