package com.key.mail.comm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import javax.naming.ConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * <p>
 * 该类用于读取.ini格式的配置文件。
 * </p>
 * <p>
 * .ini格式如下所示：
 *
 * <pre>
 * ; This line is comment
 * # This line is comment too.
 * [section1]
 * key1 = value1
 * key2 = true
 * key3 = 12
 *
 * [section2]
 * key1 =
 * key2 = abc,def,ghi
 * </pre>
 *
 * 注：分号";"是ini格式的标准注释符。但是因为在java普通使用的properties文件中使用"#"作为注释符，所以这里把#也看作注释符
 *
 * </p>
 *
 * <p>
 * 使用示例：
 *
 * <pre>
 * IniReader iniReader = new IniReader(&quot;D:/abc/config.ini&quot;);
 * iniReader.getString(&quot;sections1&quot;, &quot;key1&quot;); // --&gt; value1
 * iniReader.getBoolean(&quot;sections1&quot;, &quot;key2&quot;); // --&gt; true
 * iniReader.getInt(&quot;section1&quot;, &quot;key3&quot;); // --&gt; 12
 * iniReader.getString(&quot;section2&quot;, &quot;key1&quot;); // --&gt; &quot;&quot;
 * iniReader.getStringArray(&quot;section2&quot;, &quot;key2&quot;); // --&gt; [abc, def, ghi]
 *
 * // Exceptions will throw if key cann't be found
 * intReader.getString(&quot;section1&quot;, &quot;not-exist-key&quot;); // --&gt; throw ConfigurationException
 *
 * // But if we give it a default value, no exceptions will be thrown
 * iniReader.getString(&quot;section1&quot;, &quot;not-exist-key&quot;, &quot;default_value&quot;); // --&gt; default_value
 *
 * // to check sections and keys exist
 * iniReader.containsSection(&quot;section1&quot;); // --&gt; true
 * iniReader.containsKey(&quot;not-exist-section&quot;, &quot;key1&quot;); // --&gt; false
 * </pre>
 *
 * </p>
 * <p>
 * 线程安全：该类线程安全，因为它是不可变类。
 * </p>
 * @author Key.Xiao
 * @version 1.0
 */
public class IniReader {

	/**
	 * 使用{@link LinkedHashMap},可保持key的顺序
	 */
	private LinkedHashMap<String, Properties> data = new LinkedHashMap<String, Properties>();

	/**
	 * <p>
	 * 构造函数。使用配置文件的绝对路径作为参数。
	 * </p>
	 *
	 * @param filepath
	 *            配置文件的绝对路径。不可为null，或者trimmed empty。应该为一个有效的文件路径。
	 * @throws ConfigurationException
	 *             如果找不到配置文件，或者读取时发生错误，或者解析时发生错误
	 * @throws IllegalArgumentException
	 *             如果参数为null，或者为trimmed empty
	 *
	 */
	@SuppressWarnings( { "unchecked", "deprecation" })
	public IniReader(String filepath) {
		ArgumentValidator.notNullOrTrimmedEmpty(filepath, "filepath");
		try {
			List<String> lines = FileUtils.readLines(new File(filepath));
			loadConfig(lines);
		} catch (IOException e) {
			try {
				throw new ConfigurationException();
			} catch (ConfigurationException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * <p>
	 * 构造函数。使用配置文件的绝对路径和字符集作为参数。
	 * </p>
	 *
	 * @param filepath
	 *            配置文件的绝对路径。不可为null，或者trimmed empty。应该为一个有效的文件路径。
	 * @param charsetName
	 *            指定使用的字符集
	 *
	 * @throws IllegalArgumentException
	 *             如果参数为null，或者为trimmed empty。
	 * @throws ConfigurationException
	 *             如果找不到配置文件，或者读取时发生错误，或者解析时发生错误
	 */
	@SuppressWarnings("unchecked")
	public IniReader(String filepath, String charsetName) {
		ArgumentValidator.notNullOrTrimmedEmpty(filepath, "filepath");
		ArgumentValidator.notNullOrTrimmedEmpty(charsetName, "charsetName");
		try {
			List<String> lines = FileUtils.readLines(new File(filepath), charsetName);
			loadConfig(lines);
		} catch (IOException e) {
			try {
				throw new ConfigurationException();
			} catch (ConfigurationException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * <p>
	 * 构造函数。使用一个InputStream作为配置的数据源
	 * </p>
	 *
	 * @param inputStream
	 *            用于读取配置数据
	 * @throws IllegalArgumentException
	 *             如果参数为null
	 * @throws ConfigurationException
	 *             如果读取数据发生错误，或者解析出错
	 *
	 */
	@SuppressWarnings( { "unchecked", "deprecation" })
	public IniReader(InputStream inputStream) {
		ArgumentValidator.notNull(inputStream, "inputStream");
		try {
			List<String> lines = IOUtils.readLines(inputStream);
			loadConfig(lines);
		} catch (IOException e) {
			try {
				throw new ConfigurationException();
			} catch (ConfigurationException e1) {
				e1.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public IniReader(Reader reader) {
		ArgumentValidator.notNull(reader, "reader");
		try {
			List<String> lines = IOUtils.readLines(reader);
			loadConfig(lines);
		} catch (IOException e) {
			try {
				throw new ConfigurationException();
			} catch (ConfigurationException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * 解析配置数据
	 *
	 * @param lines
	 *            待解析的数据，每个元素表示一行
	 * @throws ConfigurationException
	 *             如果解析出错
	 */
	@SuppressWarnings("unused")
	private void loadConfig(List<String> lines) {
		String section = null;

		for (int i = 0; i < lines.size(); i++) {
			int lineNumber = i + 1;
			String oriLine = lines.get(i);
			String line = oriLine.trim();
			// blank line and comment
			if (line.equals("") || line.startsWith(";") || line.startsWith("#"))
				continue;

			// section
			if (line.startsWith("[") && line.endsWith("]")) {
				// remove "[" and "]"
				section = line.substring(1, line.length() - 1);
				if (this.data.containsKey(section)) {
				}
				this.data.put(section, new Properties());
			} else if (line.contains("=")) {
				// property
				if (section == null) {
				}
				int p = line.indexOf('=');
				String key = line.substring(0, p).trim();
				String value = line.substring(p + 1).trim();
				Properties properties = this.data.get(section);
				if (properties.containsKey(key)) {
				}
				properties.put(key, value);
			} else {
			}
		}
	}

	/**
	 * <p>
	 * 以section和key得到String类型的属性值。属性值将会被trim
	 * </p>
	 *
	 * @param section
	 *            节标题。不可为null，或trimmed empty。
	 * @param key
	 *            属性key。不可为null，或trimmed empty。
	 * @return 属性值。返回trim之后的字符串。不会为null。
	 * @throws IllegalArgumentException
	 *             如果section或key为null，或者为trimmed empty
	 * @throws ConfigurationException
	 *             如果找不到该属性
	 *
	 */
	public String getString(String section, String key) {
		if (!containsKey(section, key)) {

		}
		return this.data.get(section).getProperty(key);
	}

	/**
	 * <p>
	 * 以section和key得到int类型的属性值。如果不是一个int，将会抛出ConfigurationException。
	 * </p>
	 *
	 * @param section
	 *            节标题。不可为null，或trimmed empty。
	 * @param key
	 *            属性key。不可为null，或trimmed empty。
	 * @return int类型的属性值
	 * @throws IllegalArgumentException
	 *             如果section或key为null，或者为trimmed empty
	 * @throws ConfigurationException
	 *             如果找不到该属性，或者属性值不可以被转换为int
	 */
	public int getInt(String section, String key) {
		String value = this.getString(section, key);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			try {
				throw new ConfigurationException();
			} catch (ConfigurationException e1) {
				e1.printStackTrace();
				return 0;
			}
		}
	}

	/**
	 * <p>
	 * 以section和key得到boolean类型的属性值。字符串true和false（都不分大小写）可以被接受，其它的字符串（包括空）都将抛出ConfigurationException。
	 * </p>
	 *
	 * @param section
	 *            节标题。不可为null，或trimmed empty。
	 * @param key
	 *            属性key。不可为null，或trimmed empty。
	 * @return boolean类型的属性值
	 *
	 * @throws IllegalArgumentException
	 *             如果section或key为null，或者为trimmed empty
	 * @throws ConfigurationException
	 *             如果找不到该属性，或者属性值不能被转换为boolean
	 *
	 */
	public boolean getBoolean(String section, String key) {
		String value = this.getString(section, key).toLowerCase();
		if (!value.equals("true") && !value.equals("false")) {

		}
		return Boolean.parseBoolean(value);
	}

	/**
	 * <p>
	 * 以section和key得到String[]类型的属性值。其中各元素之间以逗号分隔。得到的各元素都将被trim。
	 * </p>
	 *
	 * @param section
	 *            节标题。不可为null，或trimmed empty。
	 * @param key
	 *            属性key。不可为null，或trimmed empty。
	 * @return 属性值。每个元素都被trim。不可为null。
	 * @throws IllegalArgumentException
	 *             如果section或key为null，或者为trimmed empty
	 * @throws ConfigurationException
	 *             如果找不到该属性
	 *
	 */
	public String[] getStringArray(String section, String key) {
		String value = this.getString(section, key);
		// NOTE: String#split()
		// "a,b" -> ["a","b"] -- expected
		// "a," -> ["a"] -- not expected
		// so we have to add one more "" string if the value end with separator
		boolean addEndingSpaceItem = value.endsWith(",");
		String[] items = value.split(",");
		String[] array = new String[addEndingSpaceItem ? items.length + 1 : items.length];
		for (int i = 0; i < items.length; i++) {
			array[i] = items[i].trim();
		}
		if (addEndingSpaceItem) {
			array[array.length - 1] = "";
		}
		return array;
	}

	/**
	 * <p>
	 * 以section和key得到String类型的属性值。如果属性中有占位符#{xxx}，则它将会按照time参数被转换为对应格式的时间。转换规则与DateFormat相同。
	 * </p>
	 *
	 * @param section
	 *            节标题。不可为null，或trimmed empty。
	 * @param key
	 *            属性key。不可为null，或trimmed empty。
	 * @param time
	 *            用于格式化的时间值。必须大于等于0
	 * @return 属性值
	 * @throws IllegalArgumentException
	 *             如果section或key为null，或者为trimmed empty,或者time<0
	 * @throws ConfigurationException
	 *             如果找不到该属性，或者属性值里的占位符在转换为时间时出错
	 */
	public String getStringWithDateReplacement(String section, String key, long time) {
		ArgumentValidator.isTrue(time >= 0, "time should >=0: " + time);
		String value = this.getString(section, key);
		try {
			return DateTimeFormatter.replaceDateTime(value, time, true);
		} catch (IllegalArgumentException e) {
			try {
				throw new ConfigurationException();
			} catch (ConfigurationException e1) {
				e1.printStackTrace();
				return null;
			}
		}
	}

	/**
	 * <p>
	 * 以section和key得到String类型的属性值。如果找不到该属性，则返回指定的默认值。
	 * </p>
	 *
	 * @param section
	 *            节标题。不可为null，或trimmed empty。
	 * @param key
	 *            属性key。不可为null，或trimmed empty。
	 * @param defaultValue
	 *            当该属性不存在时，返回的默认值。
	 * @return 属性值。
	 * @throws IllegalArgumentException
	 *             如果section或key为null，或者为trimmed empty
	 *
	 */
	public String getString(String section, String key, String defaultValue) {
		if (!containsKey(section, key))
			return defaultValue;
		return getString(section, key);
	}

	/**
	 * <p>
	 * 以section和key得到int类型的属性值。如果找不到该属性，则返回指定的默认值。如果不是一个int，将会抛出ConfigurationException。
	 * </p>
	 *
	 * @param section
	 *            节标题。不可为null，或trimmed empty。
	 * @param key
	 *            属性key。不可为null，或trimmed empty。
	 * @param defaultValue
	 *            当该属性不存在时，返回的默认值。
	 * @return 属性值。
	 * @throws IllegalArgumentException
	 *             如果section或key为null，或者为trimmed empty
	 * @throws ConfigurationException
	 *             如果属性值不能被转换为int
	 */
	public int getInt(String section, String key, int defaultValue) {
		if (!containsKey(section, key))
			return defaultValue;
		return this.getInt(section, key);
	}

	/**
	 * <p>
	 * 以section和key得到boolean类型的属性值。如果找不到该属性，将返回指定的默认值。字符串true和false（都不分大小写）可以被接受，其它的字符串（包括空）都将抛出ConfigurationException。
	 * </p>
	 *
	 * @param section
	 *            节标题。不可为null，或trimmed empty。
	 * @param key
	 *            属性key。不可为null，或trimmed empty。
	 * @param defaultValue
	 *            当该属性不存在时，返回的默认值。
	 * @return 属性值。
	 * @throws IllegalArgumentException
	 *             如果section或key为null，或者为trimmed empty
	 * @throws ConfigurationException
	 *             如果属性值不能被转换为boolean
	 *
	 */
	public boolean getBoolean(String section, String key, boolean defaultValue) {
		if (!containsKey(section, key))
			return defaultValue;
		return this.getBoolean(section, key);
	}

	/**
	 * <p>
	 * 以section和key得到String[]类型的属性值。其中各元素之间以逗号分隔。得到的各元素都将被trim。如果找不到该属性，将返回指定的默认值。
	 * </p>
	 *
	 * @param section
	 *            节标题。不可为null，或trimmed empty。
	 * @param key
	 *            属性key。不可为null，或trimmed empty。
	 * @param defaultValue
	 *            当该属性不存在时，返回的默认值。
	 *
	 * @return 属性值。
	 * @throws IllegalArgumentException
	 *             如果section或key为null，或者为trimmed empty
	 */
	public String[] getStringArray(String section, String key, String[] defaultValue) {
		if (!containsKey(section, key))
			return defaultValue;
		return this.getStringArray(section, key);
	}

	/**
	 * <p>
	 * 以section和key得到String类型的属性值。如果找不到该属性，将返回指定的默认值。如果属性中有占位符#{xxx}，则它将会按照time参数被转换为对应格式的时间。转换规则与DateFormat相同。
	 * </p>
	 *
	 * @param section
	 *            不可为null，或trimmed empty。
	 * @param key
	 *            属性key。不可为null，或trimmed empty。
	 * @param time
	 *            用于格式化的时间值。必须大于等于0
	 * @param defaultValue
	 *            当该属性不存在时，返回的默认值。
	 * @return 属性值。如果其中包含时间占位符，则将被格式化。
	 * @throws IllegalArgumentException
	 *             如果section或key为null，或者为trimmed empty, 或者time<0
	 * @throws ConfigurationException
	 *             如果时间值不能被正确转换
	 */
	public String getStringWithDateReplacement(String section, String key, long time, String defaultValue) {
		ArgumentValidator.isTrue(time >= 0, "time should >= 0: " + time);
		if (!containsKey(section, key))
			return defaultValue;
		return this.getStringWithDateReplacement(section, key, time);
	}

	/**
	 * <p>
	 * 配置中是否包含指定的section
	 * </p>
	 *
	 * @param section
	 *            节标题。不可为null，或trimmed empty。
	 * @return 配置中是否包含指定的section
	 * @throws IllegalArgumentException
	 *             如果section为null，或者为trimmed empty
	 *
	 */
	public boolean containsSection(String section) {
		ArgumentValidator.notNullOrTrimmedEmpty(section, "section");
		return this.data.containsKey(section);
	}

	/**
	 * <p>
	 * 配置中是否包含指定的section和key
	 * </p>
	 *
	 * @param key
	 *            属性key。不可为null，或trimmed empty。
	 * @param section
	 *            节标题。不可为null，或trimmed empty。
	 * @return 配置中是否包含指定的section和key
	 * @throws IllegalArgumentException
	 *             如果section或key为null，或者为trimmed empty
	 */
	public boolean containsKey(String section, String key) {
		ArgumentValidator.notNullOrTrimmedEmpty(section, "section");
		ArgumentValidator.notNullOrTrimmedEmpty(key, "key");

		if (!containsSection(section))
			return false;
		Properties properties = this.data.get(section);
		return properties.containsKey(key);
	}

	/**
	 * 得到所有的Section标题。其顺序为配置文件中的顺序。
	 *
	 * @return 所有的Section标题
	 */
	public List<String> getSectionTitles() {
		return new ArrayList<String>(this.data.keySet());
	}

	/**
	 * 得到某个section标题对应的属性集
	 *
	 * @param sectionTitle
	 *            section标题
	 * @return 该标题对应的属性集
	 */
	public Properties getSection(String sectionTitle) {
		return this.data.get(sectionTitle);
	}
}
