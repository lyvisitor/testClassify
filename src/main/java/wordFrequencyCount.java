import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * 英文词频统计
 */
public class wordFrequencyCount {


  //训练一类文本
	public static String preProcess(String path) throws Exception {
		List<String> filePathList = new ArrayList<String>();
		//遍历文件夹，读取需要统计的txt文件路径，把文件名存入到list中
    Map<String, Integer> WordNumberList1 = new HashMap<String, Integer>();// 存储进行处理过的单词列表
    Map<String, Integer> WordNumberList2 = new HashMap<String, Integer>();// 存储进过变形还原的单词列表

    readTxtPath(path, filePathList);
		Map<String, Integer> stringList = new HashMap<>();
		// 获取文章并进行处理
		String text = "";
		for (String fp : filePathList) {
			// fp代表文件路径
			text = getData(fp);// 获取需要处理的文章
			textSplitAndCount(text, stringList);// 获取一篇就处理一篇，并存入到stringList
//			System.out.println(fp);
//			System.out.println("成功");
		}
    //处理缩写词，去掉纯数字，开头大写转小写
		processWordList(stringList, WordNumberList1);//至此分词结束
    //变形还原
    WordNumberList2 = getWordNumber(WordNumberList1);

    String resultPath = "/Users/ly/IdeaProjects/testClassify/src/main/resources/测试文本分词结果.txt";
    printMapToFile(WordNumberList2, resultPath);// 输出到txt中
	  return resultPath;
	}
  //训练一篇文本
  public static String preProcess2(String filePath) throws Exception {
    List<String> filePathList = new ArrayList<String>();
    //遍历文件夹，读取需要统计的txt文件路径，把文件名存入到list中
    Map<String, Integer> WordNumberList1 = new HashMap<String, Integer>();// 存储进行处理过的单词列表
    Map<String, Integer> WordNumberList2 = new HashMap<String, Integer>();// 存储进过变形还原的单词列表
    //根据目录获取所有txt目录，存到filePathList
//    readTxtPath(path, filePathList);
    Map<String, Integer> stringList = new HashMap<>();
    // 获取文章并进行处理
    String text = "";
    // fp代表文件路径
    //处理一篇文章
    text = getData(filePath);// 获取需要处理的文章
    textSplitAndCount(text, stringList);// 获取一篇就处理一篇，并存入到stringList
//			System.out.println(fp);
//			System.out.println("成功");
    //处理缩写词，去掉纯数字，开头大写转小写
    processWordList(stringList, WordNumberList1);//至此分词结束
    //变形还原
    WordNumberList2 = getWordNumber(WordNumberList1);

    String resultPath = "/Users/ly/IdeaProjects/testClassify/src/main/resources/测试文本分词结果.txt";
    printMapToFile(WordNumberList2, resultPath);// 输出到txt中
    return resultPath;
  }


	// 按行读取txt文件
	public static String getData(String path) throws IOException {
		// 定义StringBuilder对象，接收文本（比较快）
		StringBuilder text = new StringBuilder();
		File file = new File(path);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));// 构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				text.append(s).append(" ");
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return text.toString();
	}


	/*
	遍历文件夹目录,获得文件路径（递归）
	 */
	public static void readTxtPath(String path, List<String> filePath) throws IOException {
		File file = new File(path);
		File[] fs = file.listFiles();

		if (fs == null) {
			return;
		}
		for (File f : fs) {
			if (f.isFile()) {
				filePath.add(f.getPath());
				//System.out.println(f.getPath());
			} else if (f.isDirectory()) {
				readTxtPath(f.getPath(), filePath);
			}
		}
	}
  /*
  遍历文件夹目录,获得文件路径（递归）
   */
  public static void readTxtPath2(String path, Map<String,List<String> > filePathMap) throws IOException {
    File file = new File(path);
    File[] fs = file.listFiles();

    if (fs == null) {
      return;
    }
    for (File f : fs) {
      if (f.isFile()) {
        //System.out.println(f.getPath());
      } else if (f.isDirectory()) {
        String[] s = f.getPath().split("/");
        String label = s[s.length-1];
        if(!filePathMap.containsKey(label)){
          List<String> list = new ArrayList<>();
          list.add(f.getParent());
          filePathMap.put(label,list);
        }else {
          filePathMap.get(label).add(f.getParent());
        }

      }
    }
  }

	// 对传入的英文文章字符串先做一个粗略的划分,顺便统计相同字符串出现的词数
	public static void textSplitAndCount(String englishText, Map<String, Integer> wordString) {
		// 标点符号中文替换成英文
		String result1 = englishText.replace("。", ".").replace("，", ",").replace("？", "?").replace("！", "!")
				.replace("：", ":").replace("；", ";").replace("“", "\"").replace("”", "\"").replace("‘", "'")
				.replace("’", "'").replace("＇", "\'");

		String[] saveWord;

		// 按照如下正则表达式进行分词
		saveWord = result1.split("[^a-zA-Z0-9']");

		// 遍历字符数组，取出单词，存入map（键存分好的字符串，值存在文章中出现的次数）
		for (int i = 0; i < saveWord.length; i++) {
			String temp = saveWord[i];
			// 如果word有这个词，就让值加1
			if (wordString.containsKey(temp)) {
				wordString.put(temp, wordString.get(temp) + 1);
			}
			// 如果没有，就存入词，让值为1
			else {
			  if (saveWord[i].equals("")) continue;
				wordString.put(temp, 1);
			}
		}
	}

	// 用于处理字符串前后出现单引号的情况，以及字符前出现数字的情况（也可以把纯数字的情况消灭）
	public static String proceessQuotationMarks(String word) {
		// 设置两个标记，记录单词之前分号的位置，以及单词之后分号的位置
		int length = word.length();
		int flag1 = 0;
		int flag2 = word.length();
		// 删掉单词前出现的分号或数字
		for (int i = 0; i < length; i++) {
			if (word.charAt(i) != '\'' && !Character.isDigit(word.charAt(i))) {
				break;
			} else {
				flag1++;
			}
		}
		// 删掉单词后出现的分号
		for (int i = length - 1; i >= 0; i--) {
			if (word.charAt(i) != '\'') {
				break;
			} else {
				flag2 = i;

			}
		}

		String temp = "";
		if (flag1 < flag2) {
			temp = word.substring(flag1, flag2);
		}
		return temp;

	}

	/*
	 * 对获得的粗略的map词表做精细化处理： 1：
	 */
	public static void processWordList(Map<String, Integer> wordString, Map<String, Integer> resultWordList) {
		// 先列出需要特殊展开的缩写词：map集合存储遇到缩略词需要展开的情况，便于之后进行替换
		Map<String, String> abbreviationWord = new HashMap<String, String>();
		abbreviationWord.put("i'm", "I am");
		abbreviationWord.put("he's'", "he is");
		abbreviationWord.put("she's", "she is");
		abbreviationWord.put("it's", "it is");
		abbreviationWord.put("what's", "what is");
		abbreviationWord.put("who's", "who is");
		abbreviationWord.put("where's", "where is");
		abbreviationWord.put("that's", "that is");
		abbreviationWord.put("how's", "how is");
		abbreviationWord.put("let's", "let is");
		abbreviationWord.put("shan't", "shall not");
		abbreviationWord.put("won't", "will not");
		abbreviationWord.put("isn't", "is not");
//		abbreviationWord.put("ain't", "am not");
		abbreviationWord.put("aren't", "are not");
		abbreviationWord.put("wasn't", "was not");
		abbreviationWord.put("weren't", "were not");
		abbreviationWord.put("haven't", "have not");
		abbreviationWord.put("hasn't", "has not");
		abbreviationWord.put("hadn't", "had not");
		abbreviationWord.put("can't", "can not");
		abbreviationWord.put("mustn't", "must not");
		abbreviationWord.put("couldn't", "could not");
		abbreviationWord.put("wouldn't", "would not ");
		abbreviationWord.put("shouldn't", "should not");
		abbreviationWord.put("needn't", "need not");
		abbreviationWord.put("daren't", "dare not");
		abbreviationWord.put("mayn't", "may not");
		abbreviationWord.put("mightn't", "might not");
		abbreviationWord.put("oughtn't", "ought not");
		abbreviationWord.put("whyn't", "why not");
		abbreviationWord.put("don't", "do not");
		abbreviationWord.put("doesn't", "does not");
		abbreviationWord.put("didn't", "did not");
		abbreviationWord.put("i'd", "I would");
		abbreviationWord.put("how’d", "how did");
		abbreviationWord.put("it'd", "it would");

		// 对于传进来存储粗略英文字符串的map进行精细化处理
		for (Map.Entry<String, Integer> word : wordString.entrySet()) {
			String tempWord = word.getKey();// 英文单词
			int wordNumber = word.getValue();// 存储在文章中出现的次数

			if (tempWord.isEmpty()) {
				continue;
			}
			// 如果单词有'开头或结尾，以及以数字开头的英文字符串需进行处理（直接将纯数字的或者纯撇的过滤掉）
			Pattern pattern = Pattern.compile("[0-9]*");
			Matcher startIsNum = pattern.matcher(tempWord.charAt(0) + "");
			if (tempWord.startsWith("'") || tempWord.endsWith("'") || startIsNum.matches()) {
				tempWord = proceessQuotationMarks(tempWord);
			}

			if (tempWord.isEmpty()) {
				continue;
			}

			// 匹配全是大写字母的英文单词
			Pattern changeFirstWord = Pattern.compile("[A-Z]*");
			Matcher isBig = changeFirstWord.matcher(tempWord);

			// 定义两个字符串，将处理好的单词存入字符串，最终存入传出map：resultWordList中
			String saveWord1 = "";
			String saveWord2 = "";
			boolean flag = false;// 用来标记，saveWord有没有被使用

			// 全是大写直接存
			if (isBig.matches()) {
				// 过略掉一些没有意义的单个大写字符
				if (tempWord.length() == 1) {
					if ("A".equals(tempWord) || "I".equals(tempWord)) {
						saveWord1 = tempWord;
					} else {
						continue;
					}
				} else {
					saveWord1 = tempWord;

				}
			}

			else {// 其他情况变成小写，存入时有缩写情况需进行处理
				String st = tempWord.toLowerCase();
				if (abbreviationWord.containsKey(st)) { // 在存储缩列词情况的map中进行查找，看看有没有需要展开的情况
					String[] rp = abbreviationWord.get(st).split(" ");
					saveWord1 = rp[0];
					saveWord2 = rp[1];
					flag = true;

				} else if (st.endsWith("'ll")) {
					int s = st.length() - 3;
					st = st.substring(0, s);
					saveWord1 = st;
					saveWord2 = "will";
					flag = true;
				} else if (st.endsWith("'ve")) {
					int s = st.length() - 3;
					st = st.substring(0, s);
					saveWord1 = st;
					saveWord2 = "have";
					flag = true;

				} else if (st.endsWith("'re")) {
					int s = st.length() - 3;
					st = st.substring(0, s);
					saveWord1 = st;
					saveWord2 = "are";
					flag = true;

				} else {
					st = st.replace("'s", "").replace("s'", "s");
					saveWord1 = st;
				}
			}

			// 将处理好的单词，存入传出map：resultWordList
			if (resultWordList.containsKey(saveWord1)) {
				resultWordList.put(saveWord1, resultWordList.get(saveWord1) + wordNumber);
			} else {
				resultWordList.put(saveWord1, wordNumber);
			}

			if (flag) {
				if (resultWordList.containsKey(saveWord2)) {
					resultWordList.put(saveWord2, resultWordList.get(saveWord2) + wordNumber);
				} else {
					resultWordList.put(saveWord2, wordNumber);
				}
			}

		}
	}

// map按值排序,计算词频，并输出到txt里
	public static void printMapToFile(Map<String, Integer> map, String filePath) throws IOException {
		List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return o2.getValue() - o1.getValue();
			}
		});
		int i = 0;
		FileWriter writer = new FileWriter(new File(filePath));
		// 计算词数总和
		for (Map.Entry<String, Integer> mapping : list) {
			i += mapping.getValue();

		}
		// 写到txt文件里
		for (Map.Entry<String, Integer> mapping : list) {
			 double wordFrenquncy = (double) mapping.getValue() / i;
//			 writer.write(mapping.getKey() + "\t" + mapping.getValue() + "\t" +
//			 wordFrenquncy + "\r\n");
			writer.write(mapping.getKey() + "\t" + mapping.getValue() + "\r\n");

		}
		// writer.write("总词数：" + "\t" + i + "\r\n");
    //System.out.println("总词数：" + i);
		writer.close();
	}

// 获得单词出现的次数
	public static Map<String, Integer> getWordNumber(Map<String, Integer> resultWordList) throws Exception {
		// 处理名词表
		File list1 = new File("/Users/ly/IdeaProjects/testClassify/src/main/resources/conv_table/noun.txt");
		Map<String, String> nounPlural = new HashMap<String, String>();// 复数名词

		try {
			BufferedReader br = new BufferedReader(new FileReader(list1));// 构造一个BufferedReader类来读取文件
			String s = null;
			// 取一行字符串就进行分词，定义临时字符数组用来接收
			String[] temp;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				temp = s.split("\\s+");// 由于有多个空格，按多个空格进行分词
				// map集合里，键里存单词，值里存每个单词的原型
				nounPlural.put(temp[1], temp[0]);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 处理形容词表
		File list2 = new File("/Users/ly/IdeaProjects/testClassify/src/main/resources/conv_table/adj.txt");
		Map<String, String> adjComparisonlevel = new HashMap<String, String>();// 形容词比较级
		Map<String, String> adjSuperlative = new HashMap<String, String>();// 形容词最高级

		try {
			BufferedReader br = new BufferedReader(new FileReader(list2));// 构造一个BufferedReader类来读取文件
			String s = null;
			// 取一行字符串就进行分词，定义临时字符数组用来接收
			String[] temp;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				temp = s.split("\\s+");// 由于有多个空格，按多个空格进行分词

				adjComparisonlevel.put(temp[1], temp[0]);
				adjSuperlative.put(temp[2], temp[0]);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 处理动词表
		File list3 = new File("/Users/ly/IdeaProjects/testClassify/src/main/resources/conv_table/verb.txt");
		Map<String, String> singularFromInThirdPersonal = new HashMap<String, String>();// 第三人称单数
		Map<String, String> pastForm = new HashMap<String, String>();// 过去式
		Map<String, String> pastParticiple = new HashMap<String, String>();// 过去分词
		Map<String, String> presentParticiple = new HashMap<String, String>();// 现在分词

		try {
			BufferedReader br = new BufferedReader(new FileReader(list3));// 构造一个BufferedReader类来读取文件
			String s = null;
			// 取一行字符串就进行分词，定义临时字符数组用来接收
			String[] temp;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				temp = s.split("\\s+");// 由于有多个空格，按多个空格进行分词
				// 将单词按照形态，分别存入到对应的map集合里
				singularFromInThirdPersonal.put(temp[1], temp[0]);
				pastForm.put(temp[2], temp[0]);
				pastParticiple.put(temp[3], temp[0]);
				presentParticiple.put(temp[4], temp[0]);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, Integer> wordList = new HashMap<String, Integer>();// 存储文章中出现的单词及次数
		// 定义一个字符串变量
		String tempWord = "";
		if (!resultWordList.isEmpty()) {
			for (Map.Entry<String, Integer> word : resultWordList.entrySet()) {
				String englishWord = word.getKey();
				int wordNumber = word.getValue();
				if (nounPlural.containsKey(englishWord)) {
					tempWord = nounPlural.get(englishWord);
				} else if (adjComparisonlevel.containsKey(englishWord)) {
					tempWord = adjComparisonlevel.get(englishWord);
				} else if (adjSuperlative.containsKey(englishWord)) {
					tempWord = adjSuperlative.get(englishWord);
				} else if (singularFromInThirdPersonal.containsKey(englishWord)) {
					tempWord = singularFromInThirdPersonal.get(englishWord);
				} else if (pastForm.containsKey(englishWord)) {
					tempWord = pastForm.get(englishWord);
				} else if (pastParticiple.containsKey(englishWord)) {
					tempWord = pastParticiple.get(englishWord);
				} else if (presentParticiple.containsKey(englishWord)) {
					tempWord = presentParticiple.get(englishWord);
				} else {
					tempWord = englishWord;
				}
				if (wordList.containsKey(tempWord)) {
					wordList.put(tempWord, wordList.get(tempWord) + wordNumber);
				} else {
					wordList.put(tempWord, wordNumber);
				}
			}
		}
		return wordList;
	}

}
